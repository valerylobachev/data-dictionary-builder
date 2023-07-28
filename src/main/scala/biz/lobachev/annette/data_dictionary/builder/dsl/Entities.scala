package biz.lobachev.annette.data_dictionary.builder.dsl

import biz.lobachev.annette.data_dictionary.builder.model.{EmbeddedEntity, Entity, StructEntity, TableEntity}
import biz.lobachev.annette.data_dictionary.builder.utils.StringSyntax._

trait Entities {

  def structEntity(id: String, name: String, entityName: String): Entity =
    Entity(
      id = id.pascalCase,
      name = name,
      entityType = StructEntity,
      entityName = entityName.pascalCase,
      tableName = entityName.pluralize.snakeCase
    )

  def tableEntity(id: String, name: String, entityName: String): Entity =
    Entity(
      id = id.pascalCase,
      name = name,
      entityType = TableEntity,
      entityName = entityName.pascalCase,
      tableName = entityName.pluralize.snakeCase
    )

  def embeddedEntity(id: String, name: String, entityName: String): Entity =
    Entity(
      id = id.pascalCase,
      name = name,
      entityType = EmbeddedEntity,
      entityName = entityName.pascalCase,
      tableName = entityName.pluralize.snakeCase
    )

  def structEntity(id: String, name: String): Entity =
    Entity(
      id = id.pascalCase,
      name = name,
      entityType = StructEntity,
      entityName = id.pascalCase,
      tableName = id.pluralize.snakeCase
    )

  def tableEntity(id: String, name: String): Entity =
    Entity(
      id = id.pascalCase,
      name = name,
      entityType = TableEntity,
      entityName = id.pascalCase,
      tableName = id.pluralize.snakeCase
    )

  def embeddedEntity(id: String, name: String): Entity =
    Entity(
      id = id.pascalCase,
      name = name,
      entityType = EmbeddedEntity,
      entityName = id.pascalCase,
      tableName = id.pluralize.snakeCase
    )

}
