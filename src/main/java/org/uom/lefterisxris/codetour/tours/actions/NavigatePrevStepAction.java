package org.uom.lefterisxris.codetour.tours.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.state.StateManager;
import org.uom.lefterisxris.codetour.tours.state.StepSelectionNotifier;
import org.uom.lefterisxris.codetour.tours.ui.ToolPaneWindow;

/**
 * Triggers Navigation to the previous Step.
 * Navigation is handled through {@link ToolPaneWindow}
 *
 * @author Eleftherios Chrysochoidis
 */
public class NavigatePrevStepAction extends AnAction {
   @Override
   public void actionPerformed(@NotNull AnActionEvent e) {
      final Project project = e.getProject();
      if (project == null) return;

      StateManager.getPrevStep().ifPresent(step -> {
         // Notify UI to select the step which will trigger its navigation
         project.getMessageBus().syncPublisher(StepSelectionNotifier.TOPIC).selectStep(step);
      });
   }
}