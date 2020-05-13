package zz.addons

import org.clulab.struct.DirectedGraph

object dgraph {
  implicit class DirectedGraphAddon(g: DirectedGraph[String]) {
    def shortestPath(start: Seq[Int], end: Seq[Int]): Seq[Int] = {
      val x = for {
        i <- start
        paths = for {
          j <- end
          p = g.shortestPath(i, j, true)
        } yield p
      } yield paths.minBy(_.length)
      x.minBy(_.length)
    }
  }
}
