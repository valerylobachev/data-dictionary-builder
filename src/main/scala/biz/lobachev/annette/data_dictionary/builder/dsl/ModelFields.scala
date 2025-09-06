package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.*
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.*

trait ModelFields {
  def modelField(
    fieldName: String,
    datatype: FieldDatatype,
    name: String,
    optional: Boolean = false,
  ): ModelField =
    ModelField(
      fieldName = fieldName.trim.camelCase,
      name = name.trim,
      datatype = datatype,
      optional = optional,
    )

  implicit class ModelFieldImplicit(modelField: ModelField) {
    def :@(name: String) = modelField.copy(name = name.trim)

    def :@@(description: String) = modelField.copy(description = description.trim)
  }

  implicit class FieldDatatypeImplicit(fieldName: String) {
    def :##(dataElementId: String) = modelField(fieldName.trim, DataElementField(dataElementId.trim), "")

    def :##?(dataElementId: String) = modelField(fieldName.trim, DataElementField(dataElementId.trim), "", true)

    def :#(datatype: FieldDatatype) = modelField(fieldName.trim, datatype, "")

    def :#?(datatype: FieldDatatype) = modelField(fieldName.trim, datatype, "", true)
  }

  def including(seq: String*) = IncludeFields(seq)

  def excluding(seq: String*) = ExcludeFields(seq)

}
