# Meta Logger
logging call stack method's invoke chain.

## Feature
- automatic log method's parameters and result information by annotation.

## Dependency
- scalameta

## [Example](https://github.com/LoranceChen/meta-logger/tree/master/src/test/scala)

### need log methods
```
object NeedLogMethod {
  @Log
  def add1(x: Int) = {
    x + 1
  }

  @LogFuture
  def futAdd1(x: Int) = {
    Future{
      Thread.sleep(1000)
      add1(x)
    }
  }

}
```

### define implicits to automatic log
```
object Example extends App {
  implicit def logId = LogId(java.util.UUID.randomUUID().toString)
  implicit def callStack = CallStack()
  implicit def callStackOut = CallStackOut()

	// log 
  NeedLogMethod.futAdd1(1)

  Thread.currentThread().join()
}
```

## How does it works
meta annotation `@Log`/`@LogFuture`affect a method at compile pre-process time:
```
//before
@Log
def add1(x: Int) = {
	x + 1
}
//after parsed, ===> simple as:
def add1(x: Int)(implicit metaLogContext: LogContext) = {
	println(s"current time - metaLogContext.logId - ... param: ${x}")
	val result = { x + 1 }
	println(s"current time - metaLogContext.logId - ... result: ${result}")
	result
}
```
