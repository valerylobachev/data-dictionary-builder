package biz.lobachev.annette.data_dictionary.builder.rendering.ddl

import biz.lobachev.annette.data_dictionary.builder.POSTGRESQL
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, TextRenderer}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

case class DDLSegments(
  tables: String,
  indexes: String,
  comments: String,
  relations: String,
)

case class DDLRenderer(domain: Domain, filename: String = "ddl.sql") extends TextRenderer {

  override def render(): Seq[RenderResult] = {
    val res       = domain.rootComponentIds.map(id => renderComponent(id))
    val tables    = res.map(_.tables).mkString("\n", "\n", "\n")
    val indexes   = res.map(_.indexes).filter(_.nonEmpty).mkString("\n")
    val comments  = res.map(_.comments).filter(_.nonEmpty).mkString("\n", "\n", "\n")
    val relations = res.map(_.relations).filter(_.nonEmpty).mkString("\n", "\n", "\n")
    Seq(
      RenderResult(
        s"",
        filename,
        Seq(
          s"-- ${domain.name}",
          "-- Code generated by Data Dictionary Builder (https://github.com/valerylobachev/data-dictionary-builder).",
          tables,
          indexes,
          comments,
          relations,
        ).mkString("\n"),
      ),
    )
  }

  private def renderComponent(id: String): DDLSegments = {
    val res1      = domain.entities.values
      .filter(e => e.entityType == TableEntity && e.componentId == id)
      .map(entity => renderEntity(entity))
    val component = domain.components(id)
    val res2      = component.children.map(id => renderComponent(id))
    DDLSegments(
      tables = (res1 ++ res2).map(_.tables).mkString("\n"),
      indexes = (res1 ++ res2).map(_.indexes).filter(_.nonEmpty).mkString("\n"),
      comments = (res1 ++ res2).map(_.comments).filter(_.nonEmpty).mkString("\n"),
      relations = (res1 ++ res2).map(_.relations).filter(_.nonEmpty).mkString("\n"),
    )
  }

  private def renderEntity(entity: Entity): DDLSegments =
    DDLSegments(
      tables = renderTable(entity),
      indexes = renderIndexes(entity),
      comments = renderComments(entity),
      relations = renderRelations(entity),
    )

  private def renderTable(entity: Entity): String = {
    val fields = entity.expandedFields.map { field =>
      val fieldName  = wrapQuotes(field.dbFieldName)
      var datatype   = domain.getTargetDataType(field.dataType, POSTGRESQL)
      if (field.autoIncrement && datatype == "integer") datatype = "serial"
      else if (field.autoIncrement && datatype == "bigint") datatype = "bigserial"
      val primaryKey = if (entity.pk.length == 1 && entity.pk.head == field.fieldName) " PRIMARY KEY" else ""
      val notNull    = if (field.notNull) " NOT NULL" else ""
      s"  $fieldName $datatype$primaryKey$notNull"
    }.mkString(",\n")

    val primaryKey = if (entity.pk.length > 1) {
      val pkFields = entity.pk.map(f => getEntityFieldName(entity.expandedFields, f)).mkString("(", ", ", ")")
      s",\n  PRIMARY KEY $pkFields"
    } else ""

    Seq(
      s"CREATE TABLE ${entity.tableNameWithSchema()} (",
      fields + primaryKey,
      ");\n",
    ).mkString("\n")
  }

  private def renderIndexes(entity: Entity): String =
    if (entity.indexes.nonEmpty) {
      entity.indexes.map { index =>
        val unique  = if (index.unique) "UNIQUE " else ""
        val indexId = wrapQuotes(entity.tableName + "_" + index.id.snakeCase)
        val fields  = index.fields.map(f => getEntityFieldName(entity.expandedFields, f)).mkString("(", ", ", ")")
        s"CREATE ${unique}INDEX $indexId ON ${entity.tableNameWithSchema()} $fields;\n"
      }.mkString("\n")
    } else ""

  private def renderComments(entity: Entity): String = {
    val tableName    = entity.tableNameWithSchema()
    val tableComment = escapeSingleQuotes(entity.name)
    (Seq(
      s"COMMENT ON TABLE $tableName IS '$tableComment';\n",
    ) ++
      entity.expandedFields.filter(_.name.nonEmpty).map { f =>
        val fieldName    = wrapQuotes(f.dbFieldName)
        val fieldComment = escapeSingleQuotes(f.name)
        s"COMMENT ON COLUMN $tableName.$fieldName IS '$fieldComment';\n"
      }).mkString("\n")
  }

  private def renderRelations(entity: Entity): String = {
    val tableName = entity.tableNameWithSchema()
    entity.expandedRelations.map { relation =>
      val relationId   = wrapQuotes(entity.tableName + "_" + relation.id.snakeCase)
      val refEntity    = domain.entities(relation.referenceEntityId)
      val refTableName = refEntity.tableNameWithSchema()
      val fields1      = relation.fields
        .map(f => getEntityFieldName(entity.expandedFields, f._1))
        .mkString("(", ", ", ")")
      val fields2      = relation.fields
        .map(f => getEntityFieldName(refEntity.expandedFields, f._2))
        .mkString("(", ", ", ")")
      val onDelete     = if (relation.onDelete != NoAction) s" ON DELETE ${relation.onDelete.sqlAction()}" else ""
      val onUpdate     = if (relation.onDelete != NoAction) s" ON UPDATE ${relation.onUpdate.sqlAction()}" else ""
      s"ALTER TABLE $tableName ADD CONSTRAINT $relationId " +
        s"FOREIGN KEY $fields1 REFERENCES $refTableName $fields2" +
        s"$onDelete$onUpdate;\n"
    }.mkString("\n")
  }

  def getEntityFieldName(entityFields: Seq[EntityField], fieldName: String): String =
    entityFields
      .find(f => f.fieldName == fieldName)
      .map(f => wrapQuotes(f.dbFieldName))
      .getOrElse {
        throw new IllegalArgumentException(s"not found field $fieldName")
      }
}
