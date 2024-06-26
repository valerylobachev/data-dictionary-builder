// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity


const TableNameOpenPeriodEntity = "company_code.open_periods"

// Open period
type OpenPeriodEntity struct {
    // Posting period variant id
    PostingPeriodVariantId string `gorm:"column:posting_period_variant_id;primaryKey;not null"`
    // Account type
    AccountType string `gorm:"column:account_type;primaryKey;not null"`
    // To account
    ToAccount string `gorm:"column:to_account;primaryKey;not null"`
    // From account
    FromAccount string `gorm:"column:from_account;not null"`
    // Group 1: From fiscal year
    FromFy1 int `gorm:"column:from_fy1;not null"`
    // Group 1: From period
    FromPeriod1 int `gorm:"column:from_period1;not null"`
    // Group 1: To fiscal year
    ToFy1 int `gorm:"column:to_fy1;not null"`
    // Group 1: To period
    ToPeriod1 int `gorm:"column:to_period1;not null"`
    // Group 2: From fiscal year
    FromFy2 int `gorm:"column:from_fy2;not null"`
    // Group 2: From period
    FromPeriod2 int `gorm:"column:from_period2;not null"`
    // Group 2: To fiscal year
    ToFy2 int `gorm:"column:to_fy2;not null"`
    // Group 2: To period
    ToPeriod2 int `gorm:"column:to_period2;not null"`
    // Group 3: From fiscal year
    FromFy3 int `gorm:"column:from_fy3;not null"`
    // Group 3: From period
    FromPeriod3 int `gorm:"column:from_period3;not null"`
    // Group 3: To fiscal year
    ToFy3 int `gorm:"column:to_fy3;not null"`
    // Group 3: To period
    ToPeriod3 int `gorm:"column:to_period3;not null"`
}


func (*OpenPeriodEntity) TableName() string {
	return TableNameOpenPeriodEntity
}
