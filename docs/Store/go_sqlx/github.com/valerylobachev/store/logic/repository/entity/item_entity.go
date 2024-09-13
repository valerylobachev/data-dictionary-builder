// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-13 11:22:52

package entity

import (
  "github.com/shopspring/decimal"
  "time"
)

const (
  ItemTableName = "order_items_table"
  ItemPK = "order_items_table_pkey"
)


// Item
type ItemEntity struct {
    // Item Id
    Id int `db:"id"`
    // Item Id
    ItemId int `db:"item_id"`
    // Amount
    Price decimal.Decimal `db:"price"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


