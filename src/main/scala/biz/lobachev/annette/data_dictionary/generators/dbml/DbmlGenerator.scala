package biz.lobachev.annette.data_dictionary.generators.dbml

import biz.lobachev.annette.data_dictionary.generators.{GeneratedFile, TextGenerator}
import biz.lobachev.annette.data_dictionary.model.*
import biz.lobachev.annette.data_dictionary.utils.StringSyntax.*

case class DbmlGenerator(domain: Domain, filename: String = "diagram.dbml") extends TextGenerator with DomainMetaHelper {

  override def generate(): Seq[GeneratedFile] = {
    val enums   = generateEnums()
    val content = domain.tables.values
      .map(table => generateTable(table))
      .mkString("\n")
    Seq(
      GeneratedFile(
        s"",
        filename,
        enums + content,
      ),
    )
  }

  private def generateEnums(): String =
    domain.enums.values.map { e =>
      val header = s"// ${e.name}"
      val en     = s"enum ${e.id.snakeCase} {"
      val lines  = e.elements.map(l => s"  ${l._1} [note: '${l._2}']")
      (Seq(header, en) ++: lines :+ "}\n").mkString("\n")

    }.mkString("", "\n", "\n")

  private def generateTable(entity: Table): String = {
    val fullTableName = getFullTableName(entity, domain)
    val tableName     = fullTableName.toDbName
    val fields        = generateFields(entity)
    val indexes       = renderIndexes(entity)
    val relations     = generateRelations(entity)
    s"table ${tableName} {\n$fields" +
      (if (indexes.nonEmpty) s"\n$indexes" else "") +
      (if (entity.name.nonEmpty) s"\n  note: '${entity.name}'\n" else "") +
      "}\n" +
      (if (relations.nonEmpty) s"\n$relations\n" else "")

  }

  private def generateFields(table: Table): String =
    table.fields.map { field =>
      val datatype  = getPostgresTableFieldDatatype(field, domain).datatype
      val params    = Seq(
        if (table.pk.length == 1 && table.pk.head == field.fieldName) Some("pk") else None,
        if (field.notNull) Some("not null") else None,
        if (
          field.generated.isInstanceOf[GeneratedAlwaysAsIdentity.type] ||
          field.generated.isInstanceOf[GeneratedByDefaultAsIdentity.type]
        ) Some("increment")
        else None,
        if (field.name.nonEmpty) Some(s"note: '${field.name}'") else None,
      ).flatten
      val paramStr  = if (params.nonEmpty) params.mkString("[", ", ", "]") else ""
      val fieldname = wrapQuotes(field.dbFieldName)
      s"  $fieldname $datatype $paramStr\n"
    }.mkString

  private def renderIndexes(table: Table): String = {
    val pk      = if (table.pk.length > 1) {
      val pkFields = table.pk.map(f => getTableField(table.fields, f).dbFieldName).mkString("(", ", ", ")")
      Some(s"    $pkFields [pk]\n")
    } else None
    val indexes = table.indexes.map { index =>
      val indexId  = table.tableName + '_' + index.id.snakeCase
      val fields   =
        if (index.fields.length == 1) index.fields.map(f => getTableField(table.fields, f.fieldName).dbFieldName).head
        else index.fields.map(f => getTableField(table.fields, f.fieldName).dbFieldName).mkString("(", ", ", ")")
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

  private def generateRelations(table: Table): String = {
    val tableName = getFullTableName(table, domain).toDbName
    val fields    = table.fields
    val relations = table.relations
    relations.map { relation =>
      val relationTable     = domain.tables(relation.referenceTableId)
      val relationTableName = getFullTableName(relationTable, domain).toDbName
      val relationType      = relation.relationType match {
        case ManyToOne => ">"
        case OneToOne  => "-"
      }
      val comment           = if (relation.name.nonEmpty) s"// ${relation.name}\n" else ""
      val relationId        = table.tableName + '_' + relation.id.snakeCase
      if (relation.fields.size == 1) {
        val f1 = getTableField(fields, relation.fields.head._1).dbFieldName
        val f2 = getTableField(relationTable.fields, relation.fields.head._2).dbFieldName
        s"${comment}Ref $relationId: ${tableName}.$f1 $relationType ${relationTableName}.$f2\n"
      } else {
        val f1 = relation.fields.map(f => getTableField(fields, f._1).dbFieldName).mkString("(", ", ", ")")
        val f2 =
          relation.fields
            .map(f => getTableField(relationTable.fields, f._2).dbFieldName)
            .mkString("(", ", ", ")")
        s"${comment}Ref $relationId: ${tableName}.$f1 $relationType ${relationTableName}.$f2\n"
      }
    }.mkString
  }

}
