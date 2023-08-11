package biz.lobachev.annette.data_dictionary.builder.rendering.kotlin

import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.JAVA_MODEL_PACKAGE
import biz.lobachev.annette.data_dictionary.builder.helper.RelationName.{RELATION_FIELD_NAME, RELATION_REL_FIELD_NAME}
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, Renderer}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax.CamelCase
import org.fusesource.scalate.TemplateEngine

import scala.io.Source

case class KotlinRenderer(domain: Domain) extends Renderer {

  val engine = new TemplateEngine
  engine.escapeMarkup = false

  override def render(): Seq[RenderResult] =
    renderEntityClasses()

  private def renderEntityClasses(): Seq[RenderResult] = {
    val source   = Source.fromResource("kotlin/class.ssp").getLines().mkString("\n")
    val template = engine.compileSsp(source)

    domain.entities.values
      .flatMap(renderEntity)
      .map { clazz =>
        val output = engine.layout(
          uri = "class.ssp",
          template = template,
          attributes = Map(
            "cl" -> clazz,
          ),
        )
        RenderResult(
          path = clazz.pkg.replace(".", "/"),
          filename = s"${clazz.name}.kt",
          content = output,
        )
      }
      .toSeq
  }

  private def renderEntity(entity: Entity): Seq[KtClass] = {
    val pkg                                = Attributes.findEntityAttribute(entity, domain, JAVA_MODEL_PACKAGE).getOrElse("model")
    val schema                             = entity.schema.map(s => s""", schema = "$s"""").getOrElse("")
    val comments                           = entity.name +: description2Comments(entity.description)
    val members                            = domain.rolloutEntityFields(entity) map (m => renderMember(entity, m))
    val (relationMembers, relationImports) = renderRelations(entity, pkg, members.map(_.field))
    val imports                            = datatypeImports(members.map(_.datatype))
    val annotations                        = Seq(
      "@Entity",
      s"""@Table(name = "${entity.tableName}"$schema)""",
    )
    val idClass                            = if (entity.pk.length > 1) {
      val idMembers = members
        .filter(m => entity.pk.contains(m.name))
        .map(m =>
          m.copy(
            annotations = Seq.empty,
            defaultValue = Some(idFieldDefaultValue(m.field.dataType)),
          ),
        )
      val idImports = datatypeImports(idMembers.map(_.datatype))
      Some(
        KtClass(
          pkg = pkg,
          imports = idImports,
          comments = comments,
          annotations = Seq.empty,
          name = s"${entity.entityName}Id",
          members = idMembers,
          extensions = Some("Serializable"),
        ),
      )
    } else None
    val idClassAnnotation                  = idClass.map(c => Seq(s"@IdClass(${c.name}::class)")).getOrElse(Seq.empty)
    val entityClass                        = KtClass(
      pkg = pkg,
      imports = Seq("jakarta.persistence.*") ++ imports ++ relationImports.toSeq,
      comments = entity.name +: description2Comments(entity.description),
      annotations = annotations ++ idClassAnnotation,
      name = s"${entity.entityName}Entity",
      members = members ++ relationMembers,
      None,
    )

    idClass.map(idCl => Seq(entityClass, idCl)).getOrElse(Seq(entityClass))
  }

  def renderRelations(entity: Entity, pkg: String, fields: Seq[EntityField]): (Seq[KtClassMember], Set[String]) = {
    var imports = Set.empty[String]
    var names   = Map.empty[String, Int]

    val relationMembers = entity.relations.map { relation =>
      val relatedEntity        = domain.entities(relation.referenceEntityId)
      val relatedEntityFields  = domain.rolloutEntityFields(relatedEntity)
      val relatedPkg           = Attributes.findEntityAttribute(relatedEntity, domain, JAVA_MODEL_PACKAGE).getOrElse("model")
      val relatedClass: String = s"${relatedEntity.entityName}Entity"
      if (relatedPkg != pkg) imports = imports + s"$relatedPkg.$relatedClass"
      var datatype             = ""
      datatype = s"$relatedClass?"
      val name                 = {
        val n     =
          Attributes.findRelationAttribute(relation, RELATION_FIELD_NAME).getOrElse(relatedEntity.entityName.camelCase)
        val count = names.getOrElse(n, 0)
        names = names + (n -> (count + 1))
        if (count == 0) n else s"$n$count"
      }
      val joinColumns          = relation.fields.map { case name -> refName =>
        val fieldName    = fields.find(_.fieldName == name).map(_.dbFieldName).getOrElse(name)
        val refFieldName = relatedEntityFields.find(_.fieldName == refName).map(_.dbFieldName).getOrElse(refName)
        s"""@JoinColumn(name = "$fieldName", referencedColumnName = "$refFieldName", nullable = false, updatable = false, insertable = false)"""
      }
      val join                 = if (joinColumns.length > 1) {
        Seq("@JoinColumn([") ++ joinColumns.map(jc => s"  $jc,") ++ Seq("])")
      } else joinColumns
      val joinAnnotation       = relation.relationType match {
        case ManyToOne => Seq("@ManyToOne") ++ join
        case OneToOne  => Seq("@OneToOne") ++ join
      }
      KtClassMember(
        comments = relation.name +: description2Comments(relation.description),
        annotations = "@JsonIgnore" +: joinAnnotation,
        name = name,
        datatype = datatype,
        defaultValue = Some("null"),
        field = null,
      )
    }

    val refRelationMembers =
      domain.entities.values.flatMap(e => e.relations.filter(_.referenceEntityId == entity.id).map(r => e -> r)).map {
        case relatedEntity -> relation =>
          val relatedPkg           = Attributes.findEntityAttribute(relatedEntity, domain, JAVA_MODEL_PACKAGE).getOrElse("model")
          val relatedClass: String = s"${relatedEntity.entityName}Entity"
          if (relatedPkg != pkg) imports = imports + s"$relatedPkg.$relatedClass"
          val name                 = {
            val n     =
              Attributes
                .findRelationAttribute(relation, RELATION_REL_FIELD_NAME)
                .getOrElse(relatedEntity.entityName.camelCase)
            val count = names.getOrElse(n, 0)
            names = names + (n -> (count + 1))
            if (count == 0) n else s"$n$count"
          }
          val relField             = Attributes
            .findRelationAttribute(relation, RELATION_FIELD_NAME)
            .getOrElse(relatedEntity.entityName.camelCase)
          var datatype             = ""
          val joinAnnotation       = relation.relationType match {
            case ManyToOne =>
              datatype = s"Collection<$relatedClass>?"
              Seq(s"""@OneToMany(mappedBy = "$relField")""")
            case OneToOne  =>
              datatype = s"$relatedClass?"
              Seq(s"""@OneToOne(mappedBy = "$relField")""")
          }
          KtClassMember(
            comments = relation.name +: description2Comments(relation.description),
            annotations = "@JsonIgnore" +: joinAnnotation,
            name = name,
            datatype = datatype,
            defaultValue = Some("null"),
            field = null,
          )
      }
    (relationMembers ++ refRelationMembers, imports)
  }

  def renderMember(entity: Entity, field: EntityField): KtClassMember = {
    val idAnnotation             = if (entity.pk.contains(field.fieldName)) Seq("@Id") else Seq.empty
    val generatedValueAnnotation =
      if (field.autoIncrement) Seq("@GeneratedValue(strategy = GenerationType.IDENTITY)") else Seq.empty

    val nullable         = if (field.notNull) ", nullable = false" else ""
    val length           = fieldLength(field.dataType).map(l => s", length = $l").getOrElse("")
    val precision        = fieldPrecision(field.dataType).map(p => s", precision = $p").getOrElse("")
    val scale            = fieldScale(field.dataType).map(p => s", scale = $p").getOrElse("")
    val dt               = fieldDatatype(field.dataType)
    val datatype         = if (field.notNull) dt else s"$dt?"
    val defaultValue     =
      fieldDefaultValue(field.dataType).map(Some(_)).getOrElse(if (field.notNull) None else Some("null"))
    val columnAnnotation = s"""@Column(name = "${field.dbFieldName}"$nullable$length$precision$scale)"""
    KtClassMember(
      comments = field.name +: description2Comments(field.description),
      annotations = idAnnotation ++ generatedValueAnnotation :+ columnAnnotation,
      name = field.fieldName,
      datatype = datatype,
      defaultValue = defaultValue,
      field = field,
    )
  }

  private def idFieldDefaultValue(dataType: DataType): String =
    dataType match {
      case StringVarchar(_, defaultValue)        => defaultValue.map(s => s""""$s"""").getOrElse("\"\"")
      case StringChar(_, defaultValue)           => defaultValue.map(s => s""""$s"""").getOrElse("\"\"")
      case StringText(defaultValue)              => defaultValue.map(s => s""""$s"""").getOrElse("\"\"")
      case StringJson(defaultValue)              => defaultValue.map(s => s""""$s"""").getOrElse("\"\"")
      case StringJsonB(defaultValue)             => defaultValue.map(s => s""""$s"""").getOrElse("\"\"")
      case IntInt(defaultValue)                  => defaultValue.map(s => s"$s").getOrElse("0")
      case LongLong(defaultValue)                => defaultValue.map(s => s"$s").getOrElse("0L")
      case ShortSmallint(defaultValue)           => defaultValue.map(s => s"$s").getOrElse("0")
      case BigDecimalNumeric(_, _, defaultValue) =>
        defaultValue.map(s => s"BigDecimal(\"$s\")").getOrElse("BigDecimal(0)")
      case BigIntegerNumeric(_, defaultValue)    => defaultValue.map(s => s"BigInteger(\"$s\")").getOrElse("BigInteger(0)")
      case DoubleDouble(defaultValue)            => defaultValue.map(s => s"$s").getOrElse("0.0")
      case FloatFloat(defaultValue)              => defaultValue.map(s => s"$s").getOrElse("0.0")
      case BooleanBoolean(defaultValue)          => defaultValue.map(s => s"$s").getOrElse("false")
      case UuidUuid(defaultValue)                =>
        defaultValue.map(s => s"UUID.fromString(\"$s\")").getOrElse("UUID.randomUUID()")
      case InstantTimestamp(defaultValue)        => defaultValue.map(s => s"Instant.parse(\"$s\")").getOrElse("Instant.now()")
      case OffsetDateTimeTimestamp(defaultValue) =>
        defaultValue.map(s => s"OffsetDateTime.parse(\"$s\")").getOrElse("OffsetDateTime.now()")
      case LocalDateTimeTimestamp(defaultValue)  =>
        defaultValue.map(s => s"LocalDateTime.parse(\"$s\")").getOrElse("LocalDateTime.now()")
      case LocalDateDate(defaultValue)           => defaultValue.map(s => s"LocalDate.parse(\"$s\")").getOrElse("LocalDate.now()")
      case LocalTimeTime(defaultValue)           => defaultValue.map(s => s"LocalTime.parse(\"$s\")").getOrElse("LocalTime.now()")
      case EnumString(_, defaultValue)           => defaultValue.map(s => s""""$s"""").getOrElse("\"\"")
      case EmbeddedEntityType(_, _, _)           => "\"\""
      case ObjectType(_)                         => "\"\""
      case DataElementType(dataElementId)        =>
        idFieldDefaultValue(domain.dataElements(dataElementId).dataType)
      case ListCollection(_)                     => "\"\""
      case SetCollection(_)                      => "\"\""
      case StringMapCollection(_)                => "\"\""
    }

  private def fieldDefaultValue(dataType: DataType): Option[String] =
    dataType match {
      case StringVarchar(_, defaultValue)        => defaultValue.map(s => s""""$s"""")
      case StringChar(_, defaultValue)           => defaultValue.map(s => s""""$s"""")
      case StringText(defaultValue)              => defaultValue.map(s => s""""$s"""")
      case StringJson(defaultValue)              => defaultValue.map(s => s""""$s"""")
      case StringJsonB(defaultValue)             => defaultValue.map(s => s""""$s"""")
      case IntInt(defaultValue)                  => defaultValue.map(s => s"$s")
      case LongLong(defaultValue)                => defaultValue.map(s => s"$s")
      case ShortSmallint(defaultValue)           => defaultValue.map(s => s"$s")
      case BigDecimalNumeric(_, _, defaultValue) => defaultValue.map(s => s"BigDecimal(\"$s\")")
      case BigIntegerNumeric(_, defaultValue)    => defaultValue.map(s => s"BigInteger(\"$s\")")
      case DoubleDouble(defaultValue)            => defaultValue.map(s => s"$s")
      case FloatFloat(defaultValue)              => defaultValue.map(s => s"$s")
      case BooleanBoolean(defaultValue)          => defaultValue.map(s => s"$s")
      case UuidUuid(defaultValue)                => defaultValue.map(s => s"UUID.fromString(\"$s\")")
      case InstantTimestamp(defaultValue)        => defaultValue.map(s => s"Instant.parse(\"$s\")")
      case OffsetDateTimeTimestamp(defaultValue) => defaultValue.map(s => s"OffsetDateTime.parse(\"$s\")")
      case LocalDateTimeTimestamp(defaultValue)  => defaultValue.map(s => s"LocalDateTime.parse(\"$s\")")
      case LocalDateDate(defaultValue)           => defaultValue.map(s => s"LocalDate.parse(\"$s\")")
      case LocalTimeTime(defaultValue)           => defaultValue.map(s => s"LocalTime.parse(\"$s\")")
      case EnumString(_, defaultValue)           => defaultValue.map(s => s""""$s"""")
      case EmbeddedEntityType(_, _, _)           => None
      case ObjectType(_)                         => None
      case DataElementType(dataElementId)        => fieldDefaultValue(domain.dataElements(dataElementId).dataType)
      case ListCollection(_)                     => None
      case SetCollection(_)                      => None
      case StringMapCollection(_)                => None
    }

  private def datatypeImports(datatypes: Seq[String]): Seq[String] =
    (
      datatypes.find(dt => dt.contains("BigDecimal")).map(_ => "java.math.BigDecimal") ::
        datatypes.find(dt => dt.contains("BigInteger")).map(_ => "java.math.BigInteger") ::
        datatypes.find(dt => dt.contains("Instant")).map(_ => "java.time.Instant") ::
        datatypes.find(dt => dt.contains("LocalDate")).map(_ => "java.time.LocalDate") ::
        datatypes.find(dt => dt.contains("LocalDateTime")).map(_ => "java.time.LocalDateTime") ::
        datatypes.find(dt => dt.contains("LocalTime")).map(_ => "java.time.LocalTime") ::
        datatypes.find(dt => dt.contains("OffsetDateTime")).map(_ => "java.time.OffsetDateTime") ::
        datatypes.find(dt => dt.contains("UUID")).map(_ => "java.util.UUID") ::
        Nil
    ).flatten

  private def fieldDatatype(dataType: DataType): String =
    dataType match {
      case StringVarchar(_, _)            => "String"
      case StringChar(_, _)               => "String"
      case StringText(_)                  => "String"
      case StringJson(_)                  => "String"
      case StringJsonB(_)                 => "String"
      case IntInt(_)                      => "Int"
      case LongLong(_)                    => "Long"
      case ShortSmallint(_)               => "Short"
      case BigDecimalNumeric(_, _, _)     => "BigDecimal"
      case BigIntegerNumeric(_, _)        => "BigInteger"
      case DoubleDouble(_)                => "Double"
      case FloatFloat(_)                  => "Float"
      case BooleanBoolean(_)              => "Boolean"
      case UuidUuid(_)                    => "UUID"
      case InstantTimestamp(_)            => "Instant"
      case OffsetDateTimeTimestamp(_)     => "OffsetDateTime"
      case LocalDateTimeTimestamp(_)      => "LocalDateTime"
      case LocalDateDate(_)               => "LocalDate"
      case LocalTimeTime(_)               => "LocalTime"
      case EnumString(_, _)               => "String"
      case EmbeddedEntityType(_, _, _)    => "Undefined"
      case ObjectType(entityId)           => domain.entities(entityId).entityName
      case DataElementType(dataElementId) => fieldDatatype(domain.dataElements(dataElementId).dataType)
      case ListCollection(dataType)       => s"List<${fieldDatatype(dataType)}>"
      case SetCollection(dataType)        => s"Set<${fieldDatatype(dataType)}>"
      case StringMapCollection(dataType)  => s"Map<String, ${fieldDatatype(dataType)}>"
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
