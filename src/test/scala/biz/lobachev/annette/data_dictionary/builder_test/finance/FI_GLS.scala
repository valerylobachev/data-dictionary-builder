package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.labels.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model._

trait FI_GLS {

  val glSettingsComponent = component("FI-GLS", "General Ledger settings")
    .withSchema("ledger")
    .withLabels(
      javaModelPackage("finance.data.fi.gls.model"),
      javaRepoPackage("finance.data.fi.gls"),
    )
    .withDataElements(
      dataElement("LedgerId", "ledgerId", StringVarchar(4), "Ledger Id"),
      dataElement("LedgerGroupId", "ledgerGroupId", StringVarchar(2), "Ledger Group Id"),
      dataElement("LedgerType", "ledgerType", Enum("LedgerType"), "Ledger Type"),
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
          manyToOne("underlyingLedger", "Reference to ledger", "Ledger", "underlyingLedger" -> "id"),
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
          manyToOne("ledgerId", "Reference to ledger", "Ledger", "ledgerId"                      -> "id"),
          manyToOne("ledgerGroupId", "Reference to ledger group", "LedgerGroup", "ledgerGroupId" -> "id"),
        ),
    )

}
