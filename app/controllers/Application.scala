package controllers

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.actor.Props
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._

import scala.concurrent.Promise

class Application extends Controller {

  def index = Action.async {
    val host = "localhost"
    val port = 9021
    val promise = Promise[String]()
    val props = Props(classOf[TcpClient],
      new InetSocketAddress(host, port),
      s"GET / HTTP/1.1\r\nHost: ${host}\r\nAccept: */*\r\n\r\n", promise)

    //Discover the actor
    val sys = ActorSystem.create("MyActorSystem")
    val tcpActor = sys.actorOf(props)

    //Convert the promise to Future[Result]
    promise.future map { data =>
      tcpActor ! "close"
      Ok(data)
    }
  }

}
