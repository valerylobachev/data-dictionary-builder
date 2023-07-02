package biz.lobachev.annette.data_dictionary.builder.rendering

trait Renderer {
  def render(): Seq[RenderResult]
}
