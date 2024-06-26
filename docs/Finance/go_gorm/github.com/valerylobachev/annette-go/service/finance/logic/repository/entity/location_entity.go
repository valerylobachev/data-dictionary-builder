// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "time"
)

const TableNameLocationEntity = "logistic.locations"

// Location
type LocationEntity struct {
    // Plant id
    PlantId string `gorm:"column:plant_id;primaryKey;not null"`
    // Location id
    LocationId string `gorm:"column:location_id;primaryKey;not null"`
    // Name
    Name string `gorm:"column:name;not null"`
    // Short name
    ShortName string `gorm:"column:short_name;not null"`
    // User created record
    CreatedBy string `gorm:"column:created_by;not null"`
    // Timestamp of record create
    CreatedAt time.Time `gorm:"column:created_at;not null"`
    // User updated record
    UpdatedBy string `gorm:"column:updated_by;not null"`
    // Timestamp of record update
    UpdatedAt time.Time `gorm:"column:updated_at;not null"`
}


func (*LocationEntity) TableName() string {
	return TableNameLocationEntity
}
