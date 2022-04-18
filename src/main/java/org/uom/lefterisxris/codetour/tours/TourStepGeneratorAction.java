package org.uom.lefterisxris.codetour.tours;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorGutter;
import com.intellij.openapi.editor.TextAnnotationGutterProvider;
import com.intellij.openapi.editor.colors.ColorKey;
import com.intellij.openapi.editor.colors.EditorFontType;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.IconManager;
import com.intellij.ui.JBColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.codetour.tours.domain.Tour;
import org.uom.lefterisxris.codetour.tours.domain.TourStep;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.isNull;

public class TourStepGeneratorAction extends AnAction {

   @Override
   public void actionPerformed(@NotNull AnActionEvent e) {
      final Project project = e.getProject();
      if (isNull(project) || isNull(project.getBasePath()))
         return;

      // TODO: Get the current location and add it on active Tour
      //  check e.getData(CommonDataKeys.EDITOR).getGutter()
      //  NavigationGutterIconRenderer
      System.out.println();
      // e.getData(CommonDataKeys.VIRTUAL_FILE)
      // e.getDataContext().getData("EditorGutter.LOGICAL_LINE_AT_CURSOR")
      final Editor editor = e.getData(CommonDataKeys.EDITOR);
      /*new EdiGuCompIm
      if (nonNull(editor)) {
         editor.setBr
      }*/

      final EditorGutter editorGutter = e.getData(EditorGutter.KEY);
      final Object lineObj = e.getDataContext().getData("EditorGutter.LOGICAL_LINE_AT_CURSOR");
      final int line = lineObj != null ? Integer.parseInt(lineObj.toString()) : 1;
      /*if (nonNull(editorGutter) && nonNull(lineObj)) {
         editorGutter
               .registerTextAnnotation(new MyTextAnnotationGutterProvider(Integer.parseInt(lineObj.toString())));
      }*/

      final VirtualFile virtualFile = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
      if (virtualFile == null)
         return;

      final ToursStateComponent state = new ToursStateComponent(project);

      List<TourStep> steps = new ArrayList<>();
      steps.add(generateStep(virtualFile, line));

      // Also generate 2 extra steps (first and last lines) for demonstration
      if (editor != null) {
         final int lastLine = editor.getDocument().getLineCount();
         steps.add(generateStep(virtualFile, 1));
         steps.add(generateStep(virtualFile, lastLine));
      }

      final Optional<Tour> activeTour = state.getActive();
      if (activeTour.isPresent()) {
         // Add steps to active
         activeTour.get().getSteps().addAll(steps);
         state.updateTour(activeTour.get().getTitle(), activeTour.get());
      } else {
         //TODO: Dialog to Select a Tour

         // For now, just create a new one
         final Tour tour = Tour.builder()
               .id(UUID.randomUUID().toString())
               .title("LeC") //TODO: Interactive Tour Name
               .steps(steps)
               .enabled(true)
               .build();
         state.createTour(tour);
      }

      // new ToursStateComponent().getTours(project).add(tour);
      // PsiTreeUtil.getParentOfType(e.getData(CommonDataKeys.PSI_FILE).findElementAt(editor.getCaretModel().getOffset()), com.intellij.psi.PsiMethod.class);
   }

   private TourStep generateStep(VirtualFile virtualFile, int line) {
      final String title = String.format("%s:%s", virtualFile.getName(), line);
      return TourStep.builder()
            .title(title)
            .file(virtualFile.getName())
            .line(line)
            .build();
   }

   final class MyTextAnnotationGutterProvider implements TextAnnotationGutterProvider {

      private final int line;

      MyTextAnnotationGutterProvider(int line) {
         this.line = line;
      }

      @Override
      public @Nullable String getLineText(int line, Editor editor) {
         return this.line == line ? "TOUR TEXT" : null;
      }

      @Override
      public @Nullable @NlsContexts.Tooltip String getToolTip(int line, Editor editor) {
         return this.line == line ? "THIS IS A NEW TOUR BABY" : null;
      }

      @Override
      public EditorFontType getStyle(int line, Editor editor) {
         return null;
      }

      @Override
      public @Nullable ColorKey getColor(int line, Editor editor) {
         return this.line == line ? ColorKey.createColorKey("TOUR", JBColor.BLUE) : null;
      }

      @Override
      public @Nullable Color getBgColor(int line, Editor editor) {
         return this.line == line ? JBColor.red : null;
      }

      @Override
      public List<AnAction> getPopupActions(int line, Editor editor) {
         System.out.println("Requested popup actions!");
         return null;
      }

      @Override
      public void gutterClosed() {

      }
   }

   final class MyRenderer extends GutterIconRenderer {

      @Override
      public @Nullable AnAction getClickAction() {
         System.out.println("Clicked!!");
         return super.getClickAction();
      }

      @Override
      public boolean equals(Object obj) {
         return false;
      }

      @Override
      public int hashCode() {
         return 0;
      }

      @Override
      public @NotNull Icon getIcon() {
         return IconManager.getInstance().getIcon("search.svg", MyRenderer.class);
      }
   }
}