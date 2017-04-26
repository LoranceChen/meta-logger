package metalog


import scala.meta._

case class MacroLogNotFuture(info: String) extends Exception

/**
  * if result is Future out put the future result
  * 1. log Future exception with recover and then throw the exception
  */
class LogFuture extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    defn match {
      case defn: Defn.Def =>
        val toMapImpl = MacroUtil.paramssToMap(defn)

        val body: Term = q"""
        val time = new java.util.Date()
        val methodName = ${defn.name.syntax}
        val classPath = getClass.getName
        println("[MetaLogFuture.Input] - " + time + " - " + macroLogId.id + " - " + macroCallStack.add1 + " - " + classPath + "." + methodName + " - " + $toMapImpl)
        val result = ${defn.body}

        result match {
          case rst: scala.concurrent.Future[ _ ] =>
            result.recover{
              case e =>
                macroCallStackOut.level += 1
                println("[MetaLogFuture.Output] - " + time + " - " + macroLogId.id + " - " + macroCallStackOut.level + " - " + classPath + "." + methodName + " - exception - " + e + " - cause - " + e.getCause)
                throw e
            }.map{x =>
              macroCallStackOut.level += 1
              println("[MetaLogFuture.Output] - " + time + " - " + macroLogId.id + " - " + macroCallStackOut.level + " - " + classPath + "." + methodName + " - " + x)
              x
            }
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
