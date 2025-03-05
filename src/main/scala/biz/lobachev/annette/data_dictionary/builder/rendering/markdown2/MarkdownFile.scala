package biz.lobachev.annette.data_dictionary.builder.rendering.markdown2

case class MarkdownFile(
  filename: String,
  title: String,
  chapters: List[Chapter],
)
