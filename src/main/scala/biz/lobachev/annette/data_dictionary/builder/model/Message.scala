package biz.lobachev.annette.data_dictionary.builder.model

case class Message(
  status: MessageStatus,
  message: String,
  componentId: String
)

trait MessageStatus

case object ErrorStatus extends MessageStatus
case object WarningStatus extends MessageStatus
