package org.uom.lefterisxris.trailer.tours;

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
import org.uom.lefterisxris.trailer.tours.domain.Tour;
import org.uom.lefterisxris.trailer.tours.domain.TourStep;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
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
      final Object line = e.getDataContext().getData("EditorGutter.LOGICAL_LINE_AT_CURSOR");
      /*if (nonNull(editorGutter) && nonNull(line)) {
         editorGutter
               .registerTextAnnotation(new MyTextAnnotationGutterProvider(Integer.parseInt(line.toString())));
      }*/

      final VirtualFile virtualFile = e.getDataContext().getData(CommonDataKeys.VIRTUAL_FILE);
      if (virtualFile == null)
         return;

      final ToursStateComponent state = new ToursStateComponent(project);
      final TourStep step1 = TourStep.builder()
            .description("Trial Step")
            .file(virtualFile.getName())
            .line(line != null ? Integer.parseInt(line.toString()) : 1)
            .build();

      final TourStep step2 = TourStep.builder()
            .description("Trial Step")
            .file(virtualFile.getName())
            .line(line != null ? Integer.parseInt(line.toString()) : 1)
            .build();

      final Tour tour = Tour.builder()
            .id(UUID.randomUUID().toString())
            .title("LeC")
            .description("Yolo")
            .steps(Arrays.asList(step1, step2))
            .enabled(true)
            .build();
      state.createTour(tour);

      // new ToursStateComponent().getTours(project).add(tour);
      // PsiTreeUtil.getParentOfType(e.getData(CommonDataKeys.PSI_FILE).findElementAt(editor.getCaretModel().getOffset()), com.intellij.psi.PsiMethod.class);
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