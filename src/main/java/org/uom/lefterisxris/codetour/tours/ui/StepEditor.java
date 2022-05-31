package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.codeInsight.documentation.DocumentationComponent;
import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.lang.documentation.DocumentationMarkup;
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
import org.intellij.markdown.ast.ASTNode;
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor;
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor;
import org.intellij.markdown.html.HtmlGenerator;
import org.intellij.markdown.parser.MarkdownParser;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.domain.Step;

import javax.swing.*;

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
            String.format("Description of Step '%s'", titleTextField.getText()),
            getHtml(descriptionTextArea.getText()),
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

   private String getHtml(String markdown) {
      final MarkdownFlavourDescriptor flavour = new GFMFlavourDescriptor();
      final ASTNode parsedTree = new MarkdownParser(flavour).buildMarkdownTreeFromString(markdown);
      return new HtmlGenerator(markdown, parsedTree, flavour, false).generateHtml();
   }

   private void updatePreviewComponent() {
      stepDoc = renderFullDoc(
            String.format("Description of Step '%s'", titleTextField.getText()),
            getHtml(descriptionTextArea.getText()),
            referenceTextField.getText());
      previewComponent.setData(null, stepDoc, null, null, null);
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