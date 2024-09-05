package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.labels.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model._

trait FI_GLA {

  val glAccountComponent = component("FI-GLA", "General Ledger account settings")
    .withSchema("gl_account")
    .withLabels(
      javaModelPackage("finance.data.fi.gla.model"),
      javaRepoPackage("finance.data.fi.gla")
    )
    .withDataElements(
      dataElement("AccountType", "accountType", EnumString("AccountType"), "Account type"),
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
    )
    .withEntities(
      tableEntity("ChartOfAccounts", "Chart of accounts", "ChartOfAccounts")
        .withTableName("charts_of_accounts")
        .withPK(
          "id" :# "ChartOfAccountsId"
        )
        .withFields(
          "name" :# "Name",
          include("Modification")
        ),
      tableEntity("GLAccount", "General ledger account", "GLAccount")
        .withTableName("gl_accounts")
        .withPK(
          // format: off
          "chartOfAccountsId" :# "ChartOfAccountsId",
          "glAccountId"             :# "GLAccountId"
          // format: on
        )
        .withFields(
          // format: off
          "shortName"   :# "ShortName",
          "name"              :# "Name",
          "glAccountType"     :# "GLAccountType",
          "glAccountGroupId"  :# "GLAccountGroupId",
          include("Modification")
          // format: on
        )
        .withRelations(
          manyToOne(
            "chartOfAccountsId",
            "Reference to Chart Of Accounts",
            "ChartOfAccounts",
            "chartOfAccountsId" -> "id"
          ),
          manyToOne(
            "glAccountGroupId",
            "Reference to GLAccountGroup",
            "GLAccountGroup",
            "chartOfAccountsId" -> "chartOfAccountsId",
            "glAccountGroupId"  -> "glAccountGroupId"
          )
        ),
      tableEntity("GLAccountGroup", "General ledger account group", "GLAccountGroup")
        .withTableName("gl_account_groups")
        .withPK(
          // format: off
          "chartOfAccountsId" :# "ChartOfAccountsId",
          "glAccountGroupId"        :# "GLAccountGroupId"
          // format: on
        )
        .withFields(
          // format: off
          "name"        :# "Name",
          "glAccountId"       :# "GLAccountId",
          "fromGLAccountId"   :# "GLAccountId" :> "from_gl_account_id" :@ "From GL account",
          "toGLAccountId"     :# "GLAccountId" :> "to_gl_account_id" :@ "To GL account",
          include("Modification")
          // format: on
        )
        .withRelations(
          manyToOne(
            "chartOfAccountsId",
            "Reference to Chart Of Accounts",
            "ChartOfAccounts",
            "chartOfAccountsId" -> "id"
          )
        )
    )

}
