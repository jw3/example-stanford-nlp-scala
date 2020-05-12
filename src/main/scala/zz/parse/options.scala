package zz.parse

import java.io.File
import java.nio.file.Paths

object options {
  case class ProgramArgs(files: Seq[File])

  def parse(args: Array[String]): ProgramArgs =
    args match {
      case Array(path) =>
        val f = Paths.get(path).toFile
        if (f.exists()) {
          if (f.isDirectory) {
            f.listFiles().filter(_.getName.endsWith(".msg")) match {
              case Array() =>
                throw new IllegalArgumentException(s"no msg files found in dir $path")
              case arr => ProgramArgs(arr.toSeq)
            }
          } else ProgramArgs(Seq(f))
        } else throw new IllegalArgumentException(s"$path does not exist")
      case _ =>
        println("usage: zz <path-to-msg-dir-or-file>")
        System.exit(1)
        ProgramArgs(Seq.empty)
    }
}
