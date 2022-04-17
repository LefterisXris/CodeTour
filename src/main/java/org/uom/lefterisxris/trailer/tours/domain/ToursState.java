package org.uom.lefterisxris.trailer.tours.domain;


import java.util.ArrayList;
import java.util.List;

public class ToursState {

   private final List<Tour> tours = new ArrayList<>();

   public ToursState() {
   }

   public List<Tour> getTours() {
      return tours;
   }

   public void clear() {
      this.tours.clear();
   }
}