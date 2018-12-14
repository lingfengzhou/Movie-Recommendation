package com.csye7200.datacenter

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.evaluation.ClusteringEvaluator
import org.apache.spark.ml.feature.{CountVectorizerModel, OneHotEncoder, StringIndexer, VectorAssembler}
import org.apache.spark.ml.linalg.{Vector, Vectors}
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{DoubleType, IntegerType}

object DoKmeans {
    val spark = SparkSession.builder
      .master(Config.getSparkMaster())
      .appName(Config.getSparkName())
      .getOrCreate()
    spark.sparkContext.setLogLevel(Config.getLogLevel())
    import spark.implicits._

    def train(): DataFrame = {


        val file_basic = "DataCenter/src/main/resources/title.basics.tsv"
        val df_basic_raw = spark.read.format("csv")
          .option("header", "true")
          .option("inferSchema", "true")
          .option("delimiter", "\t")
          .load(file_basic)
          .cache()
        df_basic_raw.printSchema()
        val df_basic = df_basic_raw.filter("titleType == 'movie'")
          .filter("runtimeMinutes != '\\\\\\N' AND genres != '\\\\\\N'")
          .drop("endYear", "isAdult")
        //    val genre = df_basic.select("genres").map(attributes => attributes.toString().split(",").head)


        val file_names = "DataCenter/src/main/resources/name.basics.tsv"
        val df_names_raw = spark.read.format("csv")
          .option("header","true")
          .option("inferSchema", "true")
          .option("delimiter","\t")
          .load(file_names)
          .cache()
        val df_names = df_names_raw.select("nconst","primaryName")

        val file_principals = "DataCenter/src/main/resources/title.principals.tsv"
        val df_principals_raw = spark.read.format("csv")
          .option("header","true")
          .option("inferSchema", "true")
          .option("delimiter","\t")
            .load(file_principals)
            .cache()
        df_principals_raw.printSchema()
        val df_principals = df_principals_raw.filter("category == 'actor' OR category == 'actress'").withColumn("role",lit("actor"))
          .drop("job","characters","category")
        df_principals.show(10)
        //actor join with name
        val df_p2 = df_principals.join(df_names,"nconst")
        df_p2.printSchema()
        df_p2.show(10)
        df_p2.write.mode(SaveMode.Overwrite).parquet("DataCenter/src/main/resources/actor&name.parquet")

        //actor&name
        val df_p2_saved = spark.read.parquet("DataCenter/src/main/resources/actor&name.parquet")
        val df_a_name = df_p2_saved.withColumn("OrderRole",concat($"ordering",$"role"))
          .groupBy("tconst")
          .pivot("OrderRole")
          .agg(expr("coalesce(first(primaryName))"))
        df_a_name.printSchema()
        df_a_name.show(10)

        //director&name
        val file_crew = "DataCenter/src/main/resources/title.crew.tsv"
        val df_crew_raw = spark.read.format("csv")
          .option("header","true")
          .option("inferSchema", "true")
          .option("delimiter","\t")
          .load(file_crew)
          .cache()
        df_crew_raw.printSchema()
        val df_crew = df_crew_raw.withColumn("nconst",split($"directors","\\,").getItem(0))
          .drop("directors","writers")
        val df_d_name = df_crew.join(df_names, "nconst").withColumnRenamed("primaryName","director")
        df_d_name.printSchema()
        df_d_name.show(10)



        //ratings
        val file_ratings = "DataCenter/src/main/resources/title.ratings.tsv"
        val df_ratings_raw = spark.read.format("csv")
          .option("header", "true")
          .option("inferSchema", "true")
          .option("delimiter", "\t")
          .load(file_ratings)
          .cache()
        df_ratings_raw.printSchema()
        val df_ratings = df_ratings_raw.na.drop().cache()

        //join basic and ratings
        df_basic.join(df_ratings, "tconst").write.mode(SaveMode.Overwrite).parquet("DataCenter/src/main/resources/join.parquet")



        //join rating&moive
        val df = spark.read.parquet("DataCenter/src/main/resources/join.parquet")
        //join actor &director &movie
        val df_2 = df.join(df_a_name,"tconst").join(df_d_name,"tconst")
        df_2.printSchema()
        df_2.write.mode(SaveMode.Overwrite).parquet("DataCenter/src/main/resources/all-together.parquet")
        df_2.show(10)

        val df_all = spark.read.parquet("DataCenter/src/main/resources/all-together.parquet")
          .select("primaryTitle","startYear","runtimeMinutes","genres","averageRating","numVotes","1actor",
          "2actor","3actor","director")

        //val df2 = df.withColumn("_tmp",split($"genres","\\,"))
        //.select($"*" +: (0 until 3).map(i => col("_tmp").getItem(i).as(s"genre$i")): _*)
        val df2 = df_all.withColumn("genresSeq",split($"genres","\\,"))
        val tags = df2.flatMap(r => r.getAs[Seq[String]]("genresSeq")).distinct().collect().sortWith(_<_)
        val cvmData = new CountVectorizerModel(tags).setInputCol("genresSeq").setOutputCol("sparseGenres").transform(df2)
        val asDense = udf((v: Vector) => v.toDense)
        val df_final = cvmData
          .withColumn("syear",$"startYear".cast(IntegerType)).withColumn("rms",$"runtimeMinutes".cast(DoubleType))
        df_final.printSchema()
        df_final.show(10)


        //using normal flow(pro: get clusterCenter)
        val assembler = new VectorAssembler().setInputCols(Array("sparseGenres","rms","syear"))
                                             .setOutputCol("features").setHandleInvalid("skip").transform(df_final)
        val kmeans = new KMeans().setK(108).setFeaturesCol("features").setPredictionCol("prediction")
        val kMeansPredictionModel = kmeans.fit(assembler)
        val predictionResult = kMeansPredictionModel.transform(assembler)
        // UDF that calculates for each point distance from each cluster center
        val distFromCenter = udf((features: Vector, c: Int) => Vectors.sqdist(features, kMeansPredictionModel.clusterCenters(c)))

        val distancesDF = predictionResult.withColumn("distanceFromCenter", distFromCenter($"features", $"prediction"))
        //using pipeline
//        val assembler = new VectorAssembler().setInputCols(Array("sparseGenres","rms","syear"))
//          .setOutputCol("features").setHandleInvalid("skip")
//        val kmeans = new KMeans().setK(108).setFeaturesCol("features").setPredictionCol("prediction")
//        val pipeline = new Pipeline().setStages(Array(assembler, kmeans))
//        val kMeansPredictionModel = pipeline.fit(df_final)
//        val predictionResult = kMeansPredictionModel.transform(df_final)
//        predictionResult.show(100)

//        val evaluator = new ClusteringEvaluator()
//        val silhouette = evaluator.evaluate(predictionResult)
//        println(s"silhouette: "+silhouette)
        distancesDF.show(10)
        distancesDF.write.mode(SaveMode.Overwrite).parquet("DataCenter/src/main/resources/result.parquet")
        distancesDF
    }
}
