package biz.lobachev.annette.data_dictionary.test.finance

import biz.lobachev.annette.data_dictionary.dsl.DSL.*
import biz.lobachev.annette.data_dictionary.labels.Go.{goStructPackage, goStructSourceFile, goTablePackage}
import biz.lobachev.annette.data_dictionary.labels.Java.{javaStructPackage, javaTablePackage}

trait CA {

  val caComponent = component("CA", "Common tables and data structures")
    .withLabels(
//      postgresSchema("ca"),
      javaStructPackage("finance.data.ca.model"),
      javaTablePackage("finance.data.ca"),
      goTablePackage("github.com/valerylobachev/finance/logic/repository/entity/ca"),
      goStructPackage("github.com/valerylobachev/finance/logic/data_model/ca"),
    )
    .withComponents(
      // region ***************************** Countries & Languages *****************************
      component("CA-CL", "Countries & Languages")
        .withLabels(
          javaStructPackage("finance.data.ca.cl.model"),
          javaTablePackage("finance.data.ca.cl"),
        )
        .withDataElements(
          dataElement("CountryId", "countryId", varchar(3), "Country Id"),
          dataElement("LanguageId", "languageId", varchar(3), "Language Id"),
        )
        .withTables(
          table("Country", "Country", "Country")
            .withPK(
              "id" :# "CountryId",
            )
            .withFields(
              // format: off
              "name"  :# "Name",
              "key"   :#? varchar(3) :@ "Country Key",
              dataElementTableField("LanguageId"),
              include ("Modification")
              // format: on
            )
            .withIndexes(
              uniqueIndex("countryKey", "Country key must be unique", "key"),
            )
            .withRelations(
              manyToOne("languageId", "Reference to language", "Language", "languageId" -> "id")
                .withAssociation("language"),
            ),
          table("Language", "Language", "Language")
            .withPK(
              "id" :# dataElement("LanguageId"),
            )
            .withFields(
              "name" :# "Name",
              include("Modification"),
            ),
        )
        .withLabeledStructs(
          goStructSourceFile("country"),
        )(
          struct("CountryId", "Country Id")
            .likeTablePK("Country"),
          struct("CountryDto", "Country DTO")
            .likeTable("Country"),
          struct("CountryNameDto", "Country name DTO")
            .likeTable("Country", including("id", "name")),
        )
        .withLabeledStructs(
          goStructSourceFile("language"),
        )(
          struct("LanguageId", "Language Id")
            .likeTablePK("Language"),
          struct("LanguageDto", "Language DTO")
            .likeTable("Language"),
          struct("LanguageNameDto", "Language name DTO")
            .likeTable("Language", including("id", "name")),
        ),
      // endregion
      // region ***************************** Currency *****************************
      component("CA-CUR", "Currency")
        .withLabels(
          javaStructPackage("finance.data.ca.cur.model"),
          javaTablePackage("finance.data.ca.cur"),
        )
        .withDataElements(
          // ***************************** Currency *****************************
          dataElement("CurrencyId", "currencyId", varchar(5), "Currency Id"),
          dataElement("ExchangeRateTypeId", "exchangeRateTypeId", varchar(4), "Exchange rate type Id"),
          dataElement("ExchangeRate", "exchangeRate", decimal(25, 5), "Exchange rate"),
        )
        .withTables(
          table("Currency", "Currency", "Currency")
            .withPK(
              "id" :# "CurrencyId",
            )
            .withFields(
              // format: off
              "name"  :# "Name",
              "shortName"   :# "ShortName",
              "isoCode"     :# varchar(3)   :@ "Currency ISO code",
              "key"         :# varchar(3)   :@ "Currency key",
              "decimals"    :# integer           :@ "Currency decimals",
              include("Modification")
              // format: on
            ),
          table("ExchangeRateType", "Exchange rate type", "ExchangeRateType")
            .withPK(
              "id" :# dataElement("ExchangeRateTypeId"),
            )
            .withFields(
              "name" :# "Name",
              include("Modification"),
            ),
          table("ExchangeRate", "ExchangeRate", "ExchangeRate")
            .withPK(
              // format: off
              "exchangeRateTypeId" :# "ExchangeRateTypeId",
              "fromCurrencyId"           :# "CurrencyId" :@ "From Currency Id" ,
              "toCurrencyId"             :# "CurrencyId" :@ "To Currency Id" ,
              "effectiveFrom"            :# date :@ "Effective from"
              // format: on
            )
            .withFields(
              // format: off
              "exchangeRate" :# "ExchangeRate",
              "ratioFrom"          :# integer           :@ "Currency ratio from",
              "ratioTo"            :# integer           :@ "Currency ratio to",
              "directRate"         :# bool   :@ "Direct ratio indicator",
              include("Modification")
              // format: on
            )
            .withRelations(
              manyToOne(
                "exchangeRateTypeId",
                "Reference to exchange rate type",
                "ExchangeRateType",
                "exchangeRateTypeId" -> "id",
              ).withAssociation("exchangeRateType"),
              manyToOne(
                "fromCurrencyId",
                "Reference to from currency ",
                "Currency",
                "fromCurrencyId" -> "id",
              ).withAssociation("fromCurrency"),
              manyToOne(
                "toCurrencyId",
                "Reference to to currency ",
                "Currency",
                "toCurrencyId" -> "id",
              ).withAssociation("toCurrency"),
            ),
        )
        .withLabeledStructs(
          goStructSourceFile("currency"),
        )(
          struct("CurrencyId", "Currency Id")
            .likeTablePK("Currency"),
          struct("CurrencyDto", "Currency DTO")
            .likeTable("Currency"),
          struct("CurrencyNameDto", "Currency name DTO")
            .likeTable("Currency", including("id", "name")),
        )
        .withLabeledStructs(
          goStructSourceFile("exchange_rate"),
        )(
          struct("ExchangeRateId", "ExchangeRate Id")
            .likeTablePK("ExchangeRate"),
          struct("ExchangeRateDto", "ExchangeRate DTO")
            .likeTable("ExchangeRate"),
        ),
      // endregion
      // region ***************************** UnitOfMeasurement *****************************
      component("CA-UOM", "Unit of measurement")
        .withLabels(
          javaStructPackage("finance.data.ca.uom.model"),
          javaTablePackage("finance.data.ca.uom"),
        )
        .withDataElements(
          dataElement("UoMId", "uomId", varchar(6), "Unit of measurement Id"),
        )
        .withTables(
          table("UnitOfMeasurement", "Unit of measurement", "UnitOfMeasurement")
            .withTableName("units_of_measurement")
            .withPK(
              "id" :# "UoMId",
            )
            .withFields(
              // format: off
              "name"  :#  "Name",
              "shortName"   :#  varchar(30) :@ "Short name",
              "isoCode"     :#? varchar(3)  :@ "ISOCode",
              include("Modification")
              // format: on
            ),
          table("UoMText", "Unit of measurement text", "UnitOfMeasurementText")
            .withTableName("uom_texts")
            .withPK(
              "languageId" :# "LanguageId",
              "uomId" :# "UoMId",
            )
            .withFields(
              // format: off
              "name"     :# "Name",
              "shortName"      :# "ShortName",
              "commercialCode" :# varchar(6)  :@ "UoM commercial code",
              "technicalCode"  :# varchar(6)  :@ "UoM technical code",
              include("Modification")
              // format: on
            )
            .withRelations(
              manyToOne(
                "languageId",
                "Reference to language",
                "Language",
                "languageId" -> "id",
              ).withAssociation("language"),
              manyToOne(
                "uomId",
                "Reference to unit of measurement",
                "UnitOfMeasurement",
                "uomId" -> "id",
              ).withAssociation("uom" -> "texts"),
            ),
        )
        .withLabeledStructs(
          goStructSourceFile("unit_of_measurement"),
        )(
          struct("UnitOfMeasurementId", "UnitOfMeasurement Id")
            .likeTablePK("UnitOfMeasurement"),
          struct("UnitOfMeasurementDto", "UnitOfMeasurement DTO")
            .likeTable("UnitOfMeasurement")
            .withFields(
              "texts" :## linkedStructArray("UoMTextDto"),
            ),
          struct("UnitOfMeasurementNameDto", "UnitOfMeasurement name DTO")
            .likeTable("UnitOfMeasurement", including("id", "name")),
          struct("UoMTextId", "UoMText Id")
            .likeTablePK("UoMText"),
          struct("UoMTextDto", "UoMText DTO")
            .likeTable("UoMText"),
        ),
      // endregion
    )
}
