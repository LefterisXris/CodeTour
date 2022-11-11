package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.util.ui.FormBuilder;

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

   public AppSettingsComponent() {
      mainPanel = FormBuilder.createFormBuilder()
            .addComponent(onboardingAssistantCb, 1)
            .addComponentFillVertically(new JPanel(), 0)
            .getPanel();
   }

   public JPanel getPanel() {
      return mainPanel;
   }

   public JComponent getPreferredFocusedComponent() {
      return onboardingAssistantCb;
   }

   public boolean isOnboardingAssistantOn() {
      return onboardingAssistantCb.isSelected();
   }

   public void setOnboardingAssistant(boolean newStatus) {
      onboardingAssistantCb.setSelected(newStatus);
   }

}