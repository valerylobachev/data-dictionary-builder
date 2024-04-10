package biz.lobachev.annette.data_dictionary.builder.builder

import biz.lobachev.annette.data_dictionary.builder.model.{Domain, RawDomain}

class Builder(val rawDomain: RawDomain) {
  def build(): Either[Seq[String], Domain] = Right(
    Domain(
      rawDomain.id,
      rawDomain.name,
      rawDomain.description,
      raw = rawDomain,
    ),
  )

}
