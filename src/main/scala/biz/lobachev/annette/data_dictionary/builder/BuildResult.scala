package biz.lobachev.annette.data_dictionary.builder

import biz.lobachev.annette.data_dictionary.model.Domain

case class BuildResult(
  domain: Domain,
  warnings: Seq[Message],
)
