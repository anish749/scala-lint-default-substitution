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

    innocentCode(evilLongCode,
      evilBooleanCode,
      Some(1),
      evilIntCode
    )

    innocentCode(evilLongCode,
      goodCitizenBoolean,
      None,
      evilIntCode,
      evilLongCode
    )
  }
}
