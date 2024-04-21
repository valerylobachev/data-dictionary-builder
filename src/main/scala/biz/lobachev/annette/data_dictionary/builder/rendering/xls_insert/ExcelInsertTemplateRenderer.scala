package biz.lobachev.annette.data_dictionary.builder.rendering.xls_insert

import biz.lobachev.annette.data_dictionary.builder.POSTGRESQL
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{BinaryRenderer, RenderResult}
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.io.FileOutputStream

case class ExcelInsertTemplateRenderer(
  domain: Domain,
  translation: ExcelInsertTemplateTranslation = ExcelInsertTemplateTranslation.EN,
) extends BinaryRenderer {
  val Q3 = "\"\"\""
  val Q4 = "\"\"\"\""

  val dateFormat = translation.dateFormat
  val timeFormat = translation.timeFormat

  override def render(path: String): Seq[RenderResult] = {
    val fullPath = s"$path/template_${translation.language}"
    new java.io.File(fullPath).mkdirs

    domain.components.values.foreach { group =>
      val filename = s"$fullPath/${group.id}.xlsx"
      val wb       = new XSSFWorkbook()
      domain.entities.values
        .filter(entity => entity.componentId == group.id)
        .toSeq
        .foreach(entity => renderEntity(entity, wb))
      val fileOut  = new FileOutputStream(filename)
      try wb.write(fileOut)
      finally if (fileOut != null) fileOut.close()
    }
    Seq.empty
  }

  def getOriginValue(dataType: DataType, originCell: String): String =
    dataType match {
      case _: StringText | _: StringVarchar | _: StringChar | _: StringChar | _: EnumString | _: UuidUuid =>
        s""" "'"&$originCell&"'" """
      case _: LocalDateDate                                                                               =>
        s""" "'"&TEXT($originCell,"$dateFormat")&"'" """
      case _: LocalTimeTime                                                                               =>
        s""" "'"&TEXT($originCell,"$timeFormat")&"'" """
      case _: LocalDateTimeTimestamp                                                                      =>
        s""" "'"&TEXT($originCell,"$dateFormat")&" """" +
          s"""&TEXT($originCell,"$timeFormat")&"'" """
      case _: InstantTimestamp                                                                            =>
        s""" "'"&TEXT($originCell,"$dateFormat")&" """" +
          s"""&TEXT($originCell,"$timeFormat")&"'" """
      case _: OffsetDateTimeTimestamp                                                                     =>
        s""" "'"&TEXT($originCell,"$dateFormat")&" """" +
          s"""&TEXT($originCell,"$timeFormat")&"'" """
      case DataElementType(dataElementId)                                                                 =>
        getOriginValue(domain.dataElements(dataElementId).dataType, originCell)
      case _                                                                                              =>
        originCell
    }

  def renderEntity(entity: Entity, wb: XSSFWorkbook) = {
    val sheet          = wb.createSheet(entity.id)
    val headerRow      = sheet.createRow(0)
    headerRow.createCell(0).setCellValue(entity.schema.getOrElse(""))
    headerRow.createCell(1).setCellValue(entity.tableName)
    headerRow.createCell(2).setCellValue(entity.name)
    val descriptionRow = sheet.createRow(1)
    val nameRow        = sheet.createRow(2)
    val fieldRow       = sheet.createRow(3)
    val typeRow        = sheet.createRow(4)
    val formulaRow     = sheet.createRow(5)
    val fields         = entity.expandedFields
    val N              = fields.length
    fields.zipWithIndex.foreach { case field -> index =>
      val typeSeq         = Seq(
        Some(domain.getTargetDataType(field.dataType, POSTGRESQL)),
        if (entity.pk.contains(field.fieldName)) Some("PK") else None,
        if (field.notNull) Some("NN") else None,
        if (field.autoIncrement) Some("INC") else None,
      ).flatten
      val typeStr         = if (typeSeq.nonEmpty) typeSeq.mkString(", ") else ""
      descriptionRow.createCell(index).setCellValue(field.description)
      nameRow.createCell(index).setCellValue(field.name)
      fieldRow.createCell(index).setCellValue(field.dbFieldName)
      typeRow.createCell(index).setCellValue(typeStr)
      val originColumn    = CellReference.convertNumToColString(index)
      val prevFieldColumn = CellReference.convertNumToColString(N + index)
      val fieldFormula    =
        if (index == 0)
          s"""IF(A6<>"",$Q4&A$$4&$Q4,"")"""
        else
          s"""  ${prevFieldColumn}6&IF(AND(${prevFieldColumn}6<>"",${originColumn}6<>""),", ","")&IF(${originColumn}6<>"",$Q4&${originColumn}$$4&$Q4,"") """
      formulaRow.createCell(N + index + 1).setCellFormula(fieldFormula)
      typeRow.createCell(N + index + 1).setCellValue(s"F ${field.dbFieldName}")
      val prevValColumn   = CellReference.convertNumToColString(N + N + index)
      val originCell      = s"""${originColumn}6"""
      val originValue     = getOriginValue(field.dataType, originCell)
      val valFormula      =
        if (index == 0)
          s"""IF(A6<>"",${originValue},"")"""
        else
          s"""  ${prevValColumn}6&IF(AND(${prevValColumn}6<>"",${originColumn}6<>""),", ","")&IF(${originColumn}6<>"",${originValue},"") """
      formulaRow.createCell(N + N + index + 1).setCellFormula(valFormula)
      typeRow.createCell(N + N + index + 1).setCellValue(s"V ${field.dbFieldName}")

    }
    typeRow.createCell(fields.length).setCellValue("Insert Statement")
    val lastFieldColumn = CellReference.convertNumToColString(N + N)
    val lastValColumn   = CellReference.convertNumToColString(N + N + N)
    val table           = entity.schema.map(_ => s"""$Q3&A$$1&$Q3.$Q3&B$$1&$Q3""").getOrElse(s"$Q3&B$$1&$Q3")
    formulaRow
      .createCell(fields.length)
      .setCellFormula(s""""INSERT INTO $table ("&${lastFieldColumn}6&") VALUES ("&${lastValColumn}6&");" """)
  }

}
