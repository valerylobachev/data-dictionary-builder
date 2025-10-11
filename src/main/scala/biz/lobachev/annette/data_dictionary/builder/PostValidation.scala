package biz.lobachev.annette.data_dictionary.builder

import biz.lobachev.annette.data_dictionary.model.*

trait PostValidation extends ValidationUtils with DomainMetaHelper {

  def postValidate(domain: Domain): Seq[Message] =
    tableNameDuplicates(domain) ++
      validateTables(domain) ++
      validateStructs(domain)

  private def tableNameDuplicates(domain: Domain) = {
    val seq = domain.tables.values.map { table =>
      val fullTableName = getFullTableName(table, domain).toDbName
      fullTableName -> table
    }.toSeq
    findDuplicates(seq).flatMap { case fullTableName -> tables =>
      tables.map { table =>
        val comp = table.componentId.getOrElse("<root>")
        Message.errorMessage(s"component: $comp, table: ${table.id}", s"duplicate db table name $fullTableName")
      }
    }.toSeq
  }

  private def validateTables(domain: Domain) =
    domain.tables.values.flatMap { table =>
      val comp                = table.componentId.getOrElse("<root>")
      val duplicateFieldNames = findDuplicatedStrings(table.fields.map(_.fieldName)).map { list =>
        val fields = list.mkString(", ")
        Message.errorMessage(s"component: $comp, table: ${table.id}", s"duplicate field names $fields")
      }.toSeq

      val duplicateDbFieldNames = findDuplicatedStrings(table.fields.map(_.dbFieldName)).map { list =>
        val fields = list.mkString(", ")
        Message.errorMessage(s"component: $comp, table: ${table.id}", s"duplicate db field names $fields")
      }.toSeq

      val indexValidation    = validateIndexes(table)
      val relationValidation = validateRelations(table, domain)

      duplicateFieldNames ++ duplicateDbFieldNames ++ indexValidation ++ relationValidation
    }

  private def validateIndexes(table: Table): Seq[Message] = {
    val comp = table.componentId.getOrElse("<root>")
    table.indexes.flatMap { index =>
      val nonExistingFields        = index.fields.flatMap { field =>
        table.fields.find(_.fieldName == field.fieldName) match {
          case None =>
            Seq(
              Message.errorMessage(
                s"component: $comp, table: ${table.id}, index: ${index.id}",
                s"index field $field not found",
              ),
            )
          case _    => Seq.empty
        }
      }
      val nonExistingIncludeFields = index.includeFields.flatMap { field =>
        table.fields.find(_.fieldName == field) match {
          case None =>
            Seq(
              Message.errorMessage(
                s"component: $comp, table: ${table.id}, index: ${index.id}",
                s"index include field $field not found",
              ),
            )
          case _    => Seq.empty
        }
      }
      nonExistingFields ++ nonExistingIncludeFields
    }
  }

  private def validateRelations(table: Table, domain: Domain): Seq[Message] = {
    val comp = table.componentId.getOrElse("<root>")
    table.relations.flatMap { relation =>
      val nonExistingFields    = relation.fields.flatMap { case field -> _ =>
        table.fields.find(_.fieldName == field) match {
          case None =>
            Seq(
              Message.errorMessage(
                s"component: $comp, table: ${table.id}, relation: ${relation.id}",
                s"relation field $field not found",
              ),
            )
          case _    => Seq.empty
        }
      }
      val referenceTable       = domain.tables(relation.referenceTableId)
      val nonExistingRefFields = relation.fields.flatMap { case _ -> field =>
        referenceTable.fields.find(_.fieldName == field) match {
          case None =>
            Seq(
              Message.errorMessage(
                s"component: $comp, table: ${table.id}, relation: ${relation.id}",
                s"relation reference field $field not found in reference table ${referenceTable.id}",
              ),
            )
          case _    => Seq.empty
        }
      }

      val incompatibleTypes = relation.fields.flatMap { case f1 -> f2 =>
        val field          = table.fields
          .find(f => f.fieldName == f1)
        val referenceField = referenceTable.fields
          .find(f => f.fieldName == f2)
        (
          for {
            f  <- field
            rf <- referenceField
          } yield
            if (f.datatype == rf.datatype) Seq.empty
            else
              Seq(
                Message.errorMessage(
                  s"component: $comp, table: ${table.id}, relation: ${relation.id}",
                  s"incompatible datatypes: $f1 (${f.datatype}) -> $f2 (${rf.datatype})",
                ),
              )
        ).getOrElse(Seq.empty)
      }

      nonExistingFields ++ nonExistingRefFields ++ incompatibleTypes
    }
  }

  private def validateStructs(domain: Domain) =
    domain.structs.values.flatMap { struct =>
      val comp                = struct.componentId.getOrElse("<root>")
      val duplicateFieldNames = findDuplicatedStrings(struct.fields.map(_.fieldName)).map { list =>
        val fields = list.mkString(", ")
        Message.errorMessage(s"component: $comp, struct: ${struct.id}", s"duplicate field names $fields")
      }.toSeq

      val likeTableInvalidFields = struct.likeTable.map { lt =>
        val tableFields = domain.tables(lt.tableId).fields.map(_.fieldName).toSet
        lt.fieldUsage match {
          case IncludeFields(fields) =>
            val diff = fields.toSet.diff(tableFields)
            if (diff.nonEmpty) {
              val f = diff.mkString(", ")
              Seq(
                Message.errorMessage(
                  s"component: $comp, struct: ${struct.id}",
                  s"like table include fields $f not found in table ${lt.tableId}",
                ),
              )
            } else Seq.empty
          case ExcludeFields(fields) =>
            val diff = fields.toSet.diff(tableFields)
            if (diff.nonEmpty) {
              val f = diff.mkString(", ")
              Seq(
                Message.errorMessage(
                  s"component: $comp, struct: ${struct.id}",
                  s"like table exclude fields $f not found in table ${lt.tableId}",
                ),
              )
            } else Seq.empty
          case _                     => Seq.empty
        }
      }.toSeq.flatten

      val linkedStructWithoutLikeTable = if (struct.likeTable.isEmpty) {
        struct.fields.flatMap { field =>
          field.datatype match {
            case StructDatatype(_, relation) if relation.nonEmpty      =>
              Seq(
                Message.errorMessage(
                  s"component: $comp, struct: ${struct.id}, field: ${field.fieldName}",
                  s"linked struct allowed only in struct that is like table",
                ),
              )
            case StructArrayDatatype(_, relation) if relation.nonEmpty =>
              Seq(
                Message.errorMessage(
                  s"component: $comp, struct: ${struct.id}, field: ${field.fieldName}",
                  s"linked struct array allowed only in struct that is like table",
                ),
              )
            case _                                                     =>
              Seq.empty
          }
        }
      } else Seq.empty

      val invalidLinkedStructs = struct.likeTable.map { lt =>
        val table = domain.tables(lt.tableId)
        struct.fields.flatMap { field =>
          field.datatype match {
            case StructDatatype(structId, Some(relation)) =>
              relation match {
                case OwnRelation(relationId)        =>
                  if (table.relations.find(_.id == relationId).isEmpty) {
                    Seq(
                      Message.errorMessage(
                        s"component: $comp, struct: ${struct.id}, field: ${field.fieldName}",
                        s"linked struct relation $relationId not found in like table ${table.id}",
                      ),
                    )
                  } else Seq.empty
                case AssociatedRelation(relationId) =>
                  val linkedStruct = domain.structs(structId)
                  val linkedTable = domain.tables(linkedStruct.likeTable.get.tableId)
                  if (linkedTable.relations.find(_.id == relationId).isEmpty) {
                    Seq(
                      Message.errorMessage(
                        s"component: $comp, struct: ${struct.id}, field: ${field.fieldName}",
                        s"linked struct relation $relationId not found in linked struct $structId that like table ${linkedTable.id}",
                      ),
                    )
                  } else Seq.empty
                case DeriveRelation                 =>
                  Seq(
                    Message.errorMessage(
                      s"component: $comp, struct: ${struct.id}, field: ${field.fieldName}",
                      s"cannot derive relation",
                    ),
                  )
              }

            case StructArrayDatatype(structId, Some(relationId)) if relationId != "" =>
              val linkedStruct = domain.structs(structId)
              val linkedTable = domain.tables(linkedStruct.likeTable.get.tableId)
              if (linkedTable.relations.find(_.id == relationId).isEmpty) {
                Seq(
                  Message.errorMessage(
                    s"component: $comp, struct: ${struct.id}, field: ${field.fieldName}",
                    s"linked struct array relation $relationId not found in linked struct $structId that like table ${linkedTable.id}",
                  ),
                )
              } else Seq.empty
            case _                                              =>
              Seq.empty
          }
        }
      }.getOrElse(Seq.empty)

      duplicateFieldNames ++ likeTableInvalidFields ++ linkedStructWithoutLikeTable ++ invalidLinkedStructs
    }

}
