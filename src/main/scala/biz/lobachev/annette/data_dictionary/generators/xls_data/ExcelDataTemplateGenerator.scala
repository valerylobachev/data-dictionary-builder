package biz.lobachev.annette.data_dictionary.generators.xls_data

import biz.lobachev.annette.data_dictionary.generators.{BinaryGenerator, GeneratedFile}
import biz.lobachev.annette.data_dictionary.model.{BooleanDatatype, *}
import org.apache.poi.ss.util.{CellRangeAddress, CellReference}
import org.apache.poi.xssf.usermodel.{XSSFRow, XSSFSheet, XSSFWorkbook}

import java.io.FileOutputStream
import java.time.{Instant, LocalDate, LocalDateTime, LocalTime}
import java.util.UUID

case class ExcelDataTemplateGenerator(
  domain: Domain,
) extends BinaryGenerator
    with DomainMetaHelper {
  val Q2 = "\"\""
  val Q3 = "\"\"\""
  val Q4 = "\"\"\"\""

  override def generate(path: String): Seq[GeneratedFile] = {
    val fullPath = s"$path"
    new java.io.File(fullPath).mkdirs

    generateTables(path, domain.id, None).toSeq ++
      domain.components.values.map(comp => generateTables(path, comp.id, Some(comp.id))).flatten
  }

  def generateTables(path: String, filename: String, comp: Option[String]): Option[GeneratedFile] = {
    val filePath  = s"$path/$filename.xlsx"
    val wb        = new XSSFWorkbook()
    val generated = domain.tables.values
      .filter(table => table.componentId == comp)
      .toSeq
      .map(table => generateTable(table, wb))
    if generated.nonEmpty then {
      val fileOut = new FileOutputStream(filePath)
      try wb.write(fileOut)
      finally if (fileOut != null) fileOut.close()
      Some(
        GeneratedFile(
          path = path,
          filename = s"$filename.xlsx",
          content = "",
        ),
      )
    } else {
      None
    }
  }

  def generateTable(table: Table, wb: XSSFWorkbook) = {
    val sheet                    = wb.createSheet(table.id)
    val (valueRowIndex, typeRow) = generateHeader(table, sheet)
    generateValueRow(table, sheet, valueRowIndex, typeRow)
    formatSheet(sheet)
  }

  private def generateHeader(table: Table, sheet: XSSFSheet): (Int, XSSFRow) = {
    val headerRow      = sheet.createRow(0)
    val fullTableName  = getFullTableName(table, domain)
    headerRow.createCell(0).setCellValue(fullTableName.schema.getOrElse(""))
    headerRow.createCell(1).setCellValue(table.tableName)
    headerRow.createCell(2).setCellValue(table.name)
    val descriptionRow = sheet.createRow(1)
    val nameRow        = sheet.createRow(2)
    val fieldRow       = sheet.createRow(3)
    val typeRow        = sheet.createRow(4)
    val fields         = table.fields
    fields.zipWithIndex.foreach { case field -> index =>
      val typeSeq = Seq(
        Some(getPostgresDatatype(field.datatype, domain).documentationDatatype()),
        if (table.pk.contains(field.fieldName)) Some("PK") else None,
        if (field.notNull) Some("NN") else None,
        if (
          field.generated.isInstanceOf[GeneratedAlwaysAsIdentity.type] ||
          field.generated.isInstanceOf[GeneratedByDefaultAsIdentity.type]
        ) Some("INC")
        else None,
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

  private def generateValueRow(table: Table, sheet: XSSFSheet, rowIndex: Int, typeRow: XSSFRow) = {
    val formulaRow    = sheet.createRow(rowIndex)
    val N             = table.fields.length
    val rowNum        = rowIndex + 1
    val fullTableName = getFullTableName(table, domain)
    val tableFormula  = fullTableName.schema.map(_ => s"""$Q3&A$$1&$Q3.$Q3&B$$1&$Q3""").getOrElse(s"$Q3&B$$1&$Q3")

    val fields        = table.fields.zipWithIndex.map { case field -> index =>
      val column      = CellReference.convertNumToColString(index)
      val fieldCell   = s"$$${column}$$4"
      val valueColumn = CellReference.convertNumToColString(N + 4 + index)
      val valueCell   = s"${valueColumn}6"
      typeRow.createCell(N + 4 + index).setCellFormula(fieldCell)
      formulaRow.createCell(N + 4 + index).setCellFormula(fieldValueFormula(field, rowNum, index))
      (s"$Q4&$fieldCell&$Q4", valueCell, table.pk.contains(field.fieldName))
    }
    val fieldCells    = fields.map(_._1)
    val valueCells    = fields.map(_._2)
    val fieldsFormula = fieldCells.mkString("&\", \"&")
    val valuesFormula = valueCells.mkString("&\", \"&")

    val fieldFormula = s""""INSERT INTO $tableFormula ("&$fieldsFormula&") VALUES ("&$valuesFormula&");" """
    formulaRow.createCell(N).setCellFormula(fieldFormula)
//    println(s"fieldsFormula: $fieldsFormula")
//    println(s"valuesFormula: $valuesFormula")
//    println(s"fieldFormula: $fieldFormula")
//    println()

    val insertFormula2 = s""""INSERT INTO $tableFormula ("&$fieldsFormula&") VALUES""""
    typeRow.createCell(N + 1).setCellFormula(insertFormula2)
    val nextCell       = s"${CellReference.convertNumToColString(N + 1)}${rowNum + 1}"
    val fieldFormula2  = s" \"(\"&$valuesFormula&\")\"&if($nextCell=\"\",\";\",\",\")"
    formulaRow.createCell(N + 1).setCellFormula(fieldFormula2)

    val pk = table.fields
      .filter(f => table.pk.contains(f.fieldName))
      .map(f => s"$Q2${f.dbFieldName}$Q2")
      .mkString(", \"&\"")

    val fieldFormula3 =
      s""""INSERT INTO $tableFormula ("&$fieldsFormula&") VALUES ("&$valuesFormula&") ON CONFLICT ($pk) DO NOTHING;" """
    formulaRow.createCell(N + 2).setCellFormula(fieldFormula3)

    val valuesFormula4 =
      fields.filter(!_._3).map(f => s"${f._1}&\"=EXCLUDED.\"&${f._1}").mkString("&\", \"&")
    val fieldFormula4  = if (valuesFormula4.length > 0) {
      s""""INSERT INTO $tableFormula ("&$fieldsFormula&") VALUES ("&$valuesFormula&") ON CONFLICT ($pk) DO UPDATE SET \"&$valuesFormula4&\" ;" """
    } else {
      // all fields in PK
      fieldFormula3
    }
    formulaRow.createCell(N + 3).setCellFormula(fieldFormula4)

  }

  private def getOriginValue(datatype: Datatype, originCell: String): String =
    datatype match {
      case GoIntDatatype | SmallintDatatype | IntegerDatatype | LongDatatype | SmallSerialDatatype | SerialDatatype |
          BigSerialDatatype | Int8Datatype | UIntDatatype | UInt8Datatype | UInt16Datatype | UInt32Datatype |
          UInt64Datatype | _: DecimalDatatype | FloatDatatype | DoubleDatatype | BooleanDatatype =>
        originCell
      case DataElementDatatype(dataElementId) =>
        getOriginValue(domain.dataElements(dataElementId).datatype, originCell)
      case _                                  =>
        s"\"'\"&SUBSTITUTE($originCell,\"'\",\"''\")&\"'\""
    }

  private def getEmptyOriginValue(dataType: Datatype, originCell: String): String =
    dataType match {
      case GoIntDatatype | SmallintDatatype | IntegerDatatype | LongDatatype | SmallSerialDatatype | SerialDatatype |
          BigSerialDatatype | Int8Datatype | UIntDatatype | UInt8Datatype | UInt16Datatype | UInt32Datatype |
          UInt64Datatype | _: DecimalDatatype | FloatDatatype | DoubleDatatype =>
        "\"0\""
      case BooleanDatatype                          =>
        "\"false'\""
      case UuidDatatype                             =>
        s"\"'${new UUID(0, 0)}'\""
      case InstantDatatype | OffsetDateTimeDatatype =>
        s"\"'${Instant.parse("2000-01-01T00:00:00Z")}'\""
      case DateTimeDatatype                         =>
        s"\"'${LocalDateTime.parse("2000-01-01T00:00:00")}'\""
      case DateDatatype                             =>
        s"\"'${LocalDate.parse("2000-01-01")}'\""
      case TimeDatatype                             =>
        s"\"'${LocalTime.parse("00:00:00")}'\""
      case DataElementDatatype(dataElementId)       =>
        getEmptyOriginValue(domain.dataElements(dataElementId).datatype, originCell)
      case _                                        =>
        "\"''\""
    }

  private def fieldValueFormula(field: TableField, rowNum: Int, index: Int): String = {
    val originColumn     = CellReference.convertNumToColString(index)
    val originCell       = s"""$originColumn$rowNum"""
    val originValue      = getOriginValue(field.datatype, originCell)
    val emptyOriginValue = getEmptyOriginValue(field.datatype, originCell)

    s"""IF($originCell="<empty>",$emptyOriginValue,IF($originCell="","NULL", $originValue))"""
  }

  private def formatSheet(sheet: XSSFSheet): Unit = {
    sheet.createFreezePane(0, 5)
    val headerRow = sheet.getRow(5)
    val lastCol   = headerRow.getLastCellNum() - 1
    (0 to lastCol).foreach(c => sheet.autoSizeColumn(c))
    sheet.setAutoFilter(new CellRangeAddress(4, 4, 0, lastCol)): Unit
  }

}
