package org.uom.lefterisxris.codetour.tours;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.state.StateManager;

public class TourScanner implements StartupActivity.Background {

   @Override
   public void runActivity(@NotNull Project project) {
      if (new StateManager().shouldNotify(project))
         new ToursNotification().notifyUser(project);
   }
}