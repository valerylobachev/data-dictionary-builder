package biz.lobachev.annette.data_dictionary.builder.rendering.markdown2

case class Chapter(
  title: String,
  content: String,
  chapters: List[Chapter],
)
