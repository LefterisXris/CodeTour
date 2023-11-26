package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.codetour.tours.ui.AppSettingsComponent;

import javax.swing.*;
import java.util.Optional;

/**
 * Provides controller functionality for application settings.
 *
 * @author Eleftherios Chrysochoidis
 * Date: 11/11/2022
 */
public class AppSettingsConfigurable implements Configurable {

   private AppSettingsComponent settingsComponent;

   // A default constructor with no arguments is required because this implementation
   // is registered as an applicationConfigurable EP

   @Nls(capitalization = Nls.Capitalization.Title)
   @Override
   public String getDisplayName() {
      return "CodeTour Plugin Settings";
   }

   @Override
   public JComponent getPreferredFocusedComponent() {
      return settingsComponent.getPreferredFocusedComponent();
   }

   @Nullable
   @Override
   public JComponent createComponent() {
      settingsComponent = new AppSettingsComponent();
      return settingsComponent.getPanel();
   }

   @Override
   public boolean isModified() {
      AppSettingsState settings = AppSettingsState.getInstance();
      return settingsComponent.isOnboardingAssistantOn() != settings.isOnboardingAssistant()
            || (settingsComponent.getSortOption() != settings.getSortOption())
            || (settingsComponent.getSortDirection() != settings.getSortDirection());
   }

   @Override
   public void apply() {
      AppSettingsState settings = AppSettingsState.getInstance();
      settings.setOnboardingAssistant(settingsComponent.isOnboardingAssistantOn());
      settings.setSortDirection(settingsComponent.getSortDirection());
      settings.setSortOption(Optional.ofNullable(settingsComponent.getSortOption())
            .orElse(AppSettingsState.SortOptionE.TITLE));
   }

   @Override
   public void reset() {
      AppSettingsState settings = AppSettingsState.getInstance();
      settingsComponent.setOnboardingAssistant(settings.isOnboardingAssistant());
      settingsComponent.setSortOption(settings.getSortOption());
      settingsComponent.setSortDirection(settings.getSortDirection());
   }

   @Override
   public void disposeUIResources() {
      settingsComponent = null;
   }

}