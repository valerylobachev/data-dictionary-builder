// Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder)
// Date: 2024-09-05 17:08:13

package entity

import (
  "time"
)

const (
  GroupMemberTableName = "group_members"
  GroupMemberPK = "group_members_pkey"
  GroupMemberFKGroupId = "group_members_group_id"
  GroupMemberUQPersonGroupIds = "group_members_person_group_ids"
)


// Group member
type GroupMemberEntity struct {
    // Group member id
    Id int `db:"id"`
    // Group Id
    GroupId int16 `db:"group_id"`
    // Person Id
    PersonId int `db:"person_id"`
    // User updated record
    UpdatedBy string `db:"updated_by"`
    // Timestamp of record update
    UpdatedAt time.Time `db:"updated_at"`
}


