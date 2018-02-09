#!/bin/bash
sbt package
sh "${SPARK_HOME}"/bin/spark-submit --class "app.spark.PersonNameIdentifier"  target/scala-2.11/ru-translit-metaphone_2.11-0.1.0.jar /resource/persons.csv
