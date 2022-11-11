package org.uom.lefterisxris.codetour.tours.domain;

import com.google.gson.Gson;
import com.intellij.openapi.diagnostic.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 11/11/2022
 */
public class OnboardingAssistant {

   private static final String ONBOARD_ASSISTANT_FILENAME = "tours/projectIntroduction-virtualAssistant.tour";
   public static final String ONBOARD_ASSISTANT_TITLE = "Virtual Onboarding Assistant";
   private static final Logger LOG = Logger.getInstance(OnboardingAssistant.class);

   private static OnboardingAssistant instance;
   private Tour tour = null;

   public OnboardingAssistant() {

      try (InputStream is = this.getClass().getClassLoader()
            .getResourceAsStream(ONBOARD_ASSISTANT_FILENAME)) {
         if (is == null) return;
         tour = new Gson().fromJson(new InputStreamReader(is), Tour.class);
      } catch (Exception e) {
         LOG.error(e.getMessage(), e);
      }

   }

   public static OnboardingAssistant getInstance() {
      if (instance == null || instance.getTour() == null)
         instance = new OnboardingAssistant();
      return instance;
   }

   public Tour getTour() {
      return tour;
   }
}