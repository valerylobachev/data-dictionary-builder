package biz.lobachev.annette.data_dictionary.builder.rendering.golang

import biz.lobachev.annette.data_dictionary.builder.labels.GolangPackage.GO_MODEL_PACKAGE
import biz.lobachev.annette.data_dictionary.builder.labels.JavaPackage.JAVA_MODEL_PACKAGE
import biz.lobachev.annette.data_dictionary.builder.labels.OverrideDatatype.GO_DATA_TYPE
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, TextRenderer}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.{PascalCase, SnakeCase}
import org.fusesource.scalate.TemplateEngine

import scala.io.Source

sealed trait GoTarget
case object Gorm extends GoTarget
case object Sqlx extends GoTarget

case class GolangRenderer(domain: Domain, target: GoTarget) extends TextRenderer {

  val engine = new TemplateEngine
  engine.escapeMarkup = false

  override def render(): Seq[RenderResult] =
    renderEntityClasses()

  private def renderEntityClasses(): Seq[RenderResult] = {
    val source   = Source.fromResource("golang/struct.ssp").getLines().mkString("\n")
    val template = engine.compileSsp(source)

    domain.entities.values
      .filter(_.entityType == TableEntity)
      .flatMap(renderEntity)
      .map { struct =>
        val output = engine.layout(
          uri = "struct.ssp",
          template = template,
          attributes = Map(
            "cl" -> struct,
          ),
        )
        RenderResult(
          path = struct.pkg,
          filename = s"${struct.name.snakeCase}.go",
          content = output,
        )
      }
      .toSeq
  }

  private def renderEntity(entity: Entity): Seq[GoStruct] = {
    val pkg      = domain
      .getEntityLabel(entity, GO_MODEL_PACKAGE)
      .orElse(domain.getEntityLabel(entity, JAVA_MODEL_PACKAGE).map(_.replace(".", "/")))
      .getOrElse("model")
    val splited  = pkg.split("/")
    val lastPkg  = splited.last
    // val schema   = entity.schema.map(s => s""", schema = "$s"""").getOrElse("")
    val comments = entity.name +: description2Comments(entity.description)
    val members  = entity.expandedFields.map(m => renderMember(entity, m))
//    val (relationMembers, relationImports) = renderRelations(entity, pkg, members.map(_.field))
    val imports  = datatypeImports(members.map(_.datatype))
    val pk       = Constant(
      s"${entity.entityName}PK",
      entity.tableName + "_pkey",
    )
    val fks      = entity.relations
      .filter(!_.logical)
      .map(r =>
        Constant(
          s"${entity.entityName}FK${r.id.pascalCase}",
          s"${entity.tableName}_${r.id.snakeCase}",
        ),
      )

    val uqs = entity.indexes
      .filter(_.unique)
      .map(r =>
        Constant(
          s"${entity.entityName}UQ${r.id.pascalCase}",
          s"${entity.tableName}_${r.id.snakeCase}",
        ),
      )

    val constants = Seq(pk) ++ fks ++ uqs

    val entityClass = GoStruct(
      pkg = pkg,
      lastPkg = lastPkg,
      imports = imports, // ++ relationImports.toSeq,
      comments = comments,
      name = s"${entity.entityName}Entity",
      entityName = entity.entityName,
      tableName = entity.tableName,
      isGorm = target == Gorm,
      schemaName = entity.schema,
      members = members, // ++ relationMembers,
      constants = constants,
    )

    Seq(entityClass)
  }

//  def renderRelations(entity: Entity, pkg: String, fields: Seq[EntityField]): (Seq[KtStructMember], Set[String]) = {
//    var imports = Set.empty[String]
//    var names   = Map.empty[String, Int]
//
//    val relationMembers = entity.relations.map { relation =>
//      val relatedEntity        = domain.entities(relation.referenceEntityId)
//      val relatedEntityFields  = relatedEntity.expandedFields
//      val relatedPkg           = domain.getEntityLabel(relatedEntity, JAVA_MODEL_PACKAGE).getOrElse("model")
//      val relatedClass: String = s"${relatedEntity.entityName}Entity"
//      if (relatedPkg != pkg) imports = imports + s"$relatedPkg.$relatedClass"
//      var datatype             = ""
//      datatype = s"$relatedClass?"
//      val name                 = {
//        val n     = relation.labels.getOrElse(RELATION_FIELD_NAME, relatedEntity.entityName.camelCase)
//        // Attributes.findRelationAttribute(relation, RELATION_FIELD_NAME).getOrElse(relatedEntity.entityName.camelCase)
//        val count = names.getOrElse(n, 0)
//        names = names + (n -> (count + 1))
//        if (count == 0) n else s"$n$count"
//      }
//      val joinColumns          = relation.fields.map { case name -> refName =>
//        val fieldName    = fields.find(_.fieldName == name).map(_.dbFieldName).getOrElse(name)
//        val refFieldName = relatedEntityFields.find(_.fieldName == refName).map(_.dbFieldName).getOrElse(refName)
//        s"""@JoinColumn(name = "$fieldName", referencedColumnName = "$refFieldName", nullable = false, updatable = false, insertable = false)"""
//      }
//      val join                 = if (joinColumns.length > 1) {
//        Seq("@JoinColumn([") ++ joinColumns.map(jc => s"  $jc,") ++ Seq("])")
//      } else joinColumns
//      val joinAnnotation       = relation.relationType match {
//        case ManyToOne => Seq("@ManyToOne") ++ join
//        case OneToOne  => Seq("@OneToOne") ++ join
//      }
//      KtStructMember(
//        comments = relation.name +: description2Comments(relation.description),
//        name = name,
//        datatype = datatype,
//        field = null,
//      )
//    }
//
//    val refRelationMembers =
//      domain.entities.values.flatMap(e => e.relations.filter(_.referenceEntityId == entity.id).map(r => e -> r)).map {
//        case relatedEntity -> relation =>
//          val relatedPkg           = domain.getEntityLabel(relatedEntity, JAVA_MODEL_PACKAGE).getOrElse("model")
//          val relatedClass: String = s"${relatedEntity.entityName}Entity"
//          if (relatedPkg != pkg) imports = imports + s"$relatedPkg.$relatedClass"
//          val name                 = {
//            val n     = relation.labels.getOrElse(RELATION_REL_FIELD_NAME, relatedEntity.entityName.camelCase)
//            val count = names.getOrElse(n, 0)
//            names = names + (n -> (count + 1))
//            if (count == 0) n else s"$n$count"
//          }
//          val relField             = relation.labels.getOrElse(RELATION_FIELD_NAME, relatedEntity.entityName.camelCase)
////            Attributes
////            .findRelationAttribute(relation, RELATION_FIELD_NAME)
////            .getOrElse(relatedEntity.entityName.camelCase)
//          var datatype             = ""
//          val joinAnnotation       = relation.relationType match {
//            case ManyToOne =>
//              datatype = s"Collection<$relatedClass>?"
//              Seq(s"""@OneToMany(mappedBy = "$relField")""")
//            case OneToOne  =>
//              datatype = s"$relatedClass?"
//              Seq(s"""@OneToOne(mappedBy = "$relField")""")
//          }
//          KtStructMember(
//            comments = relation.name +: description2Comments(relation.description),
//            annotations = joinAnnotation,
//            name = name,
//            datatype = datatype,
//            defaultValue = Some("null"),
//            field = null,
//          )
//      }
//    (relationMembers ++ refRelationMembers, imports)
//  }

  private def renderMember(entity: Entity, field: EntityField): KtStructMember = {

    val tags = if (target == Gorm) {
      val notNull = if (field.notNull) ";not null" else ""
      val pk      = if (entity.pk.contains(field.fieldName)) ";primaryKey" else ""
      s"gorm:\"column:${field.dbFieldName}$pk$notNull\""
    } else {
      s"db:\"${field.dbFieldName}\""
    }

    val dt       = fieldDatatype(field)
    val datatype =
      if (field.notNull) dt
      else if (target == Gorm) s"*$dt"
      else s"sql.Null[$dt]"
    KtStructMember(
      comments = field.name +: description2Comments(field.description),
      name = field.fieldName.pascalCase,
      datatype = datatype,
      field = field,
      tags = tags,
    )
  }

  private def datatypeImports(datatypes: Seq[String]): Seq[String] =
    (
      datatypes.find(dt => dt.contains("decimal.Decimal")).map(_ => "github.com/shopspring/decimal") ::
        datatypes.find(dt => dt.contains("time.Time")).map(_ => "time") ::
        datatypes.find(dt => dt.contains("sql.Null")).map(_ => "database/sql") ::
        Nil
    ).flatten

  private def fieldDatatype(field: EntityField): String =
    field.labels.get(GO_DATA_TYPE).getOrElse(fieldDatatype(field.dataType))

  private def fieldDatatype(dataElement: DataElement): String =
    dataElement.labels.get(GO_DATA_TYPE).getOrElse(fieldDatatype(dataElement.dataType))

  private def fieldDatatype(dataType: DataType): String =
    dataType match {
      case StringVarchar(_, _)            => "string"
      case StringChar(_, _)               => "string"
      case StringText(_)                  => "string"
      case StringJson(_)                  => "string"
      case StringJsonB(_)                 => "string"
      case IntInt(_)                      => "int"
      case LongLong(_)                    => "int64"
      case ShortSmallint(_)               => "int16"
      case BigDecimalNumeric(_, _, _)     => "decimal.Decimal"
      case BigIntegerNumeric(_, _)        => "decimal.Decimal"
      case DoubleDouble(_)                => "float64"
      case FloatFloat(_)                  => "float32"
      case BooleanBoolean(_)              => "bool"
      case UuidUuid(_)                    => "string"
      case InstantTimestamp(_)            => "time.Time"
      case OffsetDateTimeTimestamp(_)     => "time.Time"
      case LocalDateTimeTimestamp(_)      => "time.Time"
      case LocalDateDate(_)               => "string"
      case LocalTimeTime(_)               => "time.Time"
      case EnumString(_, _)               => "string"
      case EmbeddedEntityType(_, _, _)    => "Undefined"
      case ObjectType(entityId)           => domain.entities(entityId).entityName
      case DataElementType(dataElementId) => fieldDatatype(domain.dataElements(dataElementId))
      case ListCollection(dataType)       => s"[]${fieldDatatype(dataType)}"
      case SetCollection(dataType)        => s"[]${fieldDatatype(dataType)}"
      case StringMapCollection(dataType)  => s"map[string]${fieldDatatype(dataType)}"
    }

  private def fieldPrecision(dataType: DataType): Option[Int] =
    dataType match {
      case BigDecimalNumeric(precision, _, _) => Some(precision)
      case BigIntegerNumeric(precision, _)    => Some(precision)
      case DataElementType(dataElementId)     => fieldPrecision(domain.dataElements(dataElementId).dataType)
      case _                                  => None
    }

  private def fieldScale(dataType: DataType): Option[Int] =
    dataType match {
      case BigDecimalNumeric(_, scale, _) => Some(scale)
      case DataElementType(dataElementId) => fieldScale(domain.dataElements(dataElementId).dataType)
      case _                              => None
    }

  private def fieldLength(dataType: DataType): Option[Int] =
    dataType match {
      case StringVarchar(lenght, _)       => Some(lenght)
      case StringChar(lenght, _)          => Some(lenght)
      case DataElementType(dataElementId) => fieldLength(domain.dataElements(dataElementId).dataType)
      case EnumString(enumId, _)          => Some(domain.enums(enumId).length)
      case _                              => None
    }

  private def description2Comments(description: String): Seq[String] =
    if (description.isBlank) Seq.empty
    else
      description
        .replace("<br>", "\n")
        .split("\n")
        .toSeq

  //  private def renderDomain =
//    MdDomain(
//      domain.name,
//      description = if (domain.description.isBlank) "" else domain.description,
//      groups = domain.groups.values.map(renderGroup).toSeq
//    )
//
//  private def renderGroup(group: Group) =
//    MdGroup(
//      name = group.name,
//      description = if (group.description.isBlank) "" else group.description,
//      entities = domain.entities.values
//        .filter(entity => entity.groupId == group.id)
//        .map(renderEntity)
//        .toSeq
//    )
//
//  private def renderEntity(entity: Entity) = {
//    val fields = domain.rolloutEntityFields(entity)
//    MdEntity(
//      name = entity.name,
//      description = if (entity.description.isBlank) "" else entity.description,
//      fullTableName = entity.fullTableName(),
//      fields = fields.map(renderField(entity, _)),
//      indexes = entity.indexes.values.map(renderIndex(_, fields)).toSeq,
//      relations = entity.relations.map(renderRelation(_, fields))
//    )
//  }
//
//  private def renderField(entity: Entity, field: EntityField) = {
//    val sqlType  = domain.getTargetDataType(field.dataType, POSTGRESQL)
//    val datatype =
//      if (sqlType == "integer" && field.autoIncrement) "serial"
//      else if (sqlType == "bigint" && field.autoIncrement) "bigserial"
//      else sqlType
//    MdField(
//      dbFieldName = field.dbFieldName,
//      description =
//        if (field.description.isBlank) field.name
//        else s"${field.name}<br>${field.description.mdReplaceNL}",
//      datatype = datatype,
//      pk = if (entity.pk.contains(field.fieldName)) "X" else "",
//      required = if (field.notNull) "X" else ""
//    )
//  }
//
//  private def renderIndex(index: EntityIndex, fields: Seq[EntityField]) =
//    MdIndex(
//      fields = index.fields.map(f => getEntityField(fields, f).dbFieldName).mkString("<br>"),
//      unique = if (index.unique) "X" else "",
//      description =
//        if (index.description.isBlank) index.name
//        else s"${index.name}<br>${index.description.mdReplaceNL}"
//    )
//
//  private def renderRelation(relation: EntityRelation, fields: Seq[EntityField]) = {
//    val relationEntity = domain.entities(relation.referenceEntityId)
//    val relationFields = domain.rolloutEntityFields(relationEntity)
//    MdRelation(
//      f1 = relation.fields.map(f => getEntityField(fields, f._1).dbFieldName).mkString("<br>"),
//      relationEntityName = relationEntity.name,
//      relationEntityFullTableName = relationEntity.fullTableName(),
//      f2 = relation.fields.map(f => getEntityField(relationFields, f._2).dbFieldName).mkString("<br>"),
//      relationType = relation.relationType match {
//        case ManyToOne => "Many-To-One"
//        case OneToOne  => "One-To-One"
//      },
//      description =
//        if (relation.description.isBlank) relation.name
//        else s"${relation.name}<br>${relation.description.mdReplaceNL}"
//    )
//  }
//
//  def getEntityField(fields: Seq[EntityField], fieldName: String): EntityField =
//    fields.find(f => f.fieldName == fieldName).get

}
