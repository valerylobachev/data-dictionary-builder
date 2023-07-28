package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model.LocalDateDate

trait LedgerEntry {

  val ledgerEntryGroup = group("LedgerEntry", "Ledger entry tables")
    .withSchema("ledger_entry")
    .withAttributes(
      javaModelPackage("finance.data.ledger_entry.model"),
      javaRepoPackage("finance.data.ledger_entry"),
    )
    .withEntities(
      // ***************************** LedgerEntry *****************************
      tableEntity("LedgerEntry", "LedgerEntry", "LedgerEntry")
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
          "uomId"               :#? "UoMId",
          "basicUomId"          :#? "UoMId",

          include("Modification")
          // format: on
        )
        .withIndexes(
          uniqueIndex(
            "PK",
            "Ledger entry natural key",
            "ledgerId",
            "companyCodeId",
            "fiscalYear",
            "documentNumber",
            "lineItemNo",
          ),
        )
        .withRelations(
          manyToOneRelation(
            "ledgerId",
            "Reference to Ledger",
            "Ledger",
            "ledgerId"              -> "id",
          ),
          manyToOneRelation(
            "companyCodeId",
            "Reference to CompanyCode",
            "CompanyCode",
            "companyCodeId"         -> "id",
          ),
          manyToOneRelation(
            "glAccountId",
            "Reference to GLAccount",
            "GLAccount",
            "chartOfAccountsId"     -> "chartOfAccountsId",
            "glAccountId"           -> "glAccountId",
          ),
          manyToOneRelation(
            "creditorId",
            "Reference to Creditor",
            "Creditor",
            "creditorId"            -> "id",
          ),
          manyToOneRelation(
            "debtorId",
            "Reference to Debtor",
            "Debtor",
            "debtorId"              -> "id",
          ),
          manyToOneRelation(
            "businessPartnerId",
            "Reference to BusinessPartner",
            "BusinessPartner",
            "businessPartnerId"     -> "id",
          ),
          manyToOneRelation(
            "businessAreaId",
            "Reference to BusinessArea",
            "BusinessArea",
            "businessAreaId"        -> "id",
          ),
          manyToOneRelation(
            "functionalAreaId",
            "Reference to FunctionalArea",
            "FunctionalArea",
            "functionalAreaId"      -> "id",
          ),
          manyToOneRelation(
            "segmentId",
            "Reference to Segment",
            "Segment",
            "segmentId"             -> "id",
          ),
          manyToOneRelation(
            "valuationAreaId",
            "Reference to ValuationArea",
            "ValuationArea",
            "valuationAreaId"       -> "id",
          ),
          manyToOneRelation(
            "plantId",
            "Reference to Plant",
            "Plant",
            "plantId"               -> "id",
          ),
          manyToOneRelation(
            "locationId",
            "Reference to Location",
            "Location",
            "plantId"               -> "plantId",
            "locationId"            -> "locationId",
          ),
          manyToOneRelation(
            "materialId",
            "Reference to Material",
            "Material",
            "materialId"            -> "id",
          ),
          manyToOneRelation(
            "documentCurrencyId",
            "Reference to Currency",
            "Currency",
            "documentCurrencyId"    -> "id",
          ),
          manyToOneRelation(
            "companyCodeCurrencyId",
            "Reference to Currency",
            "Currency",
            "companyCodeCurrencyId" -> "id",
          ),
          manyToOneRelation(
            "uomId",
            "Reference to UnitOfMeasurement",
            "UnitOfMeasurement",
            "uomId"                 -> "id",
          ),
          manyToOneRelation(
            "basicUomId",
            "Reference to UnitOfMeasurement",
            "UnitOfMeasurement",
            "basicUomId"            -> "id",
          ),
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
          manyToOneRelation("entryNo", "Reference to LedgerEntry", "LedgerEntry", "entryNo" -> "entryNo"),
          manyToOneRelation(
            "attributeValueId",
            "Reference to AttributeValue",
            "AttributeValue",
            "attributeId"                                                                   -> "attributeId",
            "attributeValueId"                                                              -> "attributeValueId",
          ),
        ),
    )

}
