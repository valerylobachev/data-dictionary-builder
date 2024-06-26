// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "time"
)

const TableNameModificationEntity = "modifications"

// Modification data structure
type ModificationEntity struct {
    // User updated record
    UpdatedBy string `gorm:"column:updated_by;not null"`
    // Timestamp of record update
    UpdatedAt time.Time `gorm:"column:updated_at;not null"`
}


func (*ModificationEntity) TableName() string {
	return TableNameModificationEntity
}
