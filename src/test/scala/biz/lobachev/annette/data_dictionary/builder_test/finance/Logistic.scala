package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{javaModelPackage, javaRepoPackage}

trait Logistic {

  val logisticGroup = group("Logistic", "Logistic tables")
    .withSchema("logistic")
    .withLabels(
      javaModelPackage("finance.data.logistic.model"),
      javaRepoPackage("finance.data.logistic"),
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
