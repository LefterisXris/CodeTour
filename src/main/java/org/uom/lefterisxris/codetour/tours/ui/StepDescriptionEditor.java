package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapperPeer;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.components.JBScrollPane;
import org.uom.lefterisxris.codetour.tours.domain.Step;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * Editor (as dialog) for Step's description value
 *
 * @author Eleftherios Chrysochoidis
 * Date: 29/5/2022
 */
public class StepDescriptionEditor extends Messages.InputDialog {

   private StepDescriptionEditor(Project project, Step step) {
      super(project, String.format("Edit Step's '%s' description", step.getTitle()),
            "Edit Step", AllIcons.Actions.Edit, step.getDescription(), null,
            new String[]{Messages.getOkButton(), Messages.getCancelButton()}, 0);
   }

   public static String show(Project project, Step step, boolean preselected) {
      final StepDescriptionEditor dialog = new StepDescriptionEditor(project, step);
      if (preselected)
         dialog.preselectText();

      dialog.show();
      return dialog.getInputString();
   }

   @Override
   protected JTextComponent createTextFieldComponent() {
      return new JTextArea(7, 50);
   }

   @Override
   protected JComponent createScrollableTextComponent() {
      return new JBScrollPane(myField);
   }

   @Override
   protected JComponent createNorthPanel() {
      return null;
   }

   @Override
   protected JComponent createCenterPanel() {
      JPanel messagePanel = new JPanel(new BorderLayout());
      if (myMessage != null) {
         JComponent textComponent = createTextComponent();
         messagePanel.add(textComponent, BorderLayout.NORTH);
      }

      myField = createTextFieldComponent();
      messagePanel.add(createScrollableTextComponent(), BorderLayout.CENTER);
      return messagePanel;
   }

   /**
    * Make the text preselected (for easier custom input)
    */
   private void preselectText() {
      final JTextComponent textField = getTextField();
      final TextRange selection = TextRange.allOf(textField.getText());
      textField.select(selection.getStartOffset(), selection.getEndOffset());
      textField.putClientProperty(DialogWrapperPeer.HAVE_INITIAL_SELECTION, true);
   }

}