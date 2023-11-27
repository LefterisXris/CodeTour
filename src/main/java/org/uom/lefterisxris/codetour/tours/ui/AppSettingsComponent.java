package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.uom.lefterisxris.codetour.tours.service.AppSettingsState;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 *
 * @author Eleftherios Chrysochoidis
 * Date: 11/11/2022
 */
public class AppSettingsComponent {

   private final JPanel mainPanel;
   private final JBCheckBox onboardingAssistantCb = new JBCheckBox("Enable/disable virtual onboarding assistant");
   private final ComboBox<AppSettingsState.SortOptionE> sortOption =
         new ComboBox<>(AppSettingsState.SortOptionE.values());
   private final ComboBox<AppSettingsState.SortDirectionE> sortDirection =
         new ComboBox<>(AppSettingsState.SortDirectionE.values());

   public AppSettingsComponent() {

      mainPanel = FormBuilder.createFormBuilder()
            .addComponent(onboardingAssistantCb, 1)
            // .addComponent(new TitledSeparator())
            .addLabeledComponent(new JBLabel("Tours sort option:"), sortOption, 2)
            .addLabeledComponent(new JBLabel("Sort direction: ascending / descending"), sortDirection, 3)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
   }

   public JPanel getPanel() {
      return mainPanel;
   }

   public JComponent getPreferredFocusedComponent() {
      return onboardingAssistantCb;
   }

   public boolean isOnboardingAssistantOn() {return onboardingAssistantCb.isSelected();}

   public AppSettingsState.SortOptionE getSortOption() {return sortOption.getItem();}

   public AppSettingsState.SortDirectionE getSortDirection() {return sortDirection.getItem();}

   public void setOnboardingAssistant(boolean newStatus) {
      onboardingAssistantCb.setSelected(newStatus);
   }

   public void setSortOption(AppSettingsState.SortOptionE newSortOption) {
      sortOption.setItem(newSortOption);
   }

   public void setSortDirection(AppSettingsState.SortDirectionE newSortDirection) {
      sortDirection.setItem(newSortDirection);
   }

}