package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.codeInsight.documentation.DocumentationComponent;
import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.icons.AllIcons;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.state.StateManager;
import org.uom.lefterisxris.codetour.tours.state.StepSelectionNotifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

/**
 * Renders a Popup which includes the Step Documentation
 *
 * @author Eleftherios Chrysochoidis
 * Date: 8/5/2022
 */
public class StepRenderer extends DialogWrapper {
   private final Step step;
   private final Project project;

   private static StepRenderer instance;

   private StepRenderer(Step step, Project project) {
      super(true);
      this.step = step;
      this.project = project;
      setTitle("Step Documentation");
      init();
      setModal(false);
      setResizable(true);
   }

   public static StepRenderer getInstance(Step step, Project project) {

      Optional<UserSettings> userSettings = Optional.empty();
      if (instance != null) {
         // Keep the user settings (size and location of Modal)
         userSettings = Optional.of(new UserSettings(instance));
         instance.close(0);
      }

      instance = new StepRenderer(step, project);
      userSettings.ifPresent(settings -> settings.inject(instance));
      return instance;
   }

   private JComponent getComponent() {

      final String stepDoc = renderFullDoc(
            String.format("Description of Step '%s'", step.getTitle()),
            step.getDescription(),
            String.format("%s:%s", step.getFile(), step.getLine()));

      final DocumentationManager documentationManager = DocumentationManager.getInstance(project);
      final DocumentationComponent component = new DocumentationComponent(documentationManager);
      component.setData(null, stepDoc, null, null, null);

      return component;
   }

   private String renderFullDoc(String title, String description, String file) {
      StringBuilder sb = new StringBuilder();
      sb.append(DocumentationMarkup.DEFINITION_START);
      sb.append(title);
      sb.append(DocumentationMarkup.DEFINITION_END);
      sb.append(DocumentationMarkup.CONTENT_START);
      sb.append(description);
      sb.append(DocumentationMarkup.CONTENT_END);
      sb.append(DocumentationMarkup.SECTIONS_START);
      addKeyValueSection("File:", file, sb);
      sb.append(DocumentationMarkup.SECTIONS_END);
      return sb.toString();
   }

   private void addKeyValueSection(String key, String value, StringBuilder sb) {
      sb.append(DocumentationMarkup.SECTION_HEADER_START);
      sb.append(key);
      sb.append(DocumentationMarkup.SECTION_SEPARATOR);
      sb.append("<p>");
      sb.append(value);
      sb.append(DocumentationMarkup.SECTION_END);
   }

   @Override
   protected @Nullable JComponent createCenterPanel() {
      JPanel dialogPanel = new JPanel(new BorderLayout());
      dialogPanel.add(getComponent(), BorderLayout.CENTER);

      final JPanel buttons = new JPanel();
      final JButton previousStepButton = new JButton(AllIcons.Actions.Back);
      previousStepButton.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            StateManager.getPrevStep().ifPresent(step -> {
               // Notify UI to select the step which will trigger its navigation
               project.getMessageBus().syncPublisher(StepSelectionNotifier.TOPIC).selectStep(step);
            });
         }
      });
      previousStepButton.setToolTipText("Navigate to the Previous Step (Ctrl+Alt+Q)");
      buttons.add(previousStepButton);


      final JButton nextStepButton = new JButton(AllIcons.Actions.Forward);
      nextStepButton.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            StateManager.getNextStep().ifPresent(step -> {
               // Notify UI to select the step which will trigger its navigation
               project.getMessageBus().syncPublisher(StepSelectionNotifier.TOPIC).selectStep(step);
            });
         }
      });
      nextStepButton.setToolTipText("Navigate to the Next Step (Ctrl+Alt+Q)");
      buttons.add(nextStepButton);

      dialogPanel.add(buttons, BorderLayout.SOUTH);
      dialogPanel.setPreferredSize(new Dimension(320, 160));
      return dialogPanel;
   }

   /**
    * Aux class to hold User settings for the Step documentation modal
    */
   @Getter
   @AllArgsConstructor
   final static class UserSettings {
      private int x;
      private int y;
      private int width;
      private int height;

      public UserSettings(StepRenderer instance) {
         final Point location = instance.getLocation();
         this.x = location.x;
         this.y = location.y;

         final Dimension size = instance.getSize();
         if (size != null) {
            this.width = size.width;
            this.height = size.height;
         }
      }

      void inject(StepRenderer instance) {
         instance.setLocation(x, y);
         instance.setSize(width, height);
      }
   }
}