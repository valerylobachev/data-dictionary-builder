// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "time"
)

const TableNameLedgerEntity = "ledger.ledgers"

// Ledger
type LedgerEntity struct {
    // Ledger Id
    Id string `gorm:"column:id;primaryKey;not null"`
    // Name
    Name string `gorm:"column:name;not null"`
    // Leading ledger indicator
    Leading bool `gorm:"column:leading;not null"`
    // Ledger Type
    LedgerType string `gorm:"column:ledger_type;not null"`
    // Underlying ledger
    UnderlyingLedger *string `gorm:"column:underlying_ledger"`
    // User created record
    CreatedBy string `gorm:"column:created_by;not null"`
    // Timestamp of record create
    CreatedAt time.Time `gorm:"column:created_at;not null"`
    // User updated record
    UpdatedBy string `gorm:"column:updated_by;not null"`
    // Timestamp of record update
    UpdatedAt time.Time `gorm:"column:updated_at;not null"`
}


func (*LedgerEntity) TableName() string {
	return TableNameLedgerEntity
}
