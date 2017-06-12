package lorance.metalog

/**
  * 使用场景：Dev开发模式、Usage 用户的关键事件（数据的设定和日志监控产品部门参与）
  */
trait LogTag
case class Info() extends LogTag
case class Warning() extends LogTag
case class Error() extends LogTag
case class Logic() extends LogTag
case class Develop() extends LogTag

case class LogId(id: String = "")
object NoneLogId extends LogId("")

/**
  * todo `level` as Atom increase
  * - `CallStack` with +1
  * - [to decide ] `CallStackOut` set same as CallStack's level(seems no means)
  */
trait TCallStack {
  var level: Int
}
case class CallStack(var level: Int = 0) extends TCallStack{
  def add1 = this.synchronized{level += 1; level}
}
object DefCallStack extends CallStack(-1)

case class CallStackOut(var level: Int = 0) extends TCallStack {
  def add1 = this.synchronized{level += 1; level}
}
object DefCallStackOut extends CallStackOut(-1)

trait LogType {
  type Value
  val value: Value
  val name: String
}

case class LogParams(value: Map[String, Any]) extends LogType {
  override type Value = Map[String, Any]
  override val name: String = "INPUT"
}

// can't parse result Type in macro annotation
case class LogResult[T](value: T) extends LogType {
  override type Value = T
  override val name: String = "OUTPUT"
}

case class LogData(time: Long,
                   chainId: String,
                   tags: Seq[LogTag],
                   methodPath: String,
                   callStackIdx: Int,
                   typ: LogType//Input Output
                   )
/**
  * a helper to create implicit value
  */
trait MetaLogger {
  implicit def logContext = LogContext(
    LogId(java.util.UUID.randomUUID().toString),
    CallStack(), CallStackOut())
}

case class LogContext(logId: LogId, callStack: CallStack, callStackOut: CallStackOut)
object DefLogContext extends LogContext(NoneLogId, DefCallStack, DefCallStackOut)