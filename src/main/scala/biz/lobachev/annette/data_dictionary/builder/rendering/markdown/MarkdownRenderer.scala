package biz.lobachev.annette.data_dictionary.builder.rendering.markdown

import biz.lobachev.annette.data_dictionary.builder.POSTGRESQL
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, Renderer}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._
import biz.lobachev.annette.data_dictionary.builder.model.{RawDomain, RawComponent}
import org.fusesource.scalate.TemplateEngine

import scala.io.Source

case class MarkdownRenderer(domain: RawDomain, translation: Map[String, String] = EnglishTranslaltion) extends Renderer {

  val engine = new TemplateEngine
  engine.escapeMarkup = false

  override def render(): Seq[RenderResult] = {
    val language = translation(Language)
    val source   = Source.fromResource("markdown/template.ssp").getLines().mkString("\n")
    val template = engine.compileSsp(source)
    val output   = engine.layout(
      uri = "template.ssp",
      template = template,
      attributes = Map(
        "domain"         -> renderDomain,
        "fieldHeader"    -> translation(FieldHeader),
        "indexHeader"    -> translation(IndexHeader),
        "relationHeader" -> translation(RelationHeader)
      )
    )
    Seq(
      RenderResult(
        "",
        s"${domain.id.snakeCase}_$language.md",
        output
      )
    )
  }

  private def renderDomain =
    MdDomain(
      domain.name,
      description = if (domain.description.isBlank) "" else domain.description,
      groups = domain.components.values.map(renderGroup).toSeq
    )

  private def renderGroup(group: RawComponent) =
    MdGroup(
      name = group.name,
      description = if (group.description.isBlank) "" else group.description,
      entities = domain.entities.values
        .filter(entity => entity.componentId == group.id)
        .map(renderEntity)
        .toSeq
    )

  private def renderEntity(entity: RawEntity) = {
    val fields = domain.expandEntityFields(entity)
    MdEntity(
      name = entity.name,
      description = if (entity.description.isBlank) "" else entity.description,
      fullTableName = entity.tableNameWithSchema(),
      fields = fields.map(renderField(entity, _)),
      indexes = entity.indexes.map(renderIndex(_, fields)).toSeq,
      relations = entity.relations.map(renderRelation(_, fields))
    )
  }

  private def renderField(entity: RawEntity, field: RawEntityField) = {
    val sqlType  = domain.getTargetDataType(field.dataType, POSTGRESQL)
    val datatype =
      if (sqlType == "integer" && field.autoIncrement) "serial"
      else if (sqlType == "bigint" && field.autoIncrement) "bigserial"
      else sqlType
    MdField(
      dbFieldName = field.dbFieldName,
      description =
        if (field.description.isBlank) field.name
        else s"${field.name}<br>${field.description.mdReplaceNL}",
      datatype = datatype,
      pk = if (entity.pk.contains(field.fieldName)) "X" else "",
      required = if (field.notNull) "X" else ""
    )
  }

  private def renderIndex(index: RawEntityIndex, fields: Seq[RawEntityField]) =
    MdIndex(
      fields = index.fields.map(f => getEntityField(fields, f).dbFieldName).mkString("<br>"),
      unique = if (index.unique) "X" else "",
      description =
        if (index.description.isBlank) index.name
        else s"${index.name}<br>${index.description.mdReplaceNL}"
    )

  private def renderRelation(relation: RawEntityRelation, fields: Seq[RawEntityField]) = {
    val relationEntity = domain.entities(relation.referenceEntityId)
    val relationFields = domain.expandEntityFields(relationEntity)
    MdRelation(
      f1 = relation.fields.map(f => getEntityField(fields, f._1).dbFieldName).mkString("<br>"),
      relationEntityName = relationEntity.name,
      relationEntityFullTableName = relationEntity.tableNameWithSchema(),
      f2 = relation.fields.map(f => getEntityField(relationFields, f._2).dbFieldName).mkString("<br>"),
      relationType = relation.relationType match {
        case ManyToOne => "Many-To-One"
        case OneToOne  => "One-To-One"
      },
      description =
        if (relation.description.isBlank) relation.name
        else s"${relation.name}<br>${relation.description.mdReplaceNL}"
    )
  }

  def getEntityField(fields: Seq[RawEntityField], fieldName: String): RawEntityField =
    fields.find(f => f.fieldName == fieldName).get

}
