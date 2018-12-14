package com.csye7200.datacenter

import com.typesafe.config.ConfigFactory

object Config {

  val config = ConfigFactory.load()

  def getSparkMaster(): String ={
    config.getString("spark.mode")
  }
  def getSparkName():String ={
    config.getString("spark.appName")
  }

  def getLogLevel():String ={
    config.getString("spark.logLevel")
  }

  def getSeverInterface():String ={
    config.getString("sever.interface")
  }
  def getSeverPort():Int ={
    config.getInt("sever.port")
  }

}
