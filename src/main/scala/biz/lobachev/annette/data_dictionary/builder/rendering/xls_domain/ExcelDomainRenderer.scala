package biz.lobachev.annette.data_dictionary.builder.rendering.xls_domain

import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{BinaryRenderer, RenderResult}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._
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

    var index = 0
    components.foreach { c =>
      row.createCell(index).setCellValue(c)
      index += 1
    }

    row.createCell(depth).setCellValue(dataElement.id)
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
      case Enum(enumId, _) =>
        row.createCell(depth + 9).setCellValue(enumId)
      case _                     =>
    }
    row.createCell(depth + 10).setCellValue(default)
    row.createCell(depth + 11).setCellValue(dataElement.description)
  }

  private var enumRowIndex        = 0
  private var enumElementRowIndex = 0

  private def renderEnum(e: EnumData, components: Seq[String]) = {

    enumRowIndex += 1
    val row = dwb.enums.sheet.createRow(enumRowIndex)

    var index = 0
    components.foreach { c =>
      row.createCell(index).setCellValue(c)
      index += 1
    }

    row.createCell(depth).setCellValue(e.id)
    row.createCell(depth + 1).setCellValue(e.name)
    row.createCell(depth + 2).setCellValue(e.enumType.toString)
    if e.strict then row.createCell(depth + 3).setCellValue("X")
    row.createCell(depth + 4).setCellValue(e.length.toString)
    row.createCell(depth + 5).setCellValue(e.description)
    e.elements.map { case EnumElement(key, constName, name) =>
      enumElementRowIndex += 1
      val row = dwb.enumItems.sheet.createRow(enumElementRowIndex)

      var index = 0
      components.foreach { c =>
        row.createCell(index).setCellValue(c)
        index += 1
      }
      row.createCell(depth).setCellValue(e.id)
      row.createCell(depth + 1).setCellValue(e.name)
      row.createCell(depth + 2).setCellValue(key)
      row.createCell(depth + 3).setCellValue(constName)
      row.createCell(depth + 4).setCellValue(name)
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
    entity.indexes.foreach(index => renderEntityIndex(entity, index, components))
    entity.expandedRelations.foreach(relation => renderEntityRelation(entity, relation, components))
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
      case Enum(enumId, _)          =>
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

  private def renderEntityIndex(entity: Entity, index: EntityIndex, components: Seq[String]) = {
    entityIndexIndex += 1
    val row = dwb.indexes.sheet.createRow(entityIndexIndex)

    var idx = 0
    components.foreach { c =>
      row.createCell(idx).setCellValue(c)
      idx += 1
    }
    row.createCell(depth).setCellValue(entity.tableName)
    row.createCell(depth + 1).setCellValue(entity.name)
    row.createCell(depth + 2).setCellValue(entity.tableName + "_" + index.id.snakeCase)
    row.createCell(depth + 3).setCellValue(index.name)
    if (index.unique) row.createCell(depth + 4).setCellValue("X")
    row.createCell(depth + 5).setCellValue(index.description.replaceBR)
    index.fields.foreach { field =>
      entityIndexFieldIndex += 1
      val row         = dwb.indexFields.sheet.createRow(entityIndexFieldIndex)
      var idx         = 0
      components.foreach { c =>
        row.createCell(idx).setCellValue(c)
        idx += 1
      }
      row.createCell(depth).setCellValue(entity.tableName)
      row.createCell(depth + 1).setCellValue(entity.name)
      row.createCell(depth + 2).setCellValue(entity.tableName + "_" + index.id.snakeCase)
      row.createCell(depth + 3).setCellValue(index.name)
      if (index.unique) row.createCell(depth + 4).setCellValue("X")
      val entityField = getEntityField(entity.expandedFields, field)
      row.createCell(depth + 5).setCellValue(entityField.dbFieldName)
      row.createCell(depth + 6).setCellValue(entityField.name)

    }
  }

  private var entityRelationIndex      = 0
  private var entityRelationFieldIndex = 0

  private def renderEntityRelation(entity: Entity, relation: EntityRelation, components: Seq[String]) = {
    entityRelationIndex += 1
    val row = dwb.relations.sheet.createRow(entityRelationIndex)

    var index = 0
    components.foreach { c =>
      row.createCell(index).setCellValue(c)
      index += 1
    }

    row.createCell(depth).setCellValue(entity.tableName)
    row.createCell(depth + 1).setCellValue(entity.name)
    row.createCell(depth + 2).setCellValue(entity.tableName + "_" + relation.id.snakeCase)
    row.createCell(depth + 3).setCellValue(relation.name)
    row.createCell(depth + 4).setCellValue(relation.relationType.toString)
    if relation.logical then row.createCell(depth + 5).setCellValue("X")
    val refEntity = domain.entities(relation.referenceEntityId)
    row.createCell(depth + 6).setCellValue(refEntity.tableName)
    row.createCell(depth + 7).setCellValue(refEntity.name)
    row.createCell(depth + 8).setCellValue(relation.onUpdate.toString)
    row.createCell(depth + 9).setCellValue(relation.onDelete.toString)
    row.createCell(depth + 10).setCellValue(relation.description.replaceBR)
    relation.fields.foreach { field =>
      entityRelationFieldIndex += 1
      val row = dwb.relationFields.sheet.createRow(entityRelationFieldIndex)

      val entityField    = getEntityField(entity.expandedFields, field._1)
      val refEntityField = getEntityField(refEntity.expandedFields, field._2)

      var index = 0
      components.foreach { c =>
        row.createCell(index).setCellValue(c)
        index += 1
      }

      row.createCell(depth).setCellValue(entity.tableName)
      row.createCell(depth + 1).setCellValue(entity.name)
      row.createCell(depth + 2).setCellValue(entity.tableName + "_" + relation.id.snakeCase)
      row.createCell(depth + 3).setCellValue(relation.name)
      row.createCell(depth + 4).setCellValue(relation.relationType.toString)
      if relation.logical then row.createCell(depth + 5).setCellValue("X")
      row.createCell(depth + 6).setCellValue(refEntity.tableName)
      row.createCell(depth + 7).setCellValue(refEntity.name)
      row.createCell(depth + 8).setCellValue(entityField.dbFieldName)
      row.createCell(depth + 9).setCellValue(entityField.name)
      row.createCell(depth + 10).setCellValue(refEntityField.dbFieldName)
      row.createCell(depth + 11).setCellValue(refEntityField.name)
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
      case Enum(enumId, defaultValue)                   =>
        val en = domain.enums(enumId)
        en.enumType match {
          case NativeEnum | StringEnum =>
            datatype = "Enum"
            javaDatatype = "String(enum)"
            postgresDatatype = "varchar"
            len = en.length.toString
            default = defaultValue.map(s => s"\"$s\"")
          case IntEnum =>
            datatype = "Enum"
            javaDatatype = "Int(enum)"
            postgresDatatype = "integer"
            default = defaultValue.map(s => s"\"$s\"")
        }
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
      indexes = initSheet(wb, translation.indexes, captionFont, Some(0)),
      indexFields = initSheet(wb, translation.indexFields, captionFont, Some(0)),
      relations = initSheet(wb, translation.relations, captionFont, Some(0)),
      relationFields = initSheet(wb, translation.relationFields, captionFont, Some(0)),
      dataElements = initSheet(wb, translation.dataElements, captionFont, Some(0)),
      enums = initSheet(wb, translation.enums, captionFont, Some(0)),
      enumItems = initSheet(wb, translation.enumItems, captionFont, Some(0)),
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

  private def getEntityField(entityFields: Seq[EntityField], fieldName: String): EntityField =
    entityFields
      .find(f => f.fieldName == fieldName)
      .getOrElse {
        throw new IllegalArgumentException(s"not found field $fieldName")
      }

}
