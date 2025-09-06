package biz.lobachev.annette.data_dictionary.builder.model

import scala.collection.immutable.ListMap

case class Repository(
  id: String,
  name: String,
  description: String = "",
  serviceName: String,
  endpoints: Map[String, Endpoint] = Map.empty,
  labels: Labels = Map.empty,
  componentId: Option[String] = None,
) {
  def withDescription(description: String) = copy(description = description)

  def withEndpointSeq(seq: Seq[Endpoint]) =
    copy(endpoints = ListMap.from(seq.map(e => e.id -> e)))

  def withEndpoints(seq: Endpoint*) = withEndpointSeq(seq)

  def withLabels(seq: Label*) = copy(labels = labels ++ seq.map(a => a.key -> a.value))
}
