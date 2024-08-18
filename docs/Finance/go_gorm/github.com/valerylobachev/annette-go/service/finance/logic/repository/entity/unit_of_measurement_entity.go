// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-18 21:59:59

package entity

import (
  "time"
)

const (
  UnitOfMeasurementTableName = "units_of_measurement"
  UnitOfMeasurementPK = "units_of_measurement_pkey"
)


// Unit of measurement
type UnitOfMeasurementEntity struct {
    // Unit of measurement Id
    Id string `gorm:"column:id;primaryKey;not null"`
    // Name
    Name string `gorm:"column:name;not null"`
    // Short name
    ShortName string `gorm:"column:short_name;not null"`
    // ISOCode
    IsoCode *string `gorm:"column:iso_code"`
    // User created record
    CreatedBy string `gorm:"column:created_by;not null"`
    // Timestamp of record create
    CreatedAt time.Time `gorm:"column:created_at;not null"`
    // User updated record
    UpdatedBy string `gorm:"column:updated_by;not null"`
    // Timestamp of record update
    UpdatedAt time.Time `gorm:"column:updated_at;not null"`
}


func (*UnitOfMeasurementEntity) TableName() string {
	return UnitOfMeasurementTableName
}
