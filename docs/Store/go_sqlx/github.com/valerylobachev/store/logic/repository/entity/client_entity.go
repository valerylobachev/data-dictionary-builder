// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-08-19 17:06:09

package entity

import (
  "time"
)

const (
  ClientTableName = "client.client_clients_table"
  ClientPK = "client_clients_table_pkey"
)


// Client
type ClientEntity struct {
    // Client Id
    Id int `db:"id"`
    // Client name
    Name string `db:"name"`
    // Description
    Description *string `db:"description"`
    // First address line
    BillingAddressLine1 string `db:"billing_address_line1"`
    // Second address line
    BillingAddressLine2 *string `db:"billing_address_line2"`
    // City
    BillingCity string `db:"billing_city"`
    // State
    BillingState string `db:"billing_state"`
    // Country
    BillingCountry string `db:"billing_country"`
    // Post code
    BillingPostcode string `db:"billing_postcode"`
    // First address line
    PostalAddressLine1 string `db:"postal_address_line1"`
    // Second address line
    PostalAddressLine2 *string `db:"postal_address_line2"`
    // City
    PostalCity string `db:"postal_city"`
    // State
    PostalState string `db:"postal_state"`
    // Country
    PostalCountry string `db:"postal_country"`
    // Post code
    PostalPostcode string `db:"postal_postcode"`
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

