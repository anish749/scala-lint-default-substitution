package fix

import scalafix.v1._

import scala.meta._

/**
 * Diagnostic Report
 */
case class JavaDefaultValueSubstitution(funcName: String, position: Position) extends Diagnostic {
  override def message: String =
    s"""
       |The parameters for $funcName accepts scala values, but the argument is a
       |Java Wrapper class. Wrapper objects can be null, which leads to the $funcName
       |getting default wrapper class values (0 / false, etc) at runtime.
     """.stripMargin.trim
}

/**
 * Actual Scalafix Semantic Rule to Check for default value substitutions.
 */
class CheckJavaDefaultValueSubstitution extends SemanticRule("CheckJavaDefaultValueSubstitution") {

  /** Scala Related Constants */
  private val SymbolOwnerScala = Symbol("scala/")
  private val ScalaValueDisplayNames = Set(
    "Byte", // TODO add test cases for all.
    "Short",
    "Int",
    "Long",
    "Float",
    "Double",
    "Character",
    "Boolean"
  )

  /** Java Related Constants */
  private val SymbolOwnerJava = Symbol("java/lang/")
  private val JavaWrapperClassDisplayNames = Set(
    "Byte", // TODO add test cases for all.
    "Short",
    "Integer",
    "Long",
    "Float",
    "Double",
    "Character",
    "Boolean"
  )


  override def fix(implicit doc: SemanticDocument): Patch = {
    //    The following helps a lot while developing.
    //    println("Tree.syntax: " + doc.tree.syntax)
    //    println("Tree.structure: " + doc.tree.structure)
    //    println("Tree.structureLabeled: " + doc.tree.structureLabeled)

    doc.tree.collect {
      case Term.Apply(innocentFunc, args) =>

        val scalaValueParams = innocentFunc
          .symbol
          .info
          .map(_.signature)
          .collect {
            // Param types in the function that are Scala
            case method: MethodSignature
              if method.parameterLists.nonEmpty =>
              method
                .parameterLists
                .head
                .map(_.signature)
                .zipWithIndex
                .collect {
                  case (ValueSignature(TypeRef(_, sym, _)), i)
                    if sym.owner == SymbolOwnerScala &&
                      ScalaValueDisplayNames.contains(sym.displayName) =>
                    (i, sym)
                }
          }.getOrElse(List())

        // Arguments passed to the function are Java Wrapper Classes
        val javaWrapperArgs = args
          .zipWithIndex
          .collect {
            case (term, i) =>
              term.symbol.info.map(_.signature).collect {
                case MethodSignature(_, _, TypeRef(_, sym, _))
                  if sym.owner == SymbolOwnerJava &&
                    JavaWrapperClassDisplayNames.contains(sym.displayName) =>
                  (i, (sym, term.pos))
              }
          }.flatten

        // Find matches by parameter position and create linting errors
        innerJoin(scalaValueParams, javaWrapperArgs)
          .map {
            case (i, (scalaVal, (javaWrap, pos))) =>
              JavaDefaultValueSubstitution(funcName = innocentFunc.toString(), pos)
          }
          .map(Patch.lint)
          .asPatch
    }.asPatch
  }


  /**
   * Utility to join two pair Iterables based on matching keys
   */
  private def innerJoin[K, V, W](left: Iterable[(K, V)],
                                 right: Iterable[(K, W)]): Iterable[(K, (V, W))] = {
    for {
      (k, v1) <- left
      (`k`, v2) <- right
    } yield (k, (v1, v2))
  }
}
