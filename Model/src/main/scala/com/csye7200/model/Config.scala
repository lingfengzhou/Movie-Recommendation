package com.csye7200.model

import com.typesafe.config.{Config => typesafeConfig, ConfigFactory}

object Config {
  private val config = ConfigFactory.load()

  def getFrontendConfig(port: Int): typesafeConfig = {
    getConfig(port, "frontend")
  }

  def getBackendConfig(port: Int): typesafeConfig = {
    getConfig(port, "backend")
  }

  private def getConfig(port: Int, role: String) = {
    ConfigFactory.parseString(
      s"""
         |akka.remote.artery.canonical.port=$port
         |akka.cluster.roles = [$role]
       """.stripMargin)
      .withFallback(this.config)
  }
}
