package biz.lobachev.annette.data_dictionary.generators.kotlin

import biz.lobachev.annette.data_dictionary.generators.{GeneratedFile, TextGenerator}
import biz.lobachev.annette.data_dictionary.labels.Java.JAVA_TABLE_PACKAGE
import biz.lobachev.annette.data_dictionary.labels.Postgres.POSTGRES_SCHEMA
import biz.lobachev.annette.data_dictionary.model.*
import org.fusesource.scalate.TemplateEngine

import scala.io.Source

case class KotlinGenerator(domain: Domain) extends TextGenerator with DomainMetaHelper {

  val engine = new TemplateEngine
  engine.escapeMarkup = false

  override def generate(): Seq[GeneratedFile] =
    generateTableClasses()

  private def generateTableClasses(): Seq[GeneratedFile] = {
    val source   = Source.fromResource("kotlin/class.ssp").getLines().mkString("\n")
    val template = engine.compileSsp(source)

    domain.tables.values
      .flatMap(generateTable)
      .map { clazz =>
        val output = engine.layout(
          uri = "class.ssp",
          template = template,
          attributes = Map(
            "cl" -> clazz,
          ),
        )
        GeneratedFile(
          path = clazz.pkg.replace(".", "/"),
          filename = s"${clazz.name}.kt",
          content = output,
        )
      }
      .toSeq
  }

  private def generateTable(table: Table): Seq[KtClass] = {
    val pkg      = getTableLabel(table, JAVA_TABLE_PACKAGE, domain).getOrElse("model")
    val schema   = getTableLabel(table, POSTGRES_SCHEMA, domain).map(s => s""", schema = "$s"""").getOrElse("")
    val comments = table.name +: description2Comments(table.description)
    val members  = table.fields.map(m => generateMember(table, m))

    val (relationMembers, relationImports) = generateRelations(table, pkg, members.map(_.field))
    val imports                            = datatypeImports(members.map(_.datatype))
    val annotations                        = Seq(
      "@Entity",
      s"""@Table(name = "${table.tableName}"$schema)""",
    )
    val idClass                            = if (table.pk.length > 1) {
      val idMembers = members
        .filter(m => table.pk.contains(m.name))
        .map(m =>
          m.copy(
            annotations = Seq.empty,
            defaultValue = Some(idFieldDefaultValue(m.field.datatype)),
          ),
        )
      val idImports = datatypeImports(idMembers.map(_.datatype))
      Some(
        KtClass(
          pkg = pkg,
          imports = idImports,
          comments = comments,
          annotations = Seq.empty,
          name = s"${table.entityName}Id",
          members = idMembers,
          extensions = Some("Serializable"),
        ),
      )
    } else None
    val idClassAnnotation                  = idClass.map(c => Seq(s"@IdClass(${c.name}::class)")).getOrElse(Seq.empty)
    val entityClass                        = KtClass(
      pkg = pkg,
      imports = Seq("jakarta.persistence.*") ++ imports ++ relationImports.toSeq,
      comments = table.name +: description2Comments(table.description),
      annotations = annotations ++ idClassAnnotation,
      name = s"${table.entityName}Entity",
      members = members ++ relationMembers,
      None,
    )

    idClass.map(idCl => Seq(entityClass, idCl)).getOrElse(Seq(entityClass))
  }

  def generateRelations(table: Table, pkg: String, fields: Seq[TableField]): (Seq[KtClassMember], Set[String]) = {
    var imports = Set.empty[String]
//    var names   = Map.empty[String, Int]

    val relationMembers = table.relations
      .filter(_.association.isDefined)
      .map { relation =>
        val relatedTable         = domain.tables(relation.referenceTableId)
        val relatedTableFields   = relatedTable.fields
        val relatedPkg           = getTableLabel(relatedTable, JAVA_TABLE_PACKAGE, domain).getOrElse("model")
        val relatedClass: String = s"${relatedTable.entityName}Entity"
        if (relatedPkg != pkg) imports = imports + s"$relatedPkg.$relatedClass"
        var datatype             = ""
        datatype = s"$relatedClass?"
        val name                 = relation.association.get.name
        val joinColumns          = relation.fields.map { case name -> refName =>
          val fieldName    = fields.find(_.fieldName == name).map(_.dbFieldName).getOrElse(name)
          val refFieldName = relatedTableFields.find(_.fieldName == refName).map(_.dbFieldName).getOrElse(refName)
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
          annotations = joinAnnotation,
          name = name,
          datatype = datatype,
          defaultValue = Some("null"),
          field = null,
        )
      }

    val refRelationMembers =
      domain.tables.values
        .flatMap(e =>
          e.relations
            .filter(r =>
              r.referenceTableId == table.id &&
                r.association.map(_.referenceName.isDefined).getOrElse(false),
            )
            .map(r => e -> r),
        )
        .map { case relatedTable -> relation =>
          val relatedPkg           = getTableLabel(relatedTable, JAVA_TABLE_PACKAGE, domain).getOrElse("model")
          val relatedClass: String = s"${relatedTable.entityName}Entity"
          if (relatedPkg != pkg) imports = imports + s"$relatedPkg.$relatedClass"
          val name                 = relation.association.get.referenceName.get
          val relField             = relation.association.get.name
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
            annotations = joinAnnotation,
            name = name,
            datatype = datatype,
            defaultValue = Some("null"),
            field = null,
          )
        }
    (relationMembers ++ refRelationMembers, imports)
  }

  def generateMember(table: Table, field: TableField): KtClassMember = {
    val idAnnotation             = if (table.pk.contains(field.fieldName)) Seq("@Id") else Seq.empty
    val generatedValueAnnotation =
      if (
        field.generated.isInstanceOf[GeneratedAlwaysAsIdentity.type] ||
        field.generated.isInstanceOf[GeneratedByDefaultAsIdentity.type]
      ) Seq("@GeneratedValue(strategy = GenerationType.IDENTITY)")
      else Seq.empty

    val nullable         = if (field.notNull) ", nullable = false" else ""
    val length           = fieldLength(field.datatype).map(l => s", length = $l").getOrElse("")
    val precision        = fieldPrecision(field.datatype).map(p => s", precision = $p").getOrElse("")
    val scale            = fieldScale(field.datatype).map(p => s", scale = $p").getOrElse("")
    val dt               = getKotlinTableFieldDatatype(field, domain).datatype
    val datatype         = if (field.notNull) dt else s"$dt?"
    val defaultValue     = if (field.notNull) None else Some("null")
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

  private def idFieldDefaultValue(datatype: Datatype): String =
    datatype match {
      case VarcharDatatype(length)                   => "\"\""
      case CharDatatype(length)                      => "\"\""
      case StringDatatype                            => "\"\""
      case JsonDatatype                              => "\"\""
      case JsonBDatatype                             => "\"\""
      case GoIntDatatype                             => "0"
      case SmallintDatatype                          => "0"
      case IntegerDatatype                           => "0"
      case LongDatatype                              => "0"
      case SmallSerialDatatype                       => "0"
      case SerialDatatype                            => "0"
      case BigSerialDatatype                         => "0"
      case Int8Datatype                              => "0"
      case UIntDatatype                              => "0"
      case UInt8Datatype                             => "0"
      case UInt16Datatype                            => "0"
      case UInt32Datatype                            => "0"
      case UInt64Datatype                            => "0"
      case DecimalDatatype(precision, scale)         => "BigDecimal(0)"
      case FloatDatatype                             => "0.0"
      case DoubleDatatype                            => "0.0"
      case BooleanDatatype                           => "false"
      case UuidDatatype                              => "UUID.randomUUID()"
      case InstantDatatype                           => "Instant.now()"
      case OffsetDateTimeDatatype                    => "OffsetDateTime.now()"
      case DateTimeDatatype                          => "LocalDateTime.now()"
      case DateDatatype                              => "LocalDate.now()"
      case TimeDatatype                              => "LocalTime.now()"
      case EnumDatatype(enumId)                      =>
        domain.enums(enumId).enumType match {
          case NativeEnum => "\"\""
          case StringEnum => "\"\""
          case IntEnum    => "0"
        }
      case DataElementDatatype(dataElementId)        =>
        idFieldDefaultValue(domain.dataElements(dataElementId).datatype)
      case ArrayDatatype(datatype)                   => "emptyList()"
      case SetDatatype(datatype)                     => "emptySet()"
      case StringMapDatatype(datatype)               => "emptyMap()"
      case StructDatatype(structId, relation)        => "\"\""
      case StructArrayDatatype(structId, relationId) => "\"\""
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

  private def fieldPrecision(datatype: Datatype): Option[Int] =
    datatype match {
      case DecimalDatatype(precision, _)      => Some(precision)
      case DataElementDatatype(dataElementId) => fieldPrecision(domain.dataElements(dataElementId).datatype)
      case _                                  => None
    }

  private def fieldScale(datatype: Datatype): Option[Int] =
    datatype match {
      case DecimalDatatype(_, scale)          => Some(scale)
      case DataElementDatatype(dataElementId) => fieldScale(domain.dataElements(dataElementId).datatype)
      case _                                  => None
    }

  private def fieldLength(datatype: Datatype): Option[Int] =
    datatype match {
      case VarcharDatatype(length)            => Some(length)
      case CharDatatype(length)               => Some(length)
      case DataElementDatatype(dataElementId) => fieldLength(domain.dataElements(dataElementId).datatype)
      case EnumDatatype(enumId)               =>
        domain.enums(enumId).enumType match {
          case StringEnum => Some(domain.enums(enumId).length)
          case _          => None
        }
      case _                                  => None
    }

  private def description2Comments(description: String): Seq[String] =
    if (description.isBlank) Seq.empty
    else
      description
        .replace("<br>", "\n")
        .split("\n")
        .toSeq

}
