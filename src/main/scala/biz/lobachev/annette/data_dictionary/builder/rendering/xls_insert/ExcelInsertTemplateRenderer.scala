package biz.lobachev.annette.data_dictionary.builder.rendering.xls_insert

import biz.lobachev.annette.data_dictionary.builder.POSTGRESQL
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, Renderer}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import java.io.FileOutputStream

case class ExcelInsertTemplateRenderer(domain: Domain) extends Renderer {
  val Q3 = "\"\"\""
  val Q4 = "\"\"\"\""

  override def render(): Seq[RenderResult] = {
    val path = s"docs/${domain.id.snakeCase}/templates"
    new java.io.File(path).mkdirs

    domain.groups.values.foreach { group =>
      val filename = s"$path/${group.id}.xlsx"
      val wb       = new XSSFWorkbook()
      domain.entities.values
        .filter(entity => entity.groupId == group.id)
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
        s""" "'"&TEXT($originCell,"YYYY-MM-DD")&"'" """
      case _: LocalTimeTime                                                                               =>
        s""" "'"&TEXT($originCell,"HH:mm:ss")&"'" """
      case _: LocalDateTimeTimestamp                                                                      =>
        s""" "'"&TEXT($originCell,"YYYY-MM-DD")&" """" +
          s"""&TEXT($originCell,"HH:mm:ss")&"'" """
      case _: InstantTimestamp                                                                            =>
        s""" "'"&TEXT($originCell,"YYYY-MM-DD")&" """" +
          s"""&TEXT($originCell,"HH:mm:ss")&"'" """
      case _: OffsetDateTimeTimestamp                                                                     =>
        s""" "'"&TEXT($originCell,"YYYY-MM-DD")&" """" +
          s"""&TEXT($originCell,"HH:mm:ss")&"'" """
      case DataElementType(dataElementId)                                                                 =>
        getOriginValue(domain.dataElements(dataElementId).dataType, originCell)
      case _                                                                                              =>
        originCell
    }

  def renderEntity(entity: Entity, wb: XSSFWorkbook) = {
    val sheet           = wb.createSheet(entity.id)
    val headerRow       = sheet.createRow(0)
    headerRow.createCell(0).setCellValue(entity.schema.getOrElse(""))
    headerRow.createCell(1).setCellValue(entity.tableName)
    headerRow.createCell(2).setCellValue(entity.name)
    val descriptionRow  = sheet.createRow(1)
    val nameRow         = sheet.createRow(2)
    val typeRow         = sheet.createRow(3)
    val formulaRow      = sheet.createRow(4)
    val fields          = domain.rolloutEntityFields(entity)
    val N               = fields.length
    fields.zipWithIndex.foreach { case field -> index =>
      val typeSeq         = Seq(
        Some(domain.getTargetDataType(field.dataType, POSTGRESQL)),
        if (entity.pk.contains(field.fieldName)) Some("PK") else None,
        if (field.notNull) Some("NN") else None,
        if (field.autoIncrement) Some("INC") else None,
      ).flatten
      val typeStr         = if (typeSeq.nonEmpty) typeSeq.mkString(", ") else ""
      descriptionRow.createCell(index).setCellValue(field.description)
      nameRow.createCell(index).setCellValue(field.dbFieldName)
      typeRow.createCell(index).setCellValue(typeStr)
      val originColumn    = CellReference.convertNumToColString(index)
//        val fieldColumn      = CellReference.convertNumToColString(N + index + 1)
      val prevFieldColumn = CellReference.convertNumToColString(N + index)
      val fieldFormula    =
        if (index == 0)
          s"""IF(A5<>"",$Q4&A$$3&$Q4,"")"""
        else
          s"""  ${prevFieldColumn}5&IF(AND(${prevFieldColumn}5<>"",${originColumn}5<>""),", ","")&IF(${originColumn}5<>"",$Q4&${originColumn}$$3&$Q4,"") """
      formulaRow.createCell(N + index + 1).setCellFormula(fieldFormula)
//        val valColumn       = CellReference.convertNumToColString(N + N + index + 1)
      val prevValColumn   = CellReference.convertNumToColString(N + N + index)
      val originCell      = s"""${originColumn}5"""
      val originValue     = getOriginValue(field.dataType, originCell)
      val valFormula      =
        if (index == 0)
          s"""IF(A5<>"",${originValue},"")"""
        else
          s"""  ${prevValColumn}5&IF(AND(${prevValColumn}5<>"",${originColumn}5<>""),", ","")&IF(${originColumn}5<>"",${originValue},"") """
      formulaRow.createCell(N + N + index + 1).setCellFormula(valFormula)
    }
    descriptionRow.createCell(fields.length).setCellValue("Insert Statement")
    val lastFieldColumn = CellReference.convertNumToColString(N + N)
    val lastValColumn   = CellReference.convertNumToColString(N + N + N)
    val table           = entity.schema.map(_ => s"""$Q3&A$$1&$Q3.$Q3&B$$1&$Q3""").getOrElse(s"$Q3&B$$1&$Q3")
    formulaRow
      .createCell(fields.length)
      .setCellFormula(s""""INSERT INTO $table ("&${lastFieldColumn}5&") VALUES ("&${lastValColumn}5&");" """)
  }

}
