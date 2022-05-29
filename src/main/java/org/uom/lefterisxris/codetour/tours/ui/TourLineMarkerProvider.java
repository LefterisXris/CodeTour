package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.codeInsight.daemon.GutterName;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import icons.CodeTourIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.codetour.tours.state.StateManager;
import org.uom.lefterisxris.codetour.tours.state.StepSelectionNotifier;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eleftherios Chrysochoidis
 * Date: 21/5/2022
 */
public class TourLineMarkerProvider extends LineMarkerProviderDescriptor {

   private final Map<String, PsiElement> markedLines = new HashMap<>();

   @Override
   public @Nullable("null means disabled") @GutterName String getName() {
      return "CodeTour step";
   }

   @Override
   public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
      final PsiFile containingFile = element.getContainingFile();

      if (element instanceof LeafPsiElement && containingFile != null &&
            StateManager.isFileIncludedInAnyStep(containingFile.getName())) {
         final Document document = PsiDocumentManager.getInstance(element.getProject()).getDocument(containingFile);
         if (document != null) {
            final int lineNumber = document.getLineNumber(element.getTextOffset()) + 1;
            final String fileLine = String.format("%s:%s", containingFile.getName(), lineNumber);
            if (StateManager.isValidStep(containingFile.getName(), lineNumber)) {
               if (!markedLines.containsKey(fileLine) || element.equals(markedLines.get(fileLine))) {
                  markedLines.put(fileLine, element);
                  return new LineMarkerInfo<>(element, element.getTextRange(),
                        CodeTourIcons.STEP,
                        psiElement -> "Code Tour Step",
                        (e, elt) -> {
                           new StateManager(element.getProject()).findStepByFileLine(containingFile.getName(),
                                 lineNumber).ifPresent(step -> {
                              // Notify UI to select the step which will trigger its navigation
                              element.getProject().getMessageBus().syncPublisher(StepSelectionNotifier.TOPIC)
                                    .selectStep(step);
                           });
                        },
                        GutterIconRenderer.Alignment.CENTER,
                        () -> "Code Tour Step accessible");
               }
            }
         }
      }
      return null;
   }

}