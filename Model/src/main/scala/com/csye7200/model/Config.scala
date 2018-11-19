package com.csye7200.model

import com.typesafe.config.{Config, ConfigFactory}

object Config {
  private val config = ConfigFactory.load()

  def getFrontendConfig(args: Array[String]): Config = {
    getConfig(args, "frontend")
  }

  def getBackendConfig(args: Array[String]): Config = {
    getConfig(args, "backend")
  }

  private def getConfig(args: Array[String], role: String) = {
    val port = if (args.isEmpty) "0" else args(0)
    ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port=$port
         |akka.cluster.roles = [$role]
       """.stripMargin)
      .withFallback(this.config)
  }
}
