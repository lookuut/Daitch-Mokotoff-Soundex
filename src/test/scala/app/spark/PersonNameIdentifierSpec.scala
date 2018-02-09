package app.spark

object PersonNameIdentifierSpec extends org.specs2.mutable.Specification {

  "PersonNameIdentifier birthDateTranscode" should {
    
    "return None with empty argument" in {
      PersonNameIdentifier.birthDateTranscode("").isDefined must beFalse
    }

    "return None with wrong date" in {
      PersonNameIdentifier.birthDateTranscode("123").isDefined must beFalse
    }

    "return Some with phonetic argument" in {
      
      PersonNameIdentifier.birthDateTranscode("2001.04.01").get mustEqual PersonNameIdentifier.birthDateTranscode("01.04.01").get
      PersonNameIdentifier.birthDateTranscode("001.04.01").get mustEqual PersonNameIdentifier.birthDateTranscode("01.04.01").get
      PersonNameIdentifier.birthDateTranscode("001.4.1").get mustEqual PersonNameIdentifier.birthDateTranscode("01.04.01").get
      PersonNameIdentifier.birthDateTranscode("2001.4.1").get mustEqual PersonNameIdentifier.birthDateTranscode("01.04.01").get
      PersonNameIdentifier.birthDateTranscode("37.4.1").get mustEqual PersonNameIdentifier.birthDateTranscode("1937.04.01").get
      PersonNameIdentifier.birthDateTranscode("1937.04.01").get mustEqual PersonNameIdentifier.birthDateTranscode("1937/04/01").get
      
      PersonNameIdentifier.birthDateTranscode("98/1/4/01").get mustEqual PersonNameIdentifier.birthDateTranscode("98.1.4").get
      PersonNameIdentifier.birthDateTranscode("01|d|da|01|1").get mustEqual PersonNameIdentifier.birthDateTranscode("01.01.1").get
      PersonNameIdentifier.birthDateTranscode("28.01.98").get mustEqual PersonNameIdentifier.birthDateTranscode("1998.01.28").get
    }
  }
}



