package biz.lobachev.annette.data_dictionary.builder_test.finance

import biz.lobachev.annette.data_dictionary.builder.dsl.DSL._
import biz.lobachev.annette.data_dictionary.builder.helper.JavaPackage.{javaModelPackage, javaRepoPackage}
import biz.lobachev.annette.data_dictionary.builder.model._

trait CA {

  val caComponent = component("CA", "Common tables and data structures")
    .withSchema("ca")
    .withLabels(
      javaModelPackage("finance.data.ca.model"),
      javaRepoPackage("finance.data.ca"),
    )
    .withComponents(
      // region ***************************** Countries & Languages *****************************
      component("CL", "Countries & Languages")
        .withLabels(
          javaModelPackage("finance.data.ca.cl.model"),
          javaRepoPackage("finance.data.ca.cl"),
        )
        .withDataElements(
          dataElement("CountryId", "countryId", StringVarchar(3), "Country Id"),
          dataElement("LanguageId", "languageId", StringVarchar(3), "Language Id"),
        )
        .withEntities(
          tableEntity("Country", "Country", "Country")
            .withPK(
              "id" :# "CountryId",
            )
            .withFields(
              // format: off
              "name"  :# "Name",
              "key"         :#? StringVarchar(3) :@ "Country Key",
              "languageId"  :# "LanguageId",
              include ("Modification")
              // format: on
            )
            .withRelations(
              manyToOneRelation("languageId", "Reference to language", "Language", "languageId" -> "id"),
            ),
          tableEntity("Language", "Language", "Language")
            .withPK(
              "id" :# DataElementType("LanguageId"),
            )
            .withFields(
              "name" :# "Name",
              include("Modification"),
            ),
        ),
      // endregion
      // region ***************************** Currency *****************************
      component("CUR", "Currency")
        .withLabels(
          javaModelPackage("finance.data.ca.cur.model"),
          javaRepoPackage("finance.data.ca.cur"),
        )
        .withDataElements(
          // ***************************** Currency *****************************
          dataElement("CurrencyId", "currencyId", StringVarchar(5), "Currency Id"),
          dataElement("ExchangeRateTypeId", "exchangeRateTypeId", StringVarchar(4), "Exchange rate type Id"),
          dataElement("ExchangeRate", "exchangeRate", BigDecimalNumeric(25, 5), "Exchange rate"),
        )
        .withEntities(
          tableEntity("Currency", "Currency", "Currency")
            .withPK(
              "id" :# "CurrencyId",
            )
            .withFields(
              // format: off
              "name"  :# "Name",
              "shortName"   :# "ShortName",
              "isoCode"     :# StringVarchar(3)   :@ "Currency ISO code",
              "key"         :# StringVarchar(3)   :@ "Currency key",
              "decimals"    :# IntInt()           :@ "Currency decimals",
              include("Modification")
              // format: on
            ),
          tableEntity("ExchangeRateType", "Exchange rate type", "ExchangeRateType")
            .withPK(
              "id" :# DataElementType("ExchangeRateTypeId"),
            )
            .withFields(
              "name" :# "Name",
              include("Modification"),
            ),
          tableEntity("ExchangeRate", "ExchangeRate", "ExchangeRate")
            .withPK(
              // format: off
              "exchangeRateTypeId" :# "ExchangeRateTypeId",
              "fromCurrencyId"           :# "CurrencyId" :@ "From Currency Id" ,
              "toCurrencyId"             :# "CurrencyId" :@ "To Currency Id" ,
              "effectiveFrom"            :# LocalDateDate() :@ "Effective from"
              // format: on
            )
            .withFields(
              // format: off
              "exchangeRate" :# "ExchangeRate",
              "ratioFrom"          :# IntInt()           :@ "Currency ratio from",
              "ratioTo"            :# IntInt()           :@ "Currency ratio to",
              "directRate"         :# BooleanBoolean()   :@ "Direct ratio indicator",
              include("Modification")
              // format: on
            )
            .withRelations(
              manyToOneRelation(
                "exchangeRateTypeId",
                "Reference to exchange rate type",
                "ExchangeRateType",
                "exchangeRateTypeId" -> "id",
              ),
              manyToOneRelation(
                "fromCurrencyId",
                "Reference (from) to currency ",
                "Currency",
                "fromCurrencyId"     -> "id",
              ),
              manyToOneRelation(
                "toCurrencyId",
                "Reference (to) to currency ",
                "Currency",
                "toCurrencyId"       -> "id",
              ),
            ),
        ),
      // endregion
      // region ***************************** UnitOfMeasurement *****************************
      component("UOM", "Unit of measurement")
        .withLabels(
          javaModelPackage("finance.data.ca.uom.model"),
          javaRepoPackage("finance.data.ca.uom"),
        )
        .withDataElements(
          dataElement("UoMId", "uomId", StringVarchar(6), "Unit of measurement Id"),
        )
        .withEntities(
          tableEntity("UnitOfMeasurement", "Unit of measurement", "UnitOfMeasurement")
            .withTableName("units_of_measurement")
            .withPK(
              "id" :# "UoMId",
            )
            .withFields(
              // format: off
              "name"  :#  "Name",
              "shortName"   :#  StringVarchar(30) :@ "Short name",
              "isoCode"     :#? StringVarchar(3)  :@ "ISOCode",
              include("Modification")
              // format: on
            ),
          tableEntity("UoMText", "Unit of measurement text", "UnitOfMeasurementText")
            .withTableName("uom_texts")
            .withPK(
              "languageId" :# "LanguageId",
              "uomId" :# "UoMId",
            )
            .withFields(
              // format: off
              "name"     :# "Name",
              "shortName"      :# "ShortName",
              "commercialCode" :# StringVarchar(6)  :@ "UoM commercial code",
              "technicalCode"  :# StringVarchar(6)  :@ "UoM technical code",
              include("Modification")
              // format: on
            )
            .withRelations(
              manyToOneRelation(
                "languageId",
                "Reference to language",
                "Language",
                "languageId" -> "id",
              ),
              manyToOneRelation(
                "uomId",
                "Reference to unit of measurement",
                "UnitOfMeasurement",
                "uomId"      -> "id",
              ),
            ),
        ),
      // endregion
    )
}
