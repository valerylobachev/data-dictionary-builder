package biz.lobachev.annette.data_dictionary.builder.rendering.xls_domain

import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{BinaryRenderer, RenderResult}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.RemoveBR
import org.apache.poi.ss.usermodel.{HorizontalAlignment, VerticalAlignment}
import org.apache.poi.xssf.usermodel._

import java.io.FileOutputStream

case class ExcelDomainRenderer(domain: Domain, translation: WorkbookTranslation) extends BinaryRenderer {
  private val depth = getDepth()
  private val dwb   = initWorkbook(translation)

  override def render(path: String): Seq[RenderResult] = {
    renderDomain()
    new java.io.File(path).mkdirs
    val filename = s"$path/${domain.name}.xlsx"
    val fileOut  = new FileOutputStream(filename)
    try dwb.wb.write(fileOut)
    finally if (fileOut != null) fileOut.close()
    Seq(
      RenderResult(
        s"",
        s"${domain.name}.xlsx",
        "Binary content",
      ),
    )
  }

  private def renderDomain() = {
    val row = dwb.domain.sheet.createRow(1)
    row.createCell(0).setCellValue(domain.id)
    row.createCell(1).setCellValue(domain.name)
    row.createCell(2).setCellValue(domain.description)
    domain.rootComponentIds.foreach(componentId => renderComponent(componentId, Seq.empty))
    domain.dataElements.values.filter(_.componentId.isEmpty).foreach(de => renderDataElement(de, Seq.empty))
    domain.enums.values.filter(_.componentId.isEmpty).foreach(e => renderEnum(e, Seq.empty))
  }

  private var dataElementRowIndex = 0

  private def renderDataElement(dataElement: DataElement, components: Seq[String]) = {
    dataElementRowIndex += 1
    val row = dwb.dataElements.sheet.createRow(dataElementRowIndex)

    var index                                                           = 0
    components.foreach { c =>
      row.createCell(index).setCellValue(c)
      index += 1
    }
    row.createCell(depth ).setCellValue(dataElement.id)
    row.createCell(depth + 1).setCellValue(dataElement.fieldName)
    row.createCell(depth + 2).setCellValue(dataElement.dbFieldName)
    row.createCell(depth + 3).setCellValue(dataElement.name)
    val (datatype, javaDatatype, postgresDatatype, len, scale, default) = getFieldDatatype(dataElement.dataType)
    row.createCell(depth + 4).setCellValue(datatype)
    row.createCell(depth + 5).setCellValue(javaDatatype)
    row.createCell(depth + 6).setCellValue(postgresDatatype)
    row.createCell(depth + 7).setCellValue(len)
    row.createCell(depth + 8).setCellValue(scale)
    dataElement.dataType match {
      case EnumString(enumId, _) =>
        row.createCell(depth + 9).setCellValue(enumId)
      case _                     =>
    }
    row.createCell(depth + 10).setCellValue(default)
    row.createCell(depth + 11).setCellValue(dataElement.description)
  }

  private var enumRowIndex        = 0
  private var enumElementRowIndex = 0

  private def renderEnum(e: EnumData, components: Seq[String]) = {
    println(components)
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
      row.createCell(1).setCellValue(e.name)
      row.createCell(2).setCellValue(key)
      row.createCell(3).setCellValue(value)
    }
  }

  private var groupRowIndex = 0

  private def renderComponent(componentId: String, components: Seq[String]): Unit = {
    val component     = domain.components(componentId)
    val newComponents = components :+ component.name
    groupRowIndex += 1
    val row           = dwb.components.sheet.createRow(groupRowIndex)
    row.createCell(0).setCellValue(component.id)
    var index         = 1
    newComponents.foreach { c =>
      row.createCell(index).setCellValue(c)
      index += 1
    }
    row.createCell(depth + 1).setCellValue(component.schema.getOrElse(""))
    row.createCell(depth + 2).setCellValue(component.description)

    domain.entities.values
      .filter(_.componentId == component.id)
      .foreach(entity => renderEntity(component, entity, newComponents))
    domain.dataElements.values
      .filter(_.componentId == Some(componentId))
      .foreach(de => renderDataElement(de, newComponents))
    domain.enums.values.filter(_.componentId == Some(componentId)).foreach(e => renderEnum(e, newComponents))

    component.children.foreach(id => renderComponent(id, newComponents))
  }

  private var entityRowIndex = 0

  private def renderEntity(component: Component, entity: Entity, components: Seq[String]) = {
    entityRowIndex += 1
    val row        = dwb.entities.sheet.createRow(entityRowIndex)
    row.createCell(0).setCellValue(component.id)
    var index      = 1
    components.foreach { c =>
      row.createCell(index).setCellValue(c)
      index += 1
    }
    row.createCell(depth + 1).setCellValue(entity.id)
    row.createCell(depth + 2).setCellValue(entity.name)
    row.createCell(depth + 3).setCellValue(entity.entityName)
    row.createCell(depth + 4).setCellValue(entity.tableName)
    val entityType = entity.entityType.toString
    row.createCell(depth + 5).setCellValue(entityType.take(entityType.length - 6))
    row.createCell(depth + 6).setCellValue(entity.description)
    entity.expandedFields.foreach(field => renderEntityField(entity, field, components))
    entity.indexes.foreach(index => renderEntityIndex(entity, index))
    entity.expandedRelations.foreach(relation => renderEntityRelation(entity, relation))
  }

  private var entityFieldRowIndex = 0

  private def renderEntityField(entity: Entity, field: EntityField, components: Seq[String]) = {
    entityFieldRowIndex += 1
    val row = dwb.fields.sheet.createRow(entityFieldRowIndex)

    var index = 0
    components.foreach { c =>
      row.createCell(index).setCellValue(c)
      index += 1
    }

    row.createCell(depth).setCellValue(entity.entityName)
    row.createCell(depth + 1).setCellValue(entity.tableName)
    row.createCell(depth + 2).setCellValue(entity.name)
    field.dataType match {
      case EnumString(enumId, _)          =>
        row.createCell(depth + 12).setCellValue(enumId)
      case DataElementType(dataElementId) =>
        row.createCell(depth + 12).setCellValue(dataElementId)
      case _                              =>
    }
    row.createCell(depth + 3).setCellValue(field.name)
    row.createCell(depth + 4).setCellValue(field.fieldName)
    val (_, javaDatatype, postgresDatatype, len, scale, _) = getFieldDatatype(field.dataType)
    row.createCell(depth + 5).setCellValue(javaDatatype)
    row.createCell(depth + 6).setCellValue(field.dbFieldName)
    row.createCell(depth + 7).setCellValue(postgresDatatype)
    row.createCell(depth + 8).setCellValue(len)
    row.createCell(depth + 9).setCellValue(scale)
    if (entity.pk.contains(field.fieldName)) {
      if (field.autoIncrement) row.createCell(depth + 10).setCellValue("S")
      else row.createCell(depth + 10).setCellValue("X")
    }
    if (field.notNull) row.createCell(depth + 11).setCellValue("X")
    row.createCell(depth + 13).setCellValue(field.description.replaceBR)
  }

  private var entityIndexIndex      = 0
  private var entityIndexFieldIndex = 0

  private def renderEntityIndex(entity: Entity, index: EntityIndex) = {
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

  private def renderEntityRelation(entity: Entity, relation: EntityRelation) = {
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
      components = initSheet(wb, translation.components, captionFont, Some(1)),
      entities = initSheet(wb, translation.entities, captionFont, Some(1)),
      fields = initSheet(wb, translation.fields, captionFont, Some(0)),
      indexes = initSheet(wb, translation.indexes, captionFont),
      indexFields = initSheet(wb, translation.indexFields, captionFont),
      relations = initSheet(wb, translation.relations, captionFont),
      relationFields = initSheet(wb, translation.relationFields, captionFont),
      dataElements = initSheet(wb, translation.dataElements, captionFont, Some(0)),
      enums = initSheet(wb, translation.enums, captionFont),
      enumItems = initSheet(wb, translation.enumItems, captionFont),
    )
  }

  private def initSheet(
    wb: XSSFWorkbook,
    translation: SheetTranslation,
    font: XSSFFont,
    mbColumn: Option[Int] = None,
  ): Sheet = {
    val sheet = wb.createSheet(translation.name)
    val row   = sheet.createRow(0)
    val style = wb.createCellStyle()
    style.setAlignment(HorizontalAlignment.CENTER)
    style.setVerticalAlignment(VerticalAlignment.CENTER)
    style.setFont(font)
    var index = 0
    translation.fields.foreach { case caption =>
      mbColumn match {
        case Some(n) if index == n =>
          (1 to depth).map { i =>
            val cell = row.createCell(index)
            cell.setCellValue(s"$caption $i")
            cell.setCellStyle(style)
            index += 1
          }
        case _                     =>
          val cell = row.createCell(index)
          cell.setCellValue(caption)
          cell.setCellStyle(style)
          index += 1
      }
    }
    Sheet(name = translation.name, sheet = sheet)
  }

  private def getDepth(maybeId: Option[String] = None): Int =
    maybeId match {
      case Some(id) if domain.components(id).children.isEmpty =>
        1
      case Some(id)                                           =>
        domain.components(id).children.map(id => getDepth(Some(id))).max + 1
      case None                                               =>
        domain.rootComponentIds.map(id => getDepth(Some(id))).max
    }

}
