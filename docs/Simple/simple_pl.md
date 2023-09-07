#  Simple Example

* [Shared data structures](#Shared data structures)
* [Person Group Model](#Person Group Model)


This example provides simple person group data model

## Shared data structures

### Modification data structure ("modifications")

| Kolumna | Typ danych | KG | Wymagane | Opis |
| ------- | ------- | ------- | ------- | ------- |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

## Person Group Model

### Person ("persons")

| Kolumna | Typ danych | KG | Wymagane | Opis |
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X | Person Id |
| firstname | varchar(40) |  | X | Person first name |
| lastname | varchar(40) |  | X | Person last name |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Indeksy**

| Kolumny | Уникальный | Opis|
| ------- | ------- | ------- |
| lastname<br>firstname |  | Search index by lastname and firstname |

### Group ("groups")

| Kolumna | Typ danych | KG | Wymagane | Opis |
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X | Group Id |
| name | varchar(100) |  | X | Group name |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Group member ("group_members")

| Kolumna | Typ danych | KG | Wymagane | Opis |
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X |  Group member id |
| group_id | integer |  | X | Group Id |
| person_id | integer |  | X | Person Id |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Indeksy**

| Kolumny | Уникальный | Opis|
| ------- | ------- | ------- |
| group_id<br>person_id | X | Person id in each group must be unique |


**Relacji**

| Kolumny  | Powiązana tabela | Powiązany kolumny | Typ | Opis|
| ------- | ------- | ------- | ------- | ------- |
| group_id | Group ("groups") | id | Many-To-One | Relation to groups |
| person_id | Person ("persons") | id | Many-To-One | Relation to persons |

