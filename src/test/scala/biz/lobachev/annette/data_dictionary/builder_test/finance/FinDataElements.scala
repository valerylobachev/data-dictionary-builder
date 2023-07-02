package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.model._

trait FinDataElements {

  val dataElements = Seq(
    dataElement("UserId", "userId", StringVarchar(20), "User Id"),
    dataElement("Name", "name", StringVarchar(100), "Name"),
    dataElement("ShortName", "shortName", StringVarchar(20), "Short name"),
    // ***************************** Common *****************************
    dataElement("CountryId", "countryId", StringVarchar(3), "Country Id"),
    dataElement("LanguageId", "languageId", StringVarchar(3), "Language Id"),
    // ***************************** Currency *****************************
    dataElement("CurrencyId", "currencyId", StringVarchar(5), "Currency Id"),
    dataElement("ExchangeRateTypeId", "exchangeRateTypeId", StringVarchar(4), "Exchange rate type Id"),
    dataElement("ExchangeRate", "exchangeRate", BigDecimalNumeric(25, 5), "Exchange rate"),
    // ***************************** UnitOfMeasurement *****************************
    dataElement("UoMId", "uomId", StringVarchar(6), "Unit of measurement Id"),
    // ***************************** Ledger *****************************
    dataElement("LedgerId", "ledgerId", StringVarchar(4), "Ledger Id"),
    dataElement("LedgerGroupId", "ledgerGroupId", StringVarchar(2), "Ledger Group Id"),
    dataElement("LedgerType", "ledgerType", EnumString("LedgerType"), "Ledger Type"),
    // ***************************** GLAccount *****************************
    dataElement("ChartOfAccountsId", "chartOfAccountsId", StringVarchar(4), "Chart of accounts id"),
    dataElementDb("GLAccountId", "glAccountId", "gl_account_id", StringVarchar(10), "General ledger account id"),
    dataElementDb(
      "GLAccountGroupId",
      "glAccountGroupId",
      "gl_account_group_id",
      StringVarchar(10),
      "General ledger account group id"
    ),
    dataElementDb(
      "GLAccountType",
      "glAccountType",
      "gl_account_type",
      EnumString("GLAccountType"),
      "General ledger account type"
    ),
    // ***************************** Creditor *****************************
    dataElement("CreditorId", "creditorId", StringVarchar(10), "Creditor id"),
    // ***************************** Debtor *****************************
    dataElement("DebtorId", "debtorId", StringVarchar(10), "Debtor id"),
    // ***************************** BusinessPartner *****************************
    dataElement("BusinessPartnerId", "businessPartnerId", StringVarchar(10), "Business partner id"),
    // ***************************** Analytics *****************************
    dataElement("BusinessAreaId", "businessAreaId", StringVarchar(4), "Business area id"),
    dataElement("FunctionalAreaId", "functionalAreaId", StringVarchar(20), "Functional area id"),
    dataElement("SegmentId", "segmentId", StringVarchar(10), "Segment id"),
    dataElement("AttributeId", "attributeId", StringVarchar(10), "Attribute id"),
    dataElement("AttributeValueId", "attributeValueId", StringVarchar(20), "Attribute value id"),
    // ***************************** Logistics *****************************
    dataElement("PlantId", "plantId", StringVarchar(4), "Plant id"),
    dataElement("MaterialId", "materialId", StringVarchar(40), "Material id"),
    dataElement("LocationId", "locationId", StringVarchar(10), "Location id"),
    dataElement("ValuationAreaId", "valuationAreaId", StringVarchar(20), "Valuation area id"),
    // ***************************** CompanyCode *****************************
    dataElement("CompanyCodeId", "companyCodeId", StringVarchar(4), "Company code"),
    dataElementDb("FYVariantId", "fyVariantId", "fy_variant_id", StringVarchar(2), "Fiscal year variant id"),
    dataElement("PostingPeriodVariantId", "PostingPeriodVariantId", StringVarchar(4), "Posting period variant id"),
    dataElement("FiscalYear", "fiscalYear", IntInt(), "Fiscal year"),
    dataElement("Period", "period", IntInt(), "Period"),
    dataElement("AccountId", "accountId", StringVarchar(10), "Account id"),
    dataElement("AccountType", "accountType", EnumString("AccountType"), "Account type"),
    // ***************************** LedgerEntry *****************************
    dataElement("EntryNo", "entryNo", UuidUuid(), "Entry no"),
    dataElement("DocumentNumber", "documentNumber", StringVarchar(10), "Document number"),
    dataElement("LineItemNo", "lineItemNo", StringVarchar(6), "Line item no"),
    dataElementDb("FYPeriod", "fyPeriod", "fy_period", IntInt(), "Fiscal year period"),
    dataElement("Amount", "amount", BigDecimalNumeric(25, 2), "Amount"),
    dataElement("Quantity", "quantity", BigDecimalNumeric(25, 3), "Quantity"),
    dataElement("DebitCredit", "debitCredit", EnumString("DebitCredit"), "Debit/credit indicator")
  )

}
