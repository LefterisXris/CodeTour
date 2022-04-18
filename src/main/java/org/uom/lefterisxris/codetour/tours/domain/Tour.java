package org.uom.lefterisxris.codetour.tours.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class Tour {
   private String id;
   private String title;
   private String description;
   private Boolean enabled;
   private String nextTour;
   private List<TourStep> steps;

   public Tour() {
   }

   @Builder
   public Tour(String id, String title, String description, Boolean enabled, String nextTour,
               List<TourStep> steps) {
      this.title = title;
      this.description = description;
      this.enabled = enabled;
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