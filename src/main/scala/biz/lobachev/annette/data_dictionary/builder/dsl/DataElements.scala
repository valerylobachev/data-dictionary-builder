package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

trait DataElements {

  def dataElement(
                   id: String,
                   fieldName: String,
                   dataType: DataType,
                   name: String,
                   notNull: Boolean = true
                 ): DataElement =
    DataElement(
      id = id.pascalCase,
      name = name,
      fieldName = fieldName.camelCase,
      dbFieldName = fieldName.snakeCase,
      dataType = dataType,
      notNull = notNull
    )

  def dataElementDb(
                     id: String,
                     fieldName: String,
                     dbFieldName: String,
                     dataType: DataType,
                     name: String,
                     notNull: Boolean = true
                   ): DataElement =
    DataElement(
      id = id.pascalCase,
      name = name,
      fieldName = fieldName.camelCase,
      dbFieldName = dbFieldName.snakeCase,
      dataType = dataType,
      notNull = notNull
    )


}
