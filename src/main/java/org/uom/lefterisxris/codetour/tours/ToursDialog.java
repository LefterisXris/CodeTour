package org.uom.lefterisxris.codetour.tours;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.codetour.tours.domain.Tour;

import javax.swing.*;
import java.util.List;

public class ToursDialog extends DialogWrapper {

   private Project project;
   private String tourName;

   public ToursDialog(@Nullable Project project) {
      super(project, false);
      this.project = project;
      setTitle("Pick a Tour!");
      init();
   }

   @Override
   protected @Nullable JComponent createCenterPanel() {
      final List<Tour> tours = new ToursStateComponent().getTours(project);

      final JComboBox<String> jComboBox = new JComboBox<>();
      tours.stream().map(Tour::getTitle).forEach(jComboBox::addItem);
      jComboBox.addActionListener(e -> tourName = (String)jComboBox.getSelectedItem());

      final JPanel panel = new JPanel();
      panel.add(jComboBox);

      return panel;
   }

   public String getTourName() {
      return tourName;
   }
}