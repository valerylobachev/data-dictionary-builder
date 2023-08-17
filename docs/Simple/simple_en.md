#  Simple Example


This example provides simple person group data model

## Shared data structures

### Modification data structure ("modifications")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

## Person Group Model

### Person ("persons")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X | Person Id |
| firstname | varchar(40) |  | X | Person first name |
| lastname | varchar(40) |  | X | Person last name |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Indexes**

| Fields | Unique | Description|
| ------- | ------- | ------- |
| lastname<br>firstname |  | Search index by lastname and firstname |

### Group ("groups")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X | Group Id |
| name | varchar(100) |  | X | Group name |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Group member ("group_members")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X |  Group member id |
| group_id | integer |  | X | Group Id |
| person_id | integer |  | X | Person Id |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Indexes**

| Fields | Unique | Description|
| ------- | ------- | ------- |
| group_id<br>person_id | X | Person id in each group must be unique |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| group_id | Group ("groups") | id | Many-To-One | Relation to groups |
| person_id | Person ("persons") | id | Many-To-One | Relation to persons |

