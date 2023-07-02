package biz.lobachev.annette.data_dictionary.builder.rendering

package object markdown {
  val Language       = "Language"
  val FieldHeader    = "FieldHeader"
  val IndexHeader    = "IndexHeader"
  val RelationHeader = "RelationHeader"

  val EnglishTranslaltion = Map(
    Language       -> "en",
    FieldHeader    -> "| Field  | Data type | PK | Required | Description |\n| ------- | ------- | ------- | ------- | ------- |",
    IndexHeader    -> "**Indexes**\n\n| Fields | Unique | Description|\n| ------- | ------- | ------- |",
    RelationHeader -> "**Relations**\n\n| Fields  | Related table | Related fields | Type | Description|\n| ------- | ------- | ------- | ------- | ------- |"
  )

  val PolishTranslaltion = Map(
    Language       -> "pl",
    FieldHeader    -> "| Kolumna | Typ danych | KG | Wymagane | Opis |\n| ------- | ------- | ------- | ------- | ------- |",
    IndexHeader    -> "**Indeksy**\n\n| Kolumny | Уникальный | Opis|\n| ------- | ------- | ------- |",
    RelationHeader -> "**Relacji**\n\n| Kolumny  | Powiązana tabela | Powiązany kolumny | Typ | Opis|\n| ------- | ------- | ------- | ------- | ------- |"
  )

  val RussianTranslaltion = Map(
    Language       -> "ru",
    FieldHeader    -> "| Поле  | Тип данных | ПК | Обязательно| Описание|\n| ------- | ------- | ------- | ------- | ------- |",
    IndexHeader    -> "**Индексы**\n\n| Поля | Уникальный | Описание|\n| ------- | ------- | ------- |",
    RelationHeader -> "**Связи**\n\n| Поля  | Связанная таблица | Связанные поля | Тип | Описание|\n| ------- | ------- | ------- | ------- | ------- |"
  )

}
