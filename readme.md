# Meta Logger
logging method's calling stack even in multiple threads.

## Dependency
- [scalameta](https://github.com/scalameta/scalameta)

## [Example](https://github.com/LoranceChen/meta-logger/tree/master/src/test/scala/Example.scala)

## How does it works
meta annotation `@Log`/`@LogFuture`affect a method at compile pre-process time:
```
//before
@Log(Info() :: Nil, printLogger)
def add1(x: Int) = {
	x + 1
}
//after parsed, ===> simple as:
def add1(x: Int)(implicit metaLogContext: LogContext) = {
	println(s"current time - metaLogContext.logId.id - ... param: ${x}")
	val result = { x + 1 }
	println(s"current time - metaLogContext.logId.id - ... result: ${result}")
	result
}
```
