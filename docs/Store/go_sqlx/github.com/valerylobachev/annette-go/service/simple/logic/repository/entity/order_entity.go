// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "time"
)


// Order
type OrderEntity struct {
    // Order Id
    Id int `db:"id"`
    // Order name
    Name string `db:"name"`
    // Description
    Description *string `db:"description"`
    // Client Id
    ClientId int `db:"client_id"`
    // Delivery address
    DeliveryAddressId int `db:"delivery_address_id"`
    // Order status
    Status string `db:"status"`
    // Promotion Id
    PromotionId *string `db:"promotion_id"`
    // Segment Id
    SegmentId *string `db:"segment_id"`
    // Business Area Id
    BusinessAreaId *string `db:"business_area_id"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


