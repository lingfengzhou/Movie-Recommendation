package com.csye7200.model

import com.csye7200.model.backend.ModelBackend
import com.csye7200.model.frontend.ModelFrontend


object Main {

  def main(args: Array[String]): Unit = {
    val parser = new scopt.OptionParser[ArgsConfig]("MovieRecommendation-Model") {
      head("MovieRecommendation-Model")

      opt[String]("mode")
        .action((x, c) => c.copy(mode = x))
        .validate({
          case "frontend" | "backend" => success
          case wrongStr => failure(s""""Mode must be "frontend" or "backend", but given $wrongStr""")
        })
        .text("Choose which mode to launch")

      opt[Int]('n', "number")
        .action((x, c) => c.copy(number = x))
        .validate(x => {
          if(x > 0) success
          else failure("number must > 0.")
        })
        .text("Choose how many instances to start.")

      opt[Int]('p', "port")
        .action((x, c) => c.copy(port = x))
        .text("Choose which port to listen on for backend. 0 means randomly choose.")
    }

    parser.parse(args, ArgsConfig()) match {
      case Some(parsedArgs) =>
        parsedArgs.mode match {
          case "frontend" =>
            for(i <- 0 until Math.max(1, parsedArgs.number)) ModelFrontend(i + 2551).start
          case "backend" => parsedArgs.port match {
            case port if port > 0 =>
              for(i <- 0 until Math.max(1, parsedArgs.number)) ModelBackend(i + parsedArgs.port).start
            case _ =>
              for(_ <- 0 until Math.max(1, parsedArgs.number)) ModelBackend(0).start
          }
          case _ => defaultStart
        }
      case None =>
    }
  }

  def defaultStart = {
    for (i <- 0 until 2) ModelFrontend(2551 + i).start
    for(_ <- 0 until 2) ModelBackend(0).start
  }
}
