package com.csye7200.datacenter

import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object GetResults {

  def getmovies(title: String, limit: Int): String = {
    val spark = SparkSession.builder
      .master("local")
      .appName("getmovies")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    import spark.implicits._
    //read result
    val df = spark.read.parquet("DataCenter/src/main/resources/result.parquet")
    //get cluster
    val cluster = df.filter(row => row.get(0)==title).select("prediction").head().get(0)
    //sort with distance
    val related_df = df.filter(s"prediction == $cluster").sort(asc("distanceFromCenter"))
    //select required rows
    val movie_df = related_df.select("primaryTitle", "genresSeq", "director","1actor","rms",
        "averageRating", "numVotes","syear")

    val newNames = Seq("title", "genres", "director", "actor", "length", "score", "voteNumber", "year")
    val dfRenamed = movie_df.toDF(newNames: _*)
    dfRenamed.printSchema
    val related = dfRenamed.limit(limit).toJSON.collect.mkString("\"related\": [", "," , "]" )
    val origin = dfRenamed.filter(row => row.get(0)==title).toJSON.collect()(0)

    "{ \"origin\": ".concat(origin).concat(",").concat(related).concat(" }")

  }

}
