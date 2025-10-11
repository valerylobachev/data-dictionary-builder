package biz.lobachev.annette.data_dictionary.generators

import java.io.{BufferedWriter, File, FileWriter}

object Generator {

  def generate(generator: TextGenerator, pathPrefix: String) = {
    val result = generator.generate()
    result.foreach { r =>
      val path = s"$pathPrefix/${r.path}"
      new java.io.File(path).mkdirs
      writeFile(s"$path/${r.filename}", r.content)
    }
  }

  def generate(generator: BinaryGenerator, pathPrefix: String) = {
    val _ = generator.generate(pathPrefix)
  }

  private def writeFile(filename: String, s: String): Unit = {
    val file = new File(filename)
    val bw   = new BufferedWriter(new FileWriter(file))
    bw.write(s)
    bw.close()
  }
}
