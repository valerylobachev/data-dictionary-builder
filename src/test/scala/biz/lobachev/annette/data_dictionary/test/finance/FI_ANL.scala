package biz.lobachev.annette.data_dictionary.test.finance

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Audit.disableAudit
import biz.lobachev.annette.data_dictionary.labels.Go.{goStructPackage, goStructSourceFile, goTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Java.{javaStructPackage, javaTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Postgres.postgresSchema

trait FI_ANL {

  val analyticsComponent = component("FI-ANL", "Analytic tables")
    .withLabels(
      postgresSchema("analytics"),
      javaStructPackage("finance.data.fi.anl.model"),
      javaTablePackage("finance.data.fi.anl"),
      goTablePackage("github.com/valerylobachev/finance/logic/repository/entity/fi/anl"),
      goStructPackage("github.com/valerylobachev/finance/logic/data_model/fi/anl"),
      disableAudit(),
    )
    .withDataElements(
      dataElement("BusinessAreaId", "businessAreaId", varchar(4), "Business area id"),
      dataElement("FunctionalAreaId", "functionalAreaId", varchar(20), "Functional area id"),
      dataElement("SegmentId", "segmentId", varchar(10), "Segment id"),
      dataElement("AttributeId", "attributeId", varchar(10), "Attribute id"),
      dataElement("AttributeValueId", "attributeValueId", varchar(20), "Attribute value id"),
      dataElement("ValuationAreaId", "valuationAreaId", varchar(20), "Valuation area id"),
    )
    .withTables(
      // ***************************** Analytics *****************************
      table("BusinessArea", "Business area", "BusinessArea")
        .withPK(
          "id" :# "BusinessAreaId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      table("FunctionalArea", "Functional area", "FunctionalArea")
        .withPK(
          "id" :# "FunctionalAreaId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      table("Segment", "Segment", "Segment")
        .withPK(
          "id" :# "SegmentId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      table("Attribute", "Attribute", "Attribute")
        .withPK(
          "id" :# "AttributeId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      table("AttributeValue", "Attribute value", "AttributeValue")
        .withPK(
          // format: off
          "attributeId" :# "AttributeId",
          "attributeValueId"  :# "AttributeValueId"
          // format: on
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        )
        .withRelations(
          manyToOne("attributeId", "Relation to Attribute", "Attribute", "attributeId" -> "id")
            .withAssociation("attribute" -> "attributeValues"),
        ),
      table("ValuationArea", "Valuation area", "ValuationArea")
        .withPK(
          "id" :# "ValuationAreaId",
        )
        .withFields(
          // format: off
          "name"    :# "Name",
          "shortName"     :# "ShortName",
          "companyCodeId" :# "CompanyCodeId",
          include("Modification")
          // format: off
        )
        .withRelations(
          manyToOne("companyCodeId", "Reference to CompanyCode", "CompanyCode", "companyCodeId" -> "id")
            .withAssociation("companyCode" -> "valuationAreas")
        )
    )
    .withLabeledStructs(
      goStructSourceFile("business_area"),
    )(
      struct("BusinessAreaId", "BusinessArea Id")
        .likeTablePK("BusinessArea"),
      struct("BusinessAreaDto", "BusinessArea DTO")
        .likeTable("BusinessArea"),
      struct("BusinessAreaNameDto", "BusinessArea name DTO")
        .likeTable("BusinessArea", including("id", "name")),
    )
    .withLabeledStructs(
        goStructSourceFile("functional_area"),
      )(
      struct("FunctionalAreaId", "FunctionalArea Id")
        .likeTablePK("FunctionalArea"),
      struct("FunctionalAreaDto", "FunctionalArea DTO")
        .likeTable("FunctionalArea"),
      struct("FunctionalAreaNameDto", "FunctionalArea name DTO")
        .likeTable("FunctionalArea", including("id", "name")),
    )
    .withLabeledStructs(
      goStructSourceFile("segment"),
    )(
      struct("SegmentId", "Segment Id")
        .likeTablePK("Segment"),
      struct("SegmentDto", "Segment DTO")
        .likeTable("Segment"),
      struct("SegmentNameDto", "Segment name DTO")
        .likeTable("Segment", including("id", "name")),
    )
    .withLabeledStructs(
      goStructSourceFile("attribute"),
    )(
      struct("AttributeId", "Attribute Id")
        .likeTablePK("Attribute"),
      struct("AttributeDto", "Attribute DTO")
        .likeTable("Attribute")
        .withFields(
          "attributeValues" :## linkedStructArray("AttributeValueDto")
        ),
      struct("AttributeNameDto", "Attribute name DTO")
        .likeTable("Attribute", including("id", "name")),
      
      struct("AttributeValueId", "AttributeValue Id")
        .likeTablePK("AttributeValue"),
      struct("AttributeValueDto", "AttributeValue DTO")
        .likeTable("AttributeValue"),
      struct("AttributeValueNameDto", "AttributeValue name DTO")
        .likeTable("AttributeValue", including("attributeId", "attributeValueId", "name")),
    )
    .withLabeledStructs(
      goStructSourceFile("valuation_area"),
    )(
      struct("ValuationAreaId", "ValuationArea Id")
        .likeTablePK("ValuationArea"),
      struct("ValuationAreaDto", "ValuationArea DTO")
        .likeTable("ValuationArea"),
      struct("ValuationAreaNameDto", "ValuationArea name DTO")
        .likeTable("ValuationArea", including("id", "name")),
    )

}
