package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 *
 * @author Eleftherios Chrysochoidis
 * Date: 11/11/2022
 */
@State(
      name = "org.uom.lefterisxris.codetour.tours.service.AppSettingsState",
      storages = @Storage("CodeTourSettings.xml")
)
@Getter
@Setter
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

   private boolean onboardingAssistant = true;
   private SortOptionE sortOption = SortOptionE.TITLE;
   private SortDirectionE sortDirection = SortDirectionE.ASC;

   public static AppSettingsState getInstance() {
      return ApplicationManager.getApplication().getService(AppSettingsState.class);
   }

   @Nullable
   @Override
   public AppSettingsState getState() {
      return this;
   }

   @Override
   public void loadState(@NotNull AppSettingsState state) {
      XmlSerializerUtil.copyBean(state, this);
   }

   public boolean isOnboardingAssistantOn() {return onboardingAssistant;}

   public void toggleOnboardingAssistant() {
      onboardingAssistant = !onboardingAssistant;
   }

   public enum SortOptionE {
      TITLE, FILENAME, CREATION_DATE;
   }

   public enum SortDirectionE {
      ASC, DESC;
   }
}