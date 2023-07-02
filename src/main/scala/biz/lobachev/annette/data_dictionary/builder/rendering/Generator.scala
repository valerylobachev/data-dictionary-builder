package biz.lobachev.annette.data_dictionary.builder.rendering

import java.io.{BufferedWriter, File, FileWriter}

object Generator {

  def generate(renderer: Renderer, pathPrefix: String) = {
    val result = renderer.render()
    result.foreach { r =>
      val path = s"$pathPrefix${r.path}"
      new java.io.File(path).mkdirs
      writeFile(s"$path/${r.filename}", r.content)
    }
  }

  private def writeFile(filename: String, s: String): Unit = {
    val file = new File(filename)
    val bw   = new BufferedWriter(new FileWriter(file))
    bw.write(s)
    bw.close()
  }
}
