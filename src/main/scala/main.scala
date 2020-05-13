import rdf.implicits._
import zio.{Runtime, ZIO}
import zz.parse.body.MessageBody.Report
import zz.parse.{body, job, options}
import zz.system.ZiZaSystem

object main extends scala.App {
  val programArgs = options.parse(args)

  val system = ZiZaSystem.make()
  val deps = system

  val application = for {
    (in, out, f) <- zz.system.boot()
    jobs = programArgs.files.flatMap(f => job.id(f).map(_ -> f))

    submit = for {
      msg <- ZIO.foreach(jobs)(x => body.z.parse(x._2).map(x._1 -> _))
      req = msg
        .filter { case (_, Report(_)) => true; case _ => false }
        .map(x => zz.system.DocRequest(x._1.getLocalName, x._2.text))
      _ <- in.offerAll(req)
    } yield {
      // todo;; debug
      req.foreach { x =>
        println(s"== ${x.id}")
        println(s"== \t${x.text}")
      }
    }

    // doc processing loop
    loop = for {
      d <- out.take
      g = d.id.map(rdf.iri)
    } yield ()

    _ <- submit
    _ <- loop.forever.fork
    _ <- f.join
  } yield ()

  Runtime.unsafeFromLayer(deps).unsafeRun(application)
}
