package biz.lobachev.annette.data_dictionary.builder.rendering.`export`

import biz.lobachev.annette.data_dictionary.builder.rendering.{RenderResult, TextRenderer}
import biz.lobachev.annette.data_dictionary.builder.model._
import io.circe._
import io.circe.syntax._
import io.circe.generic.semiauto._

case class ExportJsonRenderer(domain: Domain) extends TextRenderer {
  val path = "export"

  implicit val encoder13: Encoder[IndexMethod]      = Encoder.instance(a => Json.fromString(a.method))
  implicit val encoder12: Encoder[EntityIndex]      = deriveEncoder[EntityIndex]
  implicit val encoder11: Encoder[RelationType]     = Encoder.instance(a => Json.fromString(a.toString))
  implicit val encoder10: Encoder[ForeignKeyAction] = Encoder.instance(a => Json.fromString(a.toString))
  implicit val encoder91: Encoder[Association]      = deriveEncoder[Association]
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
  implicit val encoder3: Encoder[Component]         = deriveEncoder[Component]
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
