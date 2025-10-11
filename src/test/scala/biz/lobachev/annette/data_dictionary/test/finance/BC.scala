package biz.lobachev.annette.data_dictionary.test.finance

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Java.{javaStructPackage, javaTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Postgres.postgresSchema

trait BC {

  val bcComponent = component("BC", "Shared data structures")
    .withLabels(
      postgresSchema("bc"),
      javaStructPackage("finance.data.bc.model"),
      javaTablePackage("finance.data.bc"),
    )
    .withDataElements(
      // format: off
      "UserId"    :! "userId"    :# varchar(20)  :@ "User Id",
      "Name"      :! "name"      :# varchar(100) :@ "Name",
      "ShortName" :! "shortName" :# varchar(20)  :@ "Short name",
      // format: on
    )
    .withIncludes(
      includeDef("Modification", "Modification data structure")
        .withFields(
          // format: off
          "updatedBy"       :# "UserId"             :@ "User updated record",
          "updatedAt"       :# instant   :@ "Timestamp of record update" :== "CURRENT_TIMESTAMP"
          // format: on
        ),
    )
    .withStructs(
      struct("SortBy", "Sort by structure")
        .withFields(
          "field" :## string :@ "Field",
          "desc" :## bool :@ "Descending indicator",
        ),
    )
}
