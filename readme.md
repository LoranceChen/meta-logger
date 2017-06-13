# Meta Logger
logging method's calling stack even in multiple threads.

## Dependency
- [scalameta](https://github.com/scalameta/scalameta)

## Usage
### SBT config
```
scalacOptions += "-Xplugin-require:macroparadise"
resolvers += Resolver.bintrayIvyRepo("scalameta", "maven")
addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M9" cross CrossVersion.full)

libraryDependencies += "com.scalachan" %% "meta-logger" % "0.1"
```
### add annotation to your code
```
import lorance.metalog._

object Main extends App with MetaLogger {
	NeedLog.something
}

object NeedLog {

	@Log(Info() :: Nil, printLogger)
	def something() {
		"hello world"
	}
}
```

## [More Example](https://github.com/LoranceChen/meta-logger/tree/master/src/test/scala/Example.scala)

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

## TODO
- add a @VirtualLog for trait def (seems some bug in scalameta at this point)