#  Finance model

* [Shared data structures](#Shared data structures)
* [Common tables and data structures](#Common tables and data structures)
* [Countries & Languages](#Countries & Languages)
* [Currency](#Currency)
* [Unit of measurement](#Unit of measurement)
* [Finance](#Finance)
* [Company code tables](#Company code tables)
* [General Ledger account settings](#General Ledger account settings)
* [General Ledger settings](#General Ledger settings)
* [Ledger entry tables](#Ledger entry tables)
* [Business Partners](#Business Partners)
* [Analytic tables](#Analytic tables)
* [Logistic tables](#Logistic tables)


Finance model provides tables and data structures similar to SAP ERP Finance.

## Shared data structures

### Modification data structure ("bc"."modifications")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

## Common tables and data structures

## Countries & Languages

### Country ("countries")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(3) | X | X | Country Id |
| name | varchar(100) |  | X | Name |
| key | varchar(3) |  |  | Country Key |
| language_id | varchar(3) |  | X | Language Id |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| language_id | Language ("languages") | id | Many-To-One | Reference to language |

### Language ("languages")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(3) | X | X | Language Id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

## Currency

### Currency ("currencies")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(5) | X | X | Currency Id |
| name | varchar(100) |  | X | Name |
| short_name | varchar(20) |  | X | Short name |
| iso_code | varchar(3) |  | X | Currency ISO code |
| key | varchar(3) |  | X | Currency key |
| decimals | integer |  | X | Currency decimals |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Exchange rate type ("exchange_rate_types")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(4) | X | X | Exchange rate type Id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### ExchangeRate ("exchange_rates")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| exchange_rate_type_id | varchar(4) | X | X | Exchange rate type Id |
| from_currency_id | varchar(5) | X | X | From Currency Id |
| to_currency_id | varchar(5) | X | X | To Currency Id |
| effective_from | date | X | X | Effective from |
| exchange_rate | decimal(25,5) |  | X | Exchange rate |
| ratio_from | integer |  | X | Currency ratio from |
| ratio_to | integer |  | X | Currency ratio to |
| direct_rate | boolean |  | X | Direct ratio indicator |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| exchange_rate_type_id | Exchange rate type ("exchange_rate_types") | id | Many-To-One | Reference to exchange rate type |
| from_currency_id | Currency ("currencies") | id | Many-To-One | Reference (from) to currency |
| to_currency_id | Currency ("currencies") | id | Many-To-One | Reference (to) to currency |

## Unit of measurement

### Unit of measurement ("units_of_measurement")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(6) | X | X | Unit of measurement Id |
| name | varchar(100) |  | X | Name |
| short_name | varchar(30) |  | X | Short name |
| iso_code | varchar(3) |  |  | ISOCode |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Unit of measurement text ("uom_texts")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| language_id | varchar(3) | X | X | Language Id |
| uom_id | varchar(6) | X | X | Unit of measurement Id |
| name | varchar(100) |  | X | Name |
| short_name | varchar(20) |  | X | Short name |
| commercial_code | varchar(6) |  | X | UoM commercial code |
| technical_code | varchar(6) |  | X | UoM technical code |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| language_id | Language ("languages") | id | Many-To-One | Reference to language |
| uom_id | Unit of measurement ("units_of_measurement") | id | Many-To-One | Reference to unit of measurement |

## Finance

## Company code tables

### Company code ("company_code"."company_codes")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(4) | X | X | Company code |
| name | varchar(100) |  | X | Name |
| country_id | varchar(3) |  | X | Country Id |
| language_id | varchar(3) |  | X | Language Id |
| currency_id | varchar(5) |  | X | Currency Id |
| chart_of_accounts_id | varchar(4) |  | X | Chart of accounts id |
| fy_variant_id | varchar(2) |  | X | Fiscal year variant id |
| posting_period_variant_id | varchar(4) |  | X | Posting period variant id |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| country_id | Country ("countries") | id | Many-To-One | Reference to Country |
| language_id | Language ("languages") | id | Many-To-One | Reference to Language |
| currency_id | Currency ("currencies") | id | Many-To-One | Reference to Currency |
| chart_of_accounts_id | Chart of accounts ("gl_account"."charts_of_accounts") | id | Many-To-One | Reference to ChartOfAccounts |
| fy_variant_id | Fiscal year variant ("company_code"."fy_variants") | id | Many-To-One | Reference to FYVariant |
| posting_period_variant_id | Posting period variant ("company_code"."posting_period_variants") | id | Many-To-One | Reference to PostingPeriodVariant |

### Company code ledger ("company_code"."company_code_ledgers")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| company_code_id | varchar(4) | X | X | Company code |
| ledger_id | varchar(4) | X | X | Ledger Id |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| company_code_id | Company code ("company_code"."company_codes") | id | Many-To-One | Reference to CompanyCode |
| ledger_id | Ledger ("ledger"."ledgers") | id | Many-To-One | Reference to Ledger |

### Fiscal year variant ("company_code"."fy_variants")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(2) | X | X | Fiscal year variant id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Posting period variant ("company_code"."posting_period_variants")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(4) | X | X | Posting period variant id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Open period ("company_code"."open_periods")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| posting_period_variant_id | varchar(4) | X | X | Posting period variant id |
| account_type | account_type | X | X | Account type |
| to_account | varchar(10) | X | X | To account |
| from_account | varchar(10) |  | X | From account |
| from_fy1 | integer |  | X | Group 1: From fiscal year |
| from_period1 | integer |  | X | Group 1: From period |
| to_fy1 | integer |  | X | Group 1: To fiscal year |
| to_period1 | integer |  | X | Group 1: To period |
| from_fy2 | integer |  | X | Group 2: From fiscal year |
| from_period2 | integer |  | X | Group 2: From period |
| to_fy2 | integer |  | X | Group 2: To fiscal year |
| to_period2 | integer |  | X | Group 2: To period |
| from_fy3 | integer |  | X | Group 3: From fiscal year |
| from_period3 | integer |  | X | Group 3: From period |
| to_fy3 | integer |  | X | Group 3: To fiscal year |
| to_period3 | integer |  | X | Group 3: To period |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| posting_period_variant_id | Posting period variant ("company_code"."posting_period_variants") | id | Many-To-One | Reference to PostingPeriodVariant |

## General Ledger account settings

### Chart of accounts ("gl_account"."charts_of_accounts")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(4) | X | X | Chart of accounts id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### General ledger account ("gl_account"."gl_accounts")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| chart_of_accounts_id | varchar(4) | X | X | Chart of accounts id |
| gl_account_id | varchar(10) | X | X | General ledger account id |
| short_name | varchar(20) |  | X | Short name |
| name | varchar(100) |  | X | Name |
| gl_account_type | g_l_account_type |  | X | General ledger account type |
| gl_account_group_id | varchar(10) |  | X | General ledger account group id |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| chart_of_accounts_id | Chart of accounts ("gl_account"."charts_of_accounts") | id | Many-To-One | Reference to Chart Of Accounts |
| chart_of_accounts_id<br>gl_account_group_id | General ledger account group ("gl_account"."gl_account_groups") | chart_of_accounts_id<br>gl_account_group_id | Many-To-One | Reference to GLAccountGroup |

### General ledger account group ("gl_account"."gl_account_groups")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| chart_of_accounts_id | varchar(4) | X | X | Chart of accounts id |
| gl_account_group_id | varchar(10) | X | X | General ledger account group id |
| name | varchar(100) |  | X | Name |
| gl_account_id | varchar(10) |  | X | General ledger account id |
| from_gl_account_id | varchar(10) |  | X | From GL account |
| to_gl_account_id | varchar(10) |  | X | To GL account |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| chart_of_accounts_id | Chart of accounts ("gl_account"."charts_of_accounts") | id | Many-To-One | Reference to Chart Of Accounts |

## General Ledger settings

### Ledger ("ledger"."ledgers")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(4) | X | X | Ledger Id |
| name | varchar(100) |  | X | Name |
| leading | boolean |  | X | Leading ledger indicator |
| ledger_type | ledger_type |  | X | Ledger Type |
| underlying_ledger | varchar(4) |  |  | Underlying ledger |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| underlying_ledger | Ledger ("ledger"."ledgers") | id | Many-To-One | Reference to ledger |

### Ledger Group ("ledger"."ledger_groups")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(2) | X | X | Ledger Group Id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Ledger assignments to ledger group ("ledger"."ledger_group_ledgers")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| ledger_group_id | varchar(2) | X | X | Ledger Group Id |
| ledger_id | varchar(4) | X | X | Ledger Id |
| representative | boolean |  | X | Representative ledger indicator |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| ledger_id | Ledger ("ledger"."ledgers") | id | Many-To-One | Reference to ledger |
| ledger_group_id | Ledger Group ("ledger"."ledger_groups") | id | Many-To-One | Reference to ledger group |

## Ledger entry tables

### Ledger Entry ("ledger_entry"."ledger_entries")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| entry_no | uuid | X | X | Entry no |
| ledger_id | varchar(4) |  | X | Ledger Id |
| company_code_id | varchar(4) |  | X | Company code |
| fiscal_year | integer |  | X | Fiscal year |
| document_number | varchar(10) |  | X | Document number |
| line_item_no | varchar(6) |  | X | Line item no |
| posting_date | date |  | X | Posting date |
| document_date | date |  | X | Document date |
| valuation_date | date |  | X | Valuation date |
| period | integer |  | X | Period |
| fy_period | integer |  | X | Fiscal year period |
| chart_of_accounts_id | varchar(4) |  | X | Chart of accounts id |
| gl_account_id | varchar(10) |  | X | General ledger account id |
| creditor_id | varchar(10) |  |  | Creditor id |
| debtor_id | varchar(10) |  |  | Debtor id |
| business_partner_id | varchar(10) |  |  | Business partner id |
| business_area_id | varchar(4) |  |  | Business area id |
| functional_area_id | varchar(20) |  |  | Functional area id |
| segment_id | varchar(10) |  |  | Segment id |
| valuation_area_id | varchar(20) |  |  | Valuation area id |
| plant_id | varchar(4) |  |  | Plant id |
| location_id | varchar(10) |  |  | Location id |
| material_id | varchar(40) |  |  | Material id |
| debit_credit | debit_credit |  | X | Debit/credit indicator |
| document_amount | decimal(25,2) |  | X | Document amount |
| document_currency_id | varchar(5) |  | X | Document currency |
| company_code_amount | decimal(25,2) |  | X | Company code amount |
| company_code_currency_id | varchar(5) |  | X | Company code currency |
| quantity | decimal(25,3) |  |  | Quantity |
| uom_id | varchar(6) |  |  | Unit of measure id |
| basic_uom_id | varchar(6) |  |  | Basic unit of measure id |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Indexes**

| Fields | Unique | Description|
| ------- | ------- | ------- |
| ledger_id<br>company_code_id<br>fiscal_year<br>document_number<br>line_item_no | X | Ledger entry natural key |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| ledger_id | Ledger ("ledger"."ledgers") | id | Many-To-One | Reference to Ledger |
| company_code_id | Company code ("company_code"."company_codes") | id | Many-To-One | Reference to CompanyCode |
| chart_of_accounts_id<br>gl_account_id | General ledger account ("gl_account"."gl_accounts") | chart_of_accounts_id<br>gl_account_id | Many-To-One | Reference to GLAccount |
| creditor_id | Creditor ("bp"."creditors") | id | Many-To-One | Reference to Creditor |
| debtor_id | Debtor ("bp"."debtors") | id | Many-To-One | Reference to Debtor |
| business_partner_id | Business partner ("bp"."business_partners") | id | Many-To-One | Reference to BusinessPartner |
| business_area_id | Business area ("analytics"."business_areas") | id | Many-To-One | Reference to BusinessArea |
| functional_area_id | Functional area ("analytics"."functional_areas") | id | Many-To-One | Reference to FunctionalArea |
| segment_id | Segment ("analytics"."segments") | id | Many-To-One | Reference to Segment |
| valuation_area_id | Valuation area ("analytics"."valuation_areas") | id | Many-To-One | Reference to ValuationArea |
| plant_id | Plant ("logistic"."plants") | id | Many-To-One | Reference to Plant |
| plant_id<br>location_id | Location ("logistic"."locations") | plant_id<br>location_id | Many-To-One | Reference to Location |
| material_id | Material ("logistic"."materials") | id | Many-To-One | Reference to Material |
| document_currency_id | Currency ("currencies") | id | Many-To-One | Reference to Currency |
| company_code_currency_id | Currency ("currencies") | id | Many-To-One | Reference to Currency |
| uom_id | Unit of measurement ("units_of_measurement") | id | Many-To-One | Reference to UnitOfMeasurement |
| basic_uom_id | Unit of measurement ("units_of_measurement") | id | Many-To-One | Reference to UnitOfMeasurement |

### Ledger entry attribute ("ledger_entry"."ledger_entry_attributes")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| entry_no | uuid | X | X | Entry no |
| attribute_id | varchar(10) | X | X | Attribute id |
| attribute_value_id | varchar(20) | X | X | Attribute value id |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| entry_no | Ledger Entry ("ledger_entry"."ledger_entries") | entry_no | Many-To-One | Reference to LedgerEntry |
| attribute_id<br>attribute_value_id | Attribute value ("analytics"."attribute_values") | attribute_id<br>attribute_value_id | Many-To-One | Reference to AttributeValue |

## Business Partners

### Creditor ("bp"."creditors")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(10) | X | X | Creditor id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Debtor ("bp"."debtors")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(10) | X | X | Debtor id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Business partner ("bp"."business_partners")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(10) | X | X | Business partner id |
| name | varchar(100) |  | X | Name |
| creditor_id | varchar(10) |  |  | Creditor id |
| debtor_id | varchar(10) |  |  | Debtor id |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Indexes**

| Fields | Unique | Description|
| ------- | ------- | ------- |
| creditor_id | X | Unique creditorId |
| debtor_id | X | Unique debtorId |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| creditor_id | Creditor ("bp"."creditors") | id | One-To-One | Reference to Creditor |
| debtor_id | Debtor ("bp"."debtors") | id | One-To-One | Reference to Debtor |

## Analytic tables

### Business area ("analytics"."business_areas")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(4) | X | X | Business area id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Functional area ("analytics"."functional_areas")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(20) | X | X | Functional area id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Segment ("analytics"."segments")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(10) | X | X | Segment id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Attribute ("analytics"."attributes")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(10) | X | X | Attribute id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Attribute value ("analytics"."attribute_values")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| attribute_id | varchar(10) | X | X | Attribute id |
| attribute_value_id | varchar(20) | X | X | Attribute value id |
| name | varchar(100) |  | X | Name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| attribute_id | Attribute ("analytics"."attributes") | id | Many-To-One | Relation to Attribute |

### Valuation area ("analytics"."valuation_areas")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(20) | X | X | Valuation area id |
| name | varchar(100) |  | X | Name |
| short_name | varchar(20) |  | X | Short name |
| company_code_id | varchar(4) |  | X | Company code |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| company_code_id | Company code ("company_code"."company_codes") | id | Many-To-One | Reference to CompanyCode |

## Logistic tables

### Material ("logistic"."materials")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(40) | X | X | Material id |
| name | varchar(100) |  | X | Name |
| short_name | varchar(20) |  | X | Short name |
| basic_uom_id | varchar(6) |  | X | Unit of measurement Id |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| basic_uom_id | Unit of measurement ("units_of_measurement") | id | Many-To-One | Reference to Unit Of Measurement |

### Plant ("logistic"."plants")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| id | varchar(4) | X | X | Plant id |
| name | varchar(100) |  | X | Name |
| short_name | varchar(20) |  | X | Short name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |

### Location ("logistic"."locations")

| Field  | Data type | PK | Required | Description |
| ------- | ------- | ------- | ------- | ------- |
| plant_id | varchar(4) | X | X | Plant id |
| location_id | varchar(10) | X | X | Location id |
| name | varchar(100) |  | X | Name |
| short_name | varchar(20) |  | X | Short name |
| created_by | varchar(20) |  | X | User created record |
| created_at | timestamptz |  | X | Timestamp of record create |
| updated_by | varchar(20) |  | X | User updated record |
| updated_at | timestamptz |  | X | Timestamp of record update |


**Relations**

| Fields  | Related table | Related fields | Type | Description|
| ------- | ------- | ------- | ------- | ------- |
| plant_id | Plant ("logistic"."plants") | id | Many-To-One | Reference to Plant |

