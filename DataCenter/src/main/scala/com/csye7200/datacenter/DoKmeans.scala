package com.csye7200.datacenter

import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession

object DoKmeans {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
        .master("local")
        .appName("Kmeans")
        .getOrCreate()
    println("")
    println("Define the input data")
    println("=====================")
    println("")

    val file = "DataCenter/src/main/resources/movie_metadata_5000.csv"
    val df_raw = spark.read.format("csv")
      .option("header","true")
      .option("inferSchema", "true")
      .load(file)
      .cache()
    df_raw.printSchema()
    val df = df_raw.na.drop().cache()
    println("")
    println("Create feature vectors and generate a K-Means model")
    println("===================================================")
    println("")

    val vectorAssembler = new VectorAssembler()
      .setInputCols(Array("imdb_score","aspect_ratio"))
      .setOutputCol("features")
    val output = vectorAssembler.transform(df)
    output.printSchema()
    val kmeans = new KMeans().setPredictionCol("prediction").setK(3)
    val model = kmeans.fit(output)

    println("")
    println("Calculate cluster centers")
    println("=========================")
    println("")

    val predicted = model.transform(output)
    predicted.show(5)

  }



}
