package biz.lobachev.annette.data_dictionary.generators

trait BinaryGenerator {
  def generate(path: String): Seq[GeneratedFile]
}
