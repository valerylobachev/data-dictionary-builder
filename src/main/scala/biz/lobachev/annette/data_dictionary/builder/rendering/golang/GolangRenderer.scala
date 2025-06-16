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

  private def description2Comments(description: String): Seq[String] =
    if (description.isBlank) Seq.empty
    else
      description
        .replace("<br>", "\n")
        .split("\n")
        .toSeq

}
