package biz.lobachev.annette.data_dictionary.test.validation

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Audit.audit
import biz.lobachev.annette.data_dictionary.labels.ClickHouse.clickHouseExclude
import biz.lobachev.annette.data_dictionary.labels.Go.{goDataType, goTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Java.kotlinDataType
import biz.lobachev.annette.data_dictionary.labels.Postgres.{postgresDataType, postgresSchema}
import biz.lobachev.annette.data_dictionary.labels.TablePrefixSuffix.tableNamePrefix
import biz.lobachev.annette.data_dictionary.model.{AssociatedRelation, OwnRelation}

object PostValidateErrors {

  val data = domain("Simple", "Simple Example", "This example provides simple person group data model")
    .withLabels(
      goTablePackage("github.com/valerylobachev/simple/logic/repository/entity"),
      audit("audit"),
    )
    .withDataElements(
      // format: off
      dataElement("PersonId", "personId", integer, "Person Id"),
      dataElement("GroupId", "groupId", smallint, "Group Id"),
      dataElement("ExternalIds", "externalIds", jsonb, "External Ids")
        .withLabels(
          kotlinDataType("Map<String, Int>"),
          goDataType("map[string]int")
        ),
      // format: on
    )
    .withComponents(
      component("Shared", "Shared data structures")
        .withIncludes(
          includeDef("Modification", "Modification data structure")
            .withFields(
              // format: off
              "updatedBy" :# varchar(20) :@ "User updated record",
              "updatedAt" :# instant :@ "Timestamp of record update"
              // format: on
            ),
        ),
      component("PersonGroup", "Person Group Model")
        .withLabels(
          tableNamePrefix("pg"),
          postgresSchema("gr"),
        )
        .withTables(
          table("DuplicateTableName1", "Person").withTableName("qqq"),
          table("DuplicateTableName2", "Person").withTableName("qqq"),
          table("Person", "Person")
            .withLabels(
              clickHouseExclude(),
            )
            .withPK(
              "id" :#++ "PersonId",
            )
            .withFields(
              // format: off
              "firstname" :# varchar(40) :@ "Person first name",
              "firstname" :# varchar(40) :@ "Person first name",
              "lastname"  :# varchar(40) :@ "Person last name",
              ("search" :# string :@ "Search index")
                .withLabels(postgresDataType("tsvector")),
              ("attributes" :# jsonb :@ "Person attributes" :@@ "Map string: string")
                .withLabels(
                  kotlinDataType("Map<String, String>"),
                  goDataType("map[string]string")
                ),
              "externalIds" :# "ExternalIds" :@ "Person external Ids",
              "updatedBy" :# varchar(20) :@ "User updated record",
              include("Modification")
              // format: on
            )
            .withIndexes(
              index("lastnameFirstname", "Search index by lastname and firstname", "lastname", "firstname"),
              index("invalidIndex", "Search index by lastname and firstname", "lastname1", "firstname")
                .include("qqq"),
            ),
          table("Group", "Group")
            .withPK(
              "id" :#++ "GroupId",
            )
            .withFields(
              // format: off
              "name" :# varchar(100) :@ "Group name",
              include("Modification")
              // format: on
            ),
          table("GroupMember", "Group member")
            .withPK(
              "id" :#++ integer :@ " Group member id",
            )
            .withFields(
              // format: off
              "groupId"   :# integer,
              "personId"  :# "PersonId",
              include("Modification")
              // format: on
            )
            .withIndexes(
              uniqueIndex("personGroupIds", "Person id in each group must be unique", "groupId", "personId"),
            )
            .withRelations(
              manyToOne("invalidRelation", "Relation to groups", "Group", "groupId1" -> "id1"),
              manyToOne("groupId", "Relation to groups", "Group", "groupId" -> "id")
                .withAssociation("group"                                             -> "members"),
              logicalManyToOne("personId", "Relation to persons", "Person", "personId" -> "id")
                .withAssociation("person"),
            ),
        )
        .withStructs(
          struct("Person", "Person")
            .likeTable("Person")
            .withFields(
              "lastname" :## varchar(40) :@ "Person last name",
              "groupsMembers" :## linkedStructArray("GroupMember", "personId"),
              includeStructFields("Modification"),
            ),
          struct("GroupMember", "GroupMember")
            .likeTable("GroupMember")
            .withFields(
              "person" :## linkedStruct("Person", OwnRelation("personId")),
            ),
          struct("InvalidPerson", "Person")
            .likeTable("Person")
            .withFields(
              "lastname" :## varchar(40) :@ "Person last name",
              "groupsMembers" :## linkedStructArray("InvalidGroupMember", "qqq"),
              includeStructFields("Modification"),
            ),
          struct("InvalidGroupMember", "GroupMember")
            .likeTable("GroupMember")
            .withFields(
              "person" :## linkedStruct("InvalidPerson", OwnRelation("qqq")),
              "person" :## linkedStruct("InvalidPerson", AssociatedRelation("qqq")),
            ),
          struct("Person1", "Person1")
            .likeTable("Person", including("qqq", "aaa")),
          struct("Person2", "Person2")
            .likeTable("Person", excluding("qqq", "aaa")),
        ),
    )

}
