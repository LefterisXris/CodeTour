package org.uom.lefterisxris.codetour.tours.service;

import com.google.gson.Gson;
import com.intellij.openapi.editor.impl.DocumentImpl;
import org.junit.Assert;
import org.junit.Test;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.domain.StepPosition;

public class SelectionTests {

   @Test
   public void parsesMicrosoftCodeTourSelection() {
      final String json = """
            {
              "title": "Selected code",
              "description": "Highlights a range",
              "file": "src/example.java",
              "selection": {
                "start": { "line": 2, "character": 3 },
                "end": { "line": 4, "character": 6 }
              }
            }
            """;

      final Step step = new Gson().fromJson(json, Step.class);

      Assert.assertNotNull(step.getSelection());
      Assert.assertTrue(step.getSelection().isComplete());
      Assert.assertEquals(Integer.valueOf(2), step.getNavigationLine());
      Assert.assertEquals("src/example.java:2:3-4:6", Utils.formatNavigationReference(step));
   }

   @Test
   public void convertsOneBasedPositionsAndClampsStaleRanges() {
      final DocumentImpl document = new DocumentImpl("alpha\nbravo\ncharlie");

      Assert.assertEquals(0, Navigator.positionToOffset(document, position(1, 1)));
      Assert.assertEquals(7, Navigator.positionToOffset(document, position(2, 2)));
      Assert.assertEquals(19, Navigator.positionToOffset(document, position(3, 99)));
      Assert.assertEquals(12, Navigator.positionToOffset(document, position(99, 1)));
   }

   private StepPosition position(int line, int character) {
      return StepPosition.builder().line(line).character(character).build();
   }
}
