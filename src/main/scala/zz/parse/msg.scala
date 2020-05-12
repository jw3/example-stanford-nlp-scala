package zz.parse

import java.io.{File, IOException}

import zio.{IO, ZIO}

import scala.io.Source

object msg {
  type MSGID = String

  def id(file: File): Option[String] =
    Source
      .fromFile(file, "ISO-8859-1")
      .getLines
      .find(_.startsWith("SERIAL:"))
      .map(_.split(":").last)
      .map(_.replaceAll("""\(.+?\)""", ""))
      .map(_.replaceAll("""[\s]""", ""))

  def lines(file: File): IO[IOException, Iterator[String]] =
    ZIO.effect(Source.fromFile(file, "ISO-8859-1").getLines).mapError {
      case e: IOException => e
    }
}
