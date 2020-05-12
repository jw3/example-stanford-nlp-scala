import zio.Runtime
import zz.parse.{msg, options}
import zz.system.ZimZamSystem

object main extends scala.App {
  val programArgs = options.parse(args)

  val system = ZimZamSystem.make()
  val deps = system

  val application = for {
    (in, out, f) <- zz.system.boot()
    msgs = programArgs.files.flatMap(f => msg.id(f).map(_ -> f))
  } yield ()

  Runtime.unsafeFromLayer(deps).unsafeRun(application)
}
