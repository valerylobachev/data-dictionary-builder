package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.builder.labels.Audit.audit
import biz.lobachev.annette.data_dictionary.builder.labels.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model.*

trait FI_BP {

  val businessPartnersGroup = component("FI-BP", "Business Partners")
    .withSchema("bp")
    .withLabels(
      javaModelPackage("finance.data.fi.bp.structEntity"),
      javaRepoPackage("finance.data.fi.bp"),
      audit("bp_audit"),
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
          oneToOne("creditorId", "Reference to Creditor", "Creditor", "creditorId" -> "id"),
          oneToOne("debtorId", "Reference to Debtor", "Debtor", "debtorId"         -> "id"),
        )
        .withIndexes(
          uniqueIndex("creditorId", "Unique creditorId", "creditorId"),
          uniqueIndex("debtorId", "Unique debtorId", "debtorId"),
        ),
      structEntity("CreditorId", "Creditor Id")
        .likeEntityPK("Creditor"),
      structEntity("CreditorDto", "Creditor DTO")
        .likeEntity("Creditor"),
      structEntity("CreateCreditorDto", "Create creditor DTO")
        .likeEntity("Creditor", excluding("updatedAt")),
      structEntity("CreateCreditorGwDto", "Create creditor API Gateway DTO")
        .likeEntity("Creditor", excluding("updatedBy", "updatedAt")),
      structEntity("UpdateCreditorDto", "Update creditor DTO")
        .likeEntity("Creditor", excluding("updatedAt")),
      structEntity("UpdateCreditorGwDto", "Update creditor API Gateway DTO")
        .likeEntity("Creditor", excluding("updatedBy", "updatedAt")),
      structEntity("DeleteCreditorDto", "Delete creditor DTO")
        .likeEntity("Creditor", including("id", "updatedBy")),
      structEntity("DeleteCreditorGwDto", "Delete creditor API Gateway DTO")
        .likeEntity("Creditor", including("id")),
      structEntity("CreditorFindQuery", "Find creditors")
        .withFields(
          "page" :# IntInt() :@ "page",
          "size" :# IntInt() :@ "size",
          "sortBy" :# ObjectArray("SortBy") :@ "sort by array",
          "withData" :# BooleanBoolean() :@ "with data",
          "filter" :#? StringText() :@ "filter string",
          "includeIds" :# ListCollection(StringText()) :@ "include ids",
          "excludeIds" :# ListCollection(StringText()) :@ "exclude ids",
        ),
      structEntity("CreditorFindResult", "Creditor find result")
        .withFields(
          "total" :# LongLong() :@ "total",
          "hits" :# ObjectArray("CreditorDto") :@ "hits",
        ),

      structEntity("DebtorId", "Debtor Id")
        .likeEntityPK("Debtor"),
      structEntity("DebtorDto", "Debtor DTO")
        .likeEntity("Debtor"),
      structEntity("CreateDebtorDto", "Create debtor DTO")
        .likeEntity("Debtor", excluding("updatedAt")),
      structEntity("CreateDebtorGwDto", "Create debtor API Gateway DTO")
        .likeEntity("Debtor", excluding("updatedBy", "updatedAt")),
      structEntity("UpdateDebtorDto", "Update debtor DTO")
        .likeEntity("Debtor", excluding("updatedAt")),
      structEntity("UpdateDebtorGwDto", "Update debtor API Gateway DTO")
        .likeEntity("Debtor", excluding("updatedBy", "updatedAt")),
      structEntity("DeleteDebtorDto", "Delete debtor DTO")
        .likeEntity("Debtor", including("id", "updatedBy")),
      structEntity("DeleteDebtorGwDto", "Delete debtor API Gateway DTO")
        .likeEntity("Debtor", including("id")),
      structEntity("DebtorFindQuery", "Find debtors")
        .withFields(
          "page" :# IntInt() :@ "page",
          "size" :# IntInt() :@ "size",
          "sortBy" :# ObjectArray("SortBy") :@ "sort by array",
          "withData" :# BooleanBoolean() :@ "with data",
          "filter" :#? StringText() :@ "filter string",
          "includeIds" :# ListCollection(StringText()) :@ "include ids",
          "excludeIds" :# ListCollection(StringText()) :@ "exclude ids",
        ),
      structEntity("DebtorFindResult", "Debtor find result")
        .withFields(
          "total" :# LongLong() :@ "total",
          "hits" :# ObjectArray("DebtorDto") :@ "hits",
        ),
      
      structEntity("BusinessPartnerDto", "BusinessPartner DTO")
        .likeEntity("BusinessPartner", excluding("creditorId", "debtorId"))
        .withFields(
          "creditor" :# LinkedObject("CreditorDto", OwnRelationLink("creditorId")) :@ "creditor",
          "debtor" :# LinkedObject("DebtorDto", OwnRelationLink("debtorId")) :@ "debtor",
        ),
    )

}
