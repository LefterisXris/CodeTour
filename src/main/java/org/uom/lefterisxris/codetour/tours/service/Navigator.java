package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.SlowOperations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.ui.CodeTourNotifier;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

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

         // Navigation is optional
         if (!tryNavigateToStep(step, project)) return;

         // Show a Popup
         final StepRenderer renderer = StepRenderer.getInstance(step, project);
         renderer.show();
      });
   }

   private static boolean tryNavigateToStep(@NotNull Step step, @NotNull Project project) {
      if (step.getFile() == null)
         return false;

      final String stepFileName = Paths.get(step.getFile()).getFileName().toString();
      final List<VirtualFile> validVirtualFiles = FilenameIndex
            .getVirtualFilesByName(stepFileName, GlobalSearchScope.projectScope(project)).stream()
            .filter(file -> isFileMatchesStep(file, step))
            .collect(Collectors.toList());

      // Case for not found file
      if (validVirtualFiles.isEmpty()) {
         CodeTourNotifier.error(project, String.format("Could not locate navigation target '%s' for Step '%s'",
               step.getFile(), step.getTitle()));
         return false;
      }

      // In case there is more than one file that matches with the Step, prompt User to pick the appropriate one
      if (validVirtualFiles.size() > 1) {

         final String prompt = "More Than One Target File Found! Select the One You Want to Navigate to:";
         JBPopupFactory.getInstance()
               .createListPopup(new BaseListPopupStep<>(prompt, validVirtualFiles) {
                  @Override
                  public @Nullable PopupStep<?> onChosen(VirtualFile selectedValue, boolean finalChoice) {

                     navigate(step, project, selectedValue);

                     // Show a Popup
                     final StepRenderer renderer = StepRenderer.getInstance(step, project);
                     renderer.show();

                     return super.onChosen(selectedValue, finalChoice);
                  }
               }).showInFocusCenter();

         // Notify user to be more specific
         CodeTourNotifier.warn(project, "Tip: A Step's file path can be more specific either by having a " +
               "relative path ('file' property) or by setting the 'directory' property on Step's definition");
         return false;
      } else {
         // Otherwise (exactly one match) just use it
         navigate(step, project, validVirtualFiles.get(0));
         return true;
      }
   }

   private static void navigate(@NotNull Step step, @NotNull Project project, VirtualFile targetVirtualFile) {
      final int line = step.getLine() != null ? step.getLine() - 1 : 0;
      new OpenFileDescriptor(project, targetVirtualFile, Math.max(line, 0), 1)
            .navigate(true);
   }

   private static boolean isFileMatchesStep(VirtualFile file, @NotNull Step step) {
      if (file.isDirectory())
         return false;

      final String stepDirectory = step.getDirectory() != null ? step.getDirectory() : "";
      final String stepFilePath = Paths.get(stepDirectory, step.getFile()).toString();
      final String filePath = Paths.get(file.getPath()).toString();

      return filePath.endsWith(stepFilePath);
   }


}
