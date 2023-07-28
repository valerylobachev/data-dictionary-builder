package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model._

trait Ledger {

  val ledgerGroup = group("Ledger", "Ledger settings")
    .withSchema("ledger")
    .withAttributes(
      javaModelPackage("finance.data.ledger.model"),
      javaRepoPackage("finance.data.ledger"),
    )
    .withEntities(
      // ***************************** Ledger *****************************
      tableEntity("Ledger", "Ledger", "Ledger")
        .withPK("id" :# "LedgerId")
        .withFields(
          // format: off
          "name"        :#  "Name",
          "leading"           :#  BooleanBoolean() :@ "Leading ledger indicator",
          "ledgerType"        :#  "LedgerType",
          "underlyingLedger"  :#? "LedgerId"       :@ "Underlying ledger",
          include("Modification")
          // format: on
        )
        .withRelations(
          manyToOneRelation("underlyingLedger", "Reference to ledger", "Ledger", "underlyingLedger" -> "id"),
        ),
      tableEntity("LedgerGroup", "Ledger Group", "LedgerGroup")
        .withPK(
          "id" :# "LedgerGroupId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      tableEntity("LedgerGroupLedger", "Ledger assignments to ledger group", "LedgerGroupLedger")
        .withPK(
          // format: off
          "ledgerGroupId" :# "LedgerGroupId",
          "ledgerId"            :# "LedgerId"
          // format: on
        )
        .withFields(
          "representative" :# BooleanBoolean() :@ "Representative ledger indicator",
          include("Modification"),
        )
        .withRelations(
          manyToOneRelation("ledgerId", "Reference to ledger", "Ledger", "ledgerId"                      -> "id"),
          manyToOneRelation("ledgerGroupId", "Reference to ledger group", "LedgerGroup", "ledgerGroupId" -> "id"),
        ),
    )

}
