package org.uom.lefterisxris.codetour.tours.domain;

import lombok.Builder;
import lombok.Data;

/**
 * The code range highlighted when a CodeTour step is opened.
 *
 * Start and end positions are one-based in the tour JSON. The end position is
 * exclusive, matching VS Code's Selection semantics and Microsoft's CodeTour
 * schema.
 */
@Data
public class StepSelection {
   private StepPosition start;
   private StepPosition end;

   public StepSelection() {
   }

   @Builder
   public StepSelection(StepPosition start, StepPosition end) {
      this.start = start;
      this.end = end;
   }

   public boolean isComplete() {
      return start != null && end != null
            && start.getLine() != null && start.getCharacter() != null
            && end.getLine() != null && end.getCharacter() != null;
   }
}
