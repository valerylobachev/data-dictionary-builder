package biz.lobachev.annette.data_dictionary.test.finance

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Go.{goStructPackage, goStructSourceFile, goTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Java.{javaStructPackage, javaTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Postgres.postgresSchema

trait FI_CC {

  val companyCodeComponent = component("FI-CC", "Company code tables")
    .withLabels(
      postgresSchema("company_code"),
      javaStructPackage("finance.data.fi.cc.model"),
      javaTablePackage("finance.data.fi.cc"),
      goTablePackage("github.com/valerylobachev/finance/logic/repository/entity/fi/cc"),
      goStructPackage("github.com/valerylobachev/finance/logic/data_model/fi/cc"),
    )
    .withDataElements(
      dataElement("CompanyCodeId", "companyCodeId", varchar(4), "Company code"),
      dataElementDb("FYVariantId", "fyVariantId", "fy_variant_id", varchar(2), "Fiscal year variant id"),
      dataElement("PostingPeriodVariantId", "PostingPeriodVariantId", varchar(4), "Posting period variant id"),
      dataElement("FiscalYear", "fiscalYear", integer, "Fiscal year"),
      dataElement("Period", "period", integer, "Period"),
    )
    .withTables(
      table("CompanyCode", "Company code", "CompanyCode")
        .withPK(
          "id" :# "CompanyCodeId",
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
          manyToOne("countryId", "Reference to Country", "Country", "countryId" -> "id")
            .withAssociation("country"),
          manyToOne("languageId", "Reference to Language", "Language", "languageId" -> "id")
            .withAssociation("language"),
          manyToOne("currencyId", "Reference to Currency", "Currency", "currencyId" -> "id")
            .withAssociation("currency"),
          manyToOne(
            "chartOfAccountsId",
            "Reference to ChartOfAccounts",
            "ChartOfAccounts",
            "chartOfAccountsId" -> "id",
          ).withAssociation("chartOfAccounts"      -> "companyCodes"),
          manyToOne("fyVariantId", "Reference to FYVariant", "FYVariant", "fyVariantId" -> "id")
            .withAssociation("fyVariant"           -> "companyCodes"),
          manyToOne(
            "postingPeriodVariantId",
            "Reference to PostingPeriodVariant",
            "PostingPeriodVariant",
            "postingPeriodVariantId" -> "id",
          ).withAssociation("postingPeriodVariant" -> "companyCodes"),
        ),
      table("CompanyCodeLedger", "Company code ledger", "CompanyCodeLedger")
        .withPK(
          // format: off
          "companyCodeId" :# "CompanyCodeId",
          "ledgerId"            :# "LedgerId"
          // format: on
        )
        .withFields(
          include("Modification"),
        )
        .withRelations(
          manyToOne("companyCodeId", "Reference to CompanyCode", "CompanyCode", "companyCodeId" -> "id")
            .withAssociation("companyCode" -> "companyCodeLedgers"),
          manyToOne("ledgerId", "Reference to Ledger", "Ledger", "ledgerId" -> "id")
            .withAssociation("ledger"      -> "companyCodeLedgers"),
        ),
      table("FYVariant", "Fiscal year variant", "FYVariant")
        .withTableName("fy_variants")
        .withPK(
          "id" :# "FYVariantId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      table("PostingPeriodVariant", "Posting period variant", "PostingPeriodVariant")
        .withPK(
          "id" :# "PostingPeriodVariantId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      table("OpenPeriod", "Open period", "OpenPeriod")
        .withPK(
          // format: off
          "postingPeriodVariantId" :# "PostingPeriodVariantId",
          "accountType"                  :# "AccountType",
          "toAccount"                    :# "GLAccountId"               :@ "To account"
          // format: on
        )
        .withFields(
          // format: off
          "fromAccount"       :# "GLAccountId"  :@ "From account",
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
          // format: on
        )
        .withRelations(
          manyToOne(
            "postingPeriodVariantId",
            "Reference to PostingPeriodVariant",
            "PostingPeriodVariant",
            "postingPeriodVariantId" -> "id",
          ).withAssociation("postingPeriodVariant" -> "openPeriods"),
        ),
    )
    // CompanyCode
    .withLabeledStructs(
      goStructSourceFile("company_code"),
    )(
      struct("CompanyCodeId", "CompanyCode Id")
        .likeTablePK("CompanyCode"),
      struct("CompanyCodeDto", "CompanyCode DTO")
        .likeTable("CompanyCode"),
      struct("CompanyCodeNameDto", "CompanyCode name DTO")
        .likeTable("CompanyCode", including("id", "name"))
        .withFields(
          "ledgers" :## linkedStructArray("CompanyCodeLedgerDto"),
        ),
      struct("CompanyCodeADto", "CompanyCode augmented DTO")
        .likeTable(
          "CompanyCode",
          excluding(
            "countryId",
            "languageId",
            "currencyId",
            "chartOfAccountsId",
            "fyVariantId",
            "postingPeriodVariantId",
          ),
        )
        .withFields(
          "country" :## linkedStruct("CountryNameDto"),
          "language" :## linkedStruct("LanguageNameDto"),
          "currency" :## linkedStruct("CurrencyNameDto"),
          "chartOfAccounts" :## linkedStruct("ChartOfAccountsNameDto"),
          "fyVariant" :## linkedStruct("FYVariantNameDto"),
          "postingPeriodVariant" :## linkedStruct("PostingPeriodVariantNameDto"),
        ),
    )
    // CompanyCodeLedger
    .withLabeledStructs(
      goStructSourceFile("company_code_ledger"),
    )(
      struct("CompanyCodeLedgerId", "CompanyCodeLedger Id")
        .likeTablePK("CompanyCodeLedger"),
      struct("CompanyCodeLedgerDto", "CompanyCodeLedger DTO")
        .likeTable("CompanyCodeLedger"),
    )
    // FYVariant
    .withLabeledStructs(
      goStructSourceFile("fy_variant"),
    )(
      struct("FYVariantId", "FYVariant Id")
        .likeTablePK("FYVariant"),
      struct("FYVariantDto", "FYVariant DTO")
        .likeTable("FYVariant"),
      struct("FYVariantNameDto", "FYVariant name DTO")
        .likeTable("FYVariant", including("id", "name")),
    )
    // PostingPeriodVariant
    .withLabeledStructs(
      goStructSourceFile("posting_period_variant"),
    )(
      struct("PostingPeriodVariantId", "PostingPeriodVariant Id")
        .likeTablePK("PostingPeriodVariant"),
      struct("PostingPeriodVariantDto", "PostingPeriodVariant DTO")
        .likeTable("PostingPeriodVariant")
        .withFields(
          "items" :## linkedStructArray("OpenPeriodDto"),
        ),
      struct("PostingPeriodVariantNameDto", "PostingPeriodVariant name DTO")
        .likeTable("PostingPeriodVariant", including("id", "name")),

      // OpenPeriod
      struct("OpenPeriodId", "OpenPeriod Id")
        .likeTablePK("OpenPeriod"),
      struct("OpenPeriodDto", "OpenPeriod DTO")
        .likeTable("OpenPeriod"),
    )

}
