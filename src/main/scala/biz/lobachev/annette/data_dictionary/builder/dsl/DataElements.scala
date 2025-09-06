package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.*
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.*

trait DataElements {

  def dataElement(
    id: String,
    fieldName: String,
    dataType: DataType,
    name: String,
    notNull: Boolean = true,
  ): DataElement =
    DataElement(
      id = id.trim.pascalCase,
      name = name.trim,
      fieldName = fieldName.trim.camelCase,
      dbFieldName = fieldName.trim.snakeCase,
      dataType = dataType,
      notNull = notNull,
    )

  def dataElementDb(
    id: String,
    fieldName: String,
    dbFieldName: String,
    dataType: DataType,
    name: String,
    notNull: Boolean = true,
  ): DataElement =
    DataElement(
      id = id.trim.pascalCase,
      name = name.trim,
      fieldName = fieldName.trim.camelCase,
      dbFieldName = dbFieldName.trim.snakeCase,
      dataType = dataType,
      notNull = notNull,
    )

}
