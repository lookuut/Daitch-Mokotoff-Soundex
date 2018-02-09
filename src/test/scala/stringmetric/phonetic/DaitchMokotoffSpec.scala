package stringmetric.phonetic

import stringmetric.soundex.DaitchMokotoff

object DaitchMokotoffSpec extends org.specs2.mutable.Specification {

  "DaitchMokotoff compute()" should {
    "return None with empty argument" in {
      DaitchMokotoff.compute("").isDefined must beFalse
    }

    "return None with non-phonetic argument" in {
      DaitchMokotoff.compute("123").isDefined must beFalse
    }

    "return Some with phonetic argument" in {
      DaitchMokotoff.compute("Yana").get mustEqual DaitchMokotoff.compute("Jana").get

      DaitchMokotoff.compute("Sasha").get mustEqual DaitchMokotoff.compute("Sasha").get

      DaitchMokotoff.compute("Ksenia").get mustEqual DaitchMokotoff.compute("Xenia").get

      DaitchMokotoff.compute("Nadya").get mustEqual DaitchMokotoff.compute("Nadua").get

      DaitchMokotoff.compute("Sveta").get mustEqual DaitchMokotoff.compute("Sweta").get

      DaitchMokotoff.compute("Nadezhda").get mustEqual DaitchMokotoff.compute("Nadezda").get
    }

    "isPatronymic test" in {
      
      DaitchMokotoff.isPatronymic("Struchkov") must beFalse
      DaitchMokotoff.isPatronymic("Fedorovich") must beTrue
      DaitchMokotoff.isPatronymic("Fedorovna") must beTrue
      DaitchMokotoff.isPatronymic("Popova") must beFalse
      DaitchMokotoff.isPatronymic("Petrova") must beFalse
      DaitchMokotoff.isPatronymic("Petrovva") must beFalse
      DaitchMokotoff.isPatronymic("Ya") must beFalse
      DaitchMokotoff.isPatronymic("vish") must beFalse
      DaitchMokotoff.isPatronymic("Andreevich") must beTrue
      DaitchMokotoff.isPatronymic("Pavlovna") must beTrue
    }
  }

}



