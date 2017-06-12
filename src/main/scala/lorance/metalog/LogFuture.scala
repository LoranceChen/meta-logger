package lorance.metalog

import scala.meta._

case class MacroLogNotFuture(info: String) extends Exception

/**
  * if result is Future out put the future result
  * 1. log Future exception with recover and then throw the exception
  *
  * todo:
  * 1. use ExecutionContext for log future
  *
  * question:
  * 1. how to use Lit[ExecutionContextExecutor](macroLogContext)
  */
class LogFuture(logTags: List[LogTag] = Info() :: Nil,
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

        result match {
          case rst: scala.concurrent.Future[ _ ] =>
            val macroLogContext = scala.concurrent.ExecutionContext.Implicits.global
            result.recover{
              case e =>
                val logDataResult = LogData(
                  time,
                  metaLogContext.logId.id,
                  $logTags,
                  classPath + "." + methodName,
                  metaLogContext.callStackOut.add1,
                  LogResult(s"[exception] - $$e - [cause] - $${e.getCause}")
                  )
                $logFunc(logDataResult)
                throw e
            }(macroLogContext).map{x =>
              val logDataResult = LogData(
                time,
                metaLogContext.logId.id,
                $logTags,
                classPath + "." + methodName,
                metaLogContext.callStackOut.add1,
                LogResult(x)
                )
              $logFunc(logDataResult)
              x
            }(macroLogContext)
          case _ =>
            throw MacroLogNotFuture("result should be Future[ _ ] type")
        }
        """

        defn.copy(body = body, paramss = MacroUtil.appendParams(defn))
      case _ =>
        abort("@LogFuture most annotate a def")
    }
  }
}
