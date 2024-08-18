// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-18 21:59:59

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


