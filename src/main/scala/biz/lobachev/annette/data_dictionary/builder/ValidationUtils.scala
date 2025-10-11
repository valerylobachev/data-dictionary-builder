package biz.lobachev.annette.data_dictionary.builder

trait ValidationUtils {
  def findDuplicates[T](s: Seq[(String, T)]): Map[String, Seq[T]] =
    s.groupBy(v => v._1)
      .filter { case _ -> v => v.length > 1 }
      .map(v => v._1 -> v._2.map(_._2))

  def findDuplicatedStrings(s: Seq[String]): Option[Seq[String]] = {
    val r = s.groupBy(v => v).filter { case _ -> v => v.length > 1 }.keys.toSeq
    if r.nonEmpty then Some(r) else None
  }
}
