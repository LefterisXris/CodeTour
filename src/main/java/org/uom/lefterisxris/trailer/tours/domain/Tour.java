package org.uom.lefterisxris.trailer.tours.domain;

import java.util.List;
import java.util.Objects;

public class Tour {
   private String title;
   private String description;
   private String isPrimary;
   private String nextTour;
   private List<TourStep> steps;

   public Tour() {
   }

   public Tour(String title, String description, String isPrimary, String nextTour,
               List<TourStep> steps) {
      this.title = title;
      this.description = description;
      this.isPrimary = isPrimary;
      this.nextTour = nextTour;
      this.steps = steps;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public String getIsPrimary() {
      return isPrimary;
   }

   public void setIsPrimary(String isPrimary) {
      this.isPrimary = isPrimary;
   }

   public String getNextTour() {
      return nextTour;
   }

   public void setNextTour(String nextTour) {
      this.nextTour = nextTour;
   }

   public List<TourStep> getSteps() {
      return steps;
   }

   public void setSteps(List<TourStep> steps) {
      this.steps = steps;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Tour tour = (Tour)o;
      return Objects.equals(title, tour.title) && Objects.equals(description, tour.description) &&
            Objects.equals(isPrimary, tour.isPrimary) && Objects.equals(nextTour, tour.nextTour) &&
            Objects.equals(steps, tour.steps);
   }

   @Override
   public int hashCode() {
      return Objects.hash(title, description, isPrimary, nextTour, steps);
   }

   @Override
   public String toString() {
      return "Tour{" +
            "title='" + title + '\'' +
            '}';
   }
}