package biz.lobachev.annette.data_dictionary.builder

import biz.lobachev.annette.data_dictionary.dsl.*
import biz.lobachev.annette.data_dictionary.model.*

sealed trait DomainObject
case object IncludeObject     extends DomainObject
case object DataElementObject extends DomainObject
case object TableObject       extends DomainObject
case object StructObject      extends DomainObject

trait PreValidation extends ValidationUtils {
  def preValidate(domainDef: DomainDef): Seq[Message] =
    domainLabelDuplicates(domainDef) ++
      componentIdDuplicates(domainDef) ++
      componentLabelDuplicates(domainDef) ++
      includeIdDuplicates(domainDef) ++
      validateIncludes(domainDef) ++
      validateIncludeCycles(domainDef) ++
      enumIdDuplicates(domainDef) ++
      validateEnums(domainDef) ++
      dataElementIdDuplicates(domainDef) ++
      validateDataElements(domainDef) ++
      tableIdDuplicates(domainDef) ++
      tableEntityNameDuplicates(domainDef) ++
      tableLabelDuplicates(domainDef) ++
      validateTables(domainDef) ++
      structIdDuplicates(domainDef) ++
      structNameDuplicates(domainDef) ++
      structLabelDuplicates(domainDef) ++
      validateStructs(domainDef)

  private def domainLabelDuplicates(domainDef: DomainDef) =
    findDuplicates(
      domainDef.labels.map(l => l.key -> l),
    ).keys
      .map(k => Message.errorMessage("component: <root>", s"duplicate label $k"))
      .toSeq

  private def componentLabelDuplicates(domainDef: DomainDef) =
    domainDef.components.flatMap { e =>
      findDuplicates(
        e.labels.map(l => l.key -> l),
      ).keys.map { k =>
        val comp = e.componentId.getOrElse("<root>")
        val en   = e.id
        Message.errorMessage(s"parent component: $comp, component: $en", s"duplicate label $k")
      }
    }

  private def tableLabelDuplicates(domainDef: DomainDef) =
    domainDef.tables.flatMap { e =>
      findDuplicates(
        e.labels.map(l => l.key -> l),
      ).keys.map { k =>
        val comp = e.componentId.getOrElse("<root>")
        val en   = e.id
        Message.errorMessage(s"component: $comp, table: $en", s"duplicate label $k")
      }
    }

  private def structLabelDuplicates(domainDef: DomainDef) =
    domainDef.structs.flatMap { e =>
      findDuplicates(
        e.labels.map(l => l.key -> l),
      ).keys.map { k =>
        val comp = e.componentId.getOrElse("<root>")
        val en   = e.id
        Message.errorMessage(s"component: $comp, struct: $en", s"duplicate label $k")
      }
    }

  private def includeIdDuplicates(domainDef: DomainDef) =
    findDuplicates(
      domainDef.includes.map(l => l.id -> l),
    ).map { case k -> seq =>
      val components = seq.map(_.componentId.getOrElse("<root>")).toSet.mkString(", ")
      Message.errorMessage("component: <root>", s"duplicate include $k in components $components")
    }.toSeq

  private def enumIdDuplicates(domainDef: DomainDef) =
    findDuplicates(
      domainDef.enums.map(l => l.id -> l),
    ).map { case k -> seq =>
      val components = seq.map(_.componentId.getOrElse("<root>")).toSet.mkString(", ")
      Message.errorMessage("component: <root>", s"duplicate enum $k in components $components")
    }.toSeq

  private def dataElementIdDuplicates(domainDef: DomainDef) =
    findDuplicates(
      domainDef.dataElements.map(l => l.id -> l),
    ).map { case k -> seq =>
      val components = seq.map(_.componentId.getOrElse("<root>")).toSet.mkString(", ")
      Message.errorMessage("component: <root>", s"duplicate data element $k in components $components")
    }.toSeq

  private def tableIdDuplicates(domainDef: DomainDef) =
    findDuplicates(
      domainDef.tables.map(l => l.id -> l),
    ).map { case k -> seq =>
      val components = seq.map(_.componentId.getOrElse("<root>")).toSet.mkString(", ")
      Message.errorMessage("component: <root>", s"duplicate table $k in components $components")
    }.toSeq

  private def tableEntityNameDuplicates(domainDef: DomainDef) =
    findDuplicates(
      domainDef.tables.map(l => l.entityName -> l),
    ).flatMap { case k -> seq =>
      seq.map { t =>
        val comp  = t.componentId.getOrElse("<root>")
        val table = t.id
        Message.errorMessage(s"component: $comp, table: $table", s"duplicate table entity name $k")
      }
    }.toSeq

  private def structIdDuplicates(domainDef: DomainDef) =
    findDuplicates(
      domainDef.structs.map(l => l.id -> l),
    ).map { case k -> seq =>
      val components = seq.map(_.componentId.getOrElse("<root>")).toSet.mkString(", ")
      Message.errorMessage("component: <root>", s"duplicate struct $k in components $components")
    }.toSeq

  private def structNameDuplicates(domainDef: DomainDef) =
    findDuplicates(
      domainDef.structs.map(l => l.structName -> l),
    ).flatMap { case k -> seq =>
      seq.map { t =>
        val comp   = t.componentId.getOrElse("<root>")
        val struct = t.id
        Message.errorMessage(s"component: $comp, struct: $struct", s"duplicate struct name $k")
      }
    }.toSeq

  private def componentIdDuplicates(domainDef: DomainDef) =
    findDuplicates(
      domainDef.components.map(l => l.id -> l),
    ).map { case k -> seq =>
      val components = seq.map(_.componentId.getOrElse("<root>")).toSet.mkString(", ")
      Message.errorMessage("component: <root>", s"duplicate component $k in components $components")
    }.toSeq

  private def validateIncludes(domainDef: DomainDef) = {
    val map = domainDef.includes.groupBy(_.id).map { case k -> v => k -> v.head }
    domainDef.includes.flatMap { e =>
      val comp                = e.componentId.getOrElse("<root>")
      val en                  = e.id
      val fieldNames          = extractFieldNamesFromInclude(Seq.empty, e, map)
      val duplicateFieldNames = findDuplicatedStrings(fieldNames.map(_._1)).map { list =>
        val fields = list.mkString(", ")
        Message.errorMessage(s"component: $comp, include: $en", s"duplicate field names $fields")
      }.toSeq

      val duplicateDbFieldNames = findDuplicatedStrings(fieldNames.map(_._2)).map { list =>
        val fields = list.mkString(", ")
        Message.errorMessage(s"component: $comp, include: $en", s"duplicate db field names $fields")
      }.toSeq

      val datatypeValidation  = e.fields.flatMap {
        case f: TableFieldDefinition =>
          validateDatatype(f.datatype, domainDef, IncludeObject).map(text =>
            Message.errorMessage(s"component: $comp, include: $en, field: ${f.fieldName}", text),
          )
        case _                       => Seq.empty
      }
      val nonExistingIncludes = e.fields.flatMap {
        case IncludeTableFields(includeId) if map.get(includeId).isEmpty =>
          Seq(
            Message.errorMessage(s"component: $comp, include: $en", s"include $includeId not found"),
          )
        case _                                                           => Seq.empty
      }
      duplicateFieldNames ++ duplicateDbFieldNames ++ datatypeValidation ++ nonExistingIncludes
    }
  }

  private def extractFieldNamesFromInclude(
    path: Seq[String],
    inc: IncludeDef,
    incMap: Map[String, IncludeDef],
  ): Seq[(String, String)] =
    inc.fields.flatMap { f =>
      f match {
        case t: TableFieldDefinition                                    => Seq((t.fieldName, t.dbFieldName))
        case IncludeTableFields(includeId) if !path.contains(includeId) =>
          incMap
            .get(includeId)
            .map(i => extractFieldNamesFromInclude(path :+ includeId, i, incMap))
            .getOrElse(Seq.empty)
        case _                                                          => Seq.empty
      }
    }

  private def validateIncludeCycles(domainDef: DomainDef) = {
    val includeMap = domainDef.includes.map { inc =>
      val subIncludes = inc.fields.flatMap {
        case IncludeTableFields(includeId) => Some(includeId)
        case _                             => None
      }
      inc.id -> subIncludes
    }.filter(_._2.nonEmpty).toMap

    domainDef.includes
      .filter(inc => includeMap.get(inc.id).nonEmpty)
      .flatMap { inc =>
        val comp = inc.componentId.getOrElse("<root>")
        checkCycle(Seq.empty, inc.id, includeMap).map { path =>
          Message.errorMessage(s"component: $comp, include: ${inc.id}", s"include contain cycle: ${path}")
        }
      }
  }

  private def checkCycle(path: Seq[String], include: String, includeMap: Map[String, Seq[String]]): Seq[String] =
    if (path.contains(include)) {
      Seq((path :+ include).mkString(" -> "))
    } else {
      val includes = includeMap.getOrElse(include, Seq.empty)
      includes.flatMap { newInc =>
        checkCycle(path :+ include, newInc, includeMap)
      }
    }

  private def validateEnums(domainDef: DomainDef) =
    domainDef.enums.flatMap { e =>
      val comp = e.componentId.getOrElse("<root>")
      val en   = e.id

      val duplicateLabel = findDuplicates(
        e.labels.map(l => l.key -> l),
      ).keys.map { k =>
        Message.errorMessage(s"component: $comp, enum: $en", s"duplicate label $k")
      }.toSeq

      val duplicateElementId = findDuplicates(e.elements.map(l => l.id -> l)).map { case k -> seq =>
        Message.errorMessage(s"component: $comp, enum: $en", s"duplicate enum element id $k")
      }.toSeq

      val duplicateElementConstName = findDuplicates(e.elements.map(l => l.constName -> l)).map { case k -> seq =>
        Message.errorMessage(s"component: $comp, enum: $en", s"duplicate enum element constant name $k")
      }.toSeq

      val invalidLength = if (e.enumType != IntEnum && e.length < 1) {
        Seq(Message.errorMessage(s"component: $comp, enum: $en", s"invalid length ${e.length}"))
      } else Seq.empty

      val nonNumericElement = if (e.enumType == IntEnum) {
        val ids = e.elements.filter(_.id.toIntOption == None).map(_.id).mkString(", ")
        Seq(Message.errorMessage(s"component: $comp, enum: $en", s"non numeric element ids $ids in intEnum"))
      } else Seq.empty

      val invalidLengthElement = if (e.enumType != IntEnum) {
        val ids = e.elements.filter(_.id.length > e.length).map(_.id).mkString(", ")
        if (ids.nonEmpty) {
          Seq(Message.errorMessage(s"component: $comp, enum: $en", s"element ids $ids are too long"))
        } else Seq.empty
      } else Seq.empty

      duplicateLabel ++ duplicateElementId ++ duplicateElementConstName ++ invalidLength ++ nonNumericElement ++ invalidLengthElement
    }

  private def validateDataElements(domainDef: DomainDef) =
    domainDef.dataElements.flatMap { e =>
      val comp           = e.componentId.getOrElse("<root>")
      val en             = e.id
      val duplicateLabel = findDuplicates(
        e.labels.map(l => l.key -> l),
      ).keys.map { k =>
        Message.errorMessage(s"component: $comp, data element: $en", s"duplicate label $k")
      }.toSeq

      val datatypeValidation =
        validateDatatype(e.datatype, domainDef, DataElementObject).map(text =>
          Message.errorMessage(s"component: $comp, data element: $en", text),
        )

      duplicateLabel ++ datatypeValidation
    }

  private def validateTables(domainDef: DomainDef) =
    domainDef.tables.flatMap { e =>
      val comp            = e.componentId.getOrElse("<root>")
      val en              = e.id
      val emptyEntityName = if (e.entityName.isEmpty) {
        Seq(Message.errorMessage(s"component: $comp, table: $en", s"empty entity name"))
      } else Seq.empty

      val emptyTableName = if (e.tableName.isEmpty) {
        Seq(Message.errorMessage(s"component: $comp, table: $en", s"empty table name"))
      } else Seq.empty

      val duplicateTableIndexIds    = tableIndexIdDuplicates(e)
      val duplicateTableRelationIds = tableRelationIdDuplicates(e)
      val tableFieldValidation      = validateTableFields(e, domainDef)
      val tableIndexLabelDuplicates = validateTableIndexLabelDuplicates(e)
      val tableRelationValidation   = validateTableRelations(e, domainDef)

      emptyEntityName ++
        emptyTableName ++
        duplicateTableIndexIds ++
        duplicateTableRelationIds ++
        tableFieldValidation ++
        tableIndexLabelDuplicates ++
        tableRelationValidation
    }

  private def tableIndexIdDuplicates(tableDef: TableDef) =
    findDuplicatedStrings(
      tableDef.indexes.map(_.id),
    ).map { case seq =>
      val component = tableDef.componentId.getOrElse("<root>")
      val ids       = seq.mkString(", ")
      Message.errorMessage(
        s"component: $component, table: ${tableDef.id}",
        s"duplicate table index ids $ids",
      )
    }.toSeq

  private def tableRelationIdDuplicates(tableDef: TableDef) =
    findDuplicatedStrings(
      tableDef.relations.map(_.id),
    ).map { case seq =>
      val component = tableDef.componentId.getOrElse("<root>")
      val ids       = seq.mkString(", ")
      Message.errorMessage(
        s"component: $component, table: ${tableDef.id}",
        s"duplicate table relation ids $ids",
      )
    }.toSeq

  private def validateTableFields(tableDef: TableDef, domainDef: DomainDef) = {
    val comp  = tableDef.componentId.getOrElse("<root>")
    val table = tableDef.id
    (tableDef.pk ++ tableDef.fields).flatMap {
      case IncludeTableFields(includeId) =>
        domainDef.includes
          .find(_.id == includeId)
          .map(_ => Seq.empty)
          .getOrElse(
            Seq(Message.errorMessage(s"component: $comp, table: $table", s"include $includeId not found")),
          )
      case f: TableFieldDefinition       =>
        val field          = f.fieldName
        val duplicateLabel = findDuplicatedStrings(
          f.labels.map(_._1),
        ).map { k =>
          val labels = k.mkString(", ")
          Message.errorMessage(s"component: $comp, table: $table, field: $field", s"duplicate labels $labels")
        }.toSeq

        val emptyFieldName = if (f.fieldName.isEmpty) {
          Seq(Message.errorMessage(s"component: $comp, table: $table", s"table has empty field name"))
        } else Seq.empty

        val emptyDbFieldName = if (f.dbFieldName.isEmpty) {
          Seq(Message.errorMessage(s"component: $comp, table: $table", s"table has empty db field name"))
        } else Seq.empty

        val datatypeValidation =
          validateDatatype(f.datatype, domainDef, TableObject).map(text =>
            Message.errorMessage(s"component: $comp, table: $table, field: $field", text),
          )

        duplicateLabel ++ emptyFieldName ++ emptyDbFieldName ++ datatypeValidation
    }
  }

  private def validateTableIndexLabelDuplicates(tableDef: TableDef) = {
    val comp  = tableDef.componentId.getOrElse("<root>")
    val table = tableDef.id
    tableDef.indexes.flatMap { indexDef =>
      val id = indexDef.id
      findDuplicatedStrings(
        indexDef.labels.map(_._1),
      ).map { k =>
        val labels = k.mkString(", ")
        Message.errorMessage(s"component: $comp, table: $table, index: $id", s"duplicate labels $labels")
      }.toSeq
    }
  }

  private def validateTableRelations(tableDef: TableDef, domainDef: DomainDef) = {
    val comp  = tableDef.componentId.getOrElse("<root>")
    val table = tableDef.id
    tableDef.relations.flatMap { r =>
      val id             = r.id
      val duplicateLabel = findDuplicatedStrings(
        r.labels.map(_._1),
      ).map { k =>
        val labels = k.mkString(", ")
        Message.errorMessage(s"component: $comp, table: $table, relation: $id", s"duplicate labels $labels")
      }.toSeq

      val nonExistingRelatedTable = domainDef.tables
        .find(_.id == r.referenceTableId)
        .map(_ => Seq.empty)
        .getOrElse(
          Seq(
            Message
              .errorMessage(
                s"component: $comp, table: $table, relation: $id",
                s"related table ${r.referenceTableId} not found",
              ),
          ),
        )

      duplicateLabel ++ nonExistingRelatedTable
    }
  }

  private def validateStructs(domainDef: DomainDef) =
    domainDef.structs.flatMap { e =>
      val comp            = e.componentId.getOrElse("<root>")
      val id              = e.id
      val emptyStructName = if (e.structName.isEmpty) {
        Seq(Message.errorMessage(s"component: $comp, struct: $id", s"empty struct name"))
      } else Seq.empty

      val nonExistentLikeTable = e.likeTable.map { lt =>
        domainDef.tables
          .find(_.id == lt.tableId)
          .map(_ => Seq.empty)
          .getOrElse(
            Seq(
              Message.errorMessage(s"component: $comp, struct: $id", s"like table ${lt.tableId} not found"),
            ),
          )
      }.getOrElse(Seq.empty)

      val structFieldValidation = validateStructFields(e, domainDef)

      emptyStructName ++
        nonExistentLikeTable ++
        structFieldValidation
    }

  private def validateStructFields(structDef: StructDef, domainDef: DomainDef) = {
    val comp     = structDef.componentId.getOrElse("<root>")
    val structId = structDef.id
    structDef.fields.flatMap {
      case IncludeStructFields(includeId) =>
        domainDef.includes
          .find(_.id == includeId)
          .map(_ => Seq.empty)
          .getOrElse(
            Seq(Message.errorMessage(s"component: $comp, struct: $structId", s"include $includeId not found")),
          )
      case f: StructFieldDefinition       =>
        val field          = f.fieldName
        val duplicateLabel = findDuplicatedStrings(
          f.labels.map(_._1),
        ).map { k =>
          val labels = k.mkString(", ")
          Message.errorMessage(
            s"component: $comp, struct: $structId, field: $field",
            s"duplicate labels $labels",
          )
        }.toSeq

        val emptyFieldName = if (f.fieldName.isEmpty) {
          Seq(Message.errorMessage(s"component: $comp, struct: $structId", s"table has empty field name"))
        } else Seq.empty

        val datatypeValidation =
          validateDatatype(f.datatype, domainDef, StructObject).map(text =>
            Message.errorMessage(s"component: $comp, struct: $structId, field: $field", text),
          )

        val structValidation = f.datatype match {
          case StructDatatype(structId, relation) if relation.nonEmpty      =>
            val cond1 = if (structDef.likeTable.isEmpty) {
              Some(
                Message.errorMessage(
                  s"component: $comp, struct: $structId, field: ${f.fieldName}",
                  s"linked struct in struct that is not like table",
                ),
              )
            } else None
            val cond2 = domainDef.structs.find(_.id == structId).flatMap { linkedStruct =>
              if (linkedStruct.likeTable.isEmpty) {
                Some(
                  Message.errorMessage(
                    s"component: $comp, struct: $structId, field: ${f.fieldName}",
                    s"struct ${linkedStruct.id} used in linked struct is not like table",
                  ),
                )
              } else None
            }
            val cond3 = if (relation == Some(DeriveRelation)) {
              val r: Option[Message] = (for {
                associatedTableId <- structDef.likeTable.map(_.tableId)
                associatedTable   <- domainDef.tables.find(_.id == associatedTableId)
                linkedStruct      <- domainDef.structs.find(_.id == structId)
                linkedTableId     <- linkedStruct.likeTable.map(_.tableId)
                linkedTable       <- domainDef.tables.find(_.id == linkedTableId)
              } yield {
                val associatedRelations = associatedTable.relations.filter(_.referenceTableId == linkedTableId)
                val linkedRelations     = linkedTable.relations.filter(_.referenceTableId == associatedTableId)
                if (
                  (associatedRelations.length == 1) ||
                  (associatedRelations.length != 1 && linkedRelations.length == 1)
                ) {
                  Option.empty[Message]
                } else {
                  Some(
                    Message.errorMessage(
                      s"component: $comp, struct: $structId, field: ${f.fieldName}",
                      s"cannot derive relation from tables $associatedTableId or $linkedTableId",
                    ),
                  )
                }
              }).flatten
              r.toSeq
            } else Seq.empty
            cond1.toSeq ++ cond2.toSeq ++ cond3
          case StructArrayDatatype(structId, relation) if relation.nonEmpty =>
            val cond1 = if (structDef.likeTable.isEmpty) {
              Some(
                Message.errorMessage(
                  s"component: $comp, struct: $structId, field: ${f.fieldName}",
                  s"linked struct in struct that is not like table",
                ),
              )
            } else None
            val cond2 = domainDef.structs.find(_.id == structId).flatMap { linkedStruct =>
              if (linkedStruct.likeTable.isEmpty) {
                Some(
                  Message.errorMessage(
                    s"component: $comp, struct: $structId, field: ${f.fieldName}",
                    s"struct ${linkedStruct.id} used in linked struct is not like table",
                  ),
                )
              } else None
            }
            val cond3 = if (relation == Some("")) {
              val r: Option[Message] = (for {
                associatedTableId <- structDef.likeTable.map(_.tableId)
                linkedStruct      <- domainDef.structs.find(_.id == structId)
                linkedTableId     <- linkedStruct.likeTable.map(_.tableId)
                linkedTable       <- domainDef.tables.find(_.id == linkedTableId)
              } yield {
                val relations = linkedTable.relations.filter(r =>
                  r.relationType == ManyToOne && r.referenceTableId == associatedTableId,
                )
                if (relations.length != 1) {
                  Some(
                    Message.errorMessage(
                      s"component: $comp, struct: $structId, field: ${f.fieldName}",
                      s"cannot derive relation from table ${linkedTable.id}",
                    ),
                  )

                } else Option.empty[Message]

              }).flatten
              r.toSeq
            } else Seq.empty
            cond1.toSeq ++ cond2.toSeq ++ cond3
          case _                                                            =>
            Seq.empty
        }

        duplicateLabel ++ emptyFieldName ++ datatypeValidation ++ structValidation
    }
  }

  private def validateDatatype(datatype: Datatype, domainDef: DomainDef, domainObject: DomainObject): Seq[String] =
    datatype match {
      case VarcharDatatype(length)                                   =>
        if (length < 1) {
          Seq(s"data type validation: invalid length $length")
        } else Seq.empty
      case CharDatatype(length)                                      =>
        if (length < 1) {
          Seq(s"data type validation: invalid length $length")
        } else Seq.empty
      case DecimalDatatype(precision, scale)                         =>
        Seq(
          if precision < 1 then Some(s"data type validation: invalid precision $precision") else None,
          if scale < 0 then Some(s"data type validation: invalid scale $scale") else None,
          if precision < scale then Some(s"data type validation: precision $precision is lower than scale $scale")
          else None,
        ).flatten
      case EnumDatatype(enumId)                                      =>
        if domainDef.enums.exists(_.id == enumId) then Seq.empty
        else Seq(s"data type validation: enum $enumId not found")
      case DataElementDatatype(dataElementId)                        =>
        if domainDef.dataElements.exists(_.id == dataElementId) then Seq.empty
        else Seq(s"data type validation: data element $dataElementId not found")
      case ArrayDatatype(datatype)                                   =>
        validateDatatype(datatype, domainDef, domainObject)
      case SetDatatype(datatype)                                     =>
        validateDatatype(datatype, domainDef, domainObject)
      case StringMapDatatype(datatype)                               =>
        validateDatatype(datatype, domainDef, domainObject)
      case StructDatatype(_, _) if domainObject != StructObject      =>
        Seq(s"struct data type allowed only in struct")
      case StructDatatype(structId, _)                               =>
        if domainDef.structs.exists(_.id == structId) then Seq.empty
        else Seq(s"data type validation: struct $structId not found")
      case StructArrayDatatype(_, _) if domainObject != StructObject =>
        Seq(s"struct array data type allowed only in struct")
      case StructArrayDatatype(structId, _)                          =>
        if domainDef.structs.exists(_.id == structId) then Seq.empty
        else Seq(s"data type validation: struct $structId not found")
      case _                                                         =>
        Seq.empty
    }

}
