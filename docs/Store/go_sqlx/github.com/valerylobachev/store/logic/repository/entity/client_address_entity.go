// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-13 11:22:52

package entity

import (
  "time"
)

const (
  ClientAddressTableName = "client.client_client_addresses_table"
  ClientAddressPK = "client_client_addresses_table_pkey"
  ClientAddressFKClientId = "client_client_addresses_table_client_id"
  ClientAddressUQClientIdId = "client_client_addresses_table_client_id_id"
)


// Client address
type ClientAddressEntity struct {
    // Address Id
    Id int `db:"id"`
    // Address name
    Name string `db:"name"`
    // Description
    Description *string `db:"description"`
    // Client Id
    ClientId int `db:"client_id"`
    // First address line
    AddressLine1 string `db:"address_line1"`
    // Second address line
    AddressLine2 *string `db:"address_line2"`
    // City
    City string `db:"city"`
    // State
    State string `db:"state"`
    // Country
    Country string `db:"country"`
    // Post code
    Postcode string `db:"postcode"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


