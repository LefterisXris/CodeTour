import org.junit.Assert;
import org.junit.Test;
import org.uom.lefterisxris.codetour.tours.service.Utils;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 21/5/2022
 */
public class UtilsTests {

   @Test
   public void stringTest() {
      final String title = "Basket Items Issue Reproduce";
      final String expected = "basketItemsIssueReproduce.tour";
      final String actual = Utils.fileNameFromTitle(title);
      System.out.println(title);
      System.out.println(actual);
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testMdToHtml() {
      final String md = "# Hey there\n\nHow are you **today**?";
      final String expectedHtml = "<body><h1>Hey there</h1><p>How are you <strong>today</strong>?</p></body>";

      final String html = Utils.mdToHtml(md);

      Assert.assertEquals(expectedHtml, html);
   }
}