package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.codeInsight.documentation.DocumentationComponent;
import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.codetour.tours.domain.Step;

import javax.swing.*;
import java.awt.*;

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
      if (instance != null)
         instance.close(0);

      instance = new StepRenderer(step, project);
      return instance;
   }

   /**
    * //TODO: Delete
    */
   @Deprecated
   private void showDoc() {

      final JComponent component = getComponent();

      final JBPopup popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(component, component)
            .setProject(project)
            .setResizable(true)
            .setMovable(true)
            .setFocusable(true)
            .setModalContext(true)
            .createPopup();

      popup.setUiVisible(true);

      //TODO: This seems to make the popup not moving. Should check another 'show' option
      popup.showCenteredInCurrentWindow(project);
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
}