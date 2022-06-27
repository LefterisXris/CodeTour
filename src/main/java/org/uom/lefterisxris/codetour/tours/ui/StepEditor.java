package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.codeInsight.documentation.DocumentationComponent;
import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.SideBorder;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.UI;
import com.intellij.util.ui.UIUtil;
import icons.CodeTourIcons;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.domain.Step;

import javax.swing.*;

import static org.uom.lefterisxris.codetour.tours.service.Utils.*;

/**
 * Editor (as dialog) for Step editing. Supports preview
 *
 * @author Eleftherios Chrysochoidis
 * Date: 31/5/2022
 */
public class StepEditor extends DialogWrapper {

   private JTabbedPane pane;
   private final Project project;
   private final Step step;

   private JBTextField titleTextField;
   private JBTextField referenceTextField;
   private JBTextArea descriptionTextArea;
   private DocumentationComponent previewComponent;
   private String stepDoc;

   public StepEditor(Project project, Step step) {
      super(project);
      this.project = project;
      this.step = step;
      init();
      setTitle("Step Editor");
   }

   @Override
   protected @NotNull JComponent createCenterPanel() {
      pane = new JBTabbedPane(SwingConstants.TOP);
      pane.addTab("Step Info", createEditorPanel());
      pane.addTab("Preview", createPreviewPanel());
      pane.addChangeListener(e -> {
         if (pane.getSelectedIndex() == 1)
            updatePreviewComponent();
      });
      return JBUI.Panels.simplePanel(pane);
   }

   private JComponent createEditorPanel() {
      descriptionTextArea = new JBTextArea(step.getDescription(), 10, 60);
      final JBScrollPane descriptionPane = new JBScrollPane(descriptionTextArea);
      descriptionPane.putClientProperty(UIUtil.KEEP_BORDER_SIDES, SideBorder.ALL);

      titleTextField = new JBTextField(step.getTitle());
      referenceTextField =
            new JBTextField(step.getFile() != null ? String.format("%s:%s", step.getFile(), step.getLine()) : "");

      final JPanel textFieldsGridPanel = UI.PanelFactory.grid()
            .add(UI.PanelFactory.panel(titleTextField)
                  .withLabel("&Title:")
                  .withComment("Step title"))
            .add(UI.PanelFactory.panel(referenceTextField)
                  .withLabel("&Navigation reference:")
                  .withComment("Code location where this step will Navigate to on click (optional)"))
            .createPanel();

      final JPanel textAreaPanel = UI.PanelFactory.panel(descriptionPane)
            .withLabel("Step description:")
            .anchorLabelOn(UI.Anchor.Top)
            .withComment("Markdown and HTML are supported.")
            .withCommentIcon(CodeTourIcons.MARKDOWN)
            .resizeX(true).resizeY(true).createPanel();

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(JBUI.Borders.emptyTop(5));
      panel.add(textFieldsGridPanel);
      panel.add(textAreaPanel);

      return panel;
   }

   private JComponent createPreviewPanel() {
      stepDoc = renderFullDoc(
            titleTextField.getText(),
            descriptionTextArea.getText(),
            referenceTextField.getText());

      final DocumentationManager documentationManager = DocumentationManager.getInstance(project);
      previewComponent = new DocumentationComponent(documentationManager);
      previewComponent.setData(null, stepDoc, null, null, null);

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(JBUI.Borders.emptyTop(5));
      panel.add(previewComponent);

      return panel;
   }

   private void updatePreviewComponent() {
      stepDoc = renderFullDoc(
            titleTextField.getText(),
            descriptionTextArea.getText(),
            referenceTextField.getText());
      previewComponent.setData(null, stepDoc, null, null, null);
   }

   public Step getUpdatedStep() {
      final String[] reference = referenceTextField.getText().trim().split(":");

      step.setTitle(titleTextField.getText().trim());
      step.setDescription(descriptionTextArea.getText().trim());

      // optional file:line
      final String file = reference[0] != null && !reference[0].isEmpty() ? reference[0] : null;
      final Integer line = reference.length > 1 && reference[1] != null && !reference[1].isEmpty()
            ? Integer.parseInt(reference[1])
            : null;

      step.setFile(file);
      step.setLine(line);

      return step;
   }

   public boolean isDirty() {
      final String[] reference = referenceTextField.getText().trim().split(":");
      return !equalStr(step.getTitle(), titleTextField.getText())
            || !equalStr(step.getDescription(), descriptionTextArea.getText())
            || !equalStr(step.getFile(), reference[0])
            || !equalInt(step.getLine(), reference.length > 1 ? Integer.parseInt(reference[1]) : null);
   }
}