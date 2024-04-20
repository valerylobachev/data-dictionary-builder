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
      Seq("Ид перечисления", "Наименование", "Длина кода", "Описание"),
    ),
    enumItems = SheetTranslation(
      "Элементы перечислений",
      Seq("Ид перечисления", "Наименование", "Код элемента", "Наименование элемента"),
    ),
  )

}
