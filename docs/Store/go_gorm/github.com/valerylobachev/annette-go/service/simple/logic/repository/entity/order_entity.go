// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "time"
)

const TableNameOrderEntity = "order_orders_table"

// Order
type OrderEntity struct {
    // Order Id
    Id int `gorm:"column:id;primaryKey;not null"`
    // Order name
    Name string `gorm:"column:name;not null"`
    // Description
    Description *string `gorm:"column:description"`
    // Client Id
    ClientId int `gorm:"column:client_id;not null"`
    // Delivery address
    DeliveryAddressId int `gorm:"column:delivery_address_id;not null"`
    // Order status
    Status string `gorm:"column:status;not null"`
    // Promotion Id
    PromotionId *string `gorm:"column:promotion_id"`
    // Segment Id
    SegmentId *string `gorm:"column:segment_id"`
    // Business Area Id
    BusinessAreaId *string `gorm:"column:business_area_id"`
    // User created record
    CreatedBy string `gorm:"column:created_by;not null"`
    // Timestamp of record create
    CreatedAt time.Time `gorm:"column:created_at;not null"`
    // User updated record
    UpdatedBy string `gorm:"column:updated_by;not null"`
    // Timestamp of record update
    UpdatedAt time.Time `gorm:"column:updated_at;not null"`
}


func (*OrderEntity) TableName() string {
	return TableNameOrderEntity
}
