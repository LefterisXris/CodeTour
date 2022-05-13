package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.state.StateManager;
import org.uom.lefterisxris.codetour.tours.ui.ToursNotification;

public class TourScanner implements StartupActivity.Background {

   @Override
   public void runActivity(@NotNull Project project) {
      if (new StateManager().shouldNotify(project))
         new ToursNotification().notifyUser(project);
   }
}