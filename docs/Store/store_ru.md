#  Store example


Store data model example

## Shared data structures

### Modification data structure ("modifications")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| created_by | varchar(10) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(10) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Address data structure ("addresses")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| address_line1 | varchar(100) |  | X | First address line |
| address_line2 | varchar(100) |  |  | Second address line |
| city | varchar(50) |  | X | City |
| state | varchar(50) |  | X | State |
| country | varchar(50) |  | X | Country |
| postcode | varchar(10) |  | X | Post code |

### Analytics data structure ("analyticses")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| promotion_id | varchar(10) |  |  | Promotion Id |
| segment_id | varchar(10) |  |  | Segment Id |
| business_area_id | varchar(10) |  |  | Business Area Id |


**Связи**

| Поля  | Связанная таблица | Связанные поля | Тип | Описание|
| ------- | ------- | ------- | ------- | ------- |
| promotion_id | Promotion ("analytics"."promotions") | id | Many-To-One | Reference to Promotion |
| segment_id | Segment ("analytics"."segments") | id | Many-To-One | Reference to Segment |
| business_area_id | Business area ("analytics"."business_areas") | id | Many-To-One | Reference to BusinessArea |

## Client tables

### Client ("client"."clients")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X | Client Id |
| name | varchar(100) |  | X | Client name |
| description | text |  |  | Description |
| billing_address_line1 | varchar(100) |  | X | First address line |
| billing_address_line2 | varchar(100) |  |  | Second address line |
| billing_city | varchar(50) |  | X | City |
| billing_state | varchar(50) |  | X | State |
| billing_country | varchar(50) |  | X | Country |
| billing_postcode | varchar(10) |  | X | Post code |
| postal_address_line1 | varchar(100) |  | X | First address line |
| postal_address_line2 | varchar(100) |  |  | Second address line |
| postal_city | varchar(50) |  | X | City |
| postal_state | varchar(50) |  | X | State |
| postal_country | varchar(50) |  | X | Country |
| postal_postcode | varchar(10) |  | X | Post code |
| promotion_id | varchar(10) |  |  | Promotion Id |
| segment_id | varchar(10) |  |  | Segment Id |
| business_area_id | varchar(10) |  |  | Business Area Id |
| created_by | varchar(10) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(10) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Client address ("client"."client_addresses")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X | Address Id |
| name | varchar(100) |  | X | Address name |
| description | text |  |  | Description |
| client_id | integer |  | X | Client Id |
| address_line1 | varchar(100) |  | X | First address line |
| address_line2 | varchar(100) |  |  | Second address line |
| city | varchar(50) |  | X | City |
| state | varchar(50) |  | X | State |
| country | varchar(50) |  | X | Country |
| postcode | varchar(10) |  | X | Post code |
| created_by | varchar(10) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(10) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Индексы**

| Поля | Уникальный | Описание|
| ------- | ------- | ------- |
| client_id<br>id | X | Unique clientId & id |


**Связи**

| Поля  | Связанная таблица | Связанные поля | Тип | Описание|
| ------- | ------- | ------- | ------- | ------- |
| client_id | Client ("client"."clients") | id | Many-To-One | Reference to client |

## Order tables

### Order ("orders")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X | Order Id |
| name | varchar(100) |  | X | Order name |
| description | text |  |  | Description |
| client_id | integer |  | X | Client Id |
| delivery_address_id | integer |  | X | Delivery address |
| status | order_status |  | X | Order status |
| promotion_id | varchar(10) |  |  | Promotion Id |
| segment_id | varchar(10) |  |  | Segment Id |
| business_area_id | varchar(10) |  |  | Business Area Id |
| created_by | varchar(10) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(10) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Связи**

| Поля  | Связанная таблица | Связанные поля | Тип | Описание|
| ------- | ------- | ------- | ------- | ------- |
| client_id | Client ("client"."clients") | id | Many-To-One | Reference to client |
| client_id<br>delivery_address_id | Client address ("client"."client_addresses") | client_id<br>id | Many-To-One | Reference to address |

### Order line ("order_lines")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X | OrderLine Id |
| order_id | integer |  | X | Order Id |
| item_id | integer |  | X | Item Id |
| promotion_id | varchar(10) |  |  | Promotion Id |
| segment_id | varchar(10) |  |  | Segment Id |
| business_area_id | varchar(10) |  |  | Business Area Id |
| price | decimal(10,2) |  | X | Amount |
| quantity | decimal(10,2) |  | X | Quantity |
| created_by | varchar(10) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(10) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Связи**

| Поля  | Связанная таблица | Связанные поля | Тип | Описание|
| ------- | ------- | ------- | ------- | ------- |
| order_id | Order ("orders") | id | Many-To-One | Reference to order |
| item_id | Item ("items") | id | Many-To-One | Reference to item |

### Item ("items")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| id | serial | X | X | Item Id |
| item_id | integer |  | X | Item Id |
| price | decimal(10,2) |  | X | Amount |
| created_by | varchar(10) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(10) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

## Analytic tables

### Segment ("analytics"."segments")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(10) | X | X | Segment Id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(10) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(10) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Business area ("analytics"."business_areas")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(10) | X | X | Business Area Id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(10) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(10) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Promotion ("analytics"."promotions")

| Поле  | Тип данных | ПК | Обязательно| Описание|
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(10) | X | X | Promotion Id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(10) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(10) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

