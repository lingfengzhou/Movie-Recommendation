package com.csye7200.datacenter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.csye7200.datacenter.ClassificationActor.ActionPerformed

  trait JsonSupport extends SprayJsonSupport {
    // import the default encoders for primitive types (Int, String, Lists etc)
    import spray.json.DefaultJsonProtocol._

    implicit val movieJsonFormat = jsonFormat7(Movie)
    implicit val infoJsonFormat = jsonFormat2(Info)
    implicit val recMoviesJsonFormat = jsonFormat1(RecMovies)

    implicit val actionPerformedJsonFormat = jsonFormat1(ActionPerformed)

}
