package org.uom.lefterisxris.codetour.other;

import com.intellij.ide.BrowserUtil;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SearchAction extends AnAction {

   @Override
   public void actionPerformed(@NotNull AnActionEvent e) {

      // Infer the language
      final Optional<PsiFile> psiFile = Optional.ofNullable(e.getData((LangDataKeys.PSI_FILE)));
      final String languageTag = psiFile.map(PsiFile::getLanguage)
            .map(Language::getDisplayName)
            .map(String::toLowerCase)
            .map(lang -> String.format("[%s]", lang))
            .orElse("");

      // Get the highlighted text
      final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
      final CaretModel caretModel = editor.getCaretModel();
      final String selectedText = caretModel.getCurrentCaret().getSelectedText();
      if (!caretModel.getCurrentCaret().hasSelection() || selectedText == null || selectedText.isEmpty())
         return; // nothing to do here

      final String queryText = selectedText.replace(" ", "+");

      // Search for the question
      BrowserUtil.browse(String.format("https://stackoverflow.com/search?q=%s%s", languageTag, queryText));
   }

   @Override
   public void update(@NotNull AnActionEvent e) {
      final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
      final CaretModel caretModel = editor.getCaretModel();
      e.getPresentation().setEnabledAndVisible(caretModel.getCurrentCaret().hasSelection());
   }

}