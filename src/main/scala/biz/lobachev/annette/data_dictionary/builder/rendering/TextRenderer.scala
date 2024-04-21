package biz.lobachev.annette.data_dictionary.builder.rendering

trait TextRenderer {
  def render(): Seq[RenderResult]
}
