package biz.lobachev.annette.data_dictionary.builder

case class Message(
  status: MessageStatus,
  location: String,
  text: String,
)

object Message {
  def messagesHaveErrors(messages: Seq[Message]): Boolean =
    messages.exists(m => m.status == ErrorMessage)

  def printMessages(messages: Seq[Message]) =
    messages.foreach {
      case Message(ErrorMessage, loc, msg)   => println(s"ERROR: $loc: $msg")
      case Message(WarningMessage, loc, msg) => println(s"WARN:  $loc: $msg")
    }

  def errorMessage(location: String, text: String)   = Message(ErrorMessage, location, text)
  def warningMessage(location: String, text: String) = Message(ErrorMessage, location, text)
}

sealed trait MessageStatus
case object WarningMessage extends MessageStatus
case object ErrorMessage   extends MessageStatus
