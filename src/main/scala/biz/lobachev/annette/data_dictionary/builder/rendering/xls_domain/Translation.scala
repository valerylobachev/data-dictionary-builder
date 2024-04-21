package biz.lobachev.annette.data_dictionary.builder.rendering.xls_domain

case class SheetTranslation(
  name: String,
  fields: Seq[String],
)

case class WorkbookTranslation(
  domain: SheetTranslation,
  components: SheetTranslation,
  entities: SheetTranslation,
  fields: SheetTranslation,
  indexes: SheetTranslation,
  indexFields: SheetTranslation,
  relations: SheetTranslation,
  relationFields: SheetTranslation,
  dataElements: SheetTranslation,
  enums: SheetTranslation,
  enumItems: SheetTranslation,
)

object WorkbookTranslation {
  val ru = WorkbookTranslation(
    domain = SheetTranslation(
      "Домен",
      Seq(
        "Ид",
        "Наименование",
        "Описание",
      ),
    ),
    components = SheetTranslation(
      "Компоненты",
      Seq("Ид", "Компонент", "Схема", "Описание"),
    ),
    entities = SheetTranslation(
      "Таблицы",
      Seq(
        "Ид. компонента",
        "Компонент",
        "Ид таблицы",
        "Наименование таблицы",
        "Имя сущности",
        "Имя таблицы",
        "Тип сущности ",
        "Описание",
      ),
    ),
    fields = SheetTranslation(
      "Поля таблиц",
      Seq(
        "Компонент",
        "Имя сущности",
        "Имя таблицы",
        "Наименование таблицы",
        "Наименование поля",
        "Имя поля",
        "Тип данных Java",
        "Имя поля в БД",
        "Тип данных Postgres",
        "Длина/точность",
        "Scale",
        "ПК/серийный",
        "Обязательно",
        "Элемент Данных",
        "Описание",
      ),
    ),
    indexes = SheetTranslation(
      "Индексы",
      Seq(
        "Компонент",
        "Имя таблицы",
        "Наименование таблицы",
        "Код индекса",
        "Наименование индекса",
        "Уникальный",
        "Описание",
      ),
    ),
    indexFields = SheetTranslation(
      "Поля индексов",
      Seq(
        "Компонент",
        "Имя таблицы",
        "Наименование таблицы",
        "Код индекса",
        "Наименование индекса",
        "Уникальный",
        "Имя поля",
        "Наименование поля",
      ),
    ),
    relations = SheetTranslation(
      "Отношения",
      Seq(
        "Компонент",
        "Имя таблицы",
        "Наименование таблицы",
        "Код отношения",
        "Наименование отношения",
        "Тип отношения",
        "Имя ссылочной таблицы",
        "Наименование ссылочной таблицы",
        "On update",
        "On delete",
        "Описание",
      ),
    ),
    relationFields = SheetTranslation(
      "Поля отношений",
      Seq(
        "Компонент",
        "Имя таблицы",
        "Наименование таблицы",
        "Код отношения",
        "Наименование отношения",
        "Имя ссылочной таблицы",
        "Наименование ссылочной таблицы",
        "Поле таблицы",
        "Наименование поля таблицы",
        "Поле ссылочной таблицы",
        "Наименование поля ссылочной таблицы",
      ),
    ),
    dataElements = SheetTranslation(
      "Элементы данных",
      Seq(
        "Компонент",
        "Код элемента",
        "Имя поля",
        "Имя поля в БД",
        "Наименование поля",
        "Тип данных",
        "Тип данных Java",
        "Тип данных Postgres",
        "Длина/точность",
        "Scale",
        "Перечисление",
        "Значение по умолчанию",
        "Описание",
      ),
    ),
    enums = SheetTranslation(
      "Перечисления",
      Seq(
        "Компонент",
        "Ид перечисления",
        "Наименование",
        "Длина кода",
        "Описание",
      ),
    ),
    enumItems = SheetTranslation(
      "Элементы перечислений",
      Seq(
        "Компонент",
        "Ид перечисления",
        "Наименование",
        "Код элемента",
        "Наименование элемента",
      ),
    ),
  )

}
