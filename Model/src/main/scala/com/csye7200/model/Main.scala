package com.csye7200.model

import com.csye7200.model.backend.ModelBackend
import com.csye7200.model.frontend.ModelFrontend


object Main{

  def main(args: Array[String]): Unit = {
    ModelFrontend(Seq("2551").toArray).start
    ModelBackend(Array.empty).start
  }
}
