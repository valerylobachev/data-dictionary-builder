// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "time"
)

const (
  AttributeTableName = "analytics.attributes"
  AttributePK = "attributes_pkey"
)


// Attribute
type AttributeEntity struct {
    // Attribute id
    Id string `db:"id"`
    // Name
    Name string `db:"name"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


