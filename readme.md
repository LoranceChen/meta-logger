# Meta Logger
logging call stack method's information for reactive system.

## Feature
- automatic log method's parameters and result information by annotation.

## Dependency
- scalameta (need Scala 2.11.11/2.12.x)
  - PS: 2.12.x not test
  
## Example

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
meta annotation `@Log`/`@LogFuture`affect a method at compile perprocess time:
```
//before
  @Log
  def add1(x: Int) = {
    x + 1
  }
//after parsed, ===> simple as:
	def add1(x: Int)(implicit macroLogId: LogId, macroCallStack: CallStack, macroCallStackOut: CallStackOut) = {
		println(s"current time - macroLogId - ... param: ${x}")
		val result = { x + 1 }
		println(s"current time - macroLogId - ... result: ${result}")
		result
	}
```
