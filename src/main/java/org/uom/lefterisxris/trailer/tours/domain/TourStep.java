package org.uom.lefterisxris.trailer.tours.domain;

import lombok.Builder;
import lombok.Data;

@Data
public class TourStep {
   private String description;
   private String file;
   private String directory;
   private String uri;
   private int line;
   private String pattern;
   private String title;

   public TourStep() {
   }

   @Builder
   public TourStep(String description, String file, String directory, String uri, int line, String pattern,
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
      return "TourStep{" +
            "title='" + title + '\'' +
            '}';
   }
}