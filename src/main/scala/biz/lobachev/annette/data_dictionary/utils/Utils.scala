package biz.lobachev.annette.data_dictionary.utils

object Utils {

  def findDuplicates[T](s: Seq[(String, T)]): Map[String, Seq[T]] =
    s.groupBy(v => v._1)
      .filter { case _ -> v => v.length > 1 }
      .map(v => v._1 -> v._2.map(_._2))

}
