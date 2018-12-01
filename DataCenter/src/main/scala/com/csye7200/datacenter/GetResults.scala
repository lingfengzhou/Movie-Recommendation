package com.csye7200.datacenter

import org.apache.spark.sql._

import scala.reflect.ClassTag

object GetResults {

  def getmovies(title: String, limit: Int): String = {
    val spark = SparkSession.builder
      .master("local")
      .appName("getmovies")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")
    import spark.implicits._
    implicit val classtag : ClassTag[Movie]= ClassTag(classOf[Movie])

    val df = spark.read.parquet("DataCenter/src/main/resources/result.parquet")
//    val results = df.filter($)
    val movie_df = df.select("primaryTitle", "genres", "rms",
      "syear", "director", "averageRating", "numVotes")
//    val movieEncoder: Encoder[Movie] = Encoders.bean(classOf[Movie])
//    val movieEncoder = Encoders.kryo[Movie]
    //val re: Dataset[Movie] = movie_df.as[Movie](movieEncoder)
//    movie_df.show(10)
//    movie_df.limit(1)
    val m = movie_df.limit(1).toJSON.collect()(0)
    println(m)
    m
  }

}
