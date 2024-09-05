package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.labels.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model.{
  BigDecimalNumeric,
  EnumString,
  IntInt,
  LocalDateDate,
  StringVarchar,
  UuidUuid,
}

trait FI_LE {

  val ledgerEntryComponent = component("FI-LE", "Ledger entry tables")
    .withSchema("ledger_entry")
    .withLabels(
      javaModelPackage("finance.data.fi.le.model"),
      javaRepoPackage("finance.data.fi.le"),
    )
    .withDataElements(
      dataElement("EntryNo", "entryNo", UuidUuid(), "Entry no"),
      dataElement("DocumentNumber", "documentNumber", StringVarchar(10), "Document number"),
      dataElement("LineItemNo", "lineItemNo", StringVarchar(6), "Line item no"),
      dataElementDb("FYPeriod", "fyPeriod", "fy_period", IntInt(), "Fiscal year period"),
      dataElement("Amount", "amount", BigDecimalNumeric(25, 2), "Amount"),
      dataElement("Quantity", "quantity", BigDecimalNumeric(25, 3), "Quantity"),
      dataElement("DebitCredit", "debitCredit", EnumString("DebitCredit"), "Debit/credit indicator"),
    )
    .withEntities(
      // ***************************** LedgerEntry *****************************
      tableEntity("LedgerEntry", "Ledger Entry", "LedgerEntry")
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
          "postingDate"         :# LocalDateDate()  :@ "Posting date",
          "documentDate"        :# LocalDateDate()  :@ "Document date",
          "valuationDate"       :# LocalDateDate()  :@ "Valuation date",
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
          "debitCredit"             :# "DebitCredit",
          "documentAmount"          :# "Amount"       :@ "Document amount",
          "documentCurrencyId"      :# "CurrencyId"   :@ "Document currency",
          "companyCodeAmount"       :# "Amount"       :@ "Company code amount",
          "companyCodeCurrencyId"   :# "CurrencyId"   :@ "Company code currency",
          // quantities
          "quantity"            :#? "Quantity",
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
      tableEntity("LedgerEntryAttribute", "Ledger entry attribute", "LedgerEntryAttribute")
        .withPK(
          // format: off
          "entryNo"     :# "EntryNo",
          "attributeId"       :# "AttributeId",
          "attributeValueId"  :# "AttributeValueId"
          // format: on
        )
        .withRelations(
          manyToOne("entryNo", "Reference to LedgerEntry", "LedgerEntry", "entryNo" -> "entryNo"),
          manyToOne(
            "attributeValueId",
            "Reference to AttributeValue",
            "AttributeValue",
            "attributeId"                                                                   -> "attributeId",
            "attributeValueId"                                                              -> "attributeValueId",
          ),
        ),
    )

}
