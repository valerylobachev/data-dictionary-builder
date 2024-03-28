package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.model._

trait Shared {

  val sharedGroup = component("Shared", "Shared data structures")
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
