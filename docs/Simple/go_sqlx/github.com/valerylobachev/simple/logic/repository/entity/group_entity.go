// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-13 11:22:51

package entity

import (
  "time"
)

const (
  GroupTableName = "groups"
  GroupPK = "groups_pkey"
)


// Group
type GroupEntity struct {
    // Group Id
    Id int16 `db:"id"`
    // Group name
    Name string `db:"name"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


