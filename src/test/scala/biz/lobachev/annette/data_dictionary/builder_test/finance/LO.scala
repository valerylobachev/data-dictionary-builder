package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model.StringVarchar

trait LO {

  val logisticComponent = component("LO", "Logistic tables")
    .withSchema("logistic")
    .withLabels(
      javaModelPackage("finance.data.lo.model"),
      javaRepoPackage("finance.data.lo"),
    )
    .withDataElements(
      dataElement("PlantId", "plantId", StringVarchar(4), "Plant id"),
      dataElement("MaterialId", "materialId", StringVarchar(40), "Material id"),
      dataElement("LocationId", "locationId", StringVarchar(10), "Location id"),
    )
    .withEntities(
      tableEntity("Material", "Material", "Material")
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
          manyToOneRelation("basicUomId", "Reference to Unit Of Measurement", "UnitOfMeasurement", "basicUomId" -> "id"),
        ),
      tableEntity("Plant", "Plant", "Plant")
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
      tableEntity("Location", "Location", "Location")
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
          manyToOneRelation("plantId", "Reference to Plant", "Plant", "plantId" -> "id"),
        ),
    )

}
