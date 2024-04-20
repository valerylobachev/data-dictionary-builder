package biz.lobachev.annette.data_dictionary.builder.rendering

trait BinaryRenderer {
  def render(path: String): Seq[RenderResult]
}
