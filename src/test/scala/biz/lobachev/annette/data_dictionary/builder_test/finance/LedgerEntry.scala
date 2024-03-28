package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.helper.RelationName
import biz.lobachev.annette.data_dictionary.builder.model.LocalDateDate

trait LedgerEntry {

  val ledgerEntryGroup = group("LedgerEntry", "Ledger entry tables")
    .withSchema("ledger_entry")
    .withLabels(
      javaModelPackage("finance.data.ledger_entry.model"),
      javaRepoPackage("finance.data.ledger_entry"),
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
            "pk",
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
            "ledgerId" -> "id",
          ).withLabels(
            RelationName.fieldName("ledger"),
          ),
          manyToOneRelation(
            "companyCodeId",
            "Reference to CompanyCode",
            "CompanyCode",
            "companyCodeId" -> "id",
          ).withLabels(
            RelationName.fieldName("companyCode"),
          ),
          manyToOneRelation(
            "glAccountId",
            "Reference to GLAccount",
            "GLAccount",
            "chartOfAccountsId" -> "chartOfAccountsId",
            "glAccountId"       -> "glAccountId",
          ).withLabels(
            RelationName.fieldName("glAccount"),
          ),
          manyToOneRelation(
            "creditorId",
            "Reference to Creditor",
            "Creditor",
            "creditorId" -> "id",
          ).withLabels(
            RelationName.fieldName("creditor"),
          ),
          manyToOneRelation(
            "debtorId",
            "Reference to Debtor",
            "Debtor",
            "debtorId" -> "id",
          ).withLabels(
            RelationName.fieldName("debtor"),
          ),
          manyToOneRelation(
            "businessPartnerId",
            "Reference to BusinessPartner",
            "BusinessPartner",
            "businessPartnerId" -> "id",
          ).withLabels(
            RelationName.fieldName("businessPartner"),
          ),
          manyToOneRelation(
            "businessAreaId",
            "Reference to BusinessArea",
            "BusinessArea",
            "businessAreaId" -> "id",
          ).withLabels(
            RelationName.fieldName("businessArea"),
          ),
          manyToOneRelation(
            "functionalAreaId",
            "Reference to FunctionalArea",
            "FunctionalArea",
            "functionalAreaId" -> "id",
          ).withLabels(
            RelationName.fieldName("functionalArea"),
          ),
          manyToOneRelation(
            "segmentId",
            "Reference to Segment",
            "Segment",
            "segmentId" -> "id",
          ).withLabels(
            RelationName.fieldName("segment"),
          ),
          manyToOneRelation(
            "valuationAreaId",
            "Reference to ValuationArea",
            "ValuationArea",
            "valuationAreaId" -> "id",
          ).withLabels(
            RelationName.fieldName("valuationArea"),
          ),
          manyToOneRelation(
            "plantId",
            "Reference to Plant",
            "Plant",
            "plantId" -> "id",
          ).withLabels(
            RelationName.fieldName("plant"),
          ),
          manyToOneRelation(
            "locationId",
            "Reference to Location",
            "Location",
            "plantId"    -> "plantId",
            "locationId" -> "locationId",
          ).withLabels(
            RelationName.fieldName("location"),
          ),
          manyToOneRelation(
            "materialId",
            "Reference to Material",
            "Material",
            "materialId" -> "id",
          ).withLabels(
            RelationName.fieldName("material"),
          ),
          manyToOneRelation(
            "documentCurrencyId",
            "Reference to Currency",
            "Currency",
            "documentCurrencyId" -> "id",
          ).withLabels(
//            RelationName.fieldName("documentCurrency"),
          ),
          manyToOneRelation(
            "companyCodeCurrencyId",
            "Reference to Currency",
            "Currency",
            "companyCodeCurrencyId" -> "id",
          ).withLabels(
//            RelationName.fieldName("companyCodeCurrency"),
          ),
          manyToOneRelation(
            "uomId",
            "Reference to UnitOfMeasurement",
            "UnitOfMeasurement",
            "uomId" -> "id",
          ).withLabels(
            RelationName.fieldName("uom"),
          ),
          manyToOneRelation(
            "basicUomId",
            "Reference to UnitOfMeasurement",
            "UnitOfMeasurement",
            "basicUomId" -> "id",
          ).withLabels(
            RelationName.fieldName("basicUom"),
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
