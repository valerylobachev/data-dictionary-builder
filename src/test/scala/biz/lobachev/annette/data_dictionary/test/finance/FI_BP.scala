package biz.lobachev.annette.data_dictionary.test.finance

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Audit.audit
import biz.lobachev.annette.data_dictionary.labels.Go.{goStructPackage, goStructSourceFile, goTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Java.{javaStructPackage, javaTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Postgres.postgresSchema

trait FI_BP {

  val businessPartnersGroup = component("FI-BP", "Business Partners")
    .withLabels(
      postgresSchema("bp"),
      javaStructPackage("finance.data.fi.bp.model"),
      javaTablePackage("finance.data.fi.bp"),
      goTablePackage("github.com/valerylobachev/finance/logic/repository/entity/fi/bp"),
      goStructPackage("github.com/valerylobachev/finance/logic/data_model/fi/bp"),
      audit("bp_audit"),
    )
    .withDataElements(
      dataElement("CreditorId", "creditorId", varchar(10), "Creditor id"),
      dataElement("DebtorId", "debtorId", varchar(10), "Debtor id"),
      dataElement("BusinessPartnerId", "businessPartnerId", varchar(10), "Business partner id"),
    )
    .withTables(
      // ***************************** Creditor *****************************
      table("Creditor", "Creditor", "Creditor")
        .withPK(
          "id" :# "CreditorId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      // ***************************** Debtor *****************************
      table("Debtor", "Debtor", "Debtor")
        .withPK(
          "id" :# "DebtorId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      // ***************************** BusinessPartner *****************************
      table("BusinessPartner", "Business partner", "BusinessPartner")
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
          oneToOne("creditorId", "Reference to Creditor", "Creditor", "creditorId" -> "id")
            .withAssociation("creditor" -> "businessPartner"),
          oneToOne("debtorId", "Reference to Debtor", "Debtor", "debtorId" -> "id")
            .withAssociation("debitor"  -> "businessPartner"),
        )
        .withIndexes(
          uniqueIndex("creditorId", "Unique creditorId", "creditorId"),
          uniqueIndex("debtorId", "Unique debtorId", "debtorId"),
        ),
    )
    .withLabeledStructs(
      goStructSourceFile("creditor"),
    )(
      struct("CreditorId", "Creditor Id")
        .likeTablePK("Creditor"),
      struct("CreditorDto", "Creditor DTO")
        .likeTable("Creditor"),
      struct("CreateCreditorDto", "Create creditor DTO")
        .likeTable("Creditor", excluding("updatedAt")),
      struct("CreateCreditorGwDto", "Create creditor API Gateway DTO")
        .likeTable("Creditor", excluding("updatedBy", "updatedAt")),
      struct("UpdateCreditorDto", "Update creditor DTO")
        .likeTable("Creditor", excluding("updatedAt")),
      struct("UpdateCreditorGwDto", "Update creditor API Gateway DTO")
        .likeTable("Creditor", excluding("updatedBy", "updatedAt")),
      struct("DeleteCreditorDto", "Delete creditor DTO")
        .likeTable("Creditor", including("id", "updatedBy")),
      struct("DeleteCreditorGwDto", "Delete creditor API Gateway DTO")
        .likeTable("Creditor", including("id")),
      struct("CreditorFindQuery", "Find creditors")
        .withFields(
          "page" :## integer :@ "page",
          "size" :## integer :@ "size",
          "sortBy" :## structArray("SortBy") :@ "sort by array",
          "withData" :## bool :@ "with data",
          "filter" :##? string :@ "filter string",
          "includeIds" :## array(string) :@ "include ids",
          "excludeIds" :## array(string) :@ "exclude ids",
        ),
      struct("CreditorFindResult", "Creditor find result")
        .withFields(
          "total" :## long :@ "total",
          "hits" :## structArray("CreditorDto") :@ "hits",
        ),
    )
    .withLabeledStructs(
      goStructSourceFile("debtor"),
    )(
      struct("DebtorId", "Debtor Id")
        .likeTablePK("Debtor"),
      struct("DebtorDto", "Debtor DTO")
        .likeTable("Debtor"),
      struct("CreateDebtorDto", "Create debtor DTO")
        .likeTable("Debtor", excluding("updatedAt")),
      struct("CreateDebtorGwDto", "Create debtor API Gateway DTO")
        .likeTable("Debtor", excluding("updatedBy", "updatedAt")),
      struct("UpdateDebtorDto", "Update debtor DTO")
        .likeTable("Debtor", excluding("updatedAt")),
      struct("UpdateDebtorGwDto", "Update debtor API Gateway DTO")
        .likeTable("Debtor", excluding("updatedBy", "updatedAt")),
      struct("DeleteDebtorDto", "Delete debtor DTO")
        .likeTable("Debtor", including("id", "updatedBy")),
      struct("DeleteDebtorGwDto", "Delete debtor API Gateway DTO")
        .likeTable("Debtor", including("id")),
      struct("DebtorFindQuery", "Find debtors")
        .withFields(
          "page" :## integer :@ "page",
          "size" :## integer :@ "size",
          "sortBy" :## structArray("SortBy") :@ "sort by array",
          "withData" :## bool :@ "with data",
          "filter" :##? string :@ "filter string",
          "includeIds" :## array(string) :@ "include ids",
          "excludeIds" :## array(string) :@ "exclude ids",
        ),
      struct("DebtorFindResult", "Debtor find result")
        .withFields(
          "total" :## long :@ "total",
          "hits" :## structArray("DebtorDto") :@ "hits",
        ),
    )
    .withLabeledStructs(
      goStructSourceFile("business_partner"),
    )(
      struct("BusinessPartnerDto", "BusinessPartner DTO")
        .likeTable("BusinessPartner", excluding("creditorId", "debtorId"))
        .withFields(
          "creditor" :## linkedStruct("CreditorDto") :@ "creditor",
          "debtor" :## linkedStruct("DebtorDto") :@ "debtor",
        ),
    )

}
