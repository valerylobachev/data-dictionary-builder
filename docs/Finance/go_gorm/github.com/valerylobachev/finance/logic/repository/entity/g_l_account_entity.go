// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-19 17:06:07

package entity

import (
  "time"
)

const (
  GLAccountTableName = "gl_account.gl_accounts"
  GLAccountPK = "gl_accounts_pkey"
  GLAccountFKChartOfAccountsId = "gl_accounts_chart_of_accounts_id"
  GLAccountFKGlAccountGroupId = "gl_accounts_gl_account_group_id"
)


// General ledger account
type GLAccountEntity struct {
    // Chart of accounts id
    ChartOfAccountsId string `gorm:"column:chart_of_accounts_id;primaryKey;not null"`
    // General ledger account id
    GlAccountId string `gorm:"column:gl_account_id;primaryKey;not null"`
    // Short name
    ShortName string `gorm:"column:short_name;not null"`
    // Name
    Name string `gorm:"column:name;not null"`
    // General ledger account type
    GlAccountType string `gorm:"column:gl_account_type;not null"`
    // General ledger account group id
    GlAccountGroupId string `gorm:"column:gl_account_group_id;not null"`
    // User created record
    CreatedBy string `gorm:"column:created_by;not null"`
    // Timestamp of record create
    CreatedAt time.Time `gorm:"column:created_at;not null"`
    // User updated record
    UpdatedBy string `gorm:"column:updated_by;not null"`
    // Timestamp of record update
    UpdatedAt time.Time `gorm:"column:updated_at;not null"`
}


func (*GLAccountEntity) TableName() string {
	return GLAccountTableName
}