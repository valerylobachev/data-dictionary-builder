// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "time"
)

const TableNameMaterialEntity = "logistic.materials"

// Material
type MaterialEntity struct {
    // Material id
    Id string `gorm:"column:id;primaryKey;not null"`
    // Name
    Name string `gorm:"column:name;not null"`
    // Short name
    ShortName string `gorm:"column:short_name;not null"`
    // Unit of measurement Id
    BasicUomId string `gorm:"column:basic_uom_id;not null"`
    // User created record
    CreatedBy string `gorm:"column:created_by;not null"`
    // Timestamp of record create
    CreatedAt time.Time `gorm:"column:created_at;not null"`
    // User updated record
    UpdatedBy string `gorm:"column:updated_by;not null"`
    // Timestamp of record update
    UpdatedAt time.Time `gorm:"column:updated_at;not null"`
}


func (*MaterialEntity) TableName() string {
	return TableNameMaterialEntity
}
