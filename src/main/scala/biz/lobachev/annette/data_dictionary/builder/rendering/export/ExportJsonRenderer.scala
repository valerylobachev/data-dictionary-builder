package biz.lobachev.annette.data_dictionary.builder.rendering.`export`

import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, Renderer}
import biz.lobachev.annette.data_dictionary.builder.model._
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._

case class ExportJsonRenderer(domain: RawDomain) extends Renderer {
  val path = "export"

  implicit val encoder12: Encoder[RawEntityIndex]      = deriveEncoder[RawEntityIndex]
  implicit val encoder11: Encoder[RelationType]     = Encoder.instance(a => Json.fromString(a.toString))
  implicit val encoder10: Encoder[ForeignKeyAction] = Encoder.instance(a => Json.fromString(a.toString))
  implicit val encoder9: Encoder[RawEntityRelation]    = deriveEncoder[RawEntityRelation]
  implicit val encoder8: Encoder[DataType]          = deriveEncoder[DataType]
  implicit val encoder7: Encoder[RawEntityField]       = deriveEncoder[RawEntityField]
  implicit val encoder6: Encoder[EntityType]        = Encoder.instance {
    case TableEntity    => Json.fromString("table")
    case StructEntity   => Json.fromString("struct")
    case EmbeddedEntity => Json.fromString("embedded")
  }
  implicit val encoder5: Encoder[RawEntity]            = deriveEncoder[RawEntity]
  implicit val encoder4: Encoder[RawDataElement]       = deriveEncoder[RawDataElement]
  implicit val encoder3: Encoder[RawComponent]             = deriveEncoder[RawComponent]
  implicit val encoder2: Encoder[RawEnumData]          = deriveEncoder[RawEnumData]
  implicit val encoder1: Encoder[DomainExport]      = deriveEncoder[DomainExport]

  override def render(): Seq[RenderResult] =
    Seq(
      RenderResult(
        s"$path",
        s"${domain.name}.json",
        DomainExport(domain).asJson.toString(),
      ),
    )

}
