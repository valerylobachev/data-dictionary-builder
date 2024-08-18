// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-18 21:59:59

package entity

import (
  "time"
)

const (
  UnitOfMeasurementTextTableName = "uom_texts"
  UnitOfMeasurementTextPK = "uom_texts_pkey"
  UnitOfMeasurementTextFKLanguageId = "uom_texts_language_id"
  UnitOfMeasurementTextFKUomId = "uom_texts_uom_id"
)


// Unit of measurement text
type UnitOfMeasurementTextEntity struct {
    // Language Id
    LanguageId string `db:"language_id"`
    // Unit of measurement Id
    UomId string `db:"uom_id"`
    // Name
    Name string `db:"name"`
    // Short name
    ShortName string `db:"short_name"`
    // UoM commercial code
    CommercialCode string `db:"commercial_code"`
    // UoM technical code
    TechnicalCode string `db:"technical_code"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


