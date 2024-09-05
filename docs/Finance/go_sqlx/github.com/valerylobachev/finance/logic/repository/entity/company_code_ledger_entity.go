// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-19 17:06:07

package entity

import (
  "time"
)

const (
  CompanyCodeLedgerTableName = "company_code.company_code_ledgers"
  CompanyCodeLedgerPK = "company_code_ledgers_pkey"
  CompanyCodeLedgerFKCompanyCodeId = "company_code_ledgers_company_code_id"
  CompanyCodeLedgerFKLedgerId = "company_code_ledgers_ledger_id"
)


// Company code ledger
type CompanyCodeLedgerEntity struct {
    // Company code
    CompanyCodeId string `db:"company_code_id"`
    // Ledger Id
    LedgerId string `db:"ledger_id"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}

