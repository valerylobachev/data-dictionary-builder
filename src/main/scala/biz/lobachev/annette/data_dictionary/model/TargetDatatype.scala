package biz.lobachev.annette.data_dictionary.model

sealed trait TargetDatatype {
  def documentationDatatype(): String
}

case class PostgresTargetDatatype(
  datatype: String,
) extends TargetDatatype {
  override def documentationDatatype(): String = datatype
}

case class ClickHouseTargetDatatype(
  datatype: String,
) extends TargetDatatype {
  override def documentationDatatype(): String = datatype
}

case class KotlinTargetDatatype(
  pkg: Option[String],
  datatype: String,
  collection: Option[KotlinCollectionDatatype] = None,
) extends TargetDatatype {
  override def documentationDatatype(): String =
    collection.map { c =>
      if c.datatype == "Map" then s"Map<String,$datatype>"
      else s"${c.datatype}<$datatype>"
    }
      .getOrElse(datatype)
}

case class KotlinCollectionDatatype(
  pkg: Option[String],
  datatype: String,
)

case class GoTargetDatatype(
  variant: GoTypeVariant,
  pkg: Option[GoPackage],
  datatype: String,
) extends TargetDatatype {
  override def documentationDatatype(): String =
    variant match {
      case SimpleTypeVariant                           =>
        typeWithPackage(datatype, pkg)
      case ArrayTypeVariant                            =>
        val dt: String = typeWithPackage(datatype, pkg)
        s"[]$dt"
      case MapTypeVariant                              =>
        val dt: String = typeWithPackage(datatype, pkg)
        s"map[string]$dt"
      case GenericTypeVariant(outerPkg, outerDatatype) =>
        val outerDt = typeWithPackage(outerDatatype, Some(outerPkg))
        val innerDt = typeWithPackage(datatype, pkg)
        s"$outerDt[$innerDt]"
    }

  private def typeWithPackage(datatype: String, pkg: Option[GoPackage]) = {
    val lastPkg = pkg.map(_.lastPkg())
    lastPkg.map(lp => s"$lp.$datatype").getOrElse(datatype)
  }
}

sealed trait GoTypeVariant
case object SimpleTypeVariant extends GoTypeVariant
case object ArrayTypeVariant  extends GoTypeVariant
case object MapTypeVariant    extends GoTypeVariant
case class GenericTypeVariant(
  pkg: GoPackage,
  datatype: String,
) extends GoTypeVariant

case class GoPackage(
  pkg: String,
  version: Option[String] = None,
) {
  def lastPkg() = {
    val splitted = pkg.split("/")
    splitted.last
  }

  def versionedPkg() = {
    val ver = version.map(v => s"/$v").getOrElse("")
    s"$pkg$ver"
  }
}
