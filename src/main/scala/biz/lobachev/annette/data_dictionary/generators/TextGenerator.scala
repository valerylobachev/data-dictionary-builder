package biz.lobachev.annette.data_dictionary.generators

trait TextGenerator {
  def generate(): Seq[GeneratedFile]
}
