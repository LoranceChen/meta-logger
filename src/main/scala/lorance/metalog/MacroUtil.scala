package lorance.metalog

import scala.collection.immutable.Seq
import scala.meta._


object MacroUtil {
  def paramssToMap(defn: Defn.Def) = {
    val logParamsSyntax: Seq[Term.Tuple] = defn.paramss.flatten.map{ param =>
      q"(${param.name.syntax}, ${Term.Name(param.name.value)})"
    }

    val toMapImpl: Term =
      q"_root_.scala.collection.Map[String, Any](..$logParamsSyntax)"
    toMapImpl
  }

  def methodParamsToList(d: Defn.Def): Term = d.paramss.flatten.foldRight[Term](q"Nil")(
    (param, agr) => q"""(${param.name.syntax }  + " = " +  ${Term.Name(param.name.value) }) :: $agr"""
  )

  //List[LogTag], String => Unit
  def getLogMacroParams(self: Tree): (Term, Term) = {
    val (logTags, logFunc) = {
      def getTerm(arg: Term.Arg): Term = arg match {
        case Term.Arg.Repeated(v) => v
        case Term.Arg.Named(n, v) => getTerm(v)
        case v: Term => v
        case v => abort(s"@LogAsync unexpected Term.Arg: ${v.getClass }")
      }

      self match {
        case q"new $_($v, $v1)" => getTerm(v) -> getTerm(v1)
        case x => abort(s"@Log unexpected params: got $x")
      }
    }

    (logTags, logFunc)
  }

  def appendParams(defn: Defn.Def): Seq[Seq[Term.Param]] = {
    val logParamTermName = "macroLogId"
    val logParamInfoName = logParamTermName + ".id"
    val logTypeName = "LogId"
    val logContextParamName = "metaLogContext"
    val logContextName = "LogContext"

    //params add implicit logid
    val rawParamss = defn.paramss

    val hasNameIslogContext = rawParamss.flatten.exists(param => param.name.syntax == logContextParamName)
    if(hasNameIslogContext) {
      println(s"Notice: ${defn.name} has define $logContextParamName parameter")
      return rawParamss
    }

    val impParamssGrouped = rawParamss.groupBy(_.exists(_.mods.exists(_.structure == Mod.Implicit().structure)))
    val impParam = impParamssGrouped.get(true).map(_.head)

    def logParam = (needImpMod: Boolean) => {
      if (true) {
        Seq(Term.Param(Seq(Mod.Implicit()), Term.Name(logContextParamName), Some(Type.Name(logContextName)), Some(Term.Name("DefLogContext"))))
      }
      else {
        Seq(Term.Param(Nil, Term.Name("logContext"), Some(Type.Name("LogContext")), Some(Term.Name("DefLogContext"))))
      }
    }

    val finalParamss = impParam match {
      case None => //原参数没有 implicit
        rawParamss :+ logParam(true)
      case Some(params) => //原参数表含有 implicit
        //给含隐式参数的参数组加上logId
        val newP = params ++ logParam(false)
        impParamssGrouped.get(false) match {
          case None => //原参数只有implicit一组参数
            Seq(newP)
          case Some(nonImpParamss) => //原参数表包含多个参数
            nonImpParamss :+ newP
        }
    }

    finalParamss
  }

  def appendParamsToDecl(defn: Decl.Def) = {
    val logParamTermName = "macroLogId"
    val logParamInfoName = logParamTermName + ".id"
    val logTypeName = "LogId"

    //params add implicit logid
    val rawParamss = defn.paramss
    val impParamssGrouped = rawParamss.groupBy(_.exists(_.mods.exists(_.structure == Mod.Implicit().structure)))
    val impParam = impParamssGrouped.get(true).map(_.head)

    def logParam = (needImpMod: Boolean) => {
      if(true)
        Seq(
          Term.Param(Seq(Mod.Implicit()), Term.Name(logParamTermName), Some(Type.Name(logTypeName)), None),
          Term.Param(Nil, Term.Name("macroCallStack"), Some(Type.Name("CallStack")), None),
          Term.Param(Nil, Term.Name("macroCallStackOut"), Some(Type.Name("CallStackOut")), None)
        )
      else
        Seq(
          Term.Param(Nil, Term.Name(logParamTermName), Some(Type.Name(logTypeName)), None), //should NOT use default
          Term.Param(Nil, Term.Name("macroCallStack"), Some(Type.Name("CallStack")), None),
          Term.Param(Nil, Term.Name("macroCallStackOut"), Some(Type.Name("CallStackOut")), None))
    }
    val finalParamss = impParam match {
      case None => //原参数没有 implicit
        rawParamss :+ logParam(true)
      case Some(params) => //原参数表含有 implicit
        //给含隐式参数的参数组加上logId
        val newP = params ++ logParam(false)
        impParamssGrouped.get(false) match {
          case None => //原参数只有implicit一组参数
            Seq(newP)
          case Some(nonImpParamss) => //原参数表包含多个参数
            nonImpParamss :+ newP
        }
    }

    finalParamss
  }
}

