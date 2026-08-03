package org.uom.lefterisxris.codetour.tours.domain;

import lombok.Builder;
import lombok.Data;

@Data
public class Step {
   private String title; // Step's title (visible on the tree)
   private String description; // The description of the Step (visible on the Editor)
   private String file; // File for navigation
   private Integer line; // Line for navigation
   private String directory;
   private String uri;
   private String pattern;
   private StepSelection selection;

   public Step() {
   }

   @Builder
   public Step(String description, String file, String directory, String uri, Integer line, String pattern,
               String title, StepSelection selection) {
      this.description = description;
      this.file = file;
      this.directory = directory;
      this.uri = uri;
      this.line = line;
      this.pattern = pattern;
      this.title = title;
      this.selection = selection;
   }

   /**
    * A Microsoft-compatible selection step does not need a separate line field.
    * Use the selection start wherever the plugin needs a single navigation line,
    * such as gutter markers or legacy line-based navigation.
    */
   public Integer getNavigationLine() {
      if (selection != null && selection.getStart() != null && selection.getStart().getLine() != null)
         return selection.getStart().getLine();
      return line;
   }

   @Override
   public String toString() {
      return title;
   }

}
