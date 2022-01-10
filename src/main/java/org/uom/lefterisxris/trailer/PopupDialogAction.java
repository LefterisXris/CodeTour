package org.uom.lefterisxris.trailer;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.pom.Navigatable;
import org.jetbrains.annotations.NotNull;

public class PopupDialogAction extends AnAction {

   @Override
   public void actionPerformed(@NotNull AnActionEvent event) {
      // Using the event, create and show a dialog
      Project currentProject = event.getProject();
      StringBuilder dlgMsg = new StringBuilder(event.getPresentation().getText() + " Selected!");
      String dlgTitle = event.getPresentation().getDescription();
      // If an element is selected in the editor, add info about it.
      Navigatable nav = event.getData(CommonDataKeys.NAVIGATABLE);
      if (nav != null) {
         dlgMsg.append(String.format("\nSelected Element: %s", nav.toString()));
      }
      Messages.showMessageDialog(currentProject, dlgMsg.toString(), dlgTitle, Messages.getInformationIcon());
   }


}