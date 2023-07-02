package biz.lobachev.annette.data_dictionary.builder.rendering

case class RenderResult(path: String, filename: String, content: String) {
  override def toString: String = {
    val lines = content.split("\n").map(c => s"    $c").toSeq
    (Seq(
      s"path:     $path",
      s"filename: $filename",
      s"content:"
    ) ++ lines).mkString("\n")
  }
}
