package biz.lobachev.annette.data_dictionary.generators.xls_domain

import org.apache.poi.xssf.usermodel.XSSFSheet

case class Sheet(
  name: String,
  sheet: XSSFSheet,
)
