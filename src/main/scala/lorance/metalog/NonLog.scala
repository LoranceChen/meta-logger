package lorance.metalog

import scala.meta._

/**
  * log forward log context params
  */
class NonLog extends scala.annotation.StaticAnnotation {
  inline def apply(defn: Any): Any = meta {
    defn match {
      case defn: Defn.Def =>
        defn.copy(paramss = MacroUtil.appendParams(defn))
      case _ =>
        abort("@NonLog most annotate a def")
    }
  }
}


