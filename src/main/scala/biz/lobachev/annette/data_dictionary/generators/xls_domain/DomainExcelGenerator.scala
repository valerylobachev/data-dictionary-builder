package biz.lobachev.annette.data_dictionary.generators.xls_domain

import biz.lobachev.annette.data_dictionary.generators.{BinaryGenerator, GeneratedFile}
import biz.lobachev.annette.data_dictionary.utils.StringSyntax.*
import biz.lobachev.annette.data_dictionary.model.*
import org.apache.poi.ss.usermodel.{HorizontalAlignment, VerticalAlignment}
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.*

import scala.collection.immutable.ListMap
import java.io.FileOutputStream

case class DomainExcelGenerator(
  target: Target,
  filename: String,
  translation: Map[String, String],
  domains: Domain*,
) extends BinaryGenerator
    with DomainMetaHelper {

  private val depth                       = getMaxDepth()
  private val wb                          = new XSSFWorkbook()
  private val (headerStyle, defaultStyle) = initStyle()

  override def generate(path: String): Seq[GeneratedFile] = {
    generateDomains()
    generateComponents()
    generateTables()
    generateTableFields()
    generateTableIndexes()
    generateTableRelations()
    generateDataElements()
    generateEnums()
    generateEnumElements()
    generateStructs()
    generateStructFields()
    new java.io.File(path).mkdirs
    val filePath = s"$path/$filename.xlsx"
    val fileOut  = new FileOutputStream(filePath)
    try wb.write(fileOut)
    finally if (fileOut != null) fileOut.close()
    Seq(
      GeneratedFile(
        "",
        s"$filename.xlsx",
        "Binary content",
      ),
    )
  }

  private def generateDomains() = {
    val sheet = wb.createSheet(translation.getOrElse("domain.sheetName", "Domains"))
    generateDomainHeader(sheet)

    var rowNo = 1
    domains.foreach { domain =>
      val line = DataLine(
        domainId = domain.id,
        domainName = domain.name,
        component = None,
        data = Seq(
          domain.description.replaceBR,
        ),
      )
      addLine(sheet, rowNo, line)
      rowNo += 1
    }

    formatSheet(sheet)
  }

  private def generateDomainHeader(sheet: XSSFSheet): Unit = {
    val headerLine = getHeaderLinePrefix()
      .copy(
        component = None,
        data = Seq(
          translation.getOrElse("domain.description", "Domain Description"),
        ),
      )
    addLine(sheet, 0, headerLine, Some(headerStyle))
  }

  private def generateComponents() = {
    val sheet = wb.createSheet(translation.getOrElse("component.sheetName", "Components"))
    generateComponentHeader(sheet)

    var rowNo = 1
    domains.foreach { domain =>
      domain.components.values.foreach { component =>
        val line = getDataLinePrefix(domain, Some(component.id))
          .copy(
            data = Seq(
              component.description.replaceBR,
            ),
          )
        addLine(sheet, rowNo, line)
        rowNo += 1
      }
    }
    formatSheet(sheet)
  }

  private def generateComponentHeader(sheet: XSSFSheet): Unit = {
    val headerLine = getHeaderLinePrefix().copy(
      data = Seq(
        translation.getOrElse("component.description", "Component Description"),
      ),
    )
    addLine(sheet, 0, headerLine, Some(headerStyle))
  }

  private def generateTables() = {
    val sheet = wb.createSheet(translation.getOrElse("table.sheetName", "Tables"))
    generateTableHeader(sheet)

    var rowNo = 1
    domains.foreach { domain =>
      domain.tables.values.foreach { table =>
        val fullTableName = getFullTableName(table, domain)
        val line          = getDataLinePrefix(domain, table.componentId)
          .copy(
            data = Seq(
              table.id,
              table.name,
              table.entityName,
              fullTableName.schema.getOrElse(""),
              fullTableName.tableName,
              table.description.replaceBR,
            ),
          )
        addLine(sheet, rowNo, line)
        rowNo += 1
      }
    }
    formatSheet(sheet)
  }

  private def generateTableHeader(sheet: XSSFSheet): Unit = {
    val headerLine = getHeaderLinePrefix().copy(
      data = Seq(
        translation.getOrElse("table.id", "Table Id"),
        translation.getOrElse("table.name", "Table Name"),
        translation.getOrElse("table.entityName", "Entity Name"),
        translation.getOrElse("table.schema", "Schema"),
        translation.getOrElse("table.tableName", "DB Table Name"),
        translation.getOrElse("table.description", "Table Description"),
      ),
    )
    addLine(sheet, 0, headerLine, Some(headerStyle))
  }

  private def generateTableFields() = {
    val sheet = wb.createSheet(translation.getOrElse("tableField.sheetName", "Table Fields"))
    generateTableFieldHeader(sheet)

    var rowNo = 1
    domains.foreach { domain =>
      domain.tables.values.foreach { table =>
        val fullTableName = getFullTableName(table, domain)
        table.fields.foreach { field =>
          val pk               = if (table.pk.contains(field.fieldName)) "X" else ""
          val notNull          = if (field.notNull) "X" else ""
          val dataElement      = field.datatype match {
            case EnumDatatype(enumId)               => enumId
            case DataElementDatatype(dataElementId) => dataElementId
            case _                                  => ""
          }
          val languageDatatype = getTargetDatatype(field.datatype, target, domain).documentationDatatype()
          val postgresDatatype = getPostgresTableFieldDatatype(field, domain).datatype
          val default          = field.default.map {
            case DefaultValue(value)         => s"'$value'"
            case DefaultStatement(statement) => s"$statement"
          }.getOrElse("")
          val generated        = field.generated.map {
            case GeneratedAlwaysAsIdentity             => "ALWAYS AS IDENTITY"
            case GeneratedByDefaultAsIdentity          => "BY DEFAULT AS IDENTITY"
            case GeneratedAlwaysAs(statement, Stored)  =>
              s"ALWAYS AS( $statement ) STORED"
            case GeneratedAlwaysAs(statement, Virtual) =>
              s"ALWAYS AS( $statement ) VIRTUAL"
          }.getOrElse("")
          val line             = getDataLinePrefix(domain, table.componentId)
            .copy(
              data = Seq(
                table.entityName,
                fullTableName.schema.getOrElse(""),
                fullTableName.tableName,
                table.name,
                field.name,
                field.fieldName,
                languageDatatype,
                field.dbFieldName,
                postgresDatatype,
                pk,
                notNull,
                dataElement,
                default,
                generated,
                field.description.replaceBR,
              ),
            )
          addLine(sheet, rowNo, line)
          rowNo += 1
        }
      }
    }
    formatSheet(sheet)
  }

  private def generateTableFieldHeader(sheet: XSSFSheet): Unit = {
    val headerLine = getHeaderLinePrefix().copy(
      data = Seq(
        translation.getOrElse("table.entityName", "Entity Name"),
        translation.getOrElse("table.schema", "Schema"),
        translation.getOrElse("table.tableName", "DB Table Name"),
        translation.getOrElse("table.name", "Table Name"),
        translation.getOrElse("tableField.name", "Name"),
        translation.getOrElse("tableField.fieldName", "Field"),
        target.toString + " " + translation.getOrElse("tableField.languageDatatype", "Data Type"),
        translation.getOrElse("tableField.dbFieldName", "DB Field"),
        translation.getOrElse("tableField.postgresDatatype", "Postgres Data Type"),
        translation.getOrElse("tableField.pk", "Primary Key"),
        translation.getOrElse("tableField.notNull", "Required"),
        translation.getOrElse("tableField.dataElement", "Data Element/Enum"),
        translation.getOrElse("tableField.default", "Default"),
        translation.getOrElse("tableField.generated", "Generated"),
        translation.getOrElse("tableField.description", "Description"),
      ),
    )
    addLine(sheet, 0, headerLine, Some(headerStyle))
  }

  private def generateTableIndexes() = {
    val sheet = wb.createSheet(translation.getOrElse("tableIndex.sheetName", "Table Indexes"))
    generateTableIndexHeader(sheet)

    var rowNo = 1
    domains.foreach { domain =>
      domain.tables.values.foreach { table =>
        val fullTableName = getFullTableName(table, domain)
        table.indexes.foreach { index =>
          val fields = index.fields.map { f =>
            val dbFieldName = getTableDbFieldName(table.fields, f.fieldName)
            val indexNulls  = f.indexNulls match {
              case Some(NullsFirst) => " NULLS FIRST"
              case Some(NullsLast)  => " NULLS LAST"
              case None             => ""
            }
            val desc        = if f.descending then " DESC" else ""
            s"$dbFieldName$desc$indexNulls"
          }.mkString(",\n")

          val line = getDataLinePrefix(domain, table.componentId)
            .copy(
              data = Seq(
                table.entityName,
                fullTableName.schema.getOrElse(""),
                fullTableName.tableName,
                table.name,
                index.id,
                index.name,
                if index.unique then "X" else "",
                index.method.map(_.toString).getOrElse(""),
                index.condition.getOrElse(""),
                fields,
                index.includeFields.mkString(",\n"),
                index.description.replaceBR,
              ),
            )
          addLine(sheet, rowNo, line)
          rowNo += 1
        }
      }
    }
    formatSheet(sheet)
  }

  private def generateTableIndexHeader(sheet: XSSFSheet): Unit = {
    val headerLine = getHeaderLinePrefix().copy(
      data = Seq(
        translation.getOrElse("table.entityName", "Entity Name"),
        translation.getOrElse("table.schema", "Schema"),
        translation.getOrElse("table.tableName", "DB Table Name"),
        translation.getOrElse("table.name", "Table Name"),
        translation.getOrElse("tableIndex.id", "Index Id"),
        translation.getOrElse("tableIndex.name", "Index Name"),
        translation.getOrElse("tableIndex.unique", "Unique"),
        translation.getOrElse("tableIndex.method", "Method"),
        translation.getOrElse("tableIndex.condition", "Condition"),
        translation.getOrElse("tableIndex.fields", "Fields"),
        translation.getOrElse("tableIndex.includeFields", "Include Fields"),
        translation.getOrElse("tableIndex.description", "Description"),
      ),
    )
    addLine(sheet, 0, headerLine, Some(headerStyle))
  }

  private def generateTableRelations() = {
    val sheet = wb.createSheet(translation.getOrElse("tableRelation.sheetName", "Table Relations"))
    generateTableRelationHeader(sheet)

    var rowNo = 1
    domains.foreach { domain =>
      domain.tables.values.foreach { table =>
        val fullTableName = getFullTableName(table, domain)
        table.relations.foreach { relation =>
          val refTable         = domain.tables(relation.referenceTableId)
          val refFullTableName = getFullTableName(refTable, domain)

          val line = getDataLinePrefix(domain, table.componentId)
            .copy(
              data = Seq(
                table.entityName,
                fullTableName.schema.getOrElse(""),
                fullTableName.tableName,
                table.name,
                relation.id,
                relation.name,
                relation.relationType.toString,
                if relation.logical then "X" else "",
                refFullTableName.schema.getOrElse(""),
                refFullTableName.tableName,
                refTable.name,
                relation.fields.map { case f1 -> f2 => s"$f1 ➔ $f2" }.mkString(",\n"),
                relation.onUpdate.toString,
                relation.onDelete.toString,
                relation.association.map(_.name).getOrElse(""),
                relation.association.map(_.referenceName.getOrElse("")).getOrElse(""),
                relation.description.replaceBR,
              ),
            )
          addLine(sheet, rowNo, line)
          rowNo += 1
        }
      }
    }
    formatSheet(sheet)
  }

  private def generateTableRelationHeader(sheet: XSSFSheet): Unit = {
    val headerLine = getHeaderLinePrefix().copy(
      data = Seq(
        translation.getOrElse("table.entityName", "Entity Name"),
        translation.getOrElse("table.schema", "Schema"),
        translation.getOrElse("table.tableName", "DB Table Name"),
        translation.getOrElse("table.name", "Table Name"),
        translation.getOrElse("tableRelation.id", "Relation Id"),
        translation.getOrElse("tableRelation.name", "Relation Name"),
        translation.getOrElse("tableRelation.type", "Type"),
        translation.getOrElse("tableRelation.logical", "Logical"),
        translation.getOrElse("tableRelation.refTableSchema", "Ref. Table Schema"),
        translation.getOrElse("tableRelation.refDBTableName", "Ref. DB Table Name"),
        translation.getOrElse("tableRelation.refTableName", "Ref. Table Name"),
        translation.getOrElse("tableRelation.fields", "Fields"),
        translation.getOrElse("tableRelation.onUpdate", "On Update"),
        translation.getOrElse("tableRelation.onDelete", "On Delete"),
        translation.getOrElse("tableRelation.associationFieldName", "Assoc. Field Name"),
        translation.getOrElse("tableRelation.refAssociationFieldName", "Assoc. Ref. Field Name"),
        translation.getOrElse("tableRelation.description", "Description"),
      ),
    )
    addLine(sheet, 0, headerLine, Some(headerStyle))
  }

  private def generateDataElements() = {
    val sheet = wb.createSheet(translation.getOrElse("dataElement.sheetName", "Data Elements"))
    generateDataElementHeader(sheet)

    var rowNo = 1
    domains.foreach { domain =>
      domain.dataElements.values.foreach { dataElement =>
        val languageDatatype = getDataElementTargetDatatype(dataElement, target, domain).documentationDatatype()
        val postgresDatatype = getPostgresDataElementDatatype(dataElement, domain).datatype

        val enumId = dataElement.datatype match {
          case EnumDatatype(enumId) => enumId
          case _                    => ""
        }
        val line   = getDataLinePrefix(domain, dataElement.componentId)
          .copy(
            data = Seq(
              dataElement.id,
              dataElement.name,
              dataElement.fieldName,
              dataElement.dbFieldName,
              languageDatatype,
              postgresDatatype,
              enumId,
              dataElement.description.replaceBR,
            ),
          )
        addLine(sheet, rowNo, line)
        rowNo += 1
      }
    }
    formatSheet(sheet)
  }

  private def generateDataElementHeader(sheet: XSSFSheet): Unit = {
    val headerLine = getHeaderLinePrefix().copy(
      data = Seq(
        translation.getOrElse("dataElement.id", "DataElement Id"),
        translation.getOrElse("dataElement.name", "DataElement Name"),
        translation.getOrElse("dataElement.fieldName", "Field Name"),
        translation.getOrElse("dataElement.dbFieldName", "DB Field Name"),
        target.toString + " " + translation.getOrElse("tableField.languageDatatype", "Data Type"),
        translation.getOrElse("dataElement.postgresDatatype", "Postgres Data Type"),
        translation.getOrElse("dataElement.enum", "Enum"),
        translation.getOrElse("dataElement.description", "Description"),
      ),
    )
    addLine(sheet, 0, headerLine, Some(headerStyle))
  }

  private def generateEnums() = {
    val sheet = wb.createSheet(translation.getOrElse("enum.sheetName", "Enums"))
    generateEnumHeader(sheet)

    var rowNo = 1
    domains.foreach { domain =>
      domain.enums.values.foreach { e =>
        val line = getDataLinePrefix(domain, e.componentId)
          .copy(
            data = Seq(
              e.id,
              e.name,
              e.enumType.toString,
              if e.strict then "X" else "",
              if e.enumType != IntEnum then e.length.toString else "",
              e.description.replaceBR,
            ),
          )
        addLine(sheet, rowNo, line)
        rowNo += 1
      }
    }
    formatSheet(sheet)
  }

  private def generateEnumHeader(sheet: XSSFSheet): Unit = {
    val headerLine = getHeaderLinePrefix().copy(
      data = Seq(
        translation.getOrElse("enum.id", "Enum Id"),
        translation.getOrElse("enum.name", "Enum Name"),
        translation.getOrElse("enum.type", "Type"),
        translation.getOrElse("enum.strict", "Strict"),
        translation.getOrElse("enum.length", "Length"),
        translation.getOrElse("enum.description", "Description"),
      ),
    )
    addLine(sheet, 0, headerLine, Some(headerStyle))
  }

  private def generateEnumElements() = {
    val sheet = wb.createSheet(translation.getOrElse("enumElement.sheetName", "Enum Elements"))
    generateEnumElementHeader(sheet)

    var rowNo = 1
    domains.foreach { domain =>
      domain.enums.values.foreach { e =>
        e.elements.map { el =>
          val line = getDataLinePrefix(domain, e.componentId)
            .copy(
              data = Seq(
                e.id,
                e.name,
                e.enumType.toString,
                el.id,
                el.constName,
                el.name,
              ),
            )
          addLine(sheet, rowNo, line)
          rowNo += 1
        }
      }
    }
    formatSheet(sheet)
  }

  private def generateEnumElementHeader(sheet: XSSFSheet): Unit = {
    val headerLine = getHeaderLinePrefix().copy(
      data = Seq(
        translation.getOrElse("enum.id", "Enum Id"),
        translation.getOrElse("enum.name", "Enum Name"),
        translation.getOrElse("enum.type", "Type"),
        translation.getOrElse("enumElement.id", "Element Id"),
        translation.getOrElse("enumElement.constName", "Constant Name"),
        translation.getOrElse("enumElement.name", "Name"),
      ),
    )
    addLine(sheet, 0, headerLine, Some(headerStyle))
  }

  private def generateStructs() = {
    val sheet = wb.createSheet(translation.getOrElse("struct.sheetName", "Structs"))
    generateStructHeader(sheet)

    var rowNo = 1
    domains.foreach { domain =>
      domain.structs.values.foreach { struct =>
        val (
          associatedTableId,
          associatedTableName,
          associatedTableSchema,
          associatedDbTableName,
          fieldUsage,
          includeExcludeFields,
        ) = struct.likeTable.map { lt =>
          val assocTable                         = domain.tables(lt.tableId)
          val fullTableName                      = getFullTableName(assocTable, domain)
          val (fieldUsage, includeExcludeFields) = lt.fieldUsage match {
            case IncludeAllFields      => ("Include All Fields", "")
            case IncludePKFields       => ("Include PK Fields", "")
            case IncludeFields(fields) => ("Include Fields", fields.mkString(",\n"))
            case ExcludeFields(fields) => ("Exclude Fields", fields.mkString(",\n"))
          }
          (
            lt.tableId,
            assocTable.name,
            fullTableName.schema.getOrElse(""),
            fullTableName.tableName,
            fieldUsage,
            includeExcludeFields,
          )
        }.getOrElse(("", "", "", "", "", ""))

        val line = getDataLinePrefix(domain, struct.componentId)
          .copy(
            data = Seq(
              struct.id,
              struct.name,
              struct.structName,
              associatedTableId,
              associatedTableName,
              associatedTableSchema,
              associatedDbTableName,
              fieldUsage,
              includeExcludeFields,
              struct.description.replaceBR,
            ),
          )
        addLine(sheet, rowNo, line)
        rowNo += 1
      }
    }
    formatSheet(sheet)
  }

  private def generateStructHeader(sheet: XSSFSheet): Unit = {
    val headerLine = getHeaderLinePrefix().copy(
      data = Seq(
        translation.getOrElse("struct.id", "Struct Id"),
        translation.getOrElse("struct.name", "Name"),
        translation.getOrElse("struct.structName", "Struct Name"),
        translation.getOrElse("struct.associatedTableId", "Assoc. Table Id"),
        translation.getOrElse("struct.associatedTableName", "Assoc. Table Name"),
        translation.getOrElse("struct.associatedTableSchema", "Assoc. DB Table Schema"),
        translation.getOrElse("struct.associatedDbTableName", "Assoc. DB Table Name"),
        translation.getOrElse("struct.fieldsUsage", "Field Usage"),
        translation.getOrElse("struct.includeExcludeFields", "Include/Exclude Fields"),
        translation.getOrElse("struct.description", "Description"),
      ),
    )
    addLine(sheet, 0, headerLine, Some(headerStyle))
  }

  private def generateStructFields() = {
    val sheet = wb.createSheet(translation.getOrElse("structField.sheetName", "Struct Fields"))
    generateStructFieldHeader(sheet)

    var rowNo = 1
    domains.foreach { domain =>
      domain.structs.values.foreach { struct =>
        struct.fields.foreach { field =>
          val required         = if (!field.optional) "X" else ""
          val dataElement      = field.datatype match {
            case EnumDatatype(enumId)               => enumId
            case DataElementDatatype(dataElementId) => dataElementId
            case _                                  => ""
          }
          val languageDatatype = getTargetDatatype(field.datatype, target, domain).documentationDatatype()
          val line             = getDataLinePrefix(domain, struct.componentId)
            .copy(
              data = Seq(
                struct.name,
                struct.structName,
                field.name,
                field.fieldName,
                languageDatatype,
                required,
                dataElement,
                field.description.replaceBR,
              ),
            )
          addLine(sheet, rowNo, line)
          rowNo += 1
        }
      }
    }
    formatSheet(sheet)
  }

  private def generateStructFieldHeader(sheet: XSSFSheet): Unit = {
    val headerLine = getHeaderLinePrefix().copy(
      data = Seq(
        translation.getOrElse("struct.name", "Name"),
        translation.getOrElse("struct.structName", "Struct Name"),
        translation.getOrElse("structField.name", "Name"),
        translation.getOrElse("structField.fieldName", "Field"),
        target.toString + " " + translation.getOrElse("structField.languageDatatype", "Data Type"),
        translation.getOrElse("structField.required", "Required"),
        translation.getOrElse("structField.dataElement", "Data Element/Enum"),
        translation.getOrElse("structField.description", "Description"),
      ),
    )
    addLine(sheet, 0, headerLine, Some(headerStyle))
  }

  private def getHeaderLinePrefix(): DataLine =
    DataLine(
      domainId = translation.getOrElse("domain.id", "Domain Id"),
      domainName = translation.getOrElse("domain.name", "Domain Name"),
      component = Some(
        ComponentLine(
          translation.getOrElse("component.id", "Component Id"),
          (1 to depth).map { i =>
            val name = translation.getOrElse("component.name", "Component")
            s"$name $i"
          },
        ),
      ),
      data = Seq.empty,
    )

  private def getDataLinePrefix(domain: Domain, componentId: Option[String]): DataLine = {
    val component = componentId.map(cid => domain.components(cid))
    DataLine(
      domainId = domain.id,
      domainName = domain.name,
      component = Some(
        ComponentLine(
          component.map(_.id).getOrElse(""),
          component.map(c => getComponentNames(domain.components, c.id)).getOrElse(Seq.empty),
        ),
      ),
      data = Seq.empty,
    )
  }

  private def getComponentNames(components: ListMap[String, Component], componentId: String): List[String] = {
    val component = components(componentId)
    component.parent match {
      case Some(parentId) => getComponentNames(components, parentId) ::: component.name :: Nil
      case None           => component.name :: Nil
    }
  }

  private def addLine(sheet: XSSFSheet, rowNo: Int, line: DataLine, style: Option[XSSFCellStyle] = None) = {
    val row   = sheet.createRow(rowNo)
    var colNo = 0
    createCell(row, colNo, line.domainId, style)
    colNo += 1
    createCell(row, colNo, line.domainName, style)
    colNo += 1
    line.component.foreach { c =>
      createCell(row, colNo, c.id, style)
      colNo += 1
      c.names.foreach { name =>
        createCell(row, colNo, name, style)
        colNo += 1
      }
      colNo = 3 + depth
    }
    line.data.foreach { d =>
      createCell(row, colNo, d, style)
      colNo += 1
    }
    ()
  }

  private def createCell(row: XSSFRow, colNo: Int, value: String, style: Option[XSSFCellStyle]) = {
    val cell = row.createCell(colNo)
    cell.setCellValue(value)
    style match {
      case Some(s) => cell.setCellStyle(s)
      case None    => cell.setCellStyle(defaultStyle)
    }
    ()
  }

  private def getMaxDepth(): Int =
    domains.map(domain => getDepth(domain)).max

  private def getDepth(domain: Domain, maybeId: Option[String] = None): Int =
    maybeId match {
      case Some(id) if domain.components(id).children.isEmpty =>
        1
      case Some(id)                                           =>
        domain.components(id).children.map(id => getDepth(domain, Some(id))).maxOption.getOrElse(0) + 1
      case None                                               =>
        domain.rootComponentIds.map(id => getDepth(domain, Some(id))).maxOption.getOrElse(0)
    }

  private def initStyle() = {
    val font: XSSFFont = wb.createFont()
    font.setFontHeightInPoints(11.toShort)
    font.setFontName("Calibri")
    font.setBold(true)
    font.setItalic(false)

    val headerStyle = wb.createCellStyle()
    headerStyle.setAlignment(HorizontalAlignment.CENTER)
    headerStyle.setVerticalAlignment(VerticalAlignment.CENTER)
    headerStyle.setFont(font)

    val defaultStyle = wb.createCellStyle()
    defaultStyle.setWrapText(true)
    defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER)

    (headerStyle, defaultStyle)
  }

  private def formatSheet(sheet: XSSFSheet): Unit = {
    sheet.createFreezePane(0, 1)
    val headerRow = sheet.getRow(0)
    val lastCol   = headerRow.getLastCellNum() - 1
    (0 to lastCol).foreach(c => sheet.autoSizeColumn(c))
    sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, lastCol)): Unit
  }
}
