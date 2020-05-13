package zz.parse

import java.io.{File, IOException}

import org.eclipse.rdf4j.model.{IRI, ValueFactory}
import zio.{IO, ZIO}

import scala.io.Source

object job {
  type JobID = IRI

  def id(file: File)(implicit f: ValueFactory): Option[JobID] =
    Source
      .fromFile(file, "ISO-8859-1")
      .getLines
      .find(_.startsWith("SERIAL:"))
      .map(_.split(":").last)
      .map(_.replaceAll("""\(.+?\)""", ""))
      .map(_.replaceAll("""[\s]""", ""))
      .map(rdf.iri(_))

  def lines(file: File): IO[IOException, Iterator[String]] =
    ZIO.effect(Source.fromFile(file, "ISO-8859-1").getLines).mapError {
      case e: IOException => e
    }
}
