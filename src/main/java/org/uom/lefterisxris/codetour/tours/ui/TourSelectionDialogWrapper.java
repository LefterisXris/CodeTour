package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.codetour.tours.domain.Tour;
import org.uom.lefterisxris.codetour.tours.state.StateManager;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

import static org.uom.lefterisxris.codetour.tours.domain.OnboardingAssistant.ONBOARD_ASSISTANT_TITLE;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 7/5/2022
 */
public class TourSelectionDialogWrapper extends DialogWrapper {

   private final Project project;
   private Optional<Tour> selected = Optional.empty();

   public TourSelectionDialogWrapper(Project project, String title) {
      super(true); // use current window as parent
      this.project = project;
      setTitle(title);
      init();
   }

   @Override
   protected @Nullable JComponent createCenterPanel() {
      JPanel dialogPanel = new JPanel(new BorderLayout());

      final StateManager stateManager = new StateManager(project);
      var tours = stateManager.getTours();

      // Onboarding Assistant should not be present in this selection
      tours.removeIf(tour -> ONBOARD_ASSISTANT_TITLE.equals(tour.getTitle()));

      final int toursSize = tours.size();
      final Tour[] toursOptions = new Tour[toursSize];
      for (int i = 0; i < toursSize; i++)
         toursOptions[i] = tours.get(i);

      final ComboBox<Tour> comboBox = new ComboBox<>(toursOptions);
      comboBox.addActionListener(e -> {
         selected = Optional.of(comboBox.getItem());
      });
      JLabel label = new JLabel("Select the Tour");
      // label.setPreferredSize(new Dimension(100, 100));
      dialogPanel.add(label, BorderLayout.NORTH);
      dialogPanel.add(comboBox, BorderLayout.CENTER);

      return dialogPanel;
   }

   public Optional<Tour> getSelected() {
      return selected;
   }
}