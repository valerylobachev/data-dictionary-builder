// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "time"
)


// Ledger
type LedgerEntity struct {
    // Ledger Id
    Id string `db:"id"`
    // Name
    Name string `db:"name"`
    // Leading ledger indicator
    Leading bool `db:"leading"`
    // Ledger Type
    LedgerType string `db:"ledger_type"`
    // Underlying ledger
    UnderlyingLedger *string `db:"underlying_ledger"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


