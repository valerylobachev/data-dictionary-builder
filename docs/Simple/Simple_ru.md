#  Simple Example

* [Shared data structures](#Shared data structures)
* [Person Group Model](#Person Group Model)


This example provides simple person group data model

## Shared data structures

### Modification data structure ("modifications")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

## Person Group Model

### Person ("persons")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X | Person Id |
| firstname | varchar(40) |  | X | Person first name |
| lastname | varchar(40) |  | X | Person last name |
| search | text |  | X | Search index |
| attributes | jsonb |  | X | Person attributes<br>Map string: string |
| external_ids | jsonb |  | X | Person external Ids |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Индексы**

| Поля | Уникальный | Описание|
| ------- | ------- | ------- |
| lastname<br>firstname |  | Search index by lastname and firstname |

### Group ("groups")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| id | smallint | X | X | Group Id |
| name | varchar(100) |  | X | Group name |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Group member ("group_members")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X | Group member id |
| group_id | smallint |  | X | Group Id |
| person_id | integer |  | X | Person Id |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Индексы**

| Поля | Уникальный | Описание|
| ------- | ------- | ------- |
| group_id<br>person_id | X | Person id in each group must be unique |


**Связи**

| Поля  | Связанная таблица | Связанные поля | Тип | Описание|
| ------- | ------- | ------- | ------- | ------- |
| group_id | Group ("groups") | id | Many-To-One | Relation to groups |
| person_id | Person ("persons") | id | Many-To-One | Relation to persons |

