package biz.lobachev.annette.data_dictionary.builder.rendering.xls_insert

import biz.lobachev.annette.data_dictionary.builder.POSTGRESQL
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{BinaryRenderer, RenderResult}
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}

import java.io.FileOutputStream

case class ExcelInsertTemplateRenderer(
  domain: Domain,
) extends BinaryRenderer {
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

  private def getOriginValue(dataType: DataType, originCell: String): String =
    dataType match {
      case _: IntInt | _: LongLong | _: ShortSmallint | _: BigDecimalNumeric | _: BigIntegerNumeric | _: DoubleDouble |
          _: FloatFloat | _: BooleanBoolean =>
        originCell
      case DataElementType(dataElementId) =>
        getOriginValue(domain.dataElements(dataElementId).dataType, originCell)
      case _                              =>
        s""" "'"&$originCell&"'" """
    }

  def renderEntity(entity: Entity, wb: XSSFWorkbook) = {
    val sheet         = wb.createSheet(entity.id)
    val valueRowIndex = renderHeader(entity, sheet)
    renderValueRow(entity, sheet, valueRowIndex)
  }

  private def renderHeader(entity: Entity, sheet: XSSFSheet): Int = {
    val headerRow      = sheet.createRow(0)
    headerRow.createCell(0).setCellValue(entity.schema.getOrElse(""))
    headerRow.createCell(1).setCellValue(entity.tableName)
    headerRow.createCell(2).setCellValue(entity.name)
    val descriptionRow = sheet.createRow(1)
    val nameRow        = sheet.createRow(2)
    val fieldRow       = sheet.createRow(3)
    val typeRow        = sheet.createRow(4)
    val fields         = entity.expandedFields
    val N              = fields.length
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
      typeRow.createCell(N + index + 1).setCellValue(s"F ${field.dbFieldName}")
      typeRow.createCell(N + N + index + 1).setCellValue(s"V ${field.dbFieldName}")
    }
    typeRow.createCell(fields.length).setCellValue("Insert Statement")
    5
  }

  private def renderValueRow(entity: Entity, sheet: XSSFSheet, rowIndex: Int) = {
    val formulaRow = sheet.createRow(rowIndex)
    val fields     = entity.expandedFields
    val N          = fields.length
    val rowNum     = rowIndex + 1
    fields.zipWithIndex.foreach { case field -> index =>
      val originColumn    = CellReference.convertNumToColString(index)
      val prevFieldColumn = CellReference.convertNumToColString(N + index)
      val fieldFormula    =
        if (index == 0)
          s"""IF(A${rowNum}<>"",$Q4&A$$4&$Q4,"")"""
        else
          s"""  ${prevFieldColumn}${rowNum}&IF(AND(${prevFieldColumn}${rowNum}<>"",${originColumn}${rowNum}<>""),", ","")&IF(${originColumn}${rowNum}<>"",$Q4&${originColumn}$$4&$Q4,"") """
      formulaRow.createCell(N + index + 1).setCellFormula(fieldFormula)
      val prevValColumn   = CellReference.convertNumToColString(N + N + index)
      val originCell      = s"""${originColumn}${rowNum}"""
      val originValue     = getOriginValue(field.dataType, originCell)
      val valFormula      =
        if (index == 0)
          s"""IF(A${rowNum}<>"",${originValue},"")"""
        else
          s"""  ${prevValColumn}${rowNum}&IF(AND(${prevValColumn}${rowNum}<>"",${originColumn}${rowNum}<>""),", ","")&IF(${originColumn}${rowNum}<>"",${originValue},"") """
      formulaRow.createCell(N + N + index + 1).setCellFormula(valFormula)
    }

    val lastFieldColumn = CellReference.convertNumToColString(N + N)
    val lastValColumn   = CellReference.convertNumToColString(N + N + N)
    val table           = entity.schema.map(_ => s"""$Q3&A$$1&$Q3.$Q3&B$$1&$Q3""").getOrElse(s"$Q3&B$$1&$Q3")
    formulaRow
      .createCell(fields.length)
      .setCellFormula(
        s""""INSERT INTO $table ("&${lastFieldColumn}${rowNum}&") VALUES ("&${lastValColumn}${rowNum}&");" """,
      )
  }

}
