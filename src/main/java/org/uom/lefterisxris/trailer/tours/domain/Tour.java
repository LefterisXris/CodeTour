package org.uom.lefterisxris.trailer.tours.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class Tour {
   private String title;
   private String description;
   private String isPrimary;
   private String nextTour;
   private List<TourStep> steps;

   public Tour() {
   }

   @Builder
   public Tour(String title, String description, String isPrimary, String nextTour,
               List<TourStep> steps) {
      this.title = title;
      this.description = description;
      this.isPrimary = isPrimary;
      this.nextTour = nextTour;
      this.steps = steps;
   }

   @Override
   public String toString() {
      return "Tour{" +
            "title='" + title + '\'' +
            '}';
   }
}