package org.uom.lefterisxris.trailer.tours.domain;


import java.util.ArrayList;
import java.util.List;

public class Tours {

   private final List<Tour> tours = new ArrayList<>();

   public Tours() {
   }

   public List<Tour> getTours() {
      return tours;
   }
}