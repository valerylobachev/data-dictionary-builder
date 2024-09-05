// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-19 17:06:07

package entity

import (
  "time"
)

const (
  BusinessPartnerTableName = "bp.business_partners"
  BusinessPartnerPK = "business_partners_pkey"
  BusinessPartnerFKCreditorId = "business_partners_creditor_id"
  BusinessPartnerFKDebtorId = "business_partners_debtor_id"
  BusinessPartnerUQCreditorId = "business_partners_creditor_id"
  BusinessPartnerUQDebtorId = "business_partners_debtor_id"
)


// Business partner
type BusinessPartnerEntity struct {
    // Business partner id
    Id string `db:"id"`
    // Name
    Name string `db:"name"`
    // Creditor id
    CreditorId *string `db:"creditor_id"`
    // Debtor id
    DebtorId *string `db:"debtor_id"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}

