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

   public Step() {
   }

   @Builder
   public Step(String description, String file, String directory, String uri, Integer line, String pattern,
               String title) {
      this.description = description;
      this.file = file;
      this.directory = directory;
      this.uri = uri;
      this.line = line;
      this.pattern = pattern;
      this.title = title;
   }

   @Override
   public String toString() {
      return title;
   }

}