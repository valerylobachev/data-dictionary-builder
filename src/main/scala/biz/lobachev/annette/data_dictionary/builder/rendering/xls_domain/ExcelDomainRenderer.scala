package biz.lobachev.annette.data_dictionary.builder.rendering.xls_domain

import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{JAVA_MODEL_PACKAGE, JAVA_REPO_PACKAGE}
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, Renderer}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.{RemoveBR, SnakeCase}
import org.apache.poi.ss.usermodel.{HorizontalAlignment, VerticalAlignment}
import org.apache.poi.xssf.usermodel._

import java.io.FileOutputStream

case class ExcelDomainRenderer(domain: Domain, translation: WorkbookTranslation) extends Renderer {

  override def render(): Seq[RenderResult] = {
    val dwb = initWorkbook(translation)

    renderDomain(dwb)

    val path     = s"docs/${domain.id.snakeCase}"
    new java.io.File(path).mkdirs
    val filename = s"$path/${domain.name}.xlsx"
    val fileOut  = new FileOutputStream(filename)
    try dwb.wb.write(fileOut)
    finally if (fileOut != null) fileOut.close()
    Seq.empty
  }

  private def renderDomain(dwb: DomainWorkbook) = {
    val row = dwb.domain.sheet.createRow(1)
    row.createCell(0).setCellValue(domain.id)
    row.createCell(1).setCellValue(domain.name)
    row.createCell(2).setCellValue(domain.description)
    domain.groups.values.foreach(group => renderGroup(dwb, group))
    domain.dataElements.values.foreach(de => renderDataElement(dwb, de))
    domain.enums.values.foreach(e => renderEnum(dwb, e))
  }

  private var dataElementRowIndex = 0

  def renderDataElement(dwb: DomainWorkbook, dataElement: DataElement) = {
    dataElementRowIndex += 1
    val row = dwb.dataElements.sheet.createRow(dataElementRowIndex)

    row.createCell(0).setCellValue(dataElement.id)
    row.createCell(1).setCellValue(dataElement.fieldName)
    row.createCell(2).setCellValue(dataElement.dbFieldName)
    row.createCell(3).setCellValue(dataElement.name)
    val (datatype, javaDatatype, postgresDatatype, len, scale, default) = getFieldDatatype(dataElement.dataType)
    row.createCell(4).setCellValue(datatype)
    row.createCell(5).setCellValue(javaDatatype)
    row.createCell(6).setCellValue(postgresDatatype)
    row.createCell(7).setCellValue(len)
    row.createCell(8).setCellValue(scale)
    dataElement.dataType match {
      case EnumString(enumId, _) =>
        row.createCell(9).setCellValue(enumId)
      case _                     =>
    }
    row.createCell(10).setCellValue(default)
    row.createCell(11).setCellValue(dataElement.description)
  }

  private var enumRowIndex        = 0
  private var enumElementRowIndex = 0

  private def renderEnum(dwb: DomainWorkbook, e: EnumData) = {
    enumRowIndex += 1
    val row = dwb.enums.sheet.createRow(enumRowIndex)
    row.createCell(0).setCellValue(e.id)
    row.createCell(1).setCellValue(e.name)
    row.createCell(2).setCellValue(e.length.toString)
    row.createCell(3).setCellValue(e.description)
    e.elements.map { case key -> value =>
      enumElementRowIndex += 1
      val row = dwb.enumItems.sheet.createRow(enumElementRowIndex)
      row.createCell(0).setCellValue(e.id)
      row.createCell(1).setCellFormula(s"VLOOKUP(A${enumElementRowIndex + 1},'${dwb.enums.name}'!A:B,2,0)")
      row.createCell(2).setCellValue(key)
      row.createCell(3).setCellValue(value)
    }
  }

  private var groupRowIndex = 0

  private def renderGroup(dwb: DomainWorkbook, group: Group) = {
    groupRowIndex += 1
    val row = dwb.groups.sheet.createRow(groupRowIndex)
    row.createCell(0).setCellValue(group.id)
    row.createCell(1).setCellValue(group.name)
    row.createCell(2).setCellValue(group.schema.getOrElse(""))
    row.createCell(3).setCellValue(group.description)
    row
      .createCell(4)
      .setCellValue(
        group.attributes.getOrElse(JAVA_REPO_PACKAGE, ""),
      ) // Attributes.findGroupAttribute(group, domain, JAVA_REPO_PACKAGE).getOrElse(""))
    row
      .createCell(5)
      .setCellValue(
        group.attributes.getOrElse(JAVA_MODEL_PACKAGE, ""),
      ) // Attributes.findGroupAttribute(group, domain, JAVA_MODEL_PACKAGE).getOrElse(""))
    domain.entities.values.filter(_.groupId == group.id).foreach(entity => renderEntity(dwb, group, entity))
  }

  private var entityRowIndex = 0

  private def renderEntity(dwb: DomainWorkbook, group: Group, entity: Entity) = {
    entityRowIndex += 1
    val row        = dwb.entities.sheet.createRow(entityRowIndex)
    row.createCell(0).setCellValue(group.id)
    row.createCell(1).setCellFormula(s"VLOOKUP(A${entityRowIndex + 1},'${dwb.groups.name}'!A:B,2,0)")
    row.createCell(2).setCellValue(entity.id)
    row.createCell(3).setCellValue(entity.name)
    row.createCell(4).setCellValue(entity.entityName)
    row.createCell(5).setCellValue(entity.fullTableName())
    val entityType = entity.entityType.toString
    row.createCell(6).setCellValue(entityType.take(entityType.length - 6))
    row.createCell(7).setCellValue(entity.description)
    entity.fields.foreach(field => renderEntityField(dwb, entity, field))
    entity.indexes.values.foreach(index => renderEntityIndex(dwb, entity, index))
    entity.relations.foreach(relation => renderEntityRelation(dwb, entity, relation))
  }

  private var entityFieldRowIndex = 0

  private def renderEntityField(dwb: DomainWorkbook, entity: Entity, field: EntityField) = {
    entityFieldRowIndex += 1
    val row = dwb.fields.sheet.createRow(entityFieldRowIndex)

    row.createCell(0).setCellFormula(s"""B${entityFieldRowIndex + 1}&"."&E${entityFieldRowIndex + 1}""")
    row.createCell(1).setCellValue(entity.id)
    row.createCell(2).setCellFormula(s"VLOOKUP(B${entityFieldRowIndex + 1},'${dwb.entities.name}'!C:D,2,0)")
    field.dataType match {
      case EmbeddedEntityType(entityId, withPrefix, withRelations) =>
        val p = if (withPrefix) "P" else ""
        val r = if (withRelations) "R" else ""
        row.createCell(3).setCellValue(s"Include$p$r")
        row.createCell(13).setCellValue(entityId)
      case EnumString(enumId, _)                                   =>
        row.createCell(3).setCellValue("Field")
        row.createCell(15).setCellValue(enumId)
      case DataElementType(dataElementId)                          =>
        row.createCell(3).setCellValue("Field")
        row.createCell(12).setCellValue(dataElementId)
      case _                                                       =>
        row.createCell(3).setCellValue("Field")
    }
    row.createCell(4).setCellValue(field.fieldName)
    row.createCell(5).setCellValue(field.dbFieldName)
    row.createCell(6).setCellValue(field.name)
    val (datatype, javaDatatype, postgresDatatype, len, scale, default) = getFieldDatatype(field.dataType)
    row.createCell(7).setCellValue(datatype)
    row.createCell(8).setCellValue(javaDatatype)
    row.createCell(9).setCellValue(postgresDatatype)
    row.createCell(10).setCellValue(len)
    row.createCell(11).setCellValue(scale)
    row.createCell(17).setCellValue(default)
    if (entity.pk.contains(field.fieldName)) {
      if (field.autoIncrement) row.createCell(15).setCellValue("S")
      else row.createCell(15).setCellValue("X")
    }
    if (field.notNull) row.createCell(16).setCellValue("X")
    row.createCell(18).setCellValue(field.description.replaceBR)
  }

  private var entityIndexIndex      = 0
  private var entityIndexFieldIndex = 0

  private def renderEntityIndex(dwb: DomainWorkbook, entity: Entity, index: EntityIndex) = {
    entityIndexIndex += 1
    val row = dwb.indexes.sheet.createRow(entityIndexIndex)
    row.createCell(0).setCellFormula(s"""B${entityIndexIndex + 1}&"_"&D${entityIndexIndex + 1}""")
    row.createCell(1).setCellValue(entity.id)
    row.createCell(2).setCellFormula(s"VLOOKUP(B${entityIndexIndex + 1},'${dwb.entities.name}'!C:D,2,0)")
    row.createCell(3).setCellValue(index.id)
    row.createCell(4).setCellValue(index.name)
    if (index.unique) row.createCell(5).setCellValue("X")
    row.createCell(6).setCellValue(index.description.replaceBR)
    index.fields.foreach { field =>
      entityIndexFieldIndex += 1
      val row = dwb.indexFields.sheet.createRow(entityIndexFieldIndex)
      row.createCell(0).setCellValue(s"${entity.id}_${index.id}")
      row.createCell(1).setCellFormula(s"VLOOKUP(A${entityIndexFieldIndex + 1},'${dwb.indexes.name}'!A:E,2,0)")
      row.createCell(2).setCellFormula(s"VLOOKUP(A${entityIndexFieldIndex + 1},'${dwb.indexes.name}'!A:E,3,0)")
      row.createCell(3).setCellFormula(s"VLOOKUP(A${entityIndexFieldIndex + 1},'${dwb.indexes.name}'!A:E,4,0)")
      row.createCell(4).setCellFormula(s"VLOOKUP(A${entityIndexFieldIndex + 1},'${dwb.indexes.name}'!A:E,5,0)")
      row.createCell(5).setCellValue(field)
      row
        .createCell(6)
        .setCellFormula(
          s"""VLOOKUP(B${entityIndexFieldIndex + 1}&"."&F${entityIndexFieldIndex + 1},'${dwb.fields.name}'!A:G,7,0)""",
        )
    }
  }

  private var entityRelationIndex      = 0
  private var entityRelationFieldIndex = 0

  private def renderEntityRelation(dwb: DomainWorkbook, entity: Entity, relation: EntityRelation) = {
    entityRelationIndex += 1
    val row = dwb.relations.sheet.createRow(entityRelationIndex)
    row.createCell(0).setCellFormula(s"""B${entityRelationIndex + 1}&"_"&D${entityRelationIndex + 1}""")
    row.createCell(1).setCellValue(entity.id)
    row.createCell(2).setCellFormula(s"VLOOKUP(B${entityRelationIndex + 1},'${dwb.entities.name}'!C:D,2,0)")

    row.createCell(3).setCellValue(relation.id)
    row.createCell(4).setCellValue(relation.name)

    row.createCell(5).setCellValue(relation.relationType.toString)
    row.createCell(6).setCellValue(relation.referenceEntityId)
    row.createCell(7).setCellFormula(s"VLOOKUP(G${entityRelationIndex + 1},'${dwb.entities.name}'!C:D,2,0)")
    row.createCell(8).setCellValue(relation.onUpdate.toString)
    row.createCell(9).setCellValue(relation.onDelete.toString)
    row.createCell(10).setCellValue(relation.description.replaceBR)
    relation.fields.foreach { field =>
      entityRelationFieldIndex += 1
      val row = dwb.relationFields.sheet.createRow(entityRelationFieldIndex)
      row.createCell(0).setCellValue(s"${entity.id}_${relation.id}")
      row.createCell(1).setCellFormula(s"VLOOKUP(A${entityRelationFieldIndex + 1},'${dwb.relations.name}'!A:E,2,0)")
      row.createCell(2).setCellFormula(s"VLOOKUP(A${entityRelationFieldIndex + 1},'${dwb.relations.name}'!A:E,3,0)")
      row.createCell(3).setCellValue(relation.referenceEntityId)
      row.createCell(4).setCellFormula(s"VLOOKUP(D${entityRelationFieldIndex + 1},'${dwb.entities.name}'!C:D,2,0)")
      row.createCell(5).setCellFormula(s"VLOOKUP(A${entityRelationFieldIndex + 1},'${dwb.relations.name}'!A:E,4,0)")
      row.createCell(6).setCellFormula(s"VLOOKUP(A${entityRelationFieldIndex + 1},'${dwb.relations.name}'!A:E,5,0)")
      row.createCell(7).setCellValue(field._1)
      row
        .createCell(8)
        .setCellFormula(
          s"""VLOOKUP(B${entityRelationFieldIndex + 1}&"."&H${entityRelationFieldIndex + 1},'${dwb.fields.name}'!A:G,7,0)""",
        )
      row.createCell(9).setCellValue(field._2)
      row
        .createCell(10)
        .setCellFormula(
          s"""VLOOKUP(D${entityRelationFieldIndex + 1}&"."&J${entityRelationFieldIndex + 1},'${dwb.fields.name}'!A:G,7,0)""",
        )
    }
  }

  private def getFieldDatatype(dataType: DataType): (String, String, String, String, String, String) = {
    var datatype                = ""
    var javaDatatype            = ""
    var postgresDatatype        = ""
    var len                     = ""
    var scale                   = ""
    var default: Option[String] = None
    dataType match {
      case StringVarchar(lenght, defaultValue)                =>
        datatype = "StringVarchar"
        javaDatatype = "String"
        postgresDatatype = "varchar"
        len = lenght.toString
        default = defaultValue.map(s => s"\"$s\"")
      case StringChar(lenght, defaultValue)                   =>
        datatype = "StringChar"
        javaDatatype = "String"
        postgresDatatype = "char"
        len = lenght.toString
        default = defaultValue.map(s => s"\"$s\"")
      case StringText(defaultValue)                           =>
        datatype = "StringText"
        javaDatatype = "String"
        postgresDatatype = "text"
        default = defaultValue.map(s => s"\"$s\"")
      case StringJson(defaultValue)                           =>
        datatype = "StringJson"
        javaDatatype = "String"
        postgresDatatype = "json"
        default = defaultValue.map(s => s"\"$s\"")
      case StringJsonB(defaultValue)                          =>
        datatype = "StringJsonB"
        javaDatatype = "String"
        postgresDatatype = "jsonb"
        default = defaultValue.map(s => s"\"$s\"")
      case IntInt(defaultValue)                               =>
        datatype = "IntInt"
        javaDatatype = "Int"
        postgresDatatype = "integer"
        default = defaultValue.map(s => s.toString)
      case LongLong(defaultValue)                             =>
        datatype = "LongLong"
        javaDatatype = "Long"
        postgresDatatype = "bigint"
        default = defaultValue.map(s => s.toString)
      case ShortSmallint(defaultValue)                        =>
        datatype = "ShortSmallint"
        javaDatatype = "Short"
        postgresDatatype = "smallint"
        default = defaultValue.map(s => s.toString)
      case BigDecimalNumeric(precision, scale2, defaultValue) =>
        datatype = "BigDecimalNumeric"
        javaDatatype = "BigDecimal"
        postgresDatatype = "decimal"
        len = precision.toString
        scale = scale2.toString
        default = defaultValue.map(s => s.toString)
      case BigIntegerNumeric(precision, defaultValue)         =>
        datatype = "BigIntegerNumeric"
        javaDatatype = "BigInteger"
        postgresDatatype = "decimal"
        len = precision.toString
        scale = "0"
        default = defaultValue.map(s => s.toString)
      case DoubleDouble(defaultValue)                         =>
        datatype = "DoubleDouble"
        javaDatatype = "Double"
        postgresDatatype = "float8"
        default = defaultValue.map(s => s.toString)
      case FloatFloat(defaultValue)                           =>
        datatype = "FloatFloat"
        javaDatatype = "Float"
        postgresDatatype = "float4"
        default = defaultValue.map(s => s.toString)
      case BooleanBoolean(defaultValue)                       =>
        datatype = "BooleanBoolean"
        javaDatatype = "Boolean"
        postgresDatatype = "boolean"
        default = defaultValue.map(s => s.toString)
      case UuidUuid(defaultValue)                             =>
        datatype = "UuidUuid"
        javaDatatype = "UUID"
        postgresDatatype = "uuid"
        default = defaultValue.map(s => s"\"${s.toString}\"")
      case InstantTimestamp(defaultValue)                     =>
        datatype = "InstantTimestamp"
        javaDatatype = "Instant"
        postgresDatatype = "timestamptz"
        default = defaultValue.map(s => s"\"${s.toString}\"")
      case OffsetDateTimeTimestamp(defaultValue)              =>
        datatype = "OffsetDateTimeTimestamp"
        javaDatatype = "OffsetDateTime"
        postgresDatatype = "timestamptz"
        default = defaultValue.map(s => s"\"${s.toString}\"")
      case LocalDateTimeTimestamp(defaultValue)               =>
        datatype = "LocalDateTimeTimestamp"
        javaDatatype = "LocalDateTime"
        postgresDatatype = "timestamp"
        default = defaultValue.map(s => s"\"${s.toString}\"")
      case LocalDateDate(defaultValue)                        =>
        datatype = "LocalDateDate"
        javaDatatype = "LocalDate"
        postgresDatatype = "date"
        default = defaultValue.map(s => s"\"${s.toString}\"")
      case LocalTimeTime(defaultValue)                        =>
        datatype = "LocalTimeTime"
        javaDatatype = "LocalTime"
        postgresDatatype = "time"
        default = defaultValue.map(s => s"\"${s.toString}\"")
      case EnumString(enumId, defaultValue)                   =>
        datatype = "EnumString"
        javaDatatype = "String"
        postgresDatatype = "varchar"
        len = domain.enums(enumId).length.toString
        default = defaultValue.map(s => s"\"$s\"")
      case EmbeddedEntityType(_, _, _)                        =>
        datatype = "EmbeddedEntity"
      case ObjectType(_)                                      =>
        datatype = "Object"
      case DataElementType(dataElementId)                     =>
        val (_, jdt, pdt, l, s, d) = getFieldDatatype(domain.dataElements(dataElementId).dataType)
        datatype = "DataElement"
        javaDatatype = jdt
        postgresDatatype = pdt
        len = l
        scale = s
        default = Some(d)
      case ListCollection(_)                                  =>
        datatype = "List"
      case SetCollection(_)                                   =>
        datatype = "Set"
      case StringMapCollection(_)                             =>
        datatype = "StringMap"
    }

    (datatype, javaDatatype, postgresDatatype, len, scale, default.getOrElse(""))
  }

  private def initWorkbook(translation: WorkbookTranslation): DomainWorkbook = {
    val wb: XSSFWorkbook      = new XSSFWorkbook()
    val captionFont: XSSFFont = wb.createFont()
    captionFont.setFontHeightInPoints(11.toShort)
    captionFont.setFontName("Calibri")
    captionFont.setBold(true)
    captionFont.setItalic(false)
    DomainWorkbook(
      wb = wb,
      domain = initSheet(wb, translation.domain, captionFont),
      groups = initSheet(wb, translation.groups, captionFont),
      entities = initSheet(wb, translation.entities, captionFont),
      fields = initSheet(wb, translation.fields, captionFont),
      indexes = initSheet(wb, translation.indexes, captionFont),
      indexFields = initSheet(wb, translation.indexFields, captionFont),
      relations = initSheet(wb, translation.relations, captionFont),
      relationFields = initSheet(wb, translation.relationFields, captionFont),
      dataElements = initSheet(wb, translation.dataElements, captionFont),
      enums = initSheet(wb, translation.enums, captionFont),
      enumItems = initSheet(wb, translation.enumItems, captionFont),
    )
  }

  private def initSheet(wb: XSSFWorkbook, translation: SheetTranslation, font: XSSFFont): Sheet = {
    val sheet = wb.createSheet(translation.name)
    val row   = sheet.createRow(0)
    val style = wb.createCellStyle()
    style.setAlignment(HorizontalAlignment.CENTER)
    style.setVerticalAlignment(VerticalAlignment.CENTER)
    style.setFont(font)
    translation.fields.zipWithIndex.foreach { case caption -> index =>
      val cell = row.createCell(index)
      cell.setCellValue(caption)
      cell.setCellStyle(style)
    }
    Sheet(name = translation.name, sheet = sheet)
  }

}
