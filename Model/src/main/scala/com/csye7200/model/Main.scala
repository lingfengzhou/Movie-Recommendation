package com.csye7200.model

import com.csye7200.model.backend.ModelBackend
import com.csye7200.model.frontend.ModelFrontend


object Main {

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      // test mode
      ModelFrontend(Seq("2551").toArray).start
      ModelFrontend(Seq("2552").toArray).start
      ModelFrontend(Seq("2553").toArray).start
      ModelBackend(Array.empty).start
      ModelBackend(Array.empty).start
      ModelBackend(Array.empty).start
      ModelBackend(Array.empty).start
      ModelBackend(Array.empty).start
      ModelBackend(Array.empty).start
    } else {
      val mode = args(0)
      val port = args(1)
      if (mode == "frontend") ModelFrontend(Seq(port).toArray).start
      else if (mode == "backend") ModelBackend(Seq(port).toArray).start
    }
  }
}
