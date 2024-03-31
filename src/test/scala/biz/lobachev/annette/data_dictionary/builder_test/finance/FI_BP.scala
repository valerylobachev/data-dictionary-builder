package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model.StringVarchar

trait FI_BP {

  val businessPartnersGroup = component("FI-BP", "Business Partners")
    .withSchema("bp")
    .withLabels(
      javaModelPackage("finance.data.fi.bp.model"),
      javaRepoPackage("finance.data.fi.bp"),
    )
    .withDataElements(
      dataElement("CreditorId", "creditorId", StringVarchar(10), "Creditor id"),
      dataElement("DebtorId", "debtorId", StringVarchar(10), "Debtor id"),
      dataElement("BusinessPartnerId", "businessPartnerId", StringVarchar(10), "Business partner id"),
    )
    .withEntities(
      // ***************************** Creditor *****************************
      tableEntity("Creditor", "Creditor", "Creditor")
        .withPK(
          "id" :# "CreditorId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      // ***************************** Debtor *****************************
      tableEntity("Debtor", "Debtor", "Debtor")
        .withPK(
          "id" :# "DebtorId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      // ***************************** BusinessPartner *****************************
      tableEntity("BusinessPartner", "Business partner", "BusinessPartner")
        .withPK(
          "id" :# "BusinessPartnerId",
        )
        .withFields(
          // format: off
          "name"  :# "Name",
          "creditorId"  :#? "CreditorId",
          "debtorId"    :#? "DebtorId",
          include("Modification")
          // format: on
        )
        .withRelations(
          oneToOneRelation("creditorId", "Reference to Creditor", "Creditor", "creditorId" -> "id"),
          oneToOneRelation("debtorId", "Reference to Debtor", "Debtor", "debtorId"         -> "id"),
        )
        .withIndexes(
          uniqueIndex("creditorId", "Unique creditorId", "creditorId"),
          uniqueIndex("debtorId", "Unique debtorId", "debtorId"),
        ),
    )

}
