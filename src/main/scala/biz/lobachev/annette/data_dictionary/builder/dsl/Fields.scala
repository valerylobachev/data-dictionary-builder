package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{DataElementType, DataType, EmbeddedEntityType, EntityField}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

trait Fields {
  def field(
    fieldName: String,
    dataType: DataType,
    name: String,
    notNull: Boolean = true,
    autoIncrement: Boolean = false
  ): EntityField =
    EntityField(
      name = name,
      fieldName = fieldName.camelCase,
      dbFieldName = fieldName.snakeCase,
      dataType = dataType,
      notNull = notNull,
      autoIncrement = autoIncrement
    )

  def fieldDb(
    fieldName: String,
    dbFieldName: String,
    dataType: DataType,
    name: String,
    notNull: Boolean = true,
    autoIncrement: Boolean = false
  ): EntityField =
    EntityField(
      name = name,
      fieldName = fieldName.camelCase,
      dbFieldName = dbFieldName.snakeCase,
      dataType = dataType,
      notNull = notNull,
      autoIncrement = autoIncrement
    )

  sealed trait EntityFieldModificator

  case object Null extends EntityFieldModificator

  case object AutoInc extends EntityFieldModificator

  implicit class EntityFieldImplicit(entityField: EntityField) {

    def :&(modificator: EntityFieldModificator) =
      modificator match {
        case Null    => entityField.copy(notNull = false)
        case AutoInc => entityField.copy(autoIncrement = false)
      }

    def :>(dbFieldName: String) = entityField.copy(dbFieldName = dbFieldName.snakeCase)

    def :@(name: String) = entityField.copy(name = name)

    def :@@(description: String) = entityField.copy(description = description)
  }

  implicit class DataTypeImplicit(fieldName: String) {
    def :#(dataElementId: String) = field(fieldName, DataElementType(dataElementId), "")

    def :#?(dataElementId: String) = field(fieldName, DataElementType(dataElementId), "", false)

    def :#++(dataElementId: String) = field(fieldName, DataElementType(dataElementId), "", true, true)

    def :#(dataType: DataType) = field(fieldName, dataType, "")

    def :#?(dataType: DataType) = field(fieldName, dataType, "", false)

    def :#++(dataType: DataType) = field(fieldName, dataType, "", true, true)
  }

  def include(entityId: String)              = field("", EmbeddedEntityType(entityId), "")
  def includeWithRelations(entityId: String) = field("", EmbeddedEntityType(entityId, withRelations = true), "")

  def include(fieldName: String, entityId: String)              = field(fieldName, EmbeddedEntityType(entityId, true), "")
  def includeWithRelations(fieldName: String, entityId: String) =
    field(fieldName, EmbeddedEntityType(entityId, true, true), "")

}
