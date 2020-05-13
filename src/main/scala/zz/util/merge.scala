package zz.util

import org.clulab.processors.Sentence

object merge {
  private val NP = """^([BI])-NP$""".r.anchored
  private val TP = """^([BI]-.+)$""".r.anchored
  private val I = """^[I]-(.+)$""".r.anchored
  private val B = """^[B]-(.+)$""".r.anchored
  private val O = """^[O]-(.+)$""".r.anchored
  private val IorB = """[BI]-(.+)""".r.unanchored

  type Result = Option[Seq[(String, Int)]]
  case class Ref(entity: String, value: String, span: Seq[Int]) {
    override def toString: String = value
  }
  case class Rel(s: Ref, p: Ref, o: Ref)
  case class Val(s: Ref, p: Ref)

  def default(sentence: Sentence): Result =
    for {
      t <- sentence.tags
      c <- sentence.chunks
      e <- sentence.entities
      z = (t, c, e).zipped.toSeq.zipWithIndex
    } yield z.map {
      case ((p, _, "O"), i)          => s"O-$p" -> i
      case ((_, _, TP(e)), i)        => e -> i
      case ((_, NP(c), "PERSON"), i) => s"${c}-PERSON" -> i
      case (v, i)                    => s"B-${v._3}" -> i
    }

  def repaired(sentence: Sentence): Result =
    default(sentence).map(_.toArray).map { merged =>
      val fixes = merged.sliding(2).flatMap {
        case Array((O(_), i), (I(t1), _))              => Some(s"B-$t1" -> i) // fixes 0 I-PERSON
        case Array((B(t0), _), (I(t1), j)) if t0 != t1 => Some(s"B-$t1" -> j) // fixes B-Flag I-PERSON
        case Array((B(_), _), (I(_), _))               => None
        case _                                         => None
      }

      // apply fixes
      fixes.foreach(fix => merged(fix._2) = fix)
      merged
    }

  // merge across sentences resolving cross-sentence pronouns based on subject of previous sentence
  def across(sentence: Seq[Sentence]): Unit = {}

  def inplace(sentence: Sentence): Unit =
    sentence.entities = repaired(sentence).map {
      _.map { x =>
        x._1
      }.toArray
    }

  def flatten(sentence: Sentence): Seq[Ref] = {
    val words = sentence.words
    val tags = sentence.entities.get
    val pos = sentence.tags.get

    tags.zipWithIndex.flatMap {
      case (B(t), i) =>
        var assembly = Seq.empty[String]
        var preskew = 0
        var postskew = 0

        // t is verb check for adverb
        if (i > 0 && pos(i).startsWith("VB") && pos(i - 1).startsWith("RB"))
          assembly = Seq(words(i - 1)) ++ assembly

        val all = tags.zipWithIndex
          .takeRight(tags.length - i)
          .takeWhile {
            case (IorB(tt), _) => tt == t
            case (O(v), j) if j > i =>
              if (pos(j) == "TO") postskew += 1
              // false because we dont want this token, just the range skew
              false
            case _ => false
          }
          .map(x => words(x._2))

        assembly ++= all
        preskew = Math.min(i, preskew)

        Some(Ref(t, assembly.mkString(" "), (i - preskew until i + all.length + postskew).toList))
      case _ =>
        None
    }
  }
}
