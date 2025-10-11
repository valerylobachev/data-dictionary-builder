package biz.lobachev.annette.data_dictionary.test.finance

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Go.{goStructPackage, goStructSourceFile, goTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Java.{javaStructPackage, javaTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Postgres.postgresSchema

trait FI_GLA {

  val glAccountComponent = component("FI-GLA", "General Ledger account settings")
    .withLabels(
      postgresSchema("gl_account"),
      javaStructPackage("finance.data.fi.gla.model"),
      javaTablePackage("finance.data.fi.gla"),
      goTablePackage("github.com/valerylobachev/finance/logic/repository/entity/fi/gla"),
      goStructPackage("github.com/valerylobachev/finance/logic/data_model/fi/gla"),
    )
    .withDataElements(
      dataElement("AccountType", "accountType", enumeration("AccountType"), "Account type"),
      dataElement("ChartOfAccountsId", "chartOfAccountsId", varchar(4), "Chart of accounts id"),
      dataElementDb("GLAccountId", "glAccountId", "gl_account_id", varchar(10), "General ledger account id"),
      dataElementDb(
        "GLAccountGroupId",
        "glAccountGroupId",
        "gl_account_group_id",
        varchar(10),
        "General ledger account group id",
      ),
      dataElementDb(
        "GLAccountType",
        "glAccountType",
        "gl_account_type",
        enumeration("GLAccountType"),
        "General ledger account type",
      ),
    )
    .withTables(
      table("ChartOfAccounts", "Chart of accounts", "ChartOfAccounts")
        .withTableName("charts_of_accounts")
        .withPK(
          "id" :# "ChartOfAccountsId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      table("GLAccount", "General ledger account", "GLAccount")
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
            "chartOfAccountsId" -> "id",
          ).withAssociation("chartOfAccounts" -> "glAccounts"),
          manyToOne(
            "glAccountGroupId",
            "Reference to GLAccountGroup",
            "GLAccountGroup",
            "chartOfAccountsId" -> "chartOfAccountsId",
            "glAccountGroupId"  -> "glAccountGroupId",
          ).withAssociation("glAccountGroup" -> "glAccounts"),
        ),
      table("GLAccountGroup", "General ledger account group", "GLAccountGroup")
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
          include("Modification")
          // format: on
        )
        .withRelations(
          manyToOne(
            "chartOfAccountsId",
            "Reference to Chart Of Accounts",
            "ChartOfAccounts",
            "chartOfAccountsId" -> "id",
          ).withAssociation("chartOfAccounts" -> "glAccountGroups"),
        ),
      table("GLAccountGroupItem", "General ledger account group items", "GLAccountGroupItem")
        .withTableName("gl_account_group_items")
        .withPK(
          "chartOfAccountsId" :# "ChartOfAccountsId",
          "glAccountGroupId" :# "GLAccountGroupId",
          "fromGLAccountId" :# "GLAccountId" :> "from_gl_account_id" :@ "From GL account",
        )
        .withFields(
          "toGLAccountId" :# "GLAccountId" :> "to_gl_account_id" :@ "To GL account",
          include("Modification"),
          // format: on
        )
        .withRelations(
          manyToOne(
            "glAccountGroup",
            "Reference to GL account group",
            "GLAccountGroup",
            "chartOfAccountsId" -> "chartOfAccountsId",
            "glAccountGroupId"  -> "glAccountGroupId",
          ).withAssociation("glAccountGroup" -> "glAccountGroupItems"),
        ),
    )
    // ChartOfAccounts
    .withLabeledStructs(
      goStructSourceFile("chart_of_accounts"),
    )(
      struct("ChartOfAccountsId", "ChartOfAccounts Id")
        .likeTablePK("ChartOfAccounts"),
      struct("ChartOfAccountsDto", "ChartOfAccounts DTO")
        .likeTable("ChartOfAccounts"),
      struct("ChartOfAccountsNameDto", "ChartOfAccounts name DTO")
        .likeTable("ChartOfAccounts", including("id", "name")),
    )
    // GLAccount
    .withLabeledStructs(
      goStructSourceFile("gl_account"),
    )(
      struct("GLAccountId", "GLAccount Id")
        .likeTablePK("GLAccount"),
      struct("GLAccountDto", "GLAccount DTO")
        .likeTable("GLAccount"),
      struct("GLAccountNameDto", "GLAccount name DTO")
        .likeTable("GLAccount", including("chartOfAccountsId", "glAccountId", "name")),
    )
    .withLabeledStructs(
      goStructSourceFile("gl_account_group"),
    )(
      // GLAccountGroup
      struct("GLAccountGroupId", "GLAccountGroup Id")
        .likeTablePK("GLAccountGroup"),
      struct("GLAccountGroupDto", "GLAccountGroup DTO")
        .likeTable("GLAccountGroup")
        .withFields(
          "accounts" :## linkedStructArray("GLAccountGroupItemDto"),
        ),
      struct("GLAccountGroupNameDto", "GLAccountGroup name DTO")
        .likeTable("GLAccountGroup", including("chartOfAccountsId", "glAccountGroupId", "name")),

      // GLAccountGroupItem
      struct("GLAccountGroupItemId", "GLAccountGroupItem Id")
        .likeTablePK("GLAccountGroupItem"),
      struct("GLAccountGroupItemDto", "GLAccountGroupItem DTO")
        .likeTable("GLAccountGroupItem"),
    )

}
