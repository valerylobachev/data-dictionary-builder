package biz.lobachev.annette.data_dictionary.builder.model

import biz.lobachev.annette.data_dictionary.builder.model.Labels

case class Endpoint(
  id: String,
  name: String,
  description: String = "",
  endpointName: String,
  labels: Labels = Map.empty,
)
