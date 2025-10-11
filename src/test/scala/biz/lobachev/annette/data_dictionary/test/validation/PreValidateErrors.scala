package biz.lobachev.annette.data_dictionary.test.validation

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Audit.audit
import biz.lobachev.annette.data_dictionary.labels.ClickHouse.clickHouseExclude
import biz.lobachev.annette.data_dictionary.labels.Go.{goDataType, goTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Java.kotlinDataType
import biz.lobachev.annette.data_dictionary.labels.Postgres.postgresDataType
import biz.lobachev.annette.data_dictionary.model.OwnRelation

object PreValidateErrors {

  val data = domain("Simple", "Simple Example", "This example provides simple person group data model")
    .withLabels(
      goTablePackage("github.com/valerylobachev/simple/logic/repository/entity"),
      goTablePackage("github.com/valerylobachev/simple/logic/repository/entity"),
      audit("audit"),
    )
    .withEnums(
      intEnum("LedgerType1", "Ledger Type1")
        .withValues(
          "Lw" -> "LeadingLedger"   -> "Leading ledger",
          "Xe" -> "LeadingLedger"   -> "Extension ledger",
          "1"  -> "LeadingLedger1"  -> "Extension ledger",
          "2"  -> "LeadingLedger2"  -> "Extension ledger",
        ),
      stringEnum("LedgerType2", "Ledger Type1", 1)
        .withValues(
          "Lw" -> "LeadingLedger"   -> "Leading ledger",
          "Xe" -> "ExtensionLedger" -> "Extension ledger",
        ),
      stringEnum("LedgerType3", "Ledger Type1", -1)
        .withValues(
          "Lw" -> "LeadingLedger"   -> "Leading ledger",
          "Xe" -> "ExtensionLedger" -> "Extension ledger",
        ),
      nativeEnum("LedgerType", "Ledger Type", 2)
        .withValues(
          "Lw" -> "LeadingLedger"   -> "Leading ledger",
          "Lw" -> "LeadingLedger"   -> "Leading ledger",
          "Xe" -> "ExtensionLedger" -> "Extension ledger",
        )
        .withLabels(
          goTablePackage("github.com/valerylobachev/simple/logic/repository/entity"),
          goTablePackage("github.com/valerylobachev/simple/logic/repository/entity"),
          audit("audit"),
        ),
    )
    .withDataElements(
      // format: off
      dataElement("InvalidDataElement1", "group" , struct("PersonGroup"), ""),
      dataElement("InvalidDataElement2", "groups" , structArray("PersonGroup"), ""),
      dataElement("PersonId", "personId", integer, "Person Id"),
      dataElement("PersonId", "personId", integer, "Person Id"),
      dataElement("GroupId", "groupId", smallint, "Group Id"),
      dataElement("GroupId1", "groupId", varchar(0), "Group Id"),
      dataElement("GroupId2", "groupId", char(0), "Group Id"),
      dataElement("GroupId3", "groupId", decimal(0,-1), "Group Id"),
      dataElement("GroupId4", "groupId", decimal(3,4), "Group Id"),
      dataElement("GroupId5", "groupId", enumeration("qqq"), "Group Id"),
      dataElement("GroupId6", "groupId", dataElement("qqq"), "Group Id"),
      dataElement("GroupId7", "groupId", array(varchar(0)), "Group Id"),
      dataElement("GroupId8", "groupId", array(char(0)), "Group Id"),
      dataElement("GroupId9", "groupId", array(decimal(0,-1)), "Group Id"),
      dataElement("GroupId10", "groupId", array(decimal(3,4)), "Group Id"),
      dataElement("GroupId11", "groupId", array(enumeration("qqq")), "Group Id"),
      dataElement("GroupId12", "groupId", array(dataElement("qqq")), "Group Id"),
      dataElement("GroupId13", "groupId", set(varchar(0)), "Group Id"),
      dataElement("GroupId14", "groupId", set(char(0)), "Group Id"),
      dataElement("GroupId15", "groupId", set(decimal(0,-1)), "Group Id"),
      dataElement("GroupId16", "groupId", set(decimal(3,4)), "Group Id"),
      dataElement("GroupId17", "groupId", set(enumeration("qqq")), "Group Id"),
      dataElement("GroupId18", "groupId", set(dataElement("qqq")), "Group Id"),
      dataElement("GroupId19", "groupId", stringMap(varchar(0)), "Group Id"),
      dataElement("GroupId20", "groupId", stringMap(char(0)), "Group Id"),
      dataElement("GroupId21", "groupId", stringMap(decimal(0,-1)), "Group Id"),
      dataElement("GroupId22", "groupId", stringMap(decimal(3,4)), "Group Id"),
      dataElement("GroupId23", "groupId", stringMap(enumeration("qqq")), "Group Id"),
      dataElement("GroupId24", "groupId", stringMap(dataElement("qqq")), "Group Id"),
      dataElement("ExternalIds", "externalIds", jsonb, "External Ids")
        .withLabels(
          kotlinDataType("Map<String, Int>"),
          kotlinDataType("Map<String, Int>"),
          goDataType("map[string]int")
        ),
      // format: on
    )
    .withTables(
      table("Person", "Person")
        .withLabels(
          goTablePackage("github.com/valerylobachev/simple/logic/repository/entity"),
          goTablePackage("github.com/valerylobachev/simple/logic/repository/entity"),
          audit("audit"),
        ),
      table("ParentA", "ParentA")
        .withPK(
          "id" :# uuid,
        ),
      table("ChildA1", "ChildA1")
        .withPK(
          "id" :# uuid,
        )
        .withFields(
          "parent1" :# uuid,
          "parent2" :# uuid,
        )
        .withRelations(
          manyToOne("parent1", "", "ParentA", "parent1" -> "id"),
          manyToOne("parent2", "", "ParentA", "parent2" -> "id"),
        ),
      table("ChildA2", "ChildA2")
        .withPK(
          "id" :# uuid,
        )
        .withFields(
          "parent1" :# uuid,
          "parent2" :# uuid,
        ),
      table("RelA", "RelA")
        .withPK(
          "id" :# uuid,
        ),
      table("RelB", "RelB")
        .withPK(
          "id" :# uuid,
        )
        .withFields(
          "rel1" :# uuid,
          "rel2" :# uuid,
        )
        .withRelations(
          oneToOne("rel1", "", "RelA", "rel1" -> "id"),
          oneToOne("rel2", "", "RelA", "rel2" -> "id"),
        ),
    )
    .withStructs(
      struct("ParentA1", "ParentA1")
        .likeTable("ParentA")
        .withFields(
          "children" :## linkedStructArray("ChildA1"),
        ),
      struct("ChildA1", "ChildA1")
        .likeTable("ChildA1"),
      struct("ParentA2", "ParentA2")
        .likeTable("ParentA")
        .withFields(
          "children" :## linkedStructArray("ChildA2"),
        ),
      struct("ChildA2", "ChildA2")
        .likeTable("ChildA2"),
      struct("Person", "Person")
        .likeTablePK("Person")
        .withLabels(
          goTablePackage("github.com/valerylobachev/simple/logic/repository/entity"),
          goTablePackage("github.com/valerylobachev/simple/logic/repository/entity"),
          audit("audit"),
        ),
      struct("RelA1", "RelA1")
        .likeTable("RelA")
        .withFields(
          "r" :## linkedStruct("RelB1"),
        ),
      struct("RelB1", "RelB1")
        .likeTable("RelB"),
      struct("RelA2", "RelA2")
        .likeTable("RelA"),
      struct("RelB2", "RelB2")
        .likeTable("RelB")
        .withFields(
          "r" :## linkedStruct("RelA2"),
        ),
    )
    .withComponents(
      component("Shared", "Shared data structures")
        .withDataElements(
          // format: off
          dataElement("PersonId", "personId", integer, "Person Id"),
          dataElement("PersonId", "personId", integer, "Person Id"),
          dataElement("GroupId", "groupId", smallint, "Group Id"),
        )
        .withStructs(
          struct("Person", "Person")
            .likeTablePK("Person"),
          struct("NotLikeTablePerson", "Person")
            .withFields(
              "lastname" :## varchar(40) :@ "Person last name",
              "groupsMembers" :## linkedStructArray("NotLikeTableGroupMember", "personId"),
              includeStructFields("Modification"),
            ),
          struct("NotLikeTableGroupMember", "GroupMember")
            .withFields(
              "person" :## linkedStruct("NotLikeTablePerson", OwnRelation("personId")),
            ),
          struct("InvalidPerson", "Person")
            .likeTable("Person")
            .withFields(
              "lastname" :## varchar(40) :@ "Person last name",
              "groupsMembers" :## linkedStructArray("InvalidGroupMember", "qqq"),
              "groupsMembers2" :## linkedStructArray("NotLikeTableGroupMember", "qqq"),
              includeStructFields("Modification"),
            ),
          struct("InvalidModel", "Person", "")
            .likeTablePK("qqq")
          .withFields(
            includeStructFields("qqq"),
            "" :## jsonb :@ "Person attributes",
            "zzz" :## varchar(0) :@ "User updated record",
            ("attributes" :## jsonb :@ "Person attributes" :@@ "Map string: string")
              .withLabels(
                kotlinDataType("Map<String, String>"),
                kotlinDataType("Map<String, String>"),
                goDataType("map[string]string")
              ),

          ),
        )
        .withIncludes(
          includeDef("NestedInclude1", "NestedInclude")
            .withFields(
              include("NestedInclude2"),
              "updatedBy1" :# varchar(20) :@ "User updated record",
              "updatedAt1" :# instant :@ "Timestamp of record update"
            ),
          includeDef("NestedInclude2", "NestedInclude")
            .withFields(
              include("NestedInclude3"),
              "updatedBy2" :# varchar(20) :@ "User updated record",
              "updatedAt2" :# instant :@ "Timestamp of record update"
            ),
          includeDef("NestedInclude3", "NestedInclude")
            .withFields(
              include("NestedInclude1"),
              "updatedBy3" :# varchar(20) :@ "User updated record",
              "updatedAt3" :# instant :@ "Timestamp of record update"
            ),
          includeDef("Modification", "Modification data structure")
            .withFields(
              // format: off
              "updatedBy" :# varchar(20) :@ "User updated record",
              "updatedAt" :# instant :@ "Timestamp of record update"
              // format: on
            ),
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
          goTablePackage("github.com/valerylobachev/simple/logic/repository/entity"),
          goTablePackage("github.com/valerylobachev/simple/logic/repository/entity"),
          audit("audit"),
        ),
      component("PersonGroup", "Person Group Model")
        .withStructs(
          struct("Person", "Person")
            .likeTablePK("Person")
            .withFields(
              "group" :## struct("PersonGroup"),
              "groups" :## structArray("PersonGroup"),
            ),
          struct("PersonGroup", "Person")
            .likeTablePK("PersonGroup"),
        )
        .withIncludes(
          includeDef("Modification", "Modification data structure")
            .withFields(
              // format: off
              include("qqq"),
              "updatedBy" :# varchar(20) :@ "User updated record",
              "updatedAt" :# instant :@ "Timestamp of record update"
              // format: on
            ),
          includeDef("Modification", "Modification data structure")
            .withFields(
              // format: off
              "updatedBy" :# varchar(20) :@ "User updated record",
              "updatedAt" :# instant :@ "Timestamp of record update"
              // format: on
            ),
          includeDef("NotExistingInclude", "NotExistingInclude")
            .withFields(
              include("qqq"),
            ),
          includeDef("DuplicateFields", "DuplicateFields")
            .withFields(
              include("DuplicateFields1"),
              "updatedBy" :# varchar(20) :@ "User updated record",
              "updatedAt" :# instant :@ "Timestamp of record update",
              "updatedAt" :# instant :@ "Timestamp of record update",
            ),
          includeDef("DuplicateFields1", "DuplicateFields")
            .withFields(
              "updatedBy" :# varchar(20) :@ "User updated record",
            ),
          includeDef("InvalidDatatypes", "InvalidDatatypes")
            .withFields(
              // format: off
              "groupId1" :# varchar(0)                      :@ "Group Id",
              "groupId2" :# char(0)                         :@ "Group Id",
              "groupId3" :# decimal(0,-1)                   :@ "Group Id",
              "groupId4" :# decimal(3,4)                    :@ "Group Id",
              "groupId5" :# enumeration("qqq")              :@ "Group Id",
              "groupId6" :# dataElement("qqq")              :@ "Group Id",
              "groupId7" :# array(varchar(0))               :@ "Group Id",
              "groupId8" :# array(char(0))                  :@ "Group Id",
              "groupId9" :# array(decimal(0,-1))            :@ "Group Id",
              "groupId10" :#  array(decimal(3,4))           :@ "Group Id",
              "groupId11" :#  array(enumeration("qqq"))     :@ "Group Id",
              "groupId12" :#  array(dataElement("qqq"))     :@ "Group Id",
              "groupId13" :#  set(varchar(0))               :@ "Group Id",
              "groupId14" :#  set(char(0))                  :@ "Group Id",
              "groupId15" :#  set(decimal(0,-1))            :@ "Group Id",
              "groupId16" :#  set(decimal(3,4))             :@ "Group Id",
              "groupId17" :#  set(enumeration("qqq"))       :@ "Group Id",
              "groupId18" :#  set(dataElement("qqq"))       :@ "Group Id",
              "groupId19" :#  stringMap(varchar(0))         :@ "Group Id",
              "groupId20" :#  stringMap(char(0))            :@ "Group Id",
              "groupId21" :#  stringMap(decimal(0,-1))      :@ "Group Id",
              "groupId22" :#  stringMap(decimal(3,4))       :@ "Group Id",
              "groupId23" :#  stringMap(enumeration("qqq")) :@ "Group Id",
              "groupId24" :#  stringMap(dataElement("qqq")) :@ "Group Id",
              "group"     :#  struct("PersonGroup"),
              "groups"    :#  structArray("PersonGroup"),
              // format: on
            ),
        )
        .withEnums(
          nativeEnum("LedgerType", "Ledger Type", 2)
            .withValues(
              "L" -> "LeadingLedger"   -> "Leading ledger",
              "X" -> "ExtensionLedger" -> "Extension ledger",
            ),
        )
        .withTables(
          table("Person", "Person")
            .withLabels(
              clickHouseExclude(),
            )
            .withPK(
              "id" :#++ "PersonId",
            )
            .withFields(
              // format: off
              "" :# varchar(40) :@ "Person first name",
              "firstname" :# varchar(40) :@ "Person first name",
              "lastname"  :# varchar(40) :@ "Person last name",
              "invalidType"  :# varchar(-1) :@ "Person last name",
              ("search" :# string :@ "Search index")
                .withLabels(postgresDataType("tsvector")),
              ("attributes" :# jsonb :@ "Person attributes" :@@ "Map string: string")
                .withLabels(
                  kotlinDataType("Map<String, String>"),
                  kotlinDataType("Map<String, String>"),
                  goDataType("map[string]string")
                ),
              "externalIds" :# "ExternalIds" :@ "Person external Ids",
              include("Modification"),
              include("qqq")
              // format: on
            )
            .withIndexes(
              index("lastnameFirstname", "Search index by lastname and firstname", "lastname", "firstname")
                .withLabels(
                  goDataType("map[string]string"),
                  goDataType("map[string]string"),
                ),
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
          table("GroupMember", "Group member", "")
            .withPK(
              "id" :#++ integer :@ " Group member id",
            )
            .withFields(
              // format: off
              "groupId"   :# "GroupId",
              "personId"  :# "PersonId",
              "group" :# struct("PersonGroup"),
              "groups" :# structArray("PersonGroup"),
              include("Modification")
              // format: on
            )
            .withIndexes(
              uniqueIndex("personGroupIds", "Person id in each group must be unique", "groupId", "personId"),
              uniqueIndex("personGroupIds", "Person id in each group must be unique", "groupId", "personId"),
            )
            .withRelations(
              manyToOne("groupId", "Relation to groups", "Group", "groupId" -> "id")
                .withAssociation("group"                                               -> "members"),
              logicalManyToOne("personId", "Relation to persons", "Person", "personId" -> "id")
                .withAssociation("person")
                .withLabels(
                  goDataType("map[string]string"),
                  goDataType("map[string]string"),
                ),
              logicalManyToOne("personId", "Relation to persons", "Person", "personId" -> "id"),
              logicalManyToOne("personId", "Relation to persons", "QQQ", "personId"    -> "id"),
            ),
        ),
    )

}
