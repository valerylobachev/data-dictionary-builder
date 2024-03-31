package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model._

trait BC {

  val bcComponent = component("BC", "Shared data structures")
    .withSchema("bc")
    .withLabels(
      javaModelPackage("finance.data.bc.model"),
      javaRepoPackage("finance.data.bc"),
    )
    .withDataElements(
      dataElement("UserId", "userId", StringVarchar(20), "User Id"),
      dataElement("Name", "name", StringVarchar(100), "Name"),
      dataElement("ShortName", "shortName", StringVarchar(20), "Short name"),
    )
    .withEntities(
      embeddedEntity("Modification", "Modification data structure")
        .withFields(
          // format: off
          "createdBy" :# "UserId"             :@ "User created record",
          "createdAt"       :# InstantTimestamp()   :@ "Timestamp of record create",
          "updatedBy"       :# "UserId"             :@ "User updated record",
          "updatedAt"       :# InstantTimestamp()   :@ "Timestamp of record update"
          // format: on
        ),
    )
}
