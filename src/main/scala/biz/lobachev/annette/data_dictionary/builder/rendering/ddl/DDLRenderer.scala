package biz.lobachev.annette.data_dictionary.builder.rendering.ddl

import biz.lobachev.annette.data_dictionary.builder.POSTGRESQL
import biz.lobachev.annette.data_dictionary.builder.labels.Audit.{AUDIT_TABLE, DISABLE_AUDIT}
import biz.lobachev.annette.data_dictionary.builder.labels.OverrideDatatype.POSTGRESQL_DATA_TYPE
import biz.lobachev.annette.data_dictionary.builder.model._
import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, TextRenderer}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

case class DDLRenderer(
  domain: Domain,
  filename: String = "postgres_ddl.sql",
  enableAudit: Boolean = false,
  auditFilename: String = "audit.sql",
) extends TextRenderer {

  def header = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val date      = OffsetDateTime.now().toLocalDateTime.format(formatter)
    Seq(
      s"-- ${domain.name}",
      "-- Code generated by Data Dictionary Builder (c) Valery Lobachev (https://github.com/valerylobachev/data-dictionary-builder).",
      s"-- Date: $date",
    )
  }

  override def render(): Seq[RenderResult] = {
    val res         = domain.rootComponentIds.map(id => renderComponent(id))
    val tables      = res.map(_.tables).filter(_.nonEmpty).mkString("\n", "\n", "\n")
    val indexes     = res.map(_.indexes).filter(_.nonEmpty).mkString("\n")
    val comments    = res.map(_.comments).filter(_.nonEmpty).mkString("\n", "\n", "\n")
    val relations   = res.map(_.relations).filter(_.nonEmpty).mkString("\n", "\n", "\n")
    val auditResult = if (enableAudit) {
      val audit = postProcessAudit(res.map(_.audit))
      Some(
        RenderResult(
          s"",
          auditFilename,
          (
            header ++ Seq(
              "",
              audit,
              "",
            )
          ).mkString("\n"),
        ),
      )
    } else None

    Seq(
      Some(
        RenderResult(
          s"",
          filename,
          (
            header ++ Seq(
              tables,
              indexes,
              comments,
              relations,
              "",
            )
          ).mkString("\n"),
        ),
      ),
      auditResult,
    ).flatten
  }

  private def postProcessAudit(audits: Seq[Audit]): String =
    if (enableAudit && audits.nonEmpty) {
      val audit        = Audit.aggregate(audits)
      val jsonDiffFunc = s"""CREATE OR REPLACE FUNCTION jsonb_diff_val(val1 JSONB, val2 JSONB)
                            |    RETURNS JSONB AS
                            |$$$$
                            |DECLARE
                            |    result JSONB;
                            |    v      RECORD;
                            |BEGIN
                            |    result = val1;
                            |    FOR v IN SELECT * FROM jsonb_each(val2)
                            |        LOOP
                            |            IF result @> jsonb_build_object(v.key, v.value)
                            |            THEN
                            |                result = result - v.key;
                            |            ELSIF result ? v.key THEN
                            |                CONTINUE;
                            |            ELSE
                            |                result = result || jsonb_build_object(v.key, 'null');
                            |            END IF;
                            |        END LOOP;
                            |    RETURN result;
                            |END;
                            |$$$$ LANGUAGE plpgsql;""".stripMargin
      val auditTables  = audit.auditTables
        .map(t => s"""CREATE TABLE $t (
                     |  "table" text NOT NULL,
                     |  "id" text NOT NULL,
                     |  "counter" bigserial NOT NULL,
                     |  "dt" timestamptz NOT NULL,
                     |  "operation" varchar(1) NOT NULL,
                     |  "prev_data" jsonb,
                     |  "new_data" jsonb,
                     |  "updated_by" text,
                     |  PRIMARY KEY ("table", "id", "counter")
                     |);""".stripMargin)
        .toSeq
      (auditTables ++ Seq(jsonDiffFunc) ++ audit.generatedAudit).mkString("\n\n")
    } else ""

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
      audit = Audit.aggregate((res1 ++ res2).map(_.audit)),
    )
  }

  private def renderEntity(entity: Entity): DDLSegments =
    DDLSegments(
      tables = renderTable(entity),
      indexes = renderIndexes(entity),
      comments = renderComments(entity),
      relations = renderRelations(entity),
      audit = if (enableAudit) renderAudit(entity) else Audit.empty,
    )

  private def renderTable(entity: Entity): String = {
    val fields = entity.expandedFields.map { field =>
      val fieldName  = wrapQuotes(field.dbFieldName)
      var datatype   =
        field.labels.get(POSTGRESQL_DATA_TYPE).getOrElse(domain.getTargetDataType(field.dataType, POSTGRESQL))
      if (field.autoIncrement && datatype == "integer") datatype = "serial"
      else if (field.autoIncrement && datatype == "bigint") datatype = "bigserial"
      else if (field.autoIncrement && datatype == "smallint") datatype = "smallserial"
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
        val unique      = if (index.unique) "UNIQUE " else ""
        val indexId     = wrapQuotes(entity.tableName + "_" + index.id.snakeCase)
        val fields      = index.fields.map(f => getEntityFieldName(entity.expandedFields, f)).mkString("(", ", ", ")")
        val usingMethod = index.method.map(m => s"USING ${m.method} ").getOrElse("")
        val include     = if (index.includeFields.nonEmpty) {
          val includeFields =
            index.includeFields.map(f => getEntityFieldName(entity.expandedFields, f)).mkString("(", ", ", ")")
          s" INCLUDE $includeFields "
        } else ""
        val where       = index.condition.map(c => s" WHERE $c").getOrElse("")
        s"CREATE ${unique}INDEX $indexId ON ${entity.tableNameWithSchema()} $usingMethod$fields$include$where;\n"
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
    entity.expandedRelations
      .filter(!_.logical)
      .map { relation =>
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
      }
      .mkString("\n")
  }

  def getEntityFieldName(entityFields: Seq[EntityField], fieldName: String): String =
    entityFields
      .find(f => f.fieldName == fieldName)
      .map(f => wrapQuotes(f.dbFieldName))
      .getOrElse {
        throw new IllegalArgumentException(s"not found field $fieldName")
      }

  private def renderAudit(entity: Entity): Audit =
    domain
      .getEntityLabel(entity, AUDIT_TABLE)
      .filter(_ => domain.getEntityLabel(entity, DISABLE_AUDIT).map(_ != "true").getOrElse(true))
      .map { auditTable =>
        val table           = entity.tableNameWithSchema()
        val auditFunc       = entity.tableName + "_audit_func"
        val trigger         = entity.tableName + "_audit_trigger"
        val pkFields        = entity.pk
          .map(pkField => getEntityFieldName(entity.expandedFields, pkField))
        val newIdExpression = pkFields.map(f => s"NEW.$f::text").mkString(" || '|' || ")
        val oldIdExpression = pkFields.map(f => s"OLD.$f::text").mkString(" || '|' || ")

        val updatedByField = entity.expandedFields.find { f =>
          val datatype = domain.getTargetDataType(f.dataType, POSTGRESQL)

          f.fieldName == "updatedBy" && (
            datatype.startsWith("char") ||
              datatype.startsWith("varchar") ||
              datatype.startsWith("text")
          )
        }
          .map(f => getEntityFieldName(entity.expandedFields, f.fieldName))

        val newUpdatedBy = updatedByField.map(f => s"NEW.$f").getOrElse("NULL")

        val audit =
          s"""CREATE OR REPLACE FUNCTION $auditFunc()
             |    RETURNS trigger AS
             |$$$$
             |BEGIN
             |    if (TG_OP = 'INSERT') then
             |        INSERT INTO $auditTable ("table",
             |                            id,
             |                            dt,
             |                            operation,
             |                            prev_data,
             |                            new_data,
             |                            updated_by)
             |        VALUES (TG_TABLE_NAME,
             |                $newIdExpression,
             |                CURRENT_TIMESTAMP,
             |                'I',
             |                null,
             |                to_jsonb(NEW),
             |                $newUpdatedBy);
             |
             |        RETURN NEW;
             |    elsif (TG_OP = 'UPDATE') then
             |        INSERT INTO $auditTable ("table",
             |                            id,
             |                            dt,
             |                            operation,
             |                            prev_data,
             |                            new_data,
             |                            updated_by)
             |        VALUES (TG_TABLE_NAME,
             |                $newIdExpression,
             |                CURRENT_TIMESTAMP,
             |                'U',
             |                jsonb_diff_val(to_jsonb(OLD), to_jsonb(NEW)),
             |                jsonb_diff_val(to_jsonb(NEW), to_jsonb(OLD)),
             |                $newUpdatedBy);
             |
             |        RETURN NEW;
             |    elsif (TG_OP = 'DELETE') then
             |        INSERT INTO $auditTable ("table",
             |                            id,
             |                            dt,
             |                            operation,
             |                            prev_data,
             |                            new_data,
             |                            updated_by)
             |        VALUES (TG_TABLE_NAME,
             |                $oldIdExpression,
             |                CURRENT_TIMESTAMP,
             |                'D',
             |                to_jsonb(OLD),
             |                NULL,
             |                NULL);
             |
             |        RETURN OLD;
             |    end if;
             |
             |END;
             |$$$$ LANGUAGE plpgsql;
             |
             |
             |CREATE OR REPLACE TRIGGER $trigger
             |    AFTER INSERT OR UPDATE OR DELETE
             |    ON $table
             |    FOR EACH ROW
             |EXECUTE FUNCTION $auditFunc(); """.stripMargin

        Audit(
          Set(auditTable),
          Seq(audit),
        )
      }
      .getOrElse(Audit.empty)

}
