// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-13 11:22:44

package entity

import (
  "github.com/shopspring/decimal"
  "time"
)

const (
  ExchangeRateTableName = "exchange_rates"
  ExchangeRatePK = "exchange_rates_pkey"
  ExchangeRateFKExchangeRateTypeId = "exchange_rates_exchange_rate_type_id"
  ExchangeRateFKFromCurrencyId = "exchange_rates_from_currency_id"
  ExchangeRateFKToCurrencyId = "exchange_rates_to_currency_id"
)


// ExchangeRate
type ExchangeRateEntity struct {
    // Exchange rate type Id
    ExchangeRateTypeId string `db:"exchange_rate_type_id"`
    // From Currency Id
    FromCurrencyId string `db:"from_currency_id"`
    // To Currency Id
    ToCurrencyId string `db:"to_currency_id"`
    // Effective from
    EffectiveFrom string `db:"effective_from"`
    // Exchange rate
    ExchangeRate decimal.Decimal `db:"exchange_rate"`
    // Currency ratio from
    RatioFrom int `db:"ratio_from"`
    // Currency ratio to
    RatioTo int `db:"ratio_to"`
    // Direct ratio indicator
    DirectRate bool `db:"direct_rate"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


