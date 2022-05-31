package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.icons.AllIcons;
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
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.domain.Step;

import javax.swing.*;

/**
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
      pane.addTab("Step Info", createComponentGridPanel());
      return JBUI.Panels.simplePanel(pane);
   }

   private JComponent createComponentGridPanel() {
      descriptionTextArea = new JBTextArea(step.getDescription(), 10, 60);
      final JBScrollPane descriptionPane = new JBScrollPane(descriptionTextArea);
      descriptionPane.putClientProperty(UIUtil.KEEP_BORDER_SIDES, SideBorder.ALL);

      titleTextField = new JBTextField(step.getTitle());
      referenceTextField = new JBTextField(String.format("%s:%s", step.getFile(), step.getLine()));

      final JPanel textFieldsGridPanel = UI.PanelFactory.grid()
            .add(UI.PanelFactory.panel(titleTextField)
                  .withLabel("&Title:")
                  .withComment("Step title"))
            .add(UI.PanelFactory.panel(referenceTextField)
                  .withLabel("&Navigation reference:")
                  .withComment("Code location where this step will Navigate to on click"))
            .createPanel();

      final JPanel textAreaPanel = UI.PanelFactory.panel(descriptionPane)
            .withLabel("Step description:")
            .anchorLabelOn(UI.Anchor.Top)
            .withComment("Markdown and HTML is supported.")
            .withCommentIcon(AllIcons.Xml.Html5)
            .resizeX(true).resizeY(true).createPanel();

      final JPanel panel = new JPanel();
      panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
      panel.setBorder(JBUI.Borders.emptyTop(5));
      panel.add(textFieldsGridPanel);
      panel.add(textAreaPanel);

      return panel;
   }

   public Step getUpdatedStep() {
      final String[] reference = referenceTextField.getText().trim().split(":");

      step.setTitle(titleTextField.getText().trim());
      step.setFile(reference[0]);
      step.setLine(Integer.parseInt(reference[1]));
      step.setDescription(descriptionTextArea.getText().trim());

      return step;
   }

   public boolean isDirty() {
      final String[] reference = referenceTextField.getText().trim().split(":");
      return !step.getTitle().equals(titleTextField.getText())
            || !step.getDescription().equals(descriptionTextArea.getText())
            || !step.getFile().equals(reference[0])
            || !(step.getLine() == Integer.parseInt(reference[1]));
   }
}