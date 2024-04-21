package biz.lobachev.annette.data_dictionary.builder.rendering.xls_domain

import org.apache.poi.xssf.usermodel.{XSSFSheet, XSSFWorkbook}

case class Sheet(
  name: String,
  sheet: XSSFSheet,
)
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
