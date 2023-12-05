package biz.lobachev.annette.data_dictionary.builder.rendering.xls_insert

case class ExcelInsertTemplateTranslation(
  language: String,
  dateFormat: String,
  timeFormat: String
                      )

object ExcelInsertTemplateTranslation {
  var EN = ExcelInsertTemplateTranslation("en", "YYYY-MM-DD", "HH:mm:ss")
  var RU = ExcelInsertTemplateTranslation("ru", "ГГГГ-ММ-ДД", "ЧЧ:мм:сс")

}
