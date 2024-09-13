// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-13 11:22:43

package entity

import (
  "time"
)

const (
  DebtorTableName = "bp.debtors"
  DebtorPK = "debtors_pkey"
)


// Debtor
type DebtorEntity struct {
    // Debtor id
    Id string `gorm:"column:id;primaryKey;not null"`
    // Name
    Name string `gorm:"column:name;not null"`
    // User created record
    CreatedBy string `gorm:"column:created_by;not null"`
    // Timestamp of record create
    CreatedAt time.Time `gorm:"column:created_at;not null"`
    // User updated record
    UpdatedBy string `gorm:"column:updated_by;not null"`
    // Timestamp of record update
    UpdatedAt time.Time `gorm:"column:updated_at;not null"`
}


func (*DebtorEntity) TableName() string {
	return DebtorTableName
}
