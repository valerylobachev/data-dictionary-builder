package biz.lobachev.annette.data_dictionary.test.finance

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Go.{goStructPackage, goTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Java.{javaStructPackage, javaTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Postgres.postgresSchema

trait LO {

  val logisticComponent = component("LO", "Logistic tables")
    .withLabels(
      postgresSchema("logistic"),
      javaStructPackage("finance.data.lo.model"),
      javaTablePackage("finance.data.lo"),
      goTablePackage("github.com/valerylobachev/finance/logic/repository/entity/lo"),
      goStructPackage("github.com/valerylobachev/finance/logic/data_model/lo"),
    )
    .withDataElements(
      dataElement("PlantId", "plantId", varchar(4), "Plant id"),
      dataElement("MaterialId", "materialId", varchar(40), "Material id"),
      dataElement("LocationId", "locationId", varchar(10), "Location id"),
    )
    .withTables(
      table("Material", "Material", "Material")
        .withPK(
          "id" :# "MaterialId",
        )
        .withFields(
          // format: off
          "name"  :# "Name",
          "shortName"   :# "ShortName",
          "basicUomId"  :# "UoMId",
          include("Modification")
          // format: on
        )
        .withRelations(
          manyToOne("basicUomId", "Reference to Unit Of Measurement", "UnitOfMeasurement", "basicUomId" -> "id")
          .withAssociation("basicUom"),
        ),
      table("Plant", "Plant", "Plant")
        .withPK(
          "id" :# "PlantId",
        )
        .withFields(
          // format: off
          "name"  :# "Name",
          "shortName"   :# "ShortName",
          include("Modification")
          // format: on
        ),
      table("Location", "Location", "Location")
        .withPK(
          // format: off
          "plantId" :# "PlantId",
          "locationId"    :# "LocationId"
          // format: on
        )
        .withFields(
          // format: off
          "name"  :# "Name",
          "shortName"   :# "ShortName",
          include("Modification")
          // format: on
        )
        .withRelations(
          manyToOne("plantId", "Reference to Plant", "Plant", "plantId" -> "id")
          .withAssociation("plant" -> "locations"),
        ),
    )

}
