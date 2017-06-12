
import lorance.metalog._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * log print data:
  *   case class LogData(time: Long, //the method current execute timestamp
                   chainId: String, //this time unique invoke id
                   tags: Seq[LogTag], //tags, such as, Info, Warning, etc.
                   methodPath: String, //current execute method path
                   callStackIdx: Int, //calling chain's count
                   typ: LogType//the method params or result
                   )

  * output:
      LogData(1497281520204,f35a2d1e-3e4b-4af9-9158-350c3f6e5c7b,List(Info()),NeedLogMethod$.futAdd1,1,LogParams(Map(x -> 1)))
      LogData(1497281520423,00183e3d-b3af-47fe-bac8-af75fa875cf8,List(Info()),NeedLogMethod$.futAdd1,1,LogParams(Map(x -> 100)))
      LogData(1497281521422,f35a2d1e-3e4b-4af9-9158-350c3f6e5c7b,List(Info()),NeedLogMethod$.add1,2,LogParams(Map(x -> 2)))
      LogData(1497281521422,f35a2d1e-3e4b-4af9-9158-350c3f6e5c7b,List(Info()),NeedLogMethod$.add1,1,LogResult(3))
      LogData(1497281521424,00183e3d-b3af-47fe-bac8-af75fa875cf8,List(Info()),NeedLogMethod$.add1,2,LogParams(Map(x -> 101)))
      LogData(1497281520204,f35a2d1e-3e4b-4af9-9158-350c3f6e5c7b,List(Info()),NeedLogMethod$.futAdd1,2,LogResult(3))
      LogData(1497281521424,00183e3d-b3af-47fe-bac8-af75fa875cf8,List(Info()),NeedLogMethod$.add1,1,LogResult(102))
      LogData(1497281520423,00183e3d-b3af-47fe-bac8-af75fa875cf8,List(Info()),NeedLogMethod$.futAdd1,2,LogResult(102))
  */
object Example extends App with MetaLogger {
  NeedLogMethod.futAdd1(1)
  NeedLogMethod.futAdd1(100)

  Thread.currentThread().join()
}

object NeedLogMethod {

  //log a method which result is Future[ _ ] type
  @LogFuture(Info() :: Nil, printLogger)
  def futAdd1(x: Int) = {
    Future{
      Thread.sleep(1000)
      add2(x)
    }
  }

  //don't want log the method
  @NonLog
  def add2(x: Int) = {
    add1(x + 1)
  }

  //log a method
  @Log(Info() :: Nil, printLogger)
  def add1(x: Int) = {
    x + 1
  }
}