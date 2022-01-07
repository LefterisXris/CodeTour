package org.uom.lefterisxris.trailer.tours.domain;

import java.util.Objects;

public class TourStep {
   private String description;
   private String file;
   private String directory;
   private String uri;
   private String line;
   private String pattern;
   private String title;

   public TourStep() {
   }

   public TourStep(String description, String file, String directory, String uri, String line, String pattern,
                   String title) {
      this.description = description;
      this.file = file;
      this.directory = directory;
      this.uri = uri;
      this.line = line;
      this.pattern = pattern;
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getFile() {
      return file;
   }

   public void setFile(String file) {
      this.file = file;
   }

   public String getDirectory() {
      return directory;
   }

   public void setDirectory(String directory) {
      this.directory = directory;
   }

   public String getUri() {
      return uri;
   }

   public void setUri(String uri) {
      this.uri = uri;
   }

   public String getLine() {
      return line;
   }

   public void setLine(String line) {
      this.line = line;
   }

   public String getPattern() {
      return pattern;
   }

   public void setPattern(String pattern) {
      this.pattern = pattern;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      TourStep tourStep = (TourStep)o;
      return Objects.equals(description, tourStep.description) &&
            Objects.equals(file, tourStep.file) && Objects.equals(directory, tourStep.directory) &&
            Objects.equals(uri, tourStep.uri) && Objects.equals(line, tourStep.line) &&
            Objects.equals(pattern, tourStep.pattern) && Objects.equals(title, tourStep.title);
   }

   @Override
   public int hashCode() {
      return Objects.hash(description, file, directory, uri, line, pattern, title);
   }

   @Override
   public String toString() {
      return "TourStep{" +
            "title='" + title + '\'' +
            '}';
   }
}