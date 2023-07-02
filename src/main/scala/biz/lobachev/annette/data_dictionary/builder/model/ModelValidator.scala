package biz.lobachev.annette.data_dictionary.builder.model

trait ModelValidator {

  self: Domain =>

  def validate(): Seq[String] =
    validateGroups() ++ validateFields() ++ validateIndexes() ++ validateRelations() ++ validateDataElements()

  def validateGroups(): Seq[String] = {
    val res = for {
      entity <- entities.values
      err    <- groups
                  .get(entity.groupId)
                  .map(_ => Seq.empty)
                  .getOrElse(Seq(s"Entity ${entity.id}: group ${entity.groupId} not found"))
    } yield err
    res.toSeq
  }

  def validateFields(): Seq[String] = {
    val res = for {
      entity <- entities.values
      field  <- entity.fields
      source  = s"Entity ${entity.id}, field ${field.fieldName}"
      err    <- validateDataType(field.dataType, source)
    } yield err
    res.toSeq
  }

  def validateDataElements(): Seq[String] = {
    val res = for {
      dataElement <- dataElements.values
      source       = s"Entity ${dataElement.id}"
      err         <- validateDataType(dataElement.dataType, source)
    } yield err
    res.toSeq
  }

  def validateDataType(dataType: DataType, source: String): Seq[String] =
    dataType match {
      case EmbeddedEntityType(entityId, _, _) if entities.get(entityId).isEmpty         =>
        Seq(s"$source: embedded entity $entityId not found")
      case ObjectType(entityId) if entities.get(entityId).isEmpty                    =>
        Seq(s"$source: object entity $entityId not found")
      case DataElementType(dataElementId) if dataElements.get(dataElementId).isEmpty =>
        Seq(s"$source: data element $dataElementId not found")
      case EnumString(enumId, _) if enums.get(enumId).isEmpty                        =>
        Seq(s"$source: enum $enumId not found")
      case ListCollection(dataType)                                                  =>
        validateDataType(dataType, source)
      case SetCollection(dataType)                                                   =>
        validateDataType(dataType, source)
      case StringMapCollection(dataType)                                             =>
        validateDataType(dataType, source)
      case _                                                                         =>
        Seq.empty
    }

  def validateIndexes(): Seq[String] = {
    val res = for {
      entity    <- entities.values
      index     <- entity.indexes.values
      fieldName <- index.fields
      err       <- entity.fields
                     .find(f => f.fieldName == fieldName)
                     .map(_ => None)
                     .getOrElse(Some(s"Entity ${entity.id}, index ${index.id}: field ${fieldName} not found"))

    } yield err
    res.toSeq
  }

  def validateRelations(): Seq[String] =
    entities.values.flatMap { entity =>
      entity.relations.flatMap(relation =>
        entities
          .get(relation.referenceEntityId)
          .map { referenceEntity =>
            relation.fields.flatMap {
              case f1 -> f2 =>
                entity.fields
                  .find(f => f.fieldName == f1)
                  .map(_ => Seq.empty)
                  .getOrElse(
                    Seq(
                      s"Entity ${entity.id}, relation ${relation.id}, mapping $f1 -> $f2: field $f1 not found in entity ${entity.id}"
                    )
                  ) ++
                  referenceEntity.fields
                    .find(f => f.fieldName == f2)
                    .map(_ => Seq.empty)
                    .getOrElse(
                      Seq(
                        s"Entity ${referenceEntity.id}, relation ${relation.id}, mapping $f1 -> $f2: field $f2 not found in entity ${referenceEntity.id}"
                      )
                    )
            }
          }
          .getOrElse(
            Seq(
              s"Entity ${entity.id}, relation ${relation.id}: reference entity ${relation.referenceEntityId} not found"
            )
          )
      )
    }.toSeq

}
