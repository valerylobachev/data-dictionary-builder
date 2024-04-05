package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{EmbeddedEntity, RawEntity, StructEntity, TableEntity}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

trait Entities {

  def structEntity(id: String, name: String, entityName: String): RawEntity =
    RawEntity(
      id = id.trim.pascalCase,
      name = name.trim,
      entityType = StructEntity,
      entityName = entityName.trim.pascalCase,
      tableName = entityName.trim.pluralize.snakeCase
    )

  def tableEntity(id: String, name: String, entityName: String): RawEntity =
    RawEntity(
      id = id.trim.pascalCase,
      name = name.trim,
      entityType = TableEntity,
      entityName = entityName.trim.pascalCase,
      tableName = entityName.trim.pluralize.snakeCase
    )

  def embeddedEntity(id: String, name: String, entityName: String): RawEntity =
    RawEntity(
      id = id.trim.pascalCase,
      name = name.trim,
      entityType = EmbeddedEntity,
      entityName = entityName.trim.pascalCase,
      tableName = entityName.trim.pluralize.snakeCase
    )

  def structEntity(id: String, name: String): RawEntity =
    RawEntity(
      id = id.trim.pascalCase,
      name = name.trim,
      entityType = StructEntity,
      entityName = id.trim.pascalCase,
      tableName = id.trim.pluralize.snakeCase
    )

  def tableEntity(id: String, name: String): RawEntity =
    RawEntity(
      id = id.trim.pascalCase,
      name = name.trim,
      entityType = TableEntity,
      entityName = id.trim.pascalCase,
      tableName = id.trim.pluralize.snakeCase
    )

  def embeddedEntity(id: String, name: String): RawEntity =
    RawEntity(
      id = id.trim.pascalCase,
      name = name.trim,
      entityType = EmbeddedEntity,
      entityName = id.trim.pascalCase,
      tableName = id.trim.pluralize.snakeCase
    )

}
