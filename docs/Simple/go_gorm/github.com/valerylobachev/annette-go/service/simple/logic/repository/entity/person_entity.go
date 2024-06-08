// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "time"
)

const TableNamePersonEntity = "persons"

// Person
type PersonEntity struct {
    // Person Id
    Id int `gorm:"column:id;primaryKey;not null"`
    // Person first name
    Firstname string `gorm:"column:firstname;not null"`
    // Person last name
    Lastname string `gorm:"column:lastname;not null"`
    // User updated record
    UpdatedBy string `gorm:"column:updated_by;not null"`
    // Timestamp of record update
    UpdatedAt time.Time `gorm:"column:updated_at;not null"`
}


func (*PersonEntity) TableName() string {
	return TableNamePersonEntity
}
