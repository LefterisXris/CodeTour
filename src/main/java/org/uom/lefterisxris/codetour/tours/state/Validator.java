package org.uom.lefterisxris.codetour.tours.state;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageDialogBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.domain.OnboardingAssistant;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.domain.Tour;
import org.uom.lefterisxris.codetour.tours.service.Utils;
import org.uom.lefterisxris.codetour.tours.ui.CodeTourNotifier;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 11/11/2022
 */
public class Validator {

   private static final Logger LOG = Logger.getInstance(Validator.class);

   public static void validateTours(@NotNull Project project, List<Tour> tours) {

      LOG.info("CodeTours Validation started at: " + LocalDateTime.now());
      final List<String> errors = new ArrayList<>();

      // All steps should point to a valid file reference (if configured)
      for (Tour tour : tours) {
         if (tour.getTitle().equals(OnboardingAssistant.ONBOARD_ASSISTANT_TITLE)) continue;

         for (Step step : tour.getSteps()) {
            if (step.getFile() == null) continue;

            // Try finding the appropriate file
            final String stepFileName = Paths.get(step.getFile()).getFileName().toString();
            final List<VirtualFile> validVirtualFiles = FilenameIndex
                  .getVirtualFilesByName(stepFileName, GlobalSearchScope.projectScope(project)).stream()
                  .filter(file -> Utils.isFileMatchesStep(file, step))
                  .collect(Collectors.toList());

            if (validVirtualFiles.isEmpty())
               errors.add(String.format("Step '%s' of Tour '%s' points to a non valid file: '%s'!\n",
                     step.getTitle(), tour.getTitle(), step.getFile()));

            //TODO: Line can also be checked in the future i.e. if line exists, and maybe if it has content?
         }
      }

      LOG.info(String.format("CodeTours Validation completed at: %s. Found %s errors",
            LocalDateTime.now(), errors.size()));

      // If errors found,
      if (!errors.isEmpty()) {
         final String title = String.format("%s Invalid Steps Found!", errors.size());
         errors.add("You might want to fix them for better Code Navigation.");
         final String content = String.join("\n", errors);
         MessageDialogBuilder.okCancel(title, content).ask(project);
         CodeTourNotifier.warn(project, title);
      }
   }
}