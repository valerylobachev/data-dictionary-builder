package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{javaModelPackage, javaRepoPackage}

trait CompanyCode {

  val companyCodeGroup = group("CompanyCode", "Company code tables")
    .withSchema("company_code")
    .withLabels(
      javaModelPackage("finance.data.company_code.model"),
      javaRepoPackage("finance.data.company_code")
    )
    .withEntities(
      tableEntity("CompanyCode", "Company code", "CompanyCode")
        .withPK(
          "id" :# "CompanyCodeId"
        )
        .withFields(
          // format: off
          "name"             :# "Name",
          "countryId"              :# "CountryId",
          "languageId"             :# "LanguageId",
          "currencyId"             :# "CurrencyId",
          "chartOfAccountsId"      :# "ChartOfAccountsId",
          "fyVariantId"            :# "FYVariantId",
          "postingPeriodVariantId" :# "PostingPeriodVariantId",
          include("Modification")
          // format: on
        )
        .withRelations(
          manyToOneRelation("countryId", "Reference to Country", "Country", "countryId"         -> "id"),
          manyToOneRelation("languageId", "Reference to Language", "Language", "languageId"     -> "id"),
          manyToOneRelation("currencyId", "Reference to Currency", "Currency", "currencyId"     -> "id"),
          manyToOneRelation(
            "chartOfAccountsId",
            "Reference to ChartOfAccounts",
            "ChartOfAccounts",
            "chartOfAccountsId"                                                                 -> "id"
          ),
          manyToOneRelation("fyVariantId", "Reference to FYVariant", "FYVariant", "fyVariantId" -> "id"),
          manyToOneRelation(
            "postingPeriodVariantId",
            "Reference to PostingPeriodVariant",
            "PostingPeriodVariant",
            "postingPeriodVariantId"                                                            -> "id"
          )
        ),
      tableEntity("CompanyCodeLedger", "Company code ledger", "CompanyCodeLedger")
        .withPK(
          // format: off
          "companyCodeId" :# "CompanyCodeId",
          "ledgerId"            :# "LedgerId"
          // format: on
        )
        .withFields(
          include("Modification")
        )
        .withRelations(
          manyToOneRelation("companyCodeId", "Reference to CompanyCode", "CompanyCode", "companyCodeId" -> "id"),
          manyToOneRelation("ledgerId", "Reference to Ledger", "Ledger", "ledgerId"                     -> "id")
        ),
      tableEntity("FYVariant", "Fiscal year variant", "FYVariant")
        .withTableName("fy_variants")
        .withPK(
          "id" :# "FYVariantId"
        )
        .withFields(
          "name"       :# "Name",
          include("Modification")
        ),
      tableEntity("PostingPeriodVariant", "Posting period variant", "PostingPeriodVariant")
        .withPK(
          "id" :# "PostingPeriodVariantId"
        )
        .withFields(
          "name"       :# "Name",
          include("Modification")
        ),
      tableEntity("OpenPeriod", "Open period", "OpenPeriod")
        .withPK(
          // format: off
          "postingPeriodVariantId" :# "PostingPeriodVariantId",
          "accountType"                  :# "AccountType",
          "toAccount"                    :# "AccountId"               :@ "To account"
          // format: on
        )
        .withFields(
          // format: off
          "fromAccount" :# "AccountId"  :@ "From account",
          "fromFy1"           :# "FiscalYear" :@ "Group 1: From fiscal year",
          "fromPeriod1"       :# "Period"     :@ "Group 1: From period",
          "toFy1"             :# "FiscalYear" :@ "Group 1: To fiscal year",
          "toPeriod1"         :# "Period"     :@ "Group 1: To period",
          "fromFy2"           :# "FiscalYear" :@ "Group 2: From fiscal year",
          "fromPeriod2"       :# "Period"     :@ "Group 2: From period",
          "toFy2"             :# "FiscalYear" :@ "Group 2: To fiscal year",
          "toPeriod2"         :# "Period"     :@ "Group 2: To period",
          "fromFy3"           :# "FiscalYear" :@ "Group 3: From fiscal year",
          "fromPeriod3"       :# "Period"     :@ "Group 3: From period",
          "toFy3"             :# "FiscalYear" :@ "Group 3: To fiscal year",
          "toPeriod3"         :# "Period"     :@ "Group 3: To period",
          // format: off
        )
        .withRelations(
          manyToOneRelation("postingPeriodVariantId", "Reference to PostingPeriodVariant", "PostingPeriodVariant", "postingPeriodVariantId" -> "id")
        ),


    )

}
