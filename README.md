# scala-lint-default-substitution
Scalafix Linting Rule to find default value substitution

## The problem
Java Wrapper class objects can be used interchangeably with Scala Values. The compiler is smart to automatically unbox them.
However since Java Wrapper classes exists as objects, they can be `null`. In this case the compiler doesn't cry, at runtime,
it takes default value of the Java Primitive type and substitutes that.

Consider a function `object.getSomeLongField` returns `java.lang.Long`
And this is passed to another function which takes a scala value,
`def innocentCode(value: Long): Unit` as `innocentCode(object.getSomeLongField)`
then it compiles, and at runtime, the `innocentCode` gets a `0` when `object.getSomeLongField` returns `null`

This happens because of the following problem, where a `null` gets converted to a `0` without any error at runtime.
```scala
scala> null.asInstanceOf[java.lang.Long].toLong
res0: Long = 0
```

This is a linting tool, which points out these converstions in code.

## Usage
Add the following plugin to `plugins.sbt`
```scala
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.0")
```

Start `sbt` shell.
```sh
$ sbt
```

Enable Scalafix and run the rule as follows:

```sbt
sbt> scalafixEnable
sbt> scalafix --rules=github:anish749/scala-lint-default-substitution/CheckJavaDefaultValueSubstitution
```
