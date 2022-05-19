package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.codeInsight.documentation.DocumentationComponent;
import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.codetour.tours.domain.Step;

import javax.swing.*;
import java.awt.*;
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
            String.format("%s:%s", step.getFile(), step.getLine()),
            "A sample comment");

      final DocumentationManager documentationManager = DocumentationManager.getInstance(project);
      final DocumentationComponent component = new DocumentationComponent(documentationManager);
      component.setData(null, stepDoc, null, null, null);

      return component;
   }

   private String renderFullDoc(String title, String description, String file, String docComment) {
      StringBuilder sb = new StringBuilder();
      sb.append(DocumentationMarkup.DEFINITION_START);
      sb.append(title);
      sb.append(DocumentationMarkup.DEFINITION_END);
      sb.append(DocumentationMarkup.CONTENT_START);
      sb.append(description);
      sb.append(DocumentationMarkup.CONTENT_END);
      sb.append(DocumentationMarkup.SECTIONS_START);
      addKeyValueSection("File:", file, sb);
      addKeyValueSection("Comment:", docComment, sb);
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