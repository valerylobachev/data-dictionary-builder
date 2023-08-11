package biz.lobachev.annette.data_dictionary.builder.rendering.dbdiagram

import biz.lobachev.annette.data_dictionary.builder.POSTGRESQL
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, Renderer}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._
import biz.lobachev.annette.data_dictionary.builder.model.Domain

case class DbDiagramRenderer(domain: Domain) extends Renderer {

  override def render(): Seq[RenderResult] =
    renderAll() ++ renderByGroup()

  def renderAll(): Seq[RenderResult] = {
    val enums   = renderEnums()
    val content = domain.groups.values.map { group =>
      val groupContent = domain.entities.values
        .filter(entity => entity.groupId == group.id && entity.entityType == TableEntity)
        .toSeq
        .map(entity => renderEntity(entity))
        .mkString("\n")
      if (groupContent.nonEmpty) s"// Group ${group.id} - ${group.name} \n\n$groupContent\n"
      else ""
    }.toSeq
    Seq(
      RenderResult(
        "diagrams",
        s"${domain.id}.dbml",
        (enums +: content).mkString("\n"),
      ),
    )
  }

  def renderByGroup(): Seq[RenderResult] = {
    val enums = renderEnums()
    domain.groups.values.map { group =>
      val groupContent = domain.entities.values
        .filter(entity => entity.groupId == group.id && entity.entityType == TableEntity)
        .map(entity => renderEntity(entity))
        .mkString("\n")
      val otherContent = domain.entities.values
        .filter(entity => entity.groupId == group.id && entity.entityType == TableEntity)
        .flatMap { entity =>
          (entity.relations ++ domain.rolloutEntity(entity).relations)
            .filter(r => domain.entities(r.referenceEntityId).groupId != group.id)
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
            "diagrams",
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
    s"table ${entity.fullTableName()} {\n$fields" +
      (if (indexes.nonEmpty) s"\n$indexes" else "") +
      (if (entity.name.nonEmpty) s"\n  note: '${entity.name}'\n") +
      "}\n" +
      (if (relations.nonEmpty) s"\n$relations\n" else "")

  }

  def renderFields(entity: Entity): String =
    domain
      .rolloutEntityFields(entity)
      .map { field =>
        val datatype =
          domain.getTargetDataType(field.dataType, POSTGRESQL) // domain.dataElements(field.dataElementId).sqlDataType
        val params   = Seq(
          if (entity.pk.length == 1 && entity.pk.head == field.fieldName) Some("pk") else None,
          if (field.notNull) Some("not null") else None,
          if (field.autoIncrement) Some("increment") else None,
          if (field.name.nonEmpty) Some(s"note: '${field.name}'") else None,
        ).flatten
        val paramStr = if (params.nonEmpty) params.mkString("[", ", ", "]") else ""
        s"  ${field.dbFieldName} $datatype $paramStr\n"
      }
      .mkString

  def getEntityField(entityFields: Seq[EntityField], fieldName: String): EntityField =
    entityFields.find(f => f.fieldName == fieldName).getOrElse {
      throw new IllegalArgumentException(s"not found field $fieldName")
    }

  def renderIndexes(entity: Entity): String = {
    val pk      = if (entity.pk.length > 1) {
      val pkFields = entity.pk.map(f => getEntityField(entity.fields, f).dbFieldName).mkString("(", ", ", ")")
      Some(s"    $pkFields [pk]\n")
    } else None
    val indexes = entity.indexes.values.map { index =>
      val indexId  = entity.tableName + '_' + index.id.snakeCase
      val fields   =
        if (index.fields == 1) index.fields.map(f => getEntityField(entity.fields, f).dbFieldName).head
        else index.fields.map(f => getEntityField(entity.fields, f).dbFieldName).mkString("(", ", ", ")")
      val params   = Seq(
        if (index.unique) Some("unique") else None,
        Some(s"name: '$indexId'"),
        if (index.name.nonEmpty) Some(s"note: '${index.name}'") else None,
      ).flatten
      val paramStr = if (params.nonEmpty) params.mkString("[", ", ", "]") else ""
      Some(s"    $fields $paramStr\n")
    }.toSeq
    val res     = (pk +: indexes).flatten
    if (res.nonEmpty) ("  indexes {\n" +: res :+ "  }\n").mkString
    else ""
  }

  def renderRelations(entity: Entity, group: Option[String] = None): String = {
    val fr        = domain.rolloutEntity(entity)
    val fields    = fr.fields
    val relations = entity.relations ++ fr.relations
    relations.map { relation =>
      val relationEntity = domain.entities(relation.referenceEntityId)
      val shouldRender   = group.map(groupId => relationEntity.groupId == groupId).getOrElse(true)
      if (shouldRender) {
        val relationType = relation.relationType match {
          case ManyToOne => ">"
          case OneToOne  => "-"
        }
        val comment      = if (relation.name.nonEmpty) s"// ${relation.name}\n" else ""
        val relationId   = entity.tableName + '_' + relation.id.snakeCase
        if (relation.fields.size == 1) {
          val f1 = getEntityField(fields, relation.fields.head._1).dbFieldName
          val f2 = getEntityField(relationEntity.fields, relation.fields.head._2).dbFieldName
          s"${comment}Ref $relationId: ${entity.fullTableName()}.$f1 $relationType ${relationEntity.fullTableName()}.$f2\n"
        } else {
          val f1 = relation.fields.map(f => getEntityField(fields, f._1).dbFieldName).mkString("(", ", ", ")")
          val f2 =
            relation.fields.map(f => getEntityField(relationEntity.fields, f._2).dbFieldName).mkString("(", ", ", ")")
          s"${comment}Ref $relationId: ${entity.fullTableName()}.$f1 $relationType ${relationEntity.fullTableName()}.$f2\n"
        }
      } else ""
    }.mkString
  }

}
