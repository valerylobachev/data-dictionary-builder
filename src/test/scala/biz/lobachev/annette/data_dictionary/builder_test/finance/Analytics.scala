package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{javaModelPackage, javaRepoPackage}

trait Analytics {

  val analyticsGroup = component("Analytics", "Analytic tables")
    .withSchema("analytics")
    .withLabels(
      javaModelPackage("finance.data.analytics.model"),
      javaRepoPackage("finance.data.analytics")
    )
    .withEntities(
      // ***************************** Creditor *****************************
      tableEntity("Creditor", "Creditor", "Creditor")
        .withPK(
          "id" :# "CreditorId"
        )
        .withFields(
          "name" :# "Name",
          include("Modification")
        ),
      // ***************************** Debtor *****************************
      tableEntity("Debtor", "Debtor", "Debtor")
        .withPK(
          "id" :# "DebtorId"
        )
        .withFields(
          "name" :# "Name",
          include("Modification")
        ),
      // ***************************** BusinessPartner *****************************
      tableEntity("BusinessPartner", "Business partner", "BusinessPartner")
        .withPK(
          "id" :# "BusinessPartnerId"
        )
        .withFields(
          // format: off
          "name"  :# "Name",
          "creditorId"  :#? "CreditorId",
          "debtorId"    :#? "DebtorId",
          include("Modification")
          // format: on
        )
        .withRelations(
          oneToOneRelation("creditorId", "Reference to Creditor", "Creditor", "creditorId" -> "id"),
          oneToOneRelation("debtorId", "Reference to Debtor", "Debtor", "debtorId"         -> "id")
        )
        .withIndexes(
          uniqueIndex("creditorId", "Unique creditorId", "creditorId"),
          uniqueIndex("debtorId", "Unique debtorId", "debtorId")
        ),
      // ***************************** Analytics *****************************
      tableEntity("BusinessArea", "Business area", "BusinessArea")
        .withPK(
          "id" :# "BusinessAreaId"
        )
        .withFields(
          "name" :# "Name",
          include("Modification")
        ),
      tableEntity("FunctionalArea", "Functional area", "FunctionalArea")
        .withPK(
          "id" :# "FunctionalAreaId"
        )
        .withFields(
          "name" :# "Name",
          include("Modification")
        ),
      tableEntity("Segment", "Segment", "Segment")
        .withPK(
          "id" :# "SegmentId"
        )
        .withFields(
          "name" :# "Name",
          include("Modification")
        ),
      tableEntity("Attribute", "Attribute", "Attribute")
        .withPK(
          "id" :# "AttributeId"
        )
        .withFields(
          "name" :# "Name",
          include("Modification")
        ),
      tableEntity("AttributeValue", "Attribute value", "AttributeValue")
        .withPK(
          // format: off
          "attributeId" :# "AttributeId",
          "attributeValueId"  :# "AttributeValueId"
          // format: on
        )
        .withFields(
          "name" :# "Name",
          include("Modification")
        )
        .withRelations(
          manyToOneRelation("attributeId", "Relation to Attribute", "Attribute", "attributeId" -> "id")
        )
    )

}
