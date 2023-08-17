package biz.lobachev.annette.data_dictionary.builder.utils

import com.google.common.base.CaseFormat
import org.atteo.evo.inflector.English

object StringSyntax {

  implicit class PascalCase(s: String) {
    def pascalCase = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, s.trim)
  }

  implicit class CamelCase(s: String) {
    def camelCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, s.trim)
  }

  implicit class SnakeCase(s: String) {
    def snakeCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, s.trim)
  }

  implicit class KebabCase(s: String) {
    def kebabCase = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, s.trim)
  }

  implicit class Pluralize(s: String) {
    def pluralize = English.plural(s)
  }

  implicit class MdNewLines(s: String) {
    def mdReplaceNL = s.replace("\n", "<br>")
  }

  implicit class RemoveBR(s: String) {
    def replaceBR = s.replace("<br>", "\n")
  }

  def wrapQuotes(s: String): String = s"\"$s\""
//    if (s.matches(".*\\s.*")) s"\"$s\""
//    else s

}
