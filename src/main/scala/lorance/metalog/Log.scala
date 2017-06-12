package lorance.metalog

import scala.meta._


/**
  * auto log input and output infomation for reactive runtime system
  *
  * example:
  *   before
  *   @Log
      def complicated(a: Int, b: String)(c: Int): Int = {
        a + b.length + c
      }

      after
      def complicated(a: Int, b: String)(c: Int)(implicit logId: LogId = NoneLogId): Int = {
        //just example
        println("[MetaLog] - LogId(theLogId) - Fri Apr 21 19:57:19 CST 2017 - macros.MainTest$.complicated - Map(a -> 1, b -> 2, c -> 3)")

        a + b.length + c
      }
  *
  * todo :1.[x] add implicit param String represent event id(a event is occurred by user/input trigger,in this system most is http request
        * a event call many added log annotation's method, under this calling, those method info should has same identity mark)
        * the LogId create at event source, such as a http request enter
        *
        * 2.[feature to decide] ignore log action if CallStack use default CallStack/Out object (-1) - that means log should not invoke begin with a event input source
        * but a half way log may be used as analysis,such as log DAO API time cost
        * 3.[x] add LogForward annotation - just add implicit param forward context to inner Log function call, do NOT log action in current method
        *
        * PS: implicit and default value not support
  *
  */
class Log(logTags: List[LogTag] = Info() :: Nil,
          logger: LogData => Unit = println)
  extends scala.annotation.StaticAnnotation {

  inline def apply(defn: Any): Any = meta {
    defn match {
      case defn: Defn.Def =>
        val paramsMap = MacroUtil.paramssToMap(defn)
        val (logTags, logFunc) = MacroUtil.getLogMacroParams(this)

        val body: Term = q"""
        val time = System.currentTimeMillis
        val methodName = ${defn.name.syntax}
        val classPath = getClass.getName
        val logTagStr = $logTags.mkString(", ")
        val logData = LogData(
          time,
          metaLogContext.logId.id,
          $logTags,
          classPath + "." + methodName,
          metaLogContext.callStack.add1,
          LogParams($paramsMap.toMap)
          )
        $logFunc(logData)

        val result = ${defn.body}

        val logDataResult = LogData(
          time,
          metaLogContext.logId.id,
          $logTags,
          classPath + "." + methodName,
          metaLogContext.callStackOut.add1,
          LogResult(result)
          )
        $logFunc(logDataResult)
        result
        """

        defn.copy(body = body, paramss = MacroUtil.appendParams(defn))
      case _ =>
        println(defn.structure)
        abort("@Log most annotate a def")
    }
  }
}
