// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-13 11:22:52

package entity

import (
  "time"
)

const (
  SegmentTableName = "analytics.analytics_segments_table"
  SegmentPK = "analytics_segments_table_pkey"
)


// Segment
type SegmentEntity struct {
    // Segment Id
    Id string `db:"id"`
    // Name
    Name string `db:"name"`
    // User created record
    CreatedBy string `db:"created_by"`
    // Timestamp of record create
    CreatedAt time.Time `db:"created_at"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


