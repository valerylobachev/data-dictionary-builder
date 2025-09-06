package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.*
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.*

trait Services {

  def service(id: String, name: String, serviceName: String): Repository =
    Repository(
      id = id.trim.pascalCase,
      name = name.trim,
      serviceName = serviceName.trim.pascalCase,
    )

  def service(id: String, name: String): Repository =
    Repository(
      id = id.trim.pascalCase,
      name = name.trim,
      serviceName = id.trim.pascalCase,
    )
}
