package zz.util

// some misc data prep stuff, mostly ugliness
object prep {
  def default(text: String): String =
    text
      .replaceAll("LAT:", "LAT: ")
      .replaceAll("""\(U\)""", "")
      .replaceAll(" 'S ", "'S ")
}
