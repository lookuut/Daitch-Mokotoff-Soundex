package stringmetric.soundex

import util.control.Breaks._
import scala.util.matching.Regex

/**
  * Phonetic algorithm Daitch-Mokotoff Soundex realization for unify Slavonic surnames and names
  * @author Struchkov Lookuut
  */
object DaitchMokotoff {
  
  //Surname|name regex
  private val regName : Regex = "[a-zA-Z]+".r
  
  //is char a vowel test function
  private val isVowel: ((Char) => Boolean) = (sign) =>
    Array('A','I', 'E', 'J', 'O', 'Y', 'U').indexOf(sign) != -1

  //Slavonic patronymic ends
  private val patronymicEnds = Array("ich", "vna", "ish", "ishna", "ichna", "uch")

  private val codeMaxLength = 6

  /**
   * Transode string to Int by Daitch-Mokotoff rules
   */
  private val transcode: ((Int, Char, String) => Option[Int]) = (pos, prevSign, part) =>
    if (part.length == 0) None
    else {

      val shift : ((Int, Char, Array[Int]) => Option[Int]) = (pos, prevSign, values) =>
        if (pos == 0) Some(values(0)) else if (isVowel(prevSign)) Some(values(1)) else Some(values(2))

      (part) match {
        case "AI" | "AJ" | "AY" | "EI" | "EY" | "EJ" | "OI"| "OJ" | "OY" | "UI" | "UJ" | "UY" =>
          shift(pos, prevSign, Array(0,1, -1))
        case "AU" =>
          shift(pos, prevSign, Array(0,7, -1))
        case "IA" | "IE"| "IO" | "IU" =>
          shift(pos, prevSign, Array(1,-1, -1))
        case "EU" => shift(pos, prevSign, Array(1, 1, -1))
        case "A" | "UE" | "E" | "I" | "O" | "U" | "Y" =>
          shift(pos, prevSign, Array(0, -1,-1))
        case "J" => shift(pos, prevSign, Array(1, 1, 1))
        case "SCHTSCH" | "SCHTSH" | "SCHTCH" |  "SHTCH" |
             "SHCH" | "SHTSH" | "STCH" | "STSCH" | "STRZ" |
             "STRS" | "STSH" | "SZCZ" | "SZCS" =>
          shift(pos, prevSign, Array(2,4,4))
        case "SHT" | "SCHT" | "SCHD" | "ST" | "SZT" | "SHD" | "SZD" | "SD" =>
          shift(pos, prevSign, Array(2, 43, 43))
        case "CSZ" | "CZS" | "CS" | "CZ" | "DRZ" | "DRS" |
          "DSH" | "DS" | "DZH" | "DZS" | "DZ" |
          "TRZ" | "TRS" | "TRCH" | "TSH" | "TTSZ" | "TTZ" |
          "TZS" | "TSZ" | "SZ" | "TTCH" |"TCH" | "TTSCH" |
          "ZSCH" | "ZHSH" | "SCH" | "SH" | "TTS" | "TC" |
          "TS" | "TZ" | "ZH" |"ZS" =>
          shift(pos, prevSign, Array(4,4,4))
        case "SC" => shift(pos, prevSign, Array(2, 4, 4))
        case "DT"|  "D" | "TH" | "T" => shift(pos, prevSign, Array(3,3,3))
        case "CHS" | "KS" | "X" => shift(pos, prevSign, Array(5, 54, 54))
        case "S" | "Z" => shift(pos, prevSign, Array(4, 4, 4))
        case "CH" | "CK" | "C" | "G" | "KH" | "K" | "Q" => shift(pos, prevSign, Array(5, 5, 5))
        case "MN" | "NM" => shift(pos, prevSign, Array(-1, 66, 66))
        case "M" | "N" => shift(pos, prevSign, Array(6, 6, 6))
        case "FB" | "B" | "PH" | "PF" | "F" | "P" | "V" | "W" => shift(pos, prevSign, Array(7, 7, 7))
        case "H" => shift(pos, prevSign, Array(5, 5, -1))
        case "L" => shift(pos, prevSign, Array(8, 8, 8))
        case "R" => shift(pos, prevSign, Array(9, 9, 9))
        case "YA" | "JA" => shift(pos, prevSign, Array(7,0,0))//except case
        case _ => None
      }
    }

  
  /**
   * is text patronomic test function
   */
  def isPatronymic(text : String) : Boolean = {       
      val patronymic = regName.findFirstIn(text.toLowerCase)
      patronymicEnds.count(end => text.indexOf(end) > 1 && text.indexOf(end) == (text.length - end.length)) > 0
  }

  /**
   * Compute digit soundex for text
   */
  def compute(toEncodeWord : String) : Option[String] = {

    val word = regName.findFirstIn(toEncodeWord.toUpperCase())

    if (!word.isDefined || word.get.length <= 0) {
      return None
    }

    var output : String = ""
    var pos : Int = 0
    var lastResult : Option[Int] = None
    var prevResult : Option[String] = None

    for {j <- 0 to word.get.length if pos < word.get.length} {
      var seek = 0
      breakable {
        for {
          i <- 1 to 7
        } {
          val prevSign = if (pos > 0) word.get(pos -1) else word.get(0)
          var result = transcode(pos, prevSign, word.get.substring(pos, pos + i + seek))
          if (result.isDefined) {
            lastResult = result
          }

          if (!result.isDefined || i + pos >= word.get.length)  {
            val res : String = (if (lastResult.getOrElse(-1) == -1) "" else lastResult.get.toString)
            if (res != prevResult.getOrElse("")) {
              output += res
              prevResult = Some(res)
            }

            if (i > 1 && i + pos >= word.get.length && !result.isDefined) {
              val res = transcode(pos, word.get(word.get.length - 2), word.get.substring(word.get.length - 1, word.get.length))
              val lastRes = (if (res.getOrElse(-1) == -1) "" else res.get.toString)
              if (lastRes != prevResult.getOrElse("")) {
                output += lastRes
              }
            }

            pos += (if (i + pos >= word.get.length) i else (i -1))
            seek = -1
            break
          }
        }
      }
    }

    output += "0" * (if (output.length < codeMaxLength) (codeMaxLength - output.length) else 0)
    Some(output)
  }
  
  /**
   * Compute soundex for sentence
   */
  def transcodeSentence(sentence : String) : Array[String] = {
    val trimedUpperWordArray = sentence.trim().split(" ")
    var encodedSentence : Array[String] = Array()
    trimedUpperWordArray.foreach(encodeWord  => encodedSentence :+= compute(encodeWord).getOrElse(""))
    encodedSentence
  }
}
