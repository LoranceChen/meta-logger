package metalog


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

  def appendParams(defn: Defn.Def) = {
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
          Term.Param(Seq(Mod.Implicit()), Term.Name(logParamTermName), Some(Type.Name(logTypeName)), Some(Term.Name("NoneLogId"))),
          Term.Param(Nil, Term.Name("macroCallStack"), Some(Type.Name("CallStack")), Some(Term.Name("DefCallStack"))),
          Term.Param(Nil, Term.Name("macroCallStackOut"), Some(Type.Name("CallStackOut")), Some(Term.Name("DefCallStackOut")))
        ) //should NOT use default Some(Term.Name("NoneLogId"))
      else
        Seq(
          Term.Param(Nil, Term.Name(logParamTermName), Some(Type.Name(logTypeName)), Some(Term.Name("NoneLogId"))), //should NOT use default
          Term.Param(Nil, Term.Name("macroCallStack"), Some(Type.Name("CallStack")), Some(Term.Name("DefCallStack"))),
          Term.Param(Nil, Term.Name("macroCallStackOut"), Some(Type.Name("CallStackOut")), Some(Term.Name("DefCallStackOut"))))
    }
    val finalParamss = impParam match {
      case None => //原参数没有 implicit
        rawParamss :+ logParam(true)
      case Some(params) => //原参数表含有 implicit
        //给含隐式参数的参数组加上logId
        val newP = params ++ logParam(false)
        impParamssGrouped.get(false) match {
          case None => //原参数表这implicit一组参数
            Seq(newP)
          case Some(nonImpParamss) => //原参数表包含多个参数
            nonImpParamss :+ newP
        }
    }

    finalParamss
  }
}

//data
case class LogId(id: String = "")
object NoneLogId extends LogId("")

/**
  * todo `level` as Atom increase
  * - `CallStack` with +1
  * - [to decide ] `CallStackOut` set same as CallStack's level(seems no means)
  */
case class CallStack(var level: Int = 0) {
  def add1 = this.synchronized{level += 1; level}
}
object DefCallStack extends CallStack(-1)

case class CallStackOut(var level: Int = 0) {
  //  def set(newLevel: Int) = this.synchronized{level = newLevel; level}
}
object DefCallStackOut extends CallStackOut(-1)
