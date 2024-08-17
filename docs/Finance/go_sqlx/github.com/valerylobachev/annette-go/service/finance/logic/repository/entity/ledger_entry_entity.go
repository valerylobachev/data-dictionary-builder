// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "github.com/shopspring/decimal"
  "time"
)


// Ledger Entry
type LedgerEntryEntity struct {
    // Entry no
    EntryNo string `db:"entry_no"`
    // Ledger Id
    LedgerId string `db:"ledger_id"`
    // Company code
    CompanyCodeId string `db:"company_code_id"`
    // Fiscal year
    FiscalYear int `db:"fiscal_year"`
    // Document number
    DocumentNumber string `db:"document_number"`
    // Line item no
    LineItemNo string `db:"line_item_no"`
    // Posting date
    PostingDate string `db:"posting_date"`
    // Document date
    DocumentDate string `db:"document_date"`
    // Valuation date
    ValuationDate string `db:"valuation_date"`
    // Period
    Period int `db:"period"`
    // Fiscal year period
    FyPeriod int `db:"fy_period"`
    // Chart of accounts id
    ChartOfAccountsId string `db:"chart_of_accounts_id"`
    // General ledger account id
    GlAccountId string `db:"gl_account_id"`
    // Creditor id
    CreditorId *string `db:"creditor_id"`
    // Debtor id
    DebtorId *string `db:"debtor_id"`
    // Business partner id
    BusinessPartnerId *string `db:"business_partner_id"`
    // Business area id
    BusinessAreaId *string `db:"business_area_id"`
    // Functional area id
    FunctionalAreaId *string `db:"functional_area_id"`
    // Segment id
    SegmentId *string `db:"segment_id"`
    // Valuation area id
    ValuationAreaId *string `db:"valuation_area_id"`
    // Plant id
    PlantId *string `db:"plant_id"`
    // Location id
    LocationId *string `db:"location_id"`
    // Material id
    MaterialId *string `db:"material_id"`
    // Debit/credit indicator
    DebitCredit string `db:"debit_credit"`
    // Document amount
    DocumentAmount decimal.Decimal `db:"document_amount"`
    // Document currency
    DocumentCurrencyId string `db:"document_currency_id"`
    // Company code amount
    CompanyCodeAmount decimal.Decimal `db:"company_code_amount"`
    // Company code currency
    CompanyCodeCurrencyId string `db:"company_code_currency_id"`
    // Quantity
    Quantity *decimal.Decimal `db:"quantity"`
    // Unit of measure id
    UomId *string `db:"uom_id"`
    // Basic unit of measure id
    BasicUomId *string `db:"basic_uom_id"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}

