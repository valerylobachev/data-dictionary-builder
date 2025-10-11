package biz.lobachev.annette.data_dictionary.test.finance

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Go.{goStructPackage, goTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Java.{javaStructPackage, javaTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Postgres.postgresSchema

trait FI_GLS {

  val glSettingsComponent = component("FI-GLS", "General Ledger settings")
    .withLabels(
      postgresSchema("ledger"),
      javaStructPackage("finance.data.fi.gls.model"),
      javaTablePackage("finance.data.fi.gls"),
      goTablePackage("github.com/valerylobachev/finance/logic/repository/entity/fi/gls"),
      goStructPackage("github.com/valerylobachev/finance/logic/data_model/fi/gls"),
    )
    .withDataElements(
      dataElement("LedgerId", "ledgerId", varchar(4), "Ledger Id"),
      dataElement("LedgerGroupId", "ledgerGroupId", varchar(2), "Ledger Group Id"),
      dataElement("LedgerType", "ledgerType", enumeration("LedgerType"), "Ledger Type"),
    )
    .withTables(
      // ***************************** Ledger *****************************
      table("Ledger", "Ledger", "Ledger")
        .withPK("id" :# "LedgerId")
        .withFields(
          // format: off
          "name"        :#  "Name",
          "leading"           :#  bool :@ "Leading ledger indicator",
          "ledgerType"        :#  "LedgerType",
          "underlyingLedgerId"  :#? "LedgerId"       :@ "Underlying ledger",
          include("Modification")
          // format: on
        )
        .withRelations(
          manyToOne("underlyingLedger", "Reference to ledger", "Ledger", "underlyingLedgerId" -> "id")
            .withAssociation("underlyingLedger"),
        ),
      table("LedgerGroup", "Ledger Group", "LedgerGroup")
        .withPK(
          "id" :# "LedgerGroupId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      table("LedgerGroupLedger", "Ledger assignments to ledger group", "LedgerGroupLedger")
        .withPK(
          // format: off
          "ledgerGroupId" :# "LedgerGroupId",
          "ledgerId"            :# "LedgerId"
          // format: on
        )
        .withFields(
          "representative" :# bool :@ "Representative ledger indicator",
          include("Modification"),
        )
        .withRelations(
          manyToOne("ledgerId", "Reference to ledger", "Ledger", "ledgerId" -> "id")
            .withAssociation("ledger"      -> "ledgerGroupLedgers"),
          manyToOne("ledgerGroupId", "Reference to ledger group", "LedgerGroup", "ledgerGroupId" -> "id")
            .withAssociation("ledgerGroup" -> "ledgerGroupLedgers"),
        ),
    )

}
