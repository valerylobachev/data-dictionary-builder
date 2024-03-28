package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model._

trait GLAccount {

  val glAccountGroup = component("GLAccount", "General Ledger account settings")
    .withSchema("gl_account")
    .withLabels(
      javaModelPackage("finance.data.gl_account.model"),
      javaRepoPackage("finance.data.gl_account")
    )
    .withEntities(
      tableEntity("ChartOfAccounts", "Chart of accounts", "ChartOfAccounts")
        .withTableName("charts_of_accounts")
        .withPK(
          "id" :# DataElementType("ChartOfAccountsId")
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
          manyToOneRelation(
            "chartOfAccountsId",
            "Reference to Chart Of Accounts",
            "ChartOfAccounts",
            "chartOfAccountsId" -> "id"
          ),
          manyToOneRelation(
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
          manyToOneRelation(
            "chartOfAccountsId",
            "Reference to Chart Of Accounts",
            "ChartOfAccounts",
            "chartOfAccountsId" -> "id"
          )
        )
    )

}
