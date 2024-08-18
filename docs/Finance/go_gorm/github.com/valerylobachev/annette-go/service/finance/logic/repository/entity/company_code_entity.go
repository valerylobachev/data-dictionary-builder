// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-18 21:59:59

package entity

import (
  "time"
)

const (
  CompanyCodeTableName = "company_code.company_codes"
  CompanyCodePK = "company_codes_pkey"
  CompanyCodeFKCountryId = "company_codes_country_id"
  CompanyCodeFKLanguageId = "company_codes_language_id"
  CompanyCodeFKCurrencyId = "company_codes_currency_id"
  CompanyCodeFKChartOfAccountsId = "company_codes_chart_of_accounts_id"
  CompanyCodeFKFyVariantId = "company_codes_fy_variant_id"
  CompanyCodeFKPostingPeriodVariantId = "company_codes_posting_period_variant_id"
)


// Company code
type CompanyCodeEntity struct {
    // Company code
    Id string `gorm:"column:id;primaryKey;not null"`
    // Name
    Name string `gorm:"column:name;not null"`
    // Country Id
    CountryId string `gorm:"column:country_id;not null"`
    // Language Id
    LanguageId string `gorm:"column:language_id;not null"`
    // Currency Id
    CurrencyId string `gorm:"column:currency_id;not null"`
    // Chart of accounts id
    ChartOfAccountsId string `gorm:"column:chart_of_accounts_id;not null"`
    // Fiscal year variant id
    FyVariantId string `gorm:"column:fy_variant_id;not null"`
    // Posting period variant id
    PostingPeriodVariantId string `gorm:"column:posting_period_variant_id;not null"`
    // User created record
    CreatedBy string `gorm:"column:created_by;not null"`
    // Timestamp of record create
    CreatedAt time.Time `gorm:"column:created_at;not null"`
    // User updated record
    UpdatedBy string `gorm:"column:updated_by;not null"`
    // Timestamp of record update
    UpdatedAt time.Time `gorm:"column:updated_at;not null"`
}


func (*CompanyCodeEntity) TableName() string {
	return CompanyCodeTableName
}
