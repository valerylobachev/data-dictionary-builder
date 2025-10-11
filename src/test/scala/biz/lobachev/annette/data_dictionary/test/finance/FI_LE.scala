package biz.lobachev.annette.data_dictionary.test.finance

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Go.{goStructPackage, goTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Java.{javaStructPackage, javaTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Postgres.postgresSchema

trait FI_LE {

  val ledgerEntryComponent = component("FI-LE", "Ledger entry tables")
    .withLabels(
      postgresSchema("ledger_entry"),
      javaStructPackage("finance.data.fi.le.model"),
      javaTablePackage("finance.data.fi.le"),
      goTablePackage("github.com/valerylobachev/finance/logic/repository/entity/fi/le"),
      goStructPackage("github.com/valerylobachev/finance/logic/data_model/fi/le"),
    )
    .withDataElements(
      dataElement("EntryNo", "entryNo", uuid, "Entry no"),
      dataElement("DocumentNumber", "documentNumber", varchar(10), "Document number"),
      dataElement("LineItemNo", "lineItemNo", varchar(6), "Line item no"),
      dataElementDb("FYPeriod", "fyPeriod", "fy_period", integer, "Fiscal year period"),
      dataElement("Amount", "amount", decimal(25, 2), "Amount"),
      dataElement("Quantity", "quantity", decimal(25, 3), "Quantity"),
      dataElement("DebtCredit", "debtCredit", enumeration("DebtCredit"), "Debt/credit indicator"),
    )
    .withTables(
      // ***************************** LedgerEntry *****************************
      table("LedgerEntry", "Ledger Entry", "LedgerEntry")
        .withPK(
          "entryNo" :# "EntryNo",
        )
        .withFields(
          // format: off
          // PK
          "ledgerId"      :# "LedgerId",
          "companyCodeId"       :# "CompanyCodeId",
          "fiscalYear"          :# "FiscalYear",
          "documentNumber"      :# "DocumentNumber",
          "lineItemNo"          :# "LineItemNo",
          // dates
          "postingDate"         :# date  :@ "Posting date",
          "documentDate"        :# date  :@ "Document date",
          "valuationDate"       :# date  :@ "Valuation date",
          "period"              :# "Period",
          "fyPeriod"            :# "FYPeriod",
          // analytics
          "chartOfAccountsId"   :#  "ChartOfAccountsId",
          "glAccountId"         :#  "GLAccountId",
          "creditorId"          :#? "CreditorId",
          "debtorId"            :#? "DebtorId",
          "businessPartnerId"   :#? "BusinessPartnerId",
          "businessAreaId"      :#? "BusinessAreaId",
          "functionalAreaId"    :#? "FunctionalAreaId",
          "segmentId"           :#? "SegmentId",
          "valuationAreaId"     :#? "ValuationAreaId",
          "plantId"             :#? "PlantId",
          "locationId"          :#? "LocationId",
          "materialId"          :#? "MaterialId",
          // amounts
          "debtCredit"             :# "DebtCredit",
          "documentAmount"          :# "Amount"       :@ "Document amount" := 0,
          "documentCurrencyId"      :# "CurrencyId"   :@ "Document currency",
          "companyCodeAmount"       :# "Amount"       :@ "Company code amount" := 0,
          "companyCodeCurrencyId"   :# "CurrencyId"   :@ "Company code currency",
          // quantities
          "quantity"            :#? "Quantity" := 0,
          "uomId"               :#? "UoMId" :@ "Unit of measure id",
          "basicUomId"          :#? "UoMId" :@ "Basic unit of measure id",

          include("Modification")
          // format: on
        )
        .withIndexes(
          uniqueIndex(
            "nk",
            "Ledger entry natural key",
            "ledgerId",
            "companyCodeId",
            "fiscalYear",
            "documentNumber",
            "lineItemNo",
          ),
        )
        .withRelations(
          manyToOne(
            "ledgerId",
            "Reference to Ledger",
            "Ledger",
            "ledgerId" -> "id",
          ).withAssociation("ledger"),
          manyToOne(
            "companyCodeId",
            "Reference to CompanyCode",
            "CompanyCode",
            "companyCodeId" -> "id",
          ).withAssociation("companyCode"),
          manyToOne(
            "glAccountId",
            "Reference to GLAccount",
            "GLAccount",
            "chartOfAccountsId" -> "chartOfAccountsId",
            "glAccountId"       -> "glAccountId",
          ).withAssociation("glAccount"),
          manyToOne(
            "creditorId",
            "Reference to Creditor",
            "Creditor",
            "creditorId" -> "id",
          ).withAssociation("creditor"),
          manyToOne(
            "debtorId",
            "Reference to Debtor",
            "Debtor",
            "debtorId" -> "id",
          ).withAssociation("debtor"),
          manyToOne(
            "businessPartnerId",
            "Reference to BusinessPartner",
            "BusinessPartner",
            "businessPartnerId" -> "id",
          ).withAssociation("businessPartner"),
          manyToOne(
            "businessAreaId",
            "Reference to BusinessArea",
            "BusinessArea",
            "businessAreaId" -> "id",
          ).withAssociation("businessArea"),
          manyToOne(
            "functionalAreaId",
            "Reference to FunctionalArea",
            "FunctionalArea",
            "functionalAreaId" -> "id",
          ).withAssociation("functionalArea"),
          manyToOne(
            "segmentId",
            "Reference to Segment",
            "Segment",
            "segmentId" -> "id",
          ).withAssociation("segment"),
          manyToOne(
            "valuationAreaId",
            "Reference to ValuationArea",
            "ValuationArea",
            "valuationAreaId" -> "id",
          ).withAssociation("valuationArea"),
          manyToOne(
            "plantId",
            "Reference to Plant",
            "Plant",
            "plantId" -> "id",
          ).withAssociation("plant"),
          manyToOne(
            "locationId",
            "Reference to Location",
            "Location",
            "plantId"    -> "plantId",
            "locationId" -> "locationId",
          ).withAssociation("location"),
          manyToOne(
            "materialId",
            "Reference to Material",
            "Material",
            "materialId" -> "id",
          ).withAssociation("material"),
          manyToOne(
            "documentCurrencyId",
            "Reference to Currency",
            "Currency",
            "documentCurrencyId" -> "id",
          ).withAssociation("documentCurrency"),
          manyToOne(
            "companyCodeCurrencyId",
            "Reference to Currency",
            "Currency",
            "companyCodeCurrencyId" -> "id",
          ).withAssociation("companyCodeCurrency"),
          manyToOne(
            "uomId",
            "Reference to UnitOfMeasurement",
            "UnitOfMeasurement",
            "uomId" -> "id",
          ).withAssociation("uom"),
          manyToOne(
            "basicUomId",
            "Reference to UnitOfMeasurement",
            "UnitOfMeasurement",
            "basicUomId" -> "id",
          ).withAssociation("basicUom"),
        ),
      table("LedgerEntryAttribute", "Ledger entry attribute", "LedgerEntryAttribute")
        .withPK(
          // format: off
          "entryNo"     :# "EntryNo",
          "attributeId"       :# "AttributeId",
          "attributeValueId"  :# "AttributeValueId"
          // format: on
        )
        .withRelations(
          manyToOne("entryNo", "Reference to LedgerEntry", "LedgerEntry", "entryNo" -> "entryNo")
            .withAssociation("ledgerEntry" -> "ledgerEntryAttributes"),
          manyToOne(
            "attributeValueId",
            "Reference to AttributeValue",
            "AttributeValue",
            "attributeId"      -> "attributeId",
            "attributeValueId" -> "attributeValueId",
          ).withAssociation("attributeValue"),
        ),
    )

}
