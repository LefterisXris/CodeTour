package org.uom.lefterisxris.trailer.tours;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class TourScanner implements StartupActivity.Background {

   @Override
   public void runActivity(@NotNull Project project) {
      if (new ToursStateComponent().shouldNotify(project))
         new ToursNotification().notifyUser(project);
   }
}