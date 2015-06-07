import com.trevore.dexparser.DexParser

/**
 * Created by trevor on 6/5/15.
 */
object Main {

  def main(args: Array[String]): Unit = {
    val dexParser = new DexParser()
    dexParser.load("//Users//trevor//Programming//AndroidStudioProjects//GymTracker//app//build//intermediates//dex//debug//classes.dex")
  }

}
