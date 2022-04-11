package org.uom.lefterisxris.trailer.tours.domain;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.TextRange;
import com.intellij.pom.Navigatable;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import lombok.Builder;
import lombok.Data;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@Data
public class TourStep implements Navigatable {
   private String description;
   private String file;
   private String directory;
   private String uri;
   private int line;
   private String pattern;
   private String title;

   public TourStep() {
   }

   @Builder
   public TourStep(String description, String file, String directory, String uri, int line, String pattern,
                   String title) {
      this.description = description;
      this.file = file;
      this.directory = directory;
      this.uri = uri;
      this.line = line;
      this.pattern = pattern;
      this.title = title;
   }

   @Override
   public String toString() {
      return "TourStep{" +
            "title='" + title + '\'' +
            '}';
   }

   @Override
   public void navigate(boolean requestFocus) {
      final NavigatablePsiElement myElement;
   }

   @Override
   public boolean canNavigate() {
      return true;
   }

   @Override
   public boolean canNavigateToSource() {
      return true;
   }
}