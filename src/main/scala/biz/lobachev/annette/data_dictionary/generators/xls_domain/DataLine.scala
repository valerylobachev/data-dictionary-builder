package biz.lobachev.annette.data_dictionary.generators.xls_domain

case class DataLine(
  domainId: String,
  domainName: String,
  component: Option[ComponentLine],
  data: Seq[String],
)

case class ComponentLine(
  id: String,
  names: Seq[String],
)


