package zz.parse

import java.io.{File, IOException}

import zio.{IO, ZIO}

object body {
  sealed trait MessageBody {
    def text: String
  }
  object MessageBody {
    case class Unknown(text: String) extends MessageBody
    case class Report(text: String) extends MessageBody
    case class List(text: String) extends MessageBody

  }

  def nli(num: Int) = raw"""^$num\.\s(.+)$$""".r.anchored
  val Num2 = nli(2)
  val Num3 = nli(3)
  val Mt = "^$".r.anchored

  def dropAndRoll(i: Iterator[String]): String =
    i.dropWhile(!_.startsWith("2.")).mkString("\n")

  def detectFormat(body: String): MessageBody = {
    val splits = body.split("\n")
    splits match {
      case Array(Num2(txt), Mt(), Mt(), Num3(_), _*) =>
        MessageBody.Report(txt)
      case _ =>
        MessageBody.Unknown(body)
    }
  }

  object z {
    def parse(files: Seq[File]): IO[IOException, List[MessageBody]] =
      ZIO.foreach(files)(parse)

    def parse(file: File): IO[IOException, MessageBody] =
      for {
        body <- job
          .lines(file)
          .map(dropAndRoll)
          .map(detectFormat)
      } yield body
  }
}
