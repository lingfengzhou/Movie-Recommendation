package com.csye7200.datacenter

import java.time.LocalDateTime

import org.apache.spark.sql._
import org.apache.spark.sql.functions._

object GetResults {
  val spark = SparkSession.builder
    .master(Config.getSparkMaster())
    .appName(Config.getSparkName())
    .getOrCreate()
  spark.sparkContext.setLogLevel(Config.getLogLevel())
  import spark.implicits._
  val df = spark.read.parquet("DataCenter/src/main/resources/result.parquet").cache()
  def getmovies(title: String, limit: Int): String = {

    //read result  select required rows

    val movie_df = df.select("primaryTitle", "genresSeq", "director","1actor","rms",
      "averageRating", "numVotes","syear","prediction","distanceFromCenter")

    val newNames = Seq("title", "genres", "director", "actor", "length", "score", "voteNumber",
      "year","prediction","distanceFromCenter")

    val dfRenamed = movie_df.toDF(newNames: _*)
    //get cluster
    val cluster = dfRenamed.select("title","prediction").filter($"title"===title).limit(1)("prediction")
    //sort with distance
    val related_df: Dataset[Row] = dfRenamed.filter($"prediction" === cluster).sort(asc("distanceFromCenter"))

    val all = related_df.filter($"prediction" === cluster)
      .drop("prediction","distanceFromCenter")
    val related = all.filter($"title"=!=title).limit(limit)
    val origin_df = all.filter($"title"===title)
    val res = origin_df.union(related).na.fill("").toJSON.collect()
    val origin_json = res(0)
    val related_json = res.drop(1).mkString("\"related\": [", "," , "]" )

    "{ \"origin\": ".concat(origin_json).concat(",").concat(related_json).concat(" }")

  }

}
