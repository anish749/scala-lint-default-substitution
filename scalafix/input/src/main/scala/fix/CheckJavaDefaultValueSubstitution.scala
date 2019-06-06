/*
rule = CheckJavaDefaultValueSubstitution
*/
package fix

object CheckJavaDefaultValueSubstitution {
  def main(args: Array[String]): Unit = {

    def innocentCode(a: Long, b: Boolean, c: Option[Int], i: Int, defaultParam: Long = -1): Unit = {
      println(s"a = $a") // 0
      println(s"b = $b") // false
    }

    def evilIntCode: java.lang.Integer = null

    def evilLongCode: java.lang.Long = null

    def evilBooleanCode: java.lang.Boolean = null

    def goodCitizenBoolean: Boolean = false

    innocentCode(evilLongCode,/* assert: CheckJavaDefaultValueSubstitution
                 ^^^^^^^^^^^^
The parameters for innocentCode accepts scala values, but the argument is a
Java Wrapper class. Wrapper objects can be null, which leads to the innocentCode
getting default wrapper class values (0 / false, etc) at runtime.
    */
      evilBooleanCode,/* assert: CheckJavaDefaultValueSubstitution
      ^^^^^^^^^^^^^^^
The parameters for innocentCode accepts scala values, but the argument is a
Java Wrapper class. Wrapper objects can be null, which leads to the innocentCode
getting default wrapper class values (0 / false, etc) at runtime.
    */
      Some(1),
      evilIntCode/* assert: CheckJavaDefaultValueSubstitution
      ^^^^^^^^^^^
The parameters for innocentCode accepts scala values, but the argument is a
Java Wrapper class. Wrapper objects can be null, which leads to the innocentCode
getting default wrapper class values (0 / false, etc) at runtime.
    */
    )

    innocentCode(evilLongCode,/* assert: CheckJavaDefaultValueSubstitution
                 ^^^^^^^^^^^^
The parameters for innocentCode accepts scala values, but the argument is a
Java Wrapper class. Wrapper objects can be null, which leads to the innocentCode
getting default wrapper class values (0 / false, etc) at runtime.
    */
      goodCitizenBoolean,
      None,
      evilIntCode,/* assert: CheckJavaDefaultValueSubstitution
      ^^^^^^^^^^^
The parameters for innocentCode accepts scala values, but the argument is a
Java Wrapper class. Wrapper objects can be null, which leads to the innocentCode
getting default wrapper class values (0 / false, etc) at runtime.
    */
      evilLongCode/* assert: CheckJavaDefaultValueSubstitution
      ^^^^^^^^^^^^
The parameters for innocentCode accepts scala values, but the argument is a
Java Wrapper class. Wrapper objects can be null, which leads to the innocentCode
getting default wrapper class values (0 / false, etc) at runtime.
    */
    )
  }
}
