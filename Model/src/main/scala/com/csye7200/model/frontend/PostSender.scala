package com.csye7200.model.frontend

import akka.actor._
import com.csye7200.model._

class PostSender extends Actor with ActorLogging {
  private var backends = IndexedSeq.empty[ActorRef]
  private var jobCounter = 0

  def receive = {
    case _: Movie if backends.isEmpty => sender() ! JobFailed("No available backend, try again later.")
    case job: Movie =>
      jobCounter += 1
      backends(jobCounter % backends.size) forward job
    case BackendRegistration if !backends.contains(sender()) =>
      context watch sender()
      backends = backends :+ sender()
      log.info(s"Current size of backends: ${backends.size}")
    case Terminated(a) =>
      backends = backends.filterNot(_ == a)
  }
}
