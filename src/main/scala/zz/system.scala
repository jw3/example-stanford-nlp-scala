package zz

import zio._

object system {
  type ZimZamSystem = Has[ZimZamSystem.Service]
  object ZimZamSystem {
    trait Service {}

    def make(): ZLayer.NoDeps[Nothing, ZimZamSystem] = ZLayer.succeed(
      new ZimZamSystem.Service {}
    )
  }

  def boot(): URIO[ZimZamSystem, (Queue[String], Queue[String], Fiber[_, _])] = {
    val inM = Queue.bounded[String](10)
    val outM = Queue.bounded[String](100)

    for {
      in <- inM
      out <- outM
      loop = for {
        r <- in.take
        _ <- out.offer(r)
      } yield {
        println(s"Processed request: $r")
        ()
      }
      fiber <- loop.forever.fork
    } yield (in, out, fiber)
  }
}
