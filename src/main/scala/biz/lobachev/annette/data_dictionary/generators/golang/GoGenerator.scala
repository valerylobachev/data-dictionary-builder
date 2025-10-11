package biz.lobachev.annette.data_dictionary.generators.golang

import biz.lobachev.annette.data_dictionary.generators.{GeneratedFile, TextGenerator}
import biz.lobachev.annette.data_dictionary.labels.Go
import biz.lobachev.annette.data_dictionary.model.*
import biz.lobachev.annette.data_dictionary.utils.StringSyntax.*
import org.fusesource.scalate.TemplateEngine

import scala.io.Source

case class GoGenerator(
  domain: Domain,
  options: GoGeneratorOptions,
) extends TextGenerator
    with DomainMetaHelper {

  val SQL_PACKAGE = "database/sql"

  private val (isGorm, fieldNullable) = options.dbLib match {
    case Gorm(fieldNullable) => (true, fieldNullable)
    case Sqlx                => (false, false)
  }

  private val engine = new TemplateEngine
  engine.escapeMarkup = false

  override def generate(): Seq[GeneratedFile] =
    generateTables() ++ generateEnums() ++ generateStructs()

  def generateTables(): Seq[GeneratedFile] = {
    val source   = Source.fromResource("golang/table.ssp").getLines().mkString("\n")
    val template = engine.compileSsp(source)

    domain.tables.values
      .flatMap(generateTable)
      .map { struct =>
        val output = engine.layout(
          uri = "table.ssp",
          template = template,
          attributes = Map(
            "data" -> struct,
          ),
        )
        GeneratedFile(
          path = struct.pkg,
          filename = s"${struct.name.snakeCase}.go",
          content = output,
        )
      }
      .toSeq
  }

  private def generateTable(table: Table): Seq[GoTableData] = {
    val pkg = getGoTablePackage(table, domain)

    val splited       = pkg.split("/")
    val lastPkg       = splited.last
    val fullTableName = getFullTableName(table, domain)
    val comments      = table.name +: description2Comments(table.description)
    val useSqlNull    = table.fields.find(f => !f.notNull).nonEmpty

    val (ownRelations, ownRelationTargetDatatypes) = calculateOwnRelations(table)
    val (refRelations, refRelationTargetDatatypes) = calculateRefRelations(table)

    val importMap =
      evaluateImports(
        table.fields.map(r =>
          getGoDatatype(r.datatype, domain),
        ) ++ ownRelationTargetDatatypes ++ refRelationTargetDatatypes,
        useSqlNull,
      )

    val members            = table.fields.map(m => generateTableMember(table, m, importMap))
    val ownRelationMembers = calculateOwnRelationMembers(pkg, ownRelations, importMap)
    val refRelationMembers = calculateRefRelationMembers(pkg, refRelations, importMap)
    val imports            = importMap.values.filter(_.pkg.pkg != pkg).toSeq

    val constants = getTableConstants(table)

    val tableData = GoTableData(
      pkg = pkg,
      lastPkg = lastPkg,
      imports = imports,
      comments = comments,
      name = s"${table.entityName}Entity",
      entityName = table.entityName,
      tableName = fullTableName.tableName,
      isGorm = options.dbLib.isInstanceOf[Gorm],
      schemaName = fullTableName.schema,
      members = members,
      ownRelationMembers = ownRelationMembers,
      refRelationMembers = refRelationMembers,
      constants = constants,
    )

    Seq(tableData)
  }

  private def calculateRefRelationMembers(
    structPkg: String,
    relationTuples: Seq[(Table, Seq[TableRelation])],
    pkgMap: Map[String, GoImport],
  ): Seq[GoStructMember] =
    relationTuples.flatMap { case refTable -> relations =>
      relations.map { r =>
        val pkg   = getGoTablePackage(refTable, domain)
        val alias = if (pkg != structPkg) {
          pkgMap
            .get(pkg)
            .map(gi => s"${gi.alias}.")
            .getOrElse("")
        } else ""

        val datatype =
          if r.relationType == OneToOne then s"*$alias${refTable.entityName}"
          else s"[]$alias${refTable.entityName}Entity"
        val fk1      = r.fields.map(_._2.pascalCase).mkString(",")
        val fk2      = r.fields.map(_._1.pascalCase).mkString(",")
        val tags     = s"`gorm:\"foreignKey:$fk1;references:$fk2\"`"
        GoStructMember(
          comments = Seq(s"Reference '${r.id}' from ${refTable.name}"),
          name = r.association.get.referenceName.get.pascalCase,
          datatype = datatype,
          tags = tags,
        )
      }
    }

  private def calculateOwnRelationMembers(
    structPkg: String,
    relations: Seq[TableRelation],
    pkgMap: Map[String, GoImport],
  ): Seq[GoStructMember] =
    relations.map { r =>
      val refTable = domain.tables(r.referenceTableId)
      val pkg      = getGoTablePackage(refTable, domain)
      val alias    = if (pkg != structPkg) {
        pkgMap
          .get(pkg)
          .map(gi => s"${gi.alias}.")
          .getOrElse("")
      } else ""

      val datatype = s"*$alias${refTable.entityName}Entity"
      val fk1      = r.fields.map(_._1.pascalCase).mkString(",")
      val fk2      = r.fields.map(_._2.pascalCase).mkString(",")
      val tags     = s"`gorm:\"foreignKey:$fk1;references:$fk2\"`"
      GoStructMember(
        comments = Seq(r.name),
        name = r.association.get.name.pascalCase,
        datatype = datatype,
        tags = tags,
      )
    }

  private def calculateRefRelations(table: Table) =
    if (isGorm) {
      val refRelations = domain.tables.values.flatMap { t =>
        val relations =
          t.relations.filter(r =>
            r.referenceTableId == table.id &&
              r.association.flatMap(_.referenceName).isDefined,
          )
        if relations.nonEmpty then Some(t -> relations)
        else None
      }

      val refRelationTargetDatatypes = refRelations.map { case t -> _ =>
        GoTargetDatatype(
          variant = SimpleTypeVariant,
          pkg = Some(
            GoPackage(getGoTablePackage(t, domain)),
          ),
          datatype = t.entityName + "Entity",
        )
      }
      (refRelations.toSeq, refRelationTargetDatatypes.toSeq)
    } else {
      (Seq.empty, Seq.empty)
    }

  private def calculateOwnRelations(table: Table) =
    if (isGorm) {
      val ownRelations                = table.relations.filter(_.association.isDefined)
      val ownRelationsTargetDatatypes = ownRelations.map { r =>
        val t = domain.tables(r.referenceTableId)
        GoTargetDatatype(
          variant = SimpleTypeVariant,
          pkg = Some(
            GoPackage(getGoTablePackage(t, domain)),
          ),
          datatype = t.entityName + "Entity",
        )
      }
      (ownRelations, ownRelationsTargetDatatypes)
    } else {
      (Seq.empty, Seq.empty)
    }

  private def getTableConstants(table: Table) = {
    val pk  = Constant(
      s"${table.entityName}PK",
      getTablePrimaryKeyConstraintName(table.tableName),
    )
    val fks = table.relations
      .filter(!_.logical)
      .flatMap { r =>
        val fk  = Constant(
          s"${table.entityName}FK${r.id.pascalCase}",
          getTableForeignKeyConstraintName(table.tableName, r.id),
        )
        val fku = if (r.relationType == OneToOne) {
          Some(
            Constant(
              s"${table.entityName}FKUQ${r.id.pascalCase}",
              getTableForeignKeyUniqueIndexConstraintName(table.tableName, r.id),
            ),
          )
        } else None
        Seq(fk) ++ fku.toSeq
      }

    val uqs = table.indexes
      .filter(_.unique)
      .map(r =>
        Constant(
          s"${table.entityName}UQ${r.id.pascalCase}",
          getTableIndexConstraintName(table.tableName, r.id),
        ),
      )

    Seq(pk) ++ fks ++ uqs
  }

  private def evaluateImports(targetDatatypes: Seq[GoTargetDatatype], useSqlNull: Boolean): Map[String, GoImport] = {
    val sqlNullPackage =
      if isGorm && fieldNullable then Seq.empty
      else if useSqlNull then Seq(GoPackage(SQL_PACKAGE))
      else Seq.empty
    val preImports     = (sqlNullPackage ++ targetDatatypes.flatMap { targetDatatype =>
      val genericPackage = targetDatatype.variant match {
        case GenericTypeVariant(pkg, datatype) => Some(pkg)
        case _                                 => None
      }
      genericPackage.toSeq ++ targetDatatype.pkg.toSeq
    }).toSet.map(pkg => GoImport(pkg.lastPkg(), pkg, false))
    preImports
      .groupBy(_.alias)
      .flatMap { case alias -> imports =>
        imports.zipWithIndex.map { case imp -> idx =>
          if idx == 0 then imp
          else imp.copy(alias = s"$alias$idx")
        }
      }
      .groupBy(_.pkg.pkg)
      .map { case pkg -> list => pkg -> list.head }
  }

  private def generateTableMember(table: Table, field: TableField, pkgMap: Map[String, GoImport]): GoStructMember = {
    val tags = if (isGorm) {
      val notNull = if (field.notNull) ";not null" else ""
      val pk      = if (table.pk.contains(field.fieldName)) ";primaryKey" else ""
      s"`gorm:\"column:${field.dbFieldName}$pk$notNull\"`"
    } else {
      s"`db:\"${field.dbFieldName}\"`"
    }

    val targetDatatype = getGoDatatype(field.datatype, domain)
    val alias          = targetDatatype.pkg
      .flatMap(p => pkgMap.get(p.pkg))
      .map(gi => s"${gi.alias}.")
      .getOrElse("")
    val dt             = s"$alias${targetDatatype.datatype}"
    val datatype       = targetDatatype.variant match {
      case SimpleTypeVariant                 =>
        if (field.notNull) dt
        else if (isGorm && fieldNullable) s"*$dt"
        else {
          val sqlAlias = pkgMap
            .get(SQL_PACKAGE)
            .map(gi => s"${gi.alias}.")
            .getOrElse("")
          s"${sqlAlias}Null[$dt]"
        }
      case ArrayTypeVariant                  =>
        s"[]$dt"
      case MapTypeVariant                    =>
        s"map[string]$dt"
      case GenericTypeVariant(pkg, datatype) =>
        val genAlias = pkgMap
          .get(pkg.pkg)
          .map(gi => s"${gi.alias}.")
          .getOrElse("")
        val genDt    = s"$genAlias$datatype[$dt]"
        if (field.notNull) genDt
        else s"*$genDt"
    }

    GoStructMember(
      comments = field.name +: description2Comments(field.description),
      name = field.fieldName.pascalCase,
      datatype = datatype,
      tags = tags,
    )
  }

  def generateEnums(): Seq[GeneratedFile] = {
    val source   = Source.fromResource("golang/enums.ssp").getLines().mkString("\n")
    val template = engine.compileSsp(source)
    domain.enums.values
      // group by enum package
      .groupBy(e => getGoEnumPackage(e, domain))
      .map { case pkg -> enums =>
        val splited              = pkg.split("/")
        val lastPkg              = splited.last
        val goEnums: Seq[GoEnum] = enums.map { enumData =>
          val comments = enumData.name +: description2Comments(enumData.description)
          GoEnum(
            comments = comments,
            constants = enumData.elements.map { element =>
              GoEnumConstant(
                constant = s"${enumData.id.snakeCase}_${element.constName.snakeCase}".toUpperCase,
                value = if enumData.enumType == IntEnum then element.id else s"\"${element.id}\"",
                comment = element.name,
              )
            },
          )
        }.toSeq
        val enumData             = GoEnumData(
          pkg = pkg,
          lastPkg = lastPkg,
          enums = goEnums,
        )
        val output               = engine.layout(
          uri = "enums.ssp",
          template = template,
          attributes = Map(
            "data" -> enumData,
          ),
        )
        GeneratedFile(
          path = pkg,
          filename = "enums.go",
          content = output,
        )
      }
      .toSeq
  }

  def generateStructs(): Seq[GeneratedFile] = {
    val source   = Source.fromResource("golang/struct.ssp").getLines().mkString("\n")
    val template = engine.compileSsp(source)

    val structPackages = evalStructPackages()

    structPackages
      .flatMap(generateStructPackageFiles)
      .map { struct =>
        val output = engine.layout(
          uri = "struct.ssp",
          template = template,
          attributes = Map(
            "data" -> struct,
          ),
        )
        GeneratedFile(
          path = struct.pkg,
          filename = s"${struct.filename}.go",
          content = output,
        )
      }
  }

  private def evalStructPackages(): Seq[GoStructPackage] =
    domain.structs.values.map { dm =>
      val pkg      = getGoStructPackage(dm, domain)
      val filename = getStructLabel(dm, Go.GO_STRUCT_SOURCE_FILE, domain)
        .getOrElse(dm.structName.snakeCase)
      GoStructPackageFile(pkg, filename, dm)
    }
      .groupBy(_.pkg)
      .map { case pkg -> seq =>
        val files = seq.groupBy(_.filename).map { case file -> fSeq => file -> fSeq.toSeq }
        GoStructPackage(pkg, files)
      }
      .toSeq

  private def generateStructPackageFiles(dmPackage: GoStructPackage): Seq[GoStructsData] = {

    val splited = dmPackage.pkg.split("/")
    val lastPkg = splited.last

    dmPackage.files.map { case filename -> dmSeq =>
      val fieldDatatypes = dmSeq.flatMap(_.struct.fields.map(r => getGoDatatype(r.datatype, domain)))
      val importMap      = evaluateImports(fieldDatatypes, false)
      val imports        = importMap.values.filter(_.pkg.pkg != dmPackage.pkg).toSeq
      val structs        = dmSeq.map { dms =>
        val dm       = dms.struct
        val comments = dm.name +: description2Comments(dm.description)
        val members  = dm.fields.map(f => generateStructMember(dmPackage.pkg, f, importMap))

        GoStructData(
          comments = comments,
          name = dm.name,
          structName = dm.structName,
          members = members,
        )
      }

      GoStructsData(
        filename = filename,
        pkg = dmPackage.pkg,
        lastPkg = lastPkg,
        imports = imports,
        structs = structs,
      )
    }.toSeq
  }

  private def generateStructMember(
    structPkg: String,
    field: StructField,
    pkgMap: Map[String, GoImport],
  ): GoStructMember = {
    val tags = {
      val omitempty = if (field.optional) ",omitempty" else ""
      s"`json:\"${field.fieldName}$omitempty\"`"
    }

    val targetDatatype = getGoDatatype(field.datatype, domain)
    val alias          = targetDatatype.pkg
      .flatMap(p =>
        if p.pkg != structPkg then pkgMap.get(p.pkg)
        else None,
      )
      .map(gi => s"${gi.alias}.")
      .getOrElse("")
    val dt             = s"$alias${targetDatatype.datatype}"
    val datatype       = targetDatatype.variant match {
      case SimpleTypeVariant                 =>
        if (field.optional) s"*$dt"
        else dt
      case ArrayTypeVariant                  =>
        s"[]$dt"
      case MapTypeVariant                    =>
        s"map[string]$dt"
      case GenericTypeVariant(pkg, datatype) =>
        val genAlias = if (pkg.pkg != structPkg) {
          pkgMap
            .get(pkg.pkg)
            .map(gi => s"${gi.alias}.")
            .getOrElse("")
        } else ""
        val genDt    = s"$genAlias$datatype[$dt]"
        if (field.optional) s"*$genDt"
        else genDt
    }

    GoStructMember(
      comments = field.name +: description2Comments(field.description),
      name = field.fieldName.pascalCase,
      datatype = datatype,
      tags = tags,
    )
  }

  private def description2Comments(description: String): Seq[String] =
    if (description.isBlank) Seq.empty
    else
      description
        .replace("<br>", "\n")
        .split("\n")
        .toSeq

}
