package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.*
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.*

trait Models {

  def model(id: String, name: String, modelName: String): DataModel =
    DataModel(
      id = id.trim.pascalCase,
      name = name.trim,
      modelName = modelName.trim.pascalCase,
    )

  def model(id: String, name: String): DataModel =
    DataModel(
      id = id.trim.pascalCase,
      name = name.trim,
      modelName = id.trim.pascalCase,
    )
}
