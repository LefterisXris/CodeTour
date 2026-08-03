package org.uom.lefterisxris.codetour.tours.service;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileEditorManager;
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
import org.uom.lefterisxris.codetour.tours.domain.StepPosition;
import org.uom.lefterisxris.codetour.tours.domain.StepSelection;
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

         if (step.getFile() == null) {
            // Nothing more to do. Just show Step's popup and return
            renderStepPopup(step, project);
            return;
         }

         // Try finding the appropriate file to navigate to
         final String stepFileName = Paths.get(step.getFile()).getFileName().toString();
         final List<VirtualFile> validVirtualFiles = FilenameIndex
               .getVirtualFilesByName(stepFileName, GlobalSearchScope.projectScope(project)).stream()
               .filter(file -> Utils.isFileMatchesStep(file, step))
               .collect(Collectors.toList());

         if (validVirtualFiles.isEmpty()) {
            // Case for configured but not found file
            CodeTourNotifier.error(project, String.format("Could not locate navigation target '%s' for Step '%s'",
                  step.getFile(), step.getTitle()));
         } else if (validVirtualFiles.size() > 1) {
            // In case there is more than one file that matches with the Step, prompt User to pick the appropriate one
            final String prompt = "More Than One Target File Found! Select the One You Want to Navigate to:";
            JBPopupFactory.getInstance()
                  .createListPopup(new BaseListPopupStep<>(prompt, validVirtualFiles) {
                     @Override
                     public @Nullable PopupStep<?> onChosen(VirtualFile selectedValue, boolean finalChoice) {

                        navigate(step, project, selectedValue);

                        // Show a Popup
                        renderStepPopup(step, project);

                        return super.onChosen(selectedValue, finalChoice);
                     }
                  }).showInFocusCenter();

            // Notify user to be more specific
            CodeTourNotifier.warn(project, "Tip: A Step's file path can be more specific either by having a " +
                  "relative path ('file' property) or by setting the 'directory' property on Step's definition");
            return; // Make sure we return here, because PopUp runs on another Thread (no wait for User input)
         } else {
            // Case for exactly one match. Just use it
            navigate(step, project, validVirtualFiles.get(0));
         }

         // Show Step's popup and return
         renderStepPopup(step, project);
      });
   }

   private static void navigate(@NotNull Step step, @NotNull Project project, VirtualFile targetVirtualFile) {
      final StepSelection selection = step.getSelection();
      if (selection != null && selection.isComplete()) {
         // CodeTour stores selection positions as one-based values. Open the editor at the
         // selection start, then translate both endpoints to IntelliJ document offsets.
         final int line = Math.max(selection.getStart().getLine() - 1, 0);
         final int column = Math.max(selection.getStart().getCharacter() - 1, 0);
         final Editor editor = FileEditorManager.getInstance(project)
               .openTextEditor(new OpenFileDescriptor(project, targetVirtualFile, line, column), true);
         if (editor == null) return;

         final int startOffset = positionToOffset(editor.getDocument(), selection.getStart());
         final int endOffset = positionToOffset(editor.getDocument(), selection.getEnd());
         final int selectionStart = Math.min(startOffset, endOffset);
         final int selectionEnd = Math.max(startOffset, endOffset);

         editor.getSelectionModel().setSelection(selectionStart, selectionEnd);
         editor.getCaretModel().moveToOffset(selectionStart);
         editor.getScrollingModel().scrollToCaret(ScrollType.CENTER);
         return;
      }

      final Integer navigationLine = step.getNavigationLine();
      final int line = navigationLine != null ? navigationLine - 1 : 0;
      new OpenFileDescriptor(project, targetVirtualFile, Math.max(line, 0), 1).navigate(true);
   }

   /**
    * Converts a one-based CodeTour position to a safe IntelliJ document offset.
    * Hand-authored tours can outlive edits to the target file, so both the line
    * and character are clamped instead of allowing a stale tour to crash playback.
    */
   static int positionToOffset(@NotNull Document document, @NotNull StepPosition position) {
      final int requestedLine = position.getLine() != null ? position.getLine() - 1 : 0;
      final int line = Math.max(0, Math.min(requestedLine, document.getLineCount() - 1));
      final int lineStart = document.getLineStartOffset(line);
      final int lineEnd = document.getLineEndOffset(line);
      final int requestedColumn = position.getCharacter() != null ? position.getCharacter() - 1 : 0;
      return Math.max(lineStart, Math.min(lineStart + requestedColumn, lineEnd));
   }

   private static void renderStepPopup(@NotNull Step step, @NotNull Project project) {
      // Show a Popup
      final StepRenderer renderer = StepRenderer.getInstance(step, project);
      renderer.show();
   }

}
