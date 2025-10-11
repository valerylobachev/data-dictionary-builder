package biz.lobachev.annette.data_dictionary.generators.xls_domain

import org.apache.poi.xssf.usermodel.XSSFWorkbook


case class DomainWorkbook(
  wb: XSSFWorkbook,
  domain: Sheet,
  components: Sheet,
  entities: Sheet,
  fields: Sheet,
  indexes: Sheet,
  indexFields: Sheet,
  relations: Sheet,
  relationFields: Sheet,
  dataElements: Sheet,
  enums: Sheet,
  enumItems: Sheet,
)
