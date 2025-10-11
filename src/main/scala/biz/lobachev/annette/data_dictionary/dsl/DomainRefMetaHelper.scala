package biz.lobachev.annette.data_dictionary.dsl

import biz.lobachev.annette.data_dictionary.labels.TablePrefixSuffix

trait DomainDefMetaHelper {

  def getTableName(tableDef: TableDef, domainDef: DomainDef) = {
    val prefix = getTableLabel(tableDef, TablePrefixSuffix.TABLE_NAME_PREFIX, domainDef)
      .map(s => s"${s}_")
      .getOrElse("")
    val suffix =
      getTableLabel(tableDef, TablePrefixSuffix.TABLE_NAME_SUFFIX, domainDef)
        .map(s => s"_$s")
        .getOrElse("")
    s"$prefix${tableDef.tableName.trim}$suffix"
  }

  def getTableLabel(tableDef: TableDef, labelId: String, domainDef: DomainDef): Option[String] =
    tableDef.labels
      .find(_.key == labelId)
      .map(_.value)
      .orElse(
        getComponentLabel(tableDef.componentId, labelId, domainDef),
      )

  def getComponentLabel(componentId: Option[String], labelId: String, domainDef: DomainDef): Option[String] =
    componentId match {
      case Some(compId) =>
        domainDef.components
          .find(_.id == compId)
          .flatMap(c =>
            c.labels
              .find(_.key == labelId)
              .map(_.value)
              .orElse(getComponentLabel(c.componentId, labelId, domainDef)),
          )
      case None         => getDomainLabel(domainDef, labelId)
    }

  def getDomainLabel(domainDef: DomainDef, labelId: String): Option[String] =
    domainDef.labels.find(_.key == labelId).map(_.value)
}
