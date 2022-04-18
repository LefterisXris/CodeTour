package org.uom.lefterisxris.codetour.tours;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RunTourByNameAction extends AnAction {

   private final String title;
   private final String selected;

   public RunTourByNameAction(String title, String selected) {
      this.title = title;
      this.selected = selected;
   }

   @Override
   public void actionPerformed(@NotNull AnActionEvent e) {
      System.out.println("Yes! Someone called me!!!!");
   }
}