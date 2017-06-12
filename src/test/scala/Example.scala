
import lorance.metalog._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * log format:
  *   1. meta-calling-type   2. time     3. invoke-chain-uuid    4. stack-call   5. class-path-and-method-name   6. load: params/return-result
  * output:
  *
  * [MetaLogFuture.Input] - Wed Apr 26 10:34:52 CST 2017 - 56a74fdd-5330-4937-a881-c58714c505a5 - 1 - NeedLogMethod$.futAdd1 - Map(x -> 1)
    [MetaLog.Input] - Wed Apr 26 10:34:53 CST 2017 - 56a74fdd-5330-4937-a881-c58714c505a5 - 2 - NeedLogMethod$.add1 - Map(x -> 1)
    [MetaLog.Output] - Wed Apr 26 10:34:53 CST 2017 - 56a74fdd-5330-4937-a881-c58714c505a5 - 1 - NeedLogMethod$.add1 - 2
    [MetaLogFuture.Output] - Wed Apr 26 10:34:52 CST 2017 - 56a74fdd-5330-4937-a881-c58714c505a5 - 2 - NeedLogMethod$.futAdd1 - 2
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