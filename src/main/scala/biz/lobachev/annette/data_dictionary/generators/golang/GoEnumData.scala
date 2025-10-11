package biz.lobachev.annette.data_dictionary.generators.golang

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

case class GoEnumData(
  pkg: String,
  lastPkg: String,
  enums: Seq[GoEnum],
  date: String = OffsetDateTime.now().toLocalDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
)

case class GoEnum(
  comments: Seq[String],
  constants: Seq[GoEnumConstant],
)

case class GoEnumConstant(
  constant: String,
  value: String,
  comment: String,
)
