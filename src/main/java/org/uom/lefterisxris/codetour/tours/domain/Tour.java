package org.uom.lefterisxris.codetour.tours.domain;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class Tour {
   private String id;
   private String tourFile; // The file that this tour would be persisted
   private String title; // The title of the Tour (visible on the tree)
   private String description; // Description (visible on hover as tooltip)
   private Boolean enabled;
   private String nextTour;
   private List<Step> steps;

   public Tour() {
   }

   @Builder
   public Tour(String id, String touFile, String title, String description, Boolean enabled, String nextTour,
               List<Step> steps) {
      this.id = id;
      this.tourFile = touFile;
      this.title = title;
      this.description = description;
      this.enabled = enabled;
      this.nextTour = nextTour;
      this.steps = steps;
   }

   @Override
   public String toString() {
      return title;
   }
}