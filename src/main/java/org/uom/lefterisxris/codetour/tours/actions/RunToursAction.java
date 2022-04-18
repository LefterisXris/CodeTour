package org.uom.lefterisxris.codetour.tours.actions;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.ui.ToursDialog;
import org.uom.lefterisxris.codetour.tours.state.StateManager;
import org.uom.lefterisxris.codetour.tours.domain.Tour;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RunToursAction extends AnAction {

   private final List<Tour> tours = new ArrayList<>();

   @Override
   public void actionPerformed(@NotNull AnActionEvent e) {
      final Project project = e.getProject();
      final ToursDialog dialog = new ToursDialog(project);
      if (dialog.showAndGet()) {
         ActionManager.getInstance()
               .tryToExecute(new RunTourByNameAction(dialog.getTitle(), dialog.getTourName()), e.getInputEvent(), null,
                     null, false);
      }
   }

   @Override
   public void update(@NotNull AnActionEvent e) {
      final Project project = e.getProject();
      if (Objects.isNull(project)) {
         e.getPresentation().setEnabledAndVisible(false);
         return;
      }
      if (tours.isEmpty()) {
         tours.addAll(new StateManager().getTours(project));
      }

      e.getPresentation().setEnabledAndVisible(true);
   }
}