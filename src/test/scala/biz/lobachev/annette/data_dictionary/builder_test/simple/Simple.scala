package biz.lobachev.annette.data_dictionary.builder_test.simple

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.labels.Audit.audit
import biz.lobachev.annette.data_dictionary.builder.labels.ClickHouse._
import biz.lobachev.annette.data_dictionary.builder.labels.GolangPackage.goModelPackage
import biz.lobachev.annette.data_dictionary.builder.labels.OverrideDatatype.{goDataType, kotlinDataType, postgreSqlDataType}
import biz.lobachev.annette.data_dictionary.builder.model._

object Simple {

  val data = domain("Simple", "Simple Example", "This example provides simple person group data model")
    .withLabels(
      goModelPackage("github.com/valerylobachev/simple/logic/repository/entity"),
      audit("audit"),
    )
    .withDataElements(
      // format: off
      dataElement("PersonId", "personId", IntInt(), "Person Id"),
      dataElement("GroupId", "groupId", ShortSmallint(), "Group Id"),
      dataElement("ExternalIds", "externalIds", StringJsonB(), "External Ids")
        .withLabels(
          kotlinDataType("Map<String, Int>"),
          goDataType("map[string]int")
        ),
      // format: on
    )
    .withComponents(
      component("Shared", "Shared data structures")
        .withEntities(
          embeddedEntity("Modification", "Modification data structure")
            .withFields(
              // format: off
              "updatedBy" :# StringVarchar(20) :@ "User updated record",
              "updatedAt" :# InstantTimestamp() :@ "Timestamp of record update"
              // format: on
            ),
        ),
      component("PersonGroup", "Person Group Model")
        .withEntities (
          tableEntity("Person", "Person")
            .withLabels(
              clickHouseExclude(),
            )
            .withPK(
              "id" :#++ "PersonId",
            )
            .withFields(
              // format: off
              "firstname" :# StringVarchar(40) :@ "Person first name",
              "lastname"  :# StringVarchar(40) :@ "Person last name",
              ("search" :# StringText() :@ "Search index")
                .withLabels(postgreSqlDataType("tsvector")),
              ("attributes" :# StringJsonB() :@ "Person attributes" :@@ "Map string: string")
                .withLabels(
                  kotlinDataType("Map<String, String>"),
                  goDataType("map[string]string")
                ),
              "externalIds" :# "ExternalIds" :@ "Person external Ids",
              include("Modification")
              // format: on
            )
            .withIndexes(
              index("lastnameFirstname", "Search index by lastname and firstname", "lastname", "firstname"),
            ),
          tableEntity("Group", "Group")
            .withPK(
              "id" :#++ "GroupId",
            )
            .withFields(
              // format: off
             "name" :# StringVarchar(100) :@ "Group name",
              include("Modification")
              // format: on
            ),
          tableEntity("GroupMember", "Group member")
            .withPK(
              "id" :#++ IntInt() :@ " Group member id",
            )
            .withFields(
              // format: off
              "groupId"   :# "GroupId",
              "personId"  :# "PersonId",
              include("Modification")
              // format: on
            )
            .withIndexes(
              uniqueIndex("personGroupIds", "Person id in each group must be unique", "groupId", "personId"),
            )
            .withRelations(
              manyToOne("groupId", "Relation to groups", "Group", "groupId" -> "id")
                .withAssociation("group" -> "members"),
              logicalManyToOne("personId", "Relation to persons", "Person", "personId" -> "id")
                .withAssociation("person"),
            )
        ),
    )

}
