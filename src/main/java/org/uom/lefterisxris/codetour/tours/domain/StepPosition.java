package org.uom.lefterisxris.codetour.tours.domain;

import lombok.Builder;
import lombok.Data;

/**
 * A one-based line and character position in a CodeTour step selection.
 *
 * This matches Microsoft's CodeTour JSON schema so the same tour file can be
 * played in VS Code and IntelliJ Platform IDEs.
 */
@Data
public class StepPosition {
   private Integer line;
   private Integer character;

   public StepPosition() {
   }

   @Builder
   public StepPosition(Integer line, Integer character) {
      this.line = line;
      this.character = character;
   }
}
