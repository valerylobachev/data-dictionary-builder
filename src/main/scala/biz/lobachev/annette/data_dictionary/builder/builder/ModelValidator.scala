package biz.lobachev.annette.data_dictionary.builder.builder

import biz.lobachev.annette.data_dictionary.builder.model._

trait ModelValidator {

  def validate(domain: Domain): Option[Seq[String]] = {
    val errors = domain.errors ++
      validateComponents(domain) ++
      validateFields(domain) ++
      validateIndexes(domain) ++
      validateRelations(domain) ++
      validateDataElements(domain)
    if (errors.nonEmpty) Some(errors)
    else None
  }

  def validateComponents(domain: Domain): Seq[String] = {
    val res = for {
      entity <- domain.entities.values
      err    <- domain.components
                  .get(entity.componentId)
                  .map(_ => Seq.empty)
                  .getOrElse(Seq(s"Entity ${entity.id}: group ${entity.componentId} not found"))
    } yield err
    res.toSeq
  }

  def validateFields(domain: Domain): Seq[String] = {
    val res = for {
      entity <- domain.entities.values
      field  <- entity.fields
      source  = s"Entity ${entity.id}, field ${field.fieldName}"
      err    <- validateDataType(domain, field.dataType, source)
    } yield err
    res.toSeq
  }

  def validateDataElements(domain: Domain): Seq[String] = {
    val res = for {
      dataElement <- domain.dataElements.values
      source       = s"Entity ${dataElement.id}"
      err         <- validateDataType(domain, dataElement.dataType, source)
    } yield err
    res.toSeq
  }

  def validateDataType(domain: Domain, dataType: DataType, source: String): Seq[String] =
    dataType match {
      case EmbeddedEntityType(entityId, _, _) if domain.entities.get(entityId).isEmpty      =>
        Seq(s"$source: embedded entity $entityId not found")
      case ObjectType(entityId) if domain.entities.get(entityId).isEmpty                    =>
        Seq(s"$source: object entity $entityId not found")
      case DataElementType(dataElementId) if domain.dataElements.get(dataElementId).isEmpty =>
        Seq(s"$source: data element $dataElementId not found")
      case EnumString(enumId, _) if domain.enums.get(enumId).isEmpty                        =>
        Seq(s"$source: enum $enumId not found")
      case ListCollection(dataType)                                                         =>
        validateDataType(domain, dataType, source)
      case SetCollection(dataType)                                                          =>
        validateDataType(domain, dataType, source)
      case StringMapCollection(dataType)                                                    =>
        validateDataType(domain, dataType, source)
      case _                                                                                =>
        Seq.empty
    }

  def validateIndexes(domain: Domain): Seq[String] = {
    val res = for {
      entity    <- domain.entities.values
      index     <- entity.indexes
      fieldName <- index.fields
      err       <- entity.expandedFields
                     .find(f => f.fieldName == fieldName)
                     .map(_ => None)
                     .getOrElse(Some(s"Entity ${entity.id}, index ${index.id}: field ${fieldName} not found"))

    } yield err
    res.toSeq
  }

  def isDatatypesEqual(f: EntityField, rf: EntityField): Boolean = f.dataType == rf.dataType

  def validateRelations(domain: Domain): Seq[String] =
    domain.entities.values.flatMap { entity =>
      entity.relations.flatMap(relation =>
        domain.entities
          .get(relation.referenceEntityId)
          .map { referenceEntity =>
            relation.fields.flatMap { case f1 -> f2 =>
              val field          = entity.expandedFields
                .find(f => f.fieldName == f1)
              val referenceField = referenceEntity.expandedFields
                .find(f => f.fieldName == f2)

              val incompatibleTypes = (for {
                f  <- field
                rf <- referenceField
              } yield
                if (isDatatypesEqual(f, rf)) Seq.empty
                else
                  Seq(
                    s"Entity ${entity.id}, relation ${relation.id}, mapping $f1 -> $f2: incompatible datatypes (${f.dataType} != ${rf.dataType})",
                  )).getOrElse(Seq.empty)

              field
                .map(_ => Seq.empty)
                .getOrElse(
                  Seq(
                    s"Entity ${entity.id}, relation ${relation.id}, mapping $f1 -> $f2: field $f1 not found in entity ${entity.id}",
                  ),
                ) ++
                referenceField
                  .map(_ => Seq.empty)
                  .getOrElse(
                    Seq(
                      s"Entity ${referenceEntity.id}, relation ${relation.id}, mapping $f1 -> $f2: field $f2 not found in entity ${referenceEntity.id}",
                    ),
                  ) ++ incompatibleTypes

            }
          }
          .getOrElse(
            Seq(
              s"Entity ${entity.id}, relation ${relation.id}: reference entity ${relation.referenceEntityId} not found",
            ),
          ),
      )
    }.toSeq

}
