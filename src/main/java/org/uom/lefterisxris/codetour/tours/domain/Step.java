package org.uom.lefterisxris.codetour.tours.domain;

import lombok.Builder;
import lombok.Data;

@Data
public class Step {
   private String title;
   private String description;
   private String file;
   private int line;
   private String directory;
   private String uri;
   private String pattern;

   public Step() {
   }

   @Builder
   public Step(String description, String file, String directory, String uri, int line, String pattern,
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
      return "Step{" +
            "title='" + title + '\'' +
            '}';
   }

}