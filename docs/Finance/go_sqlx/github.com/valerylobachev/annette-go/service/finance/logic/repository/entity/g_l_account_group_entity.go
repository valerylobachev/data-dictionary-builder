// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "time"
)

const (
  GLAccountGroupTableName = "gl_account.gl_account_groups"
  GLAccountGroupPK = "gl_account_groups_pkey"
  GLAccountGroupFKChartOfAccountsId = "gl_account_groups_chart_of_accounts_id"
)


// General ledger account group
type GLAccountGroupEntity struct {
    // Chart of accounts id
    ChartOfAccountsId string `db:"chart_of_accounts_id"`
    // General ledger account group id
    GlAccountGroupId string `db:"gl_account_group_id"`
    // Name
    Name string `db:"name"`
    // General ledger account id
    GlAccountId string `db:"gl_account_id"`
    // From GL account
    FromGLAccountId string `db:"from_gl_account_id"`
    // To GL account
    ToGLAccountId string `db:"to_gl_account_id"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


