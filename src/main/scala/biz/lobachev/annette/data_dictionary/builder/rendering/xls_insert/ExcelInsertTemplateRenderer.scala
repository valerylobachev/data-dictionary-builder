package biz.lobachev.annette.data_dictionary.builder.rendering.xls_insert

import biz.lobachev.annette.data_dictionary.builder.POSTGRESQL
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{BinaryRenderer, RenderResult}
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.{XSSFRow, XSSFSheet, XSSFWorkbook}

import java.io.FileOutputStream
import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}
import java.util.UUID

case class ExcelInsertTemplateRenderer(
  domain: Domain,
) extends BinaryRenderer {
  val Q2 = "\"\""
  val Q3 = "\"\"\""
  val Q4 = "\"\"\"\""

  override def render(path: String): Seq[RenderResult] = {
    val fullPath = s"$path/template"
    new java.io.File(fullPath).mkdirs

    domain.components.values.foreach { group =>
      val filename = s"$fullPath/${group.id}.xlsx"
      val wb       = new XSSFWorkbook()
      domain.entities.values
        .filter(entity => entity.componentId == group.id && entity.entityType == TableEntity)
        .toSeq
        .foreach(entity => renderEntity(entity, wb))
      val fileOut  = new FileOutputStream(filename)
      try wb.write(fileOut)
      finally if (fileOut != null) fileOut.close()
    }
    Seq.empty
  }

  def renderEntity(entity: Entity, wb: XSSFWorkbook) = {
    val sheet                    = wb.createSheet(entity.id)
    val (valueRowIndex, typeRow) = renderHeader(entity, sheet)
    renderValueRow(entity, sheet, valueRowIndex, typeRow)
  }

  private def renderHeader(entity: Entity, sheet: XSSFSheet): (Int, XSSFRow) = {
    val headerRow      = sheet.createRow(0)
    headerRow.createCell(0).setCellValue(entity.schema.getOrElse(""))
    headerRow.createCell(1).setCellValue(entity.tableName)
    headerRow.createCell(2).setCellValue(entity.name)
    val descriptionRow = sheet.createRow(1)
    val nameRow        = sheet.createRow(2)
    val fieldRow       = sheet.createRow(3)
    val typeRow        = sheet.createRow(4)
    val fields         = entity.expandedFields
    fields.zipWithIndex.foreach { case field -> index =>
      val typeSeq = Seq(
        Some(domain.getTargetDataType(field.dataType, POSTGRESQL)),
        if (entity.pk.contains(field.fieldName)) Some("PK") else None,
        if (field.notNull) Some("NN") else None,
        if (field.autoIncrement) Some("INC") else None,
      ).flatten
      val typeStr = if (typeSeq.nonEmpty) typeSeq.mkString(", ") else ""
      descriptionRow.createCell(index).setCellValue(field.description)
      nameRow.createCell(index).setCellValue(field.name)
      fieldRow.createCell(index).setCellValue(field.dbFieldName)
      typeRow.createCell(index).setCellValue(typeStr)
    }
    typeRow.createCell(fields.length).setCellValue("Insert statement")
    typeRow.createCell(fields.length + 2).setCellValue("Insert ignoring errors statement")
    typeRow.createCell(fields.length + 3).setCellValue("Upsert statement")
    (5, typeRow)
  }

  private def renderValueRow(entity: Entity, sheet: XSSFSheet, rowIndex: Int, typeRow: XSSFRow) = {
    val formulaRow = sheet.createRow(rowIndex)
    val N          = entity.expandedFields.length
    val rowNum     = rowIndex + 1
    val table      = entity.schema.map(_ => s"""$Q3&A$$1&$Q3.$Q3&B$$1&$Q3""").getOrElse(s"$Q3&B$$1&$Q3")

    val fields        = entity.expandedFields.zipWithIndex.map { case field -> index =>
      val column      = CellReference.convertNumToColString(index)
      val fieldCell   = s"$$${column}$$4"
      val valueColumn = CellReference.convertNumToColString(N + 4 + index)
      val valueCell   = s"${valueColumn}6"
      typeRow.createCell(N + 4 + index).setCellFormula(fieldCell)
      formulaRow.createCell(N + 4 + index).setCellFormula(fieldValueFormula(field, rowNum, index))
      (s"$Q4&$fieldCell&$Q4", valueCell, entity.pk.contains(field.fieldName))
    }
    val fieldCells    = fields.map(_._1)
    val valueCells    = fields.map(_._2)
    val fieldsFormula = fieldCells.mkString("&\", \"&")
    val valuesFormula = valueCells.mkString("&\", \"&")

    val fieldFormula = s""""INSERT INTO $table ("&$fieldsFormula&") VALUES ("&$valuesFormula&");" """
    formulaRow.createCell(N).setCellFormula(fieldFormula)
//    println(s"fieldsFormula: $fieldsFormula")
//    println(s"valuesFormula: $valuesFormula")
//    println(s"fieldFormula: $fieldFormula")
//    println()

    val insertFormula2 = s""""INSERT INTO $table ("&$fieldsFormula&") VALUES""""
    typeRow.createCell(N + 1).setCellFormula(insertFormula2)
    val nextCell       = s"${CellReference.convertNumToColString(N + 1)}${rowNum + 1}"
    val fieldFormula2  = s" \"(\"&$valuesFormula&\")\"&if($nextCell=\"\",\";\",\",\")"
    formulaRow.createCell(N + 1).setCellFormula(fieldFormula2)

    val pk = entity.expandedFields
      .filter(f => entity.pk.contains(f.fieldName))
      .map(f => s"$Q2${f.dbFieldName}$Q2")
      .mkString(", \"&\"")

    val fieldFormula3 =
      s""""INSERT INTO $table ("&$fieldsFormula&") VALUES ("&$valuesFormula&") ON CONFLICT ($pk) DO NOTHING;" """
    formulaRow.createCell(N + 2).setCellFormula(fieldFormula3)

    val valuesFormula4 =
      fields.filter(!_._3).map(f => s"${f._1}&\"=EXCLUDED.\"&${f._1}").mkString("&\", \"&")
    val fieldFormula4  = if (valuesFormula4.length > 0) {
      s""""INSERT INTO $table ("&$fieldsFormula&") VALUES ("&$valuesFormula&") ON CONFLICT ($pk) DO UPDATE SET \"&$valuesFormula4&\" ;" """
    } else {
      // all fields in PK
      fieldFormula3
    }
    formulaRow.createCell(N + 3).setCellFormula(fieldFormula4)

  }

  private def getOriginValue(dataType: DataType, originCell: String): String =
    dataType match {
      case _: IntInt | _: LongLong | _: ShortSmallint | _: BigDecimalNumeric | _: BigIntegerNumeric | _: DoubleDouble |
          _: FloatFloat | _: BooleanBoolean =>
        originCell
      case DataElementType(dataElementId) =>
        getOriginValue(domain.dataElements(dataElementId).dataType, originCell)
      case _                              =>
        s"\"'\"&SUBSTITUTE($originCell,\"'\",\"''\")&\"'\""
    }

  private def getEmptyOriginValue(dataType: DataType, originCell: String): String =
    dataType match {
      case _: IntInt | _: LongLong | _: ShortSmallint | _: BigDecimalNumeric | _: BigIntegerNumeric | _: DoubleDouble |
          _: FloatFloat =>
        "\"0\""
      case _: BooleanBoolean                                =>
        "\"false'\""
      case _: UuidUuid                                      =>
        s"\"'${new UUID(0, 0)}'\""
      case _: InstantTimestamp | _: OffsetDateTimeTimestamp =>
        s"\"'${Instant.parse("2000-01-01T00:00:00Z")}'\""
      case _: LocalDateTimeTimestamp                        =>
        s"\"'${LocalDateTime.parse("2000-01-01T00:00:00")}'\""
      case _: LocalDateDate                                 =>
        s"\"'${LocalDate.parse("2000-01-01")}'\""
      case _: LocalTimeTime                                 =>
        s"\"'${LocalTime.parse("00:00:00")}'\""
      case DataElementType(dataElementId)                   =>
        getEmptyOriginValue(domain.dataElements(dataElementId).dataType, originCell)
      case _                                                =>
        "\"''\""
    }

  private def fieldValueFormula(field: EntityField, rowNum: Int, index: Int): String = {
    val originColumn     = CellReference.convertNumToColString(index)
    val originCell       = s"""$originColumn$rowNum"""
    val originValue      = getOriginValue(field.dataType, originCell)
    val emptyOriginValue = getEmptyOriginValue(field.dataType, originCell)

    s"""IF($originCell="<empty>",$emptyOriginValue,IF($originCell="","NULL", $originValue))"""
  }

}
