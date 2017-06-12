package lorance.metalog

import scala.meta._


/**
  * log for val and var
  * NOTICE: [bug] not support tuple pattern match, eg: var (a, b) = (1, 2)
  */
class LogVar(logTags: List[LogTag] = Info() :: Nil,
          logger: LogData => Unit = println)
  extends scala.annotation.StaticAnnotation {

  inline def apply(defn: Any): Any = meta {
    defn match {
      case defnVal: Defn.Val =>
//        println(defnVal.rhs)
        val (logTags, logFunc) = MacroUtil.getLogMacroParams(this)

        val body: Term = q"""
          val time = System.currentTimeMillis
          val valName = ${defnVal.pats.map(_.syntax + ", ").mkString}
          val classPath = getClass.getName

          val result = ${defnVal.rhs}

          val logDataResult = LogData(
            time,
            metaLogContext.logId.id,
            $logTags,
            classPath + "." + valName,
            metaLogContext.callStackOut.add1,
            LogResult(result)
            )
          $logFunc(logDataResult)
          result
        """
        defnVal.copy(rhs = body)
      case defnVar: Defn.Var =>
        val (logTags, logFunc) = MacroUtil.getLogMacroParams(this)

        val body: Option[Term] =
          if(defnVar.rhs.isDefined) {
            Some(q"""
              val time = System.currentTimeMillis
              val varName = ${defnVar.pats.map(_.syntax + ", ").mkString}
              val classPath = getClass.getName
              val result = ${defnVar.rhs.get}

              val logDataResult = LogData(
                time,
                metaLogContext.logId.id,
                $logTags,
                classPath + "." + varName,
                metaLogContext.callStackOut.add1,
                LogResult(result)
                )
              $logFunc(logDataResult)
              result
            """)
          } else None
        defnVar.copy(rhs = body)
      case _ =>
        println(defn.structure)
        abort("@Log most annotate a def")
    }
  }
}
