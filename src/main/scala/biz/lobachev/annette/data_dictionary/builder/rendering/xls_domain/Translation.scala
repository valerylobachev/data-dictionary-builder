package biz.lobachev.annette.data_dictionary.builder.rendering.xls_domain

case class SheetTranslation(
  name: String,
  fields: Seq[String],
)

case class WorkbookTranslation(
  domain: SheetTranslation,
  groups: SheetTranslation,
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
    groups = SheetTranslation(
      "Группы",
      Seq("Ид", "Наименование", "Схема", "Описание", "Пакет репозиториев", "Пакет сущностей"),
    ),
    entities = SheetTranslation(
      "Таблицы",
      Seq(
        "Группа",
        "Наименование группы",
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
        "Код поля",
        "Ид таблицы",
        "Наименование таблицы",
        "Вид поля",
        "Имя поля",
        "Имя поля в БД",
        "Наименование поля",
        "Тип данных",
        "Тип данных Java",
        "Тип данных Postgres",
        "Длина/точность",
        "Scale",
        "Элемент Данных",
        "Ид сущности include",
        "Перечисление",
        "ПК/серийный",
        "Обязательно",
        "Значение по умолчанию",
        "Описание",
      ),
    ),
    indexes = SheetTranslation(
      "Индексы",
      Seq(
        "Код индекса",
        "Ид таблицы",
        "Наименование таблицы",
        "Ид индекса",
        "Наименование индекса",
        "Уникальный",
        "Описание",
      ),
    ),
    indexFields = SheetTranslation(
      "Поля индексов",
      Seq(
        "Код индекса",
        "Ид таблицы",
        "Наименование таблицы",
        "Ид индекса",
        "Наименование индекса",
        "Имя поля",
        "Наименование поля",
      ),
    ),
    relations = SheetTranslation(
      "Отношения",
      Seq(
        "Код отношения",
        "Ид таблицы",
        "Наименование таблицы",
        "Ид отношения",
        "Наименование отношения",
        "Тип отношения",
        "Ид ссылочной таблицы",
        "Наименование ссылочной таблицы",
        "On update",
        "On delete",
        "Описание",
      ),
    ),
    relationFields = SheetTranslation(
      "Поля отношений",
      Seq(
        "Код отношения",
        "Ид таблицы",
        "Наименование таблицы",
        "Ид ссылочной таблицы",
        "Наименование ссылочной таблицы",
        "Ид отношения",
        "Наименование отношения",
        "Поле таблицы",
        "Наименование поля таблицы",
        "Поле ссылочной таблицы",
        "Наименование поля ссылочной таблицы",
      ),
    ),
    dataElements = SheetTranslation(
      "Элементы данных",
      Seq(
        "Ид элемента",
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
      Seq("Ид перечисления", "Наименование", "Длина кода", "Описание"),
    ),
    enumItems = SheetTranslation(
      "Элементы перечислений",
      Seq("Ид перечисления", "Наименование", "Код элемента", "Наименование элемента"),
    ),
  )

}
