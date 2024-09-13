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
    Id int `gorm:"column:id;primaryKey;not null"`
    // Item Id
    ItemId int `gorm:"column:item_id;not null"`
    // Amount
    Price decimal.Decimal `gorm:"column:price;not null"`
    // User created record
    CreatedBy string `gorm:"column:created_by;not null"`
    // Timestamp of record create
    CreatedAt time.Time `gorm:"column:created_at;not null"`
    // User updated record
    UpdatedBy string `gorm:"column:updated_by;not null"`
    // Timestamp of record update
    UpdatedAt time.Time `gorm:"column:updated_at;not null"`
}


func (*ItemEntity) TableName() string {
	return ItemTableName
}
