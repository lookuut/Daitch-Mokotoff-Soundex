package app.spark

/**
  * Read and parse csv file via SPARK, apply Daitch-Mokotoff Soundex for Surname and name
  * Encode BirthDate column and build ID for person unique by Surname, name and birth date
  * write to csv file with added ID column
  * @author Struchkov Lookuut
  */
import stringmetric.soundex.DaitchMokotoff
import java.nio.file.{Paths, Files}
import java.util.Calendar
import java.time.LocalDateTime

import org.apache.spark.sql.Row
import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

/**
 * Dataframe row class
 */
case class Person(FIO : String, BirthDate : String)


/**
 * Identity person by surname, name and birth day
 */
object PersonNameIdentifier {
	
	private val applicationName = "Person name identifier application"
	private val dateFormat = "yyyy-M-d-hh:mm:aa"
	private val defaultDestPath = "/tmp/person-identifier-result"
	/**
	 * Read source file, parse then apply unique identifier for each row
	 * Write to dest path with added ID column
	 */
	def run (sourceFile : String, destPath : String) {
		val conf = new SparkConf().
						setAppName(applicationName).
						setMaster("local[*]")
		val sparkContext = new SparkContext(conf)
		val sqlContext = new SQLContext(sparkContext)
		
		import sqlContext.implicits._

		val df = sqlContext.read.format("com.databricks.spark.csv").
				option("delimiter", "|").
				option("header", "true").
				option("inferSchema", "true").
				load(sourceFile)

		df.createOrReplaceTempView("persons")

		val addColumn = (FIO : String, birthDate : String) => {
			var codes = FIO.
				split("\\s").
		    	filter(x => !x.contains(".") && !DaitchMokotoff.isPatronymic(x)).
		    	map(DaitchMokotoff.compute(_).getOrElse("")).
		    	sorted
			birthDateTranscode(birthDate).getOrElse("") + codes.mkString("")
		}
		val addColumnUDF = udf(addColumn)
		val dataFrameWithID = df.withColumn("ID", addColumnUDF(df.col("FIO"), df.col("BirthDate")))	

		val selectedData = dataFrameWithID.select("ID", "FIO", "BirthDate")

		selectedData.coalesce(1).write
		.format("com.databricks.spark.csv")
		.option("header", "true")
		.option("delimiter", "|")
		.save(destPath + "/" + LocalDateTime.now())
	}

	//Current year last 2 signs
	private val currentYearTail = Calendar.getInstance().get(Calendar.YEAR).toString.slice(2,4).toInt
	private val ansiiLiteralStartPos = 65

	/**
	 * Transcode birthday
	 */
	def birthDateTranscode(birhtDate : String) : Option[String] = {
		if (birhtDate.isEmpty) {
		  return None
		}

		val format = new java.text.SimpleDateFormat("yyyy.MM.dd")
		var year : Option[String] = None
		var month : Option[String] = None
		var day : Option[String] = None

		def beforeMilleniumYearFormat:((String) => String) = (year) =>
		    (if (year.toInt <= currentYearTail) "200" else "199").slice(0, 4 - year.length) + year

		birhtDate.split("[^0-9]").filter(_.nonEmpty).filter(_.forall(_.isDigit)).zipWithIndex.foreach{
		  case(datePiece, index) => 
		    if (index < 3) {
		      if (datePiece.length > 2) {
		        year = Some(beforeMilleniumYearFormat(datePiece))
		      } else if (datePiece.toInt > 31) {
		        year = Some(beforeMilleniumYearFormat(datePiece))
		      } else if (datePiece.toInt > 12) {
		        day = Some(datePiece)
		      } else {
		        (index) match {
		          case 0 => year = Some(beforeMilleniumYearFormat(datePiece))
		          case 1 => month = Some(datePiece)
		          case 2 => if (day.isDefined) (month = Some(datePiece)) else (day = Some(datePiece))
		        }
		      }  
		    }
		    
		}

		if (!year.isDefined || !month.isDefined || !day.isDefined) {
		  return None
		}

		Some((year.get.slice(0,2).toInt + ansiiLiteralStartPos).toChar.toString +
		    (year.get.slice(2,3).toInt + ansiiLiteralStartPos).toChar.toString +
		    (year.get.slice(3,4).toInt + ansiiLiteralStartPos).toChar.toString +
		    (month.get.toInt + ansiiLiteralStartPos).toChar.toString +
		    (day.get.toInt + ansiiLiteralStartPos).toChar.toString )
	}

	def main(args: Array[String]) {

		if (args.length < 1) {
			throw new IllegalArgumentException("Need source file in params")
		}
		
		val sourceFile = args(0)
		val destPath = if (args.length < 2) defaultDestPath else args(1)

		if (!Files.exists(Paths.get(sourceFile))) {
			throw new IllegalArgumentException(s"Cant find source file $sourceFile")		
		}

		run(sourceFile, destPath)
	}
}
