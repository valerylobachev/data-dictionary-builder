package biz.lobachev.annette.data_dictionary.builder

import biz.lobachev.annette.data_dictionary.dsl.*
import biz.lobachev.annette.data_dictionary.model.*

import scala.collection.immutable.ListMap

object DomainBuilder extends PreValidation with PostValidation with DomainDefMetaHelper {

  def build(domainDef: DomainDef): Either[Seq[Message], BuildResult] = {
    val startPreValidation  = System.currentTimeMillis()
    println("Running pre-validation...")
    val enrichedDomainDef   = enrichDomainDef(domainDef)
    val preValidateMessages = preValidate(enrichedDomainDef)
    println(s"Pre-validation done (${System.currentTimeMillis() - startPreValidation} ms)")
    if (!Message.messagesHaveErrors(preValidateMessages)) {
      val startBuilding        = System.currentTimeMillis()
      println("Building...")
      val includeMap           = buildIncludes(enrichedDomainDef.includes)
      val enums                = buildEnums(enrichedDomainDef.enums)
      val dataElements         = buildDataElements(enrichedDomainDef.dataElements)
      val components           = buildComponents(enrichedDomainDef.components)
      val tables               = buildTables(enrichedDomainDef.tables, includeMap, enrichedDomainDef, dataElements)
      val structs              = buildStructs(enrichedDomainDef.structs, includeMap, tables, dataElements)
      val domain               = Domain(
        id = domainDef.id,
        name = domainDef.name,
        description = domainDef.description,
        labels = labelsToMap(domainDef.labels),
        rootComponentIds = enrichedDomainDef.components.filter(_.componentId.isEmpty).map(_.id),
        components = components,
        enums = enums,
        dataElements = dataElements,
        tables = tables,
        structs = structs,
      )
      println(s"Building done (${System.currentTimeMillis() - startBuilding} ms)")
      val startPostValidation  = System.currentTimeMillis()
      println("Running post-validation...")
      val postValidateMessages = postValidate(domain)
      println(s"Post-validation done (${System.currentTimeMillis() - startPostValidation} ms)")

      if (Message.messagesHaveErrors(postValidateMessages)) {
        Left(preValidateMessages ++ postValidateMessages)
      } else {
        Right(
          BuildResult(
            domain,
            preValidateMessages,
          ),
        )
      }
    } else {
      Left(preValidateMessages)
    }

  }

  private def enrichDomainDef(domainDef: DomainDef): DomainDef = {
    val d1       = domainDef.copy(
      includes = domainDef.includes ++ domainDef.components.flatMap(c => enrichIncludes(c)),
      enums = domainDef.enums ++ domainDef.components.flatMap(c => enrichEnums(c)),
      dataElements = domainDef.dataElements ++ domainDef.components.flatMap(c => enrichDataElements(c)),
      tables = domainDef.tables ++ domainDef.components.flatMap(c => enrichTables(c)),
      structs = domainDef.structs ++ domainDef.components.flatMap(c => enrichStructs(c)),
      components = domainDef.components.flatMap(c => c +: enrichComponents(c)),
    )
    val tables   = d1.tables.map { t =>
      val fields = enrichTableFieldsFromDataElements(t.fields, d1.dataElements)
      t.copy(fields = fields)
    }
    val includes = d1.includes.map { i =>
      val fields = enrichTableFieldsFromDataElements(i.fields, d1.dataElements)
      i.copy(fields = fields)
    }
    val structs  = d1.structs.map { t =>
      val fields = enrichStructFieldsFromDataElements(t.fields, d1.dataElements)
      t.copy(fields = fields)
    }
    val d2       = d1.copy(includes = includes, tables = tables, structs = structs)
    d2
  }

  private def enrichTableFieldsFromDataElements(fields: Seq[TableFieldDef], dataElements: Seq[DataElementDef]) =
    fields.map {
      case f: TableFieldDefinition if f.fieldName == "" && f.datatype.isInstanceOf[DataElementDatatype] =>
        dataElements
          .find(_.id == f.datatype.asInstanceOf[DataElementDatatype].dataElementId)
          .map { de =>
            val dbFieldName = if f.dbFieldName.isEmpty then de.dbFieldName else f.dbFieldName
            f.copy(fieldName = de.fieldName, dbFieldName = dbFieldName)
          }
          .getOrElse(f)
      case f                                                                                            => f
    }

  private def enrichStructFieldsFromDataElements(fields: Seq[StructFieldDef], dataElements: Seq[DataElementDef]) =
    fields.map {
      case f: StructFieldDefinition if f.fieldName == "" && f.datatype.isInstanceOf[DataElementDatatype] =>
        dataElements
          .find(_.id == f.datatype.asInstanceOf[DataElementDatatype].dataElementId)
          .map { de =>
            f.copy(fieldName = de.fieldName)
          }
          .getOrElse(f)
      case f                                                                                             => f
    }

  private def enrichIncludes(c: ComponentDef): Seq[IncludeDef] =
    c.includes.map(v => v.copy(componentId = Some(c.id))) ++
      c.components.flatMap(c => enrichIncludes(c))

  private def enrichEnums(c: ComponentDef): Seq[EnumDataDef] =
    c.enums.map(v => v.copy(componentId = Some(c.id))) ++
      c.components.flatMap(c => enrichEnums(c))

  private def enrichDataElements(c: ComponentDef): Seq[DataElementDef] =
    c.dataElements.map(v => v.copy(componentId = Some(c.id))) ++
      c.components.flatMap(c => enrichDataElements(c))

  private def enrichTables(c: ComponentDef): Seq[TableDef] =
    c.tables.map(v => v.copy(componentId = Some(c.id))) ++
      c.components.flatMap(c => enrichTables(c))

  private def enrichStructs(c: ComponentDef): Seq[StructDef] =
    c.structs.map(v => v.copy(componentId = Some(c.id))) ++
      c.components.flatMap(c => enrichStructs(c))

  private def enrichComponents(c: ComponentDef): Seq[ComponentDef] =
    c.components.map(v => v.copy(componentId = Some(c.id))) ++
      c.components.flatMap(c => enrichComponents(c))

  private def buildIncludes(includes: Seq[IncludeDef]): Map[String, IncludeDef] = {
    val map = includes.groupBy(_.id).map { case k -> v => k -> v.head }
    val res = map.values.map(inc => inc.copy(fields = extractFieldsFromInclude(inc, map)))
    res.groupBy(_.id).map { case k -> v => k -> v.head }
  }

  private def extractFieldsFromInclude(
    inc: IncludeDef,
    incMap: Map[String, IncludeDef],
  ): Seq[TableFieldDefinition] =
    inc.fields.flatMap { f =>
      f match {
        case IncludeTableFields(includeId) =>
          incMap
            .get(includeId)
            .map(i => extractFieldsFromInclude(i, incMap))
            .getOrElse(Seq.empty)
        case t: TableFieldDefinition       => Seq(t)
      }
    }

  private def buildComponents(components: Seq[ComponentDef]): ListMap[String, Component] =
    ListMap.from(
      components.map { e =>
        e.id -> Component(
          id = e.id,
          name = e.name,
          description = e.description,
          parent = e.componentId,
          children = e.components.map(_.id),
          labels = labelsToMap(e.labels),
        )
      },
    )

  private def buildEnums(enums: Seq[EnumDataDef]): ListMap[String, EnumData] =
    ListMap.from(
      enums.map { e =>
        e.id -> EnumData(
          id = e.id,
          name = e.name,
          description = e.description,
          componentId = e.componentId,
          labels = labelsToMap(e.labels),
          enumType = e.enumType,
          strict = e.strict,
          length = e.length,
          elements = e.elements,
        )
      },
    )

  private def buildDataElements(dataElements: Seq[DataElementDef]): ListMap[String, DataElement] =
    ListMap.from(
      dataElements.map { e =>
        e.id -> DataElement(
          id = e.id,
          name = e.name,
          description = e.description,
          componentId = e.componentId,
          labels = labelsToMap(e.labels),
          fieldName = e.fieldName,
          dbFieldName = e.dbFieldName,
          datatype = e.datatype,
        )
      },
    )

  private def buildTables(
    tables: Seq[TableDef],
    incMap: Map[String, IncludeDef],
    domainDef: DomainDef,
    dataElements: ListMap[String, DataElement],
  ): ListMap[String, Table] =
    ListMap.from(
      tables.map { e =>
        val pkFields  = buildTableFields(e.pk, incMap, dataElements)
        val fields    = buildTableFields(e.fields, incMap, dataElements)
        val tableName = getTableName(e, domainDef)
        e.id -> Table(
          id = e.id,
          name = e.name,
          description = e.description,
          componentId = e.componentId,
          labels = labelsToMap(e.labels),
          entityName = e.entityName,
          tableName = tableName,
          fields = pkFields ++ fields,
          pk = pkFields.map(_.fieldName),
          indexes = buildIndexes(e.indexes),
          relations = buildRelations(e.relations),
        )
      },
    )

  private def buildTableFields(
    fields: Seq[TableFieldDef],
    incMap: Map[String, IncludeDef],
    dataElements: ListMap[String, DataElement],
  ): Seq[TableField] =
    fields.flatMap { f =>
      val r = f match {
        case IncludeTableFields(includeId) =>
          incMap
            .get(includeId) match {
            case Some(inc) =>
              inc.fields.flatMap {
                case tfd: TableFieldDefinition     => Some(tfd)
                case IncludeTableFields(includeId) => None
              }
            case None      => Seq.empty
          }

        case tfd: TableFieldDefinition => Seq(tfd)
      }
      r.map { f =>
        val (name, description) = f.datatype match {
          case DataElementDatatype(dataElementId) =>
            val maybeDataElement = dataElements.get(dataElementId)
            val name             = maybeDataElement
              .map(dataElement => if (f.name.trim.isEmpty) dataElement.name else f.name)
              .getOrElse(f.name)
            val description      = maybeDataElement
              .map(dataElement => if (f.description.trim.isEmpty) dataElement.description else f.description)
              .getOrElse(f.description)
            (name, description)
          case _                                  =>
            (f.name, f.description)
        }
        TableField(
          fieldName = f.fieldName,
          name = name,
          description = description,
          labels = labelsToMap(f.labels),
          dbFieldName = f.dbFieldName,
          datatype = f.datatype,
          notNull = f.notNull,
          default = f.default,
          generated = f.generated,
        )
      }
    }

  private def buildIndexes(indexes: Seq[TableIndexDef]): Seq[TableIndex] =
    indexes.map { i =>
      TableIndex(
        id = i.id,
        name = i.name,
        description = i.description,
        labels = labelsToMap(i.labels),
        unique = i.unique,
        fields = i.fields,
        method = i.method,
        condition = i.condition,
        includeFields = i.includeFields,
      )
    }

  private def buildRelations(rels: Seq[TableRelationDef]): Seq[TableRelation] =
    rels.map { r =>
      TableRelation(
        id = r.id,
        name = r.name,
        description = r.description,
        labels = labelsToMap(r.labels),
        relationType = r.relationType,
        referenceTableId = r.referenceTableId,
        fields = r.fields,
        onUpdate = r.onUpdate,
        onDelete = r.onDelete,
        logical = r.logical,
        association = r.association,
      )
    }

  private def buildStructs(
    structDefs: Seq[StructDef],
    incMap: Map[String, IncludeDef],
    tableMap: ListMap[String, Table],
    dataElements: ListMap[String, DataElement],
  ): ListMap[String, Struct] = {
    val structMap = structDefs.groupBy(_.id).map { case k -> v => k -> v.head }

    ListMap.from(
      structDefs.map { e =>
        val tableFields = e.likeTable match {
          case Some(lt) =>
            tableMap
              .get(lt.tableId)
              .map(t =>
                t.fields.filter { f =>
                  lt.fieldUsage match {
                    case IncludeAllFields      => true
                    case IncludePKFields       => t.pk.contains(f.fieldName)
                    case IncludeFields(fields) => fields.contains(f.fieldName)
                    case ExcludeFields(fields) => !fields.contains(f.fieldName)
                  }
                }.map(f =>
                  StructField(
                    fieldName = f.fieldName,
                    name = f.name,
                    description = f.description,
                    labels = f.labels,
                    datatype = f.datatype,
                    optional = !f.notNull,
                  ),
                ),
              )
              .getOrElse(Seq.empty)
          case None     => Seq.empty
        }

        val fields = buildStructFields(e, e.fields, incMap, dataElements, structMap, tableMap)

        e.id -> Struct(
          id = e.id,
          name = e.name,
          description = e.description,
          componentId = e.componentId,
          labels = labelsToMap(e.labels),
          structName = e.structName,
          likeTable = e.likeTable,
          fields = tableFields ++ fields,
        )
      },
    )
  }

  private def buildStructFields(
    structDef: StructDef,
    fields: Seq[StructFieldDef],
    incMap: Map[String, IncludeDef],
    dataElementMap: ListMap[String, DataElement],
    structMap: Map[String, StructDef],
    tableMap: ListMap[String, Table],
  ): Seq[StructField] = {
    val expFields = fields.flatMap { f =>
      f match {
        case IncludeStructFields(includeId) =>
          incMap
            .get(includeId) match {
            case Some(inc) =>
              inc.fields.flatMap {
                case f: TableFieldDefinition =>
                  Some(
                    StructField(
                      fieldName = f.fieldName,
                      name = f.name,
                      description = f.description,
                      labels = labelsToMap(f.labels),
                      datatype = f.datatype,
                      optional = !f.notNull,
                    ),
                  )
                case IncludeTableFields(_)   => Option.empty[StructField]
              }
            case None      => Seq.empty[StructField]
          }

        case f: StructFieldDefinition =>
          Seq(
            StructField(
              fieldName = f.fieldName,
              name = f.name,
              description = f.description,
              labels = labelsToMap(f.labels),
              datatype = f.datatype,
              optional = f.optional,
            ),
          )
      }
    }
    expFields.map { f =>
      f.datatype match {
        case DataElementDatatype(dataElementId)      =>
          val maybeDataElement = dataElementMap.get(dataElementId)
          val name             = maybeDataElement
            .map(dataElement => if (f.name.trim.isEmpty) dataElement.name else f.name)
            .getOrElse(f.name)
          val description      = maybeDataElement
            .map(dataElement => if (f.description.trim.isEmpty) dataElement.description else f.description)
            .getOrElse(f.description)
          f.copy(name = name, description = description)
        case StructDatatype(structId, relation)      =>
          val maybeStruct = structMap.get(structId)
          val name        = maybeStruct
            .map(struct => if (f.name.trim.isEmpty) struct.name else f.name)
            .getOrElse(f.name)
          val f1          = f.copy(name = name)
          if (relation == Some(DeriveRelation)) {
            val associatedTableId = structDef.likeTable.get.tableId
            val associatedTable   = tableMap(associatedTableId)
            val linkedStruct      = structMap(structId)
            val linkedTableId     = linkedStruct.likeTable.get.tableId
            val ownRelation       = associatedTable.relations.find(r => r.referenceTableId == linkedTableId)
            ownRelation.map { r =>
              f1.copy(datatype = StructDatatype(structId, Some(OwnRelation(r.id))))
            }.getOrElse {
              val linkedTable   = tableMap(linkedTableId)
              val assocRelation = linkedTable.relations.find(r => r.referenceTableId == associatedTableId)
              assocRelation.map { r =>
                f1.copy(datatype = StructDatatype(structId, Some(AssociatedRelation(r.id))))
              }.getOrElse(f1)
            }
          } else f1
        case StructArrayDatatype(structId, relation) =>
          val maybeStruct = structMap.get(structId)
          val name        = maybeStruct
            .map(struct => if (f.name.trim.isEmpty) struct.name else f.name)
            .getOrElse(f.name)
          val f1          = f.copy(name = name)
          if (relation == Some("")) {
            val associatedTableId = structDef.likeTable.get.tableId
            val linkedStruct      = structMap(structId)
            val linkedTableId     = linkedStruct.likeTable.get.tableId
            val linkedTable       = tableMap(linkedTableId)
            val newRelation       = linkedTable.relations.find(r => r.referenceTableId == associatedTableId)
            newRelation
              .map(r => f1.copy(datatype = StructArrayDatatype(structId, Some(r.id))))
              .getOrElse(f1)
          } else f1
        case _                                       =>
          f
      }
    }
  }

  private def labelsToMap(labels: Seq[Label]): Labels =
    labels.groupBy(_.key).map { case k -> v => k -> v.head._2 }

}
