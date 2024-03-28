package biz.lobachev.annette.data_dictionary.builder.utils

object Utils {

  def findDuplicates(s: Seq[String]): Seq[String] =
    s.groupBy(v => v).filter { case _ -> v => v.length > 1 }.map(d => d._1).toSeq


}
