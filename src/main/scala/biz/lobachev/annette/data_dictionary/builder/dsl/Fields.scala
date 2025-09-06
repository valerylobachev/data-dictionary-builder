package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

trait Fields {
  def field(
    fieldName: String,
    dataType: DataType,
    name: String,
    notNull: Boolean = true,
    autoIncrement: Boolean = false,
  ): EntityField =
    EntityField(
      name = name.trim,
      fieldName = fieldName.trim.camelCase,
      dbFieldName = fieldName.trim.snakeCase,
      dataType = dataType,
      notNull = notNull,
      autoIncrement = autoIncrement,
    )

  def fieldDb(
    fieldName: String,
    dbFieldName: String,
    dataType: DataType,
    name: String,
    notNull: Boolean = true,
    autoIncrement: Boolean = false,
  ): EntityField =
    EntityField(
      name = name.trim,
      fieldName = fieldName.trim.camelCase,
      dbFieldName = dbFieldName.trim.snakeCase,
      dataType = dataType,
      notNull = notNull,
      autoIncrement = autoIncrement,
    )

  sealed trait EntityFieldModificator

  case object Null extends EntityFieldModificator

  case object AutoInc extends EntityFieldModificator

  implicit class EntityFieldImplicit(entityField: EntityField) {

    def :&(modificator: EntityFieldModificator) =
      modificator match {
        case Null    => entityField.copy(notNull = false)
        case AutoInc => entityField.copy(autoIncrement = true)
      }

    def :>(dbFieldName: String) = entityField.copy(dbFieldName = dbFieldName.trim.snakeCase)

    def :@(name: String) = entityField.copy(name = name.trim)

    def :@@(description: String) = entityField.copy(description = description.trim)
  }

  implicit class DataTypeImplicit(fieldName: String) {
    def :#(dataElementId: String) = field(fieldName.trim, DataElementType(dataElementId.trim), "")

    def :#?(dataElementId: String) = field(fieldName.trim, DataElementType(dataElementId.trim), "", false)

    def :#++(dataElementId: String) = field(fieldName.trim, DataElementType(dataElementId.trim), "", true, true)

    def :#(dataType: DataType) = field(fieldName.trim, dataType, "")

    def :#?(dataType: DataType) = field(fieldName.trim, dataType, "", false)

    def :#++(dataType: DataType) = field(fieldName.trim, dataType, "", true, true)

  }

  def include(entityId: String)              = field("", EmbeddedEntityType(entityId.trim), "")
  def includeWithRelations(entityId: String) = field("", EmbeddedEntityType(entityId.trim, withRelations = true), "")

  def include(fieldName: String, entityId: String)              = field(fieldName.trim, EmbeddedEntityType(entityId.trim, true), "")
  def includeWithRelations(fieldName: String, entityId: String) =
    field(fieldName.trim, EmbeddedEntityType(entityId.trim, true, true), "")

}
