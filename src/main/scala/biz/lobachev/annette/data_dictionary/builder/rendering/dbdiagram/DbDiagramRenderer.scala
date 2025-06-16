package biz.lobachev.annette.data_dictionary.builder.rendering.dbdiagram

import biz.lobachev.annette.data_dictionary.builder.POSTGRESQL
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, TextRenderer}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._
import biz.lobachev.annette.data_dictionary.builder.model.Domain

case class DbDiagramRenderer(domain: Domain, logical: Boolean = false) extends TextRenderer {
  val path                = "diagrams"
  val diagramName: String = if (logical) "logical_schema" else "physical_schema"
  val byGroupDir: String  = if (logical) "logical" else "physical"

  override def render(): Seq[RenderResult] =
    renderAll() ++ renderByGroup()

  def renderAll(): Seq[RenderResult] = {
    val enums   = renderEnums()
    val content = domain.components.values.map { group =>
      val groupContent = domain.entities.values
        .filter(entity => entity.componentId == group.id && entity.entityType == TableEntity)
        .toSeq
        .map(entity => renderEntity(entity))
        .mkString("\n")
      if (groupContent.nonEmpty) s"// Group ${group.id} - ${group.name} \n\n$groupContent\n"
      else ""
    }.toSeq
    Seq(
      RenderResult(
        s"$path",
        s"$diagramName.dbml",
        (enums +: content).mkString("\n"),
      ),
    )
  }

  def renderByGroup(): Seq[RenderResult] = {
    val enums = renderEnums()
    domain.components.values.map { group =>
      val groupContent = domain.entities.values
        .filter(entity => entity.componentId == group.id && entity.entityType == TableEntity)
        .map(entity => renderEntity(entity))
        .mkString("\n")
      val otherContent = domain.entities.values
        .filter(entity => entity.componentId == group.id && entity.entityType == TableEntity)
        .flatMap { entity =>
          (entity.expandedRelations)
            .filter(r => domain.entities(r.referenceEntityId).componentId != group.id)
            .map(_.referenceEntityId)
        }
        .toSet
        .toSeq
        .map((entityId: String) => renderEntity(domain.entities(entityId), Some(group.id)))
        .mkString("\n")
      if (groupContent.nonEmpty) {
        val related = if (otherContent.nonEmpty) s"// Related entities \n\n$otherContent\n" else ""
        val content = s"// Group ${group.id} - ${group.name} \n\n$groupContent\n\n$related"
        Some(
          RenderResult(
            s"$path/$byGroupDir",
            s"${group.id}.dbml",
            s"$enums\n$content",
          ),
        )
      } else None
    }.flatten.toSeq

  }

  def renderEnums(): String =
    domain.enums.values.map { e =>
      val header = s"// ${e.name}"
      val en     = s"enum ${e.id.snakeCase} {"
      val lines  = e.elements.map(l => s"  ${l._1} [note: '${l._2}']")
      (Seq(header, en) ++: lines :+ "}\n").mkString("\n")

    }.mkString("\n")

  def renderEntity(entity: Entity, group: Option[String] = None): String = {
    val fields    = renderFields(entity)
    val indexes   = renderIndexes(entity)
    val relations = renderRelations(entity, group)
    s"table ${entity.tableNameWithSchema(logical)} {\n$fields" +
      (if (indexes.nonEmpty) s"\n$indexes" else "") +
      (if (entity.name.nonEmpty) s"\n  note: '${entity.name}'\n") +
      "}\n" +
      (if (relations.nonEmpty) s"\n$relations\n" else "")

  }

  def renderFields(entity: Entity): String =
    entity.expandedFields.map { field =>
      val datatype =
        domain.getTargetDataType(field.dataType, POSTGRESQL) // domain.dataElements(field.dataElementId).sqlDataType
      val params    = Seq(
        if (entity.pk.length == 1 && entity.pk.head == field.fieldName) Some("pk") else None,
        if (field.notNull) Some("not null") else None,
        if (field.autoIncrement) Some("increment") else None,
        if (field.name.nonEmpty) Some(s"note: '${field.name}'") else None,
      ).flatten
      val paramStr  = if (params.nonEmpty) params.mkString("[", ", ", "]") else ""
      val fieldname = if (logical && field.name.trim.nonEmpty) wrapQuotes(field.name) else wrapQuotes(field.dbFieldName)
      s"  ${fieldname} $datatype $paramStr\n"
    }.mkString

  def getEntityFieldName(entityFields: Seq[EntityField], fieldName: String, logical: Boolean): String =
    entityFields
      .find(f => f.fieldName == fieldName)
      .map(f => if (logical && f.name.trim.nonEmpty) wrapQuotes(f.name) else wrapQuotes(f.dbFieldName))
      .getOrElse {
        throw new IllegalArgumentException(s"not found field $fieldName")
      }

  def renderIndexes(entity: Entity): String = {
    val pk      = if (entity.pk.length > 1) {
      val pkFields = entity.pk.map(f => getEntityFieldName(entity.fields, f, logical)).mkString("(", ", ", ")")
      Some(s"    $pkFields [pk]\n")
    } else None
    val indexes = entity.indexes.map { index =>
      val indexId  = entity.tableName + '_' + index.id.snakeCase
      val fields   =
        if (index.fields.length == 1) index.fields.map(f => getEntityFieldName(entity.expandedFields, f, logical)).head
        else index.fields.map(f => getEntityFieldName(entity.expandedFields, f, logical)).mkString("(", ", ", ")")
      val params   = Seq(
        if (index.unique) Some("unique") else None,
        Some(s"name: '$indexId'"),
        if (index.name.nonEmpty) Some(s"note: '${index.name}'") else None,
      ).flatten
      val paramStr = if (params.nonEmpty) params.mkString("[", ", ", "]") else ""
      Some(s"    $fields $paramStr\n")
    }
    val res     = (pk +: indexes).flatten
    if (res.nonEmpty) ("  indexes {\n" +: res :+ "  }\n").mkString
    else ""
  }

  def renderRelations(entity: Entity, group: Option[String] = None): String = {
    val fields    = entity.expandedFields
    val relations = entity.expandedRelations
    relations.map { relation =>
      val relationEntity = domain.entities(relation.referenceEntityId)
      val shouldRender   = group.map(groupId => relationEntity.componentId == groupId).getOrElse(true)
      if (shouldRender) {
        val relationType = relation.relationType match {
          case ManyToOne => ">"
          case OneToOne  => "-"
        }
        val comment      = if (relation.name.nonEmpty) s"// ${relation.name}\n" else ""
        val relationId   = entity.tableName + '_' + relation.id.snakeCase
        if (relation.fields.size == 1) {
          val f1 = getEntityFieldName(fields, relation.fields.head._1, logical)
          val f2 = getEntityFieldName(relationEntity.fields, relation.fields.head._2, logical)
          s"${comment}Ref $relationId: ${entity.tableNameWithSchema(logical)}.$f1 $relationType ${relationEntity
              .tableNameWithSchema(logical)}.$f2\n"
        } else {
          val f1 = relation.fields.map(f => getEntityFieldName(fields, f._1, logical)).mkString("(", ", ", ")")
          val f2 =
            relation.fields
              .map(f => getEntityFieldName(relationEntity.fields, f._2, logical))
              .mkString("(", ", ", ")")
          s"${comment}Ref $relationId: ${entity.tableNameWithSchema(logical)}.$f1 $relationType ${relationEntity
              .tableNameWithSchema(logical)}.$f2\n"
        }
      } else ""
    }.mkString
  }

}
