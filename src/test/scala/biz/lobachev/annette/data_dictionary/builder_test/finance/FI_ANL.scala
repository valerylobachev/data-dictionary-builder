package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.labels.Audit.disableAudit
import biz.lobachev.annette.data_dictionary.builder.labels.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model.StringVarchar

trait FI_ANL {

  val analyticsComponent = component("FI-ANL", "Analytic tables")
    .withSchema("analytics")
    .withLabels(
      javaModelPackage("finance.data.fi.anl.model"),
      javaRepoPackage("finance.data.fi.anl"),
      disableAudit(),
    )
    .withDataElements(
      dataElement("BusinessAreaId", "businessAreaId", StringVarchar(4), "Business area id"),
      dataElement("FunctionalAreaId", "functionalAreaId", StringVarchar(20), "Functional area id"),
      dataElement("SegmentId", "segmentId", StringVarchar(10), "Segment id"),
      dataElement("AttributeId", "attributeId", StringVarchar(10), "Attribute id"),
      dataElement("AttributeValueId", "attributeValueId", StringVarchar(20), "Attribute value id"),
      dataElement("ValuationAreaId", "valuationAreaId", StringVarchar(20), "Valuation area id"),
    )
    .withEntities(
      // ***************************** Analytics *****************************
      tableEntity("BusinessArea", "Business area", "BusinessArea")
        .withPK(
          "id" :# "BusinessAreaId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      tableEntity("FunctionalArea", "Functional area", "FunctionalArea")
        .withPK(
          "id" :# "FunctionalAreaId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      tableEntity("Segment", "Segment", "Segment")
        .withPK(
          "id" :# "SegmentId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
        ),
      tableEntity("Attribute", "Attribute", "Attribute")
        .withPK(
          "id" :# "AttributeId",
        )
        .withFields(
          "name" :# "Name",
          include("Modification"),
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
          include("Modification"),
        )
        .withRelations(
          manyToOneRelation("attributeId", "Relation to Attribute", "Attribute", "attributeId" -> "id"),
        ),
      tableEntity("ValuationArea", "Valuation area", "ValuationArea")
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
          manyToOneRelation("companyCodeId", "Reference to CompanyCode", "CompanyCode", "companyCodeId" -> "id")
        )
    )

}
