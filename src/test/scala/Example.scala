
import lorance.metalog._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * log format:
  *   1. meta-calling-type   2. time     3. invoke-chain-uuid    4. stack-call   5. class-path-and-method-name   6. load: params/return-result
  * output:
  *
  * LogData(1497276559579,8096b808-a687-4766-87a2-991fae4167c7,List(Info()),NeedLogMethod$.futAdd1,1,LogParams(Map(x -> 1)))
    LogData(1497276560814,8096b808-a687-4766-87a2-991fae4167c7,List(Info()),NeedLogMethod$.add1,2,LogParams(Map(x -> 1)))
    LogData(1497276560814,8096b808-a687-4766-87a2-991fae4167c7,List(Info()),NeedLogMethod$.add1,1,LogResult(2))
    LogData(1497276559579,8096b808-a687-4766-87a2-991fae4167c7,List(Info()),NeedLogMethod$.futAdd1,2,LogResult(2))
  */
object Example extends App with MetaLogger {
  NeedLogMethod.futAdd1(1)

  Thread.currentThread().join()
}

object NeedLogMethod {

  @Log(Info() :: Nil, printLogger)
  def add1(x: Int) = {
    x + 1
  }

  @LogFuture(Info() :: Nil, printLogger)
  def futAdd1(x: Int) = {
    Future{
      Thread.sleep(1000)
      add1(x)
    }
  }

}