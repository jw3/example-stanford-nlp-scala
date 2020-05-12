package zz

import org.clulab.processors.Document
import org.clulab.processors.corenlp.CoreNLPProcessor
import zio._
import zz.util.prep

import scala.util.Random

object system {
  case class DocRequest(id: String, text: String)
  object DocRequest {
    def apply(text: String): DocRequest = DocRequest(randID, text)

    private def randID = Random.alphanumeric.take(5).mkString
  }

  type ZimZamSystem = Has[ZimZamSystem.Service]
  object ZimZamSystem {
    trait Service {
      def process(id: String, s: String): UIO[Document]
      def process(id: String, s: Seq[String]): UIO[Document]
    }

    def make(): ZLayer.NoDeps[Nothing, ZimZamSystem] = ZLayer.succeed(
      new ZimZamSystem.Service {
        private val proc = new CoreNLPProcessor(withRelationExtraction = true)

        def process(id: String, s: String): UIO[Document] = IO.effectAsync[Nothing, Document] { callback =>
          callback(IO.succeed(proc.annotate(s, keepText = true)).map { d =>
            d.id = Some(id)
            d
          })
        }

        def process(id: String, s: Seq[String]): UIO[Document] = IO.effectAsync[Nothing, Document] { callback =>
          callback(IO.succeed(proc.annotateFromSentences(s, keepText = true)).map { d =>
            d.id = Some(id)
            d
          })
        }
      }
    )

    def process(id: String, s: String): URIO[ZimZamSystem, Document] = ZIO.accessM(_.get.process(id, s))
    def process(id: String, s: Seq[String]): URIO[ZimZamSystem, Document] = ZIO.accessM(_.get.process(id, s))
  }

  def boot(): URIO[ZimZamSystem, (Queue[DocRequest], Queue[Document], Fiber[_, _])] = {
    val inM = Queue.bounded[DocRequest](10)
    val outM = Queue.bounded[Document](100)

    for {
      in <- inM
      out <- outM
      loop = for {
        r <- in.take
        doc <- ZimZamSystem.process(r.id, prep.default(r.text))
        _ <- out.offer(doc)
      } yield {
        println(s"Processed request: $r")
        ()
      }
      fiber <- loop.forever.fork
    } yield (in, out, fiber)
  }
}
