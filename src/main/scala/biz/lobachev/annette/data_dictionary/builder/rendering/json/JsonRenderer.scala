package biz.lobachev.annette.data_dictionary.builder.rendering.json

import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, Renderer}
import biz.lobachev.annette.data_dictionary.builder.model._
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._

case class JsonRenderer(domain: Domain) extends Renderer {
  val path = "json"

  implicit val encoder12: Encoder[EntityIndex]      = deriveEncoder[EntityIndex]
  implicit val encoder11: Encoder[RelationType]     = Encoder.instance(a => Json.fromString(a.toString))
  implicit val encoder10: Encoder[ForeignKeyAction] = Encoder.instance(a => Json.fromString(a.toString))
  implicit val encoder9: Encoder[EntityRelation]    = deriveEncoder[EntityRelation]
  implicit val encoder8: Encoder[DataType]          = deriveEncoder[DataType]
  implicit val encoder7: Encoder[EntityField]       = deriveEncoder[EntityField]
  implicit val encoder6: Encoder[EntityType]        = Encoder.instance {
    case TableEntity    => Json.fromString("table")
    case StructEntity   => Json.fromString("struct")
    case EmbeddedEntity => Json.fromString("embedded")
  }
  implicit val encoder5: Encoder[Entity]            = deriveEncoder[Entity]
  implicit val encoder4: Encoder[DataElement]       = deriveEncoder[DataElement]
  implicit val encoder3: Encoder[Group]             = deriveEncoder[Group]
  implicit val encoder2: Encoder[EnumData]          = deriveEncoder[EnumData]
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
