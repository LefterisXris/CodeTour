package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.SlowOperations;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.ui.CodeTourNotifier;

import java.util.Collection;
import java.util.Optional;

/**
 * Navigator class that navigates the user to the location that a step indicates.
 * Also renders the Step's description to the editor (as notification for now)
 *
 * @author Eleftherios Chrysochoidis
 * Date: 16/4/2022
 */
public class Navigator {

   public static void navigate(@NotNull Step step, @NotNull Project project) {
      if (project.getBasePath() == null) return;

      SlowOperations.allowSlowOperations(() -> {
         final Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName(step.getFile(),
               GlobalSearchScope.projectScope(project));
         //TODO: What if multiple files are found?
         final Optional<VirtualFile> virtualFile = virtualFiles.stream()
               .filter(file -> !file.isDirectory() && file.getName().equals(step.getFile()))
               .findFirst();
         if (virtualFile.isEmpty()) {
            CodeTourNotifier.error(project, String.format("Could not locate navigation target '%s' for Step '%s'",
                  step.getFile(), step.getTitle()));
            return;
         }

         // Navigate
         new OpenFileDescriptor(project, virtualFile.get(), step.getLine(), 1).navigate(true);

         // Show a Balloon
         CodeTourNotifier.notifyStepDescription(project, step.getDescription());

         // Show a Popup
         //TODO: It would be nice to provide the Tour as well, for info like 1/5 steps etc
         new StepRenderer(step, project)
               .showDoc();
      });
   }

}