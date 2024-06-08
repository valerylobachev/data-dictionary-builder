// Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).

package entity

import (
  "time"
)


// Location
type LocationEntity struct {
    // Plant id
    PlantId string `db:"plant_id"`
    // Location id
    LocationId string `db:"location_id"`
    // Name
    Name string `db:"name"`
    // Short name
    ShortName string `db:"short_name"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


