package org.uom.lefterisxris.codetour.tours.domain;

import com.google.gson.Gson;

import java.io.FileReader;
import java.net.URL;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 11/11/2022
 */
public class ReadingAssistant {

   private static ReadingAssistant instance;
   private final Tour tour;

   public ReadingAssistant() {

      final URL resource = this.getClass().getClassLoader()
            .getResource("tours/projectIntroduction-virtualAssistant.tour");
      if (resource != null) {
         try {
            tour = new Gson().fromJson(new FileReader(resource.getFile()), Tour.class);
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      } else
         tour = null;
   }

   public static ReadingAssistant getInstance() {
      if (instance == null)
         instance = new ReadingAssistant();
      return instance;
   }

}