package org.uom.lefterisxris.trailer.tours;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class CodeTrailerAction extends AnAction {

   @Override
   public void actionPerformed(@NotNull AnActionEvent e) {

      // Open the browser
      BrowserUtil.browse("https://github.com/LefterisXris/CodeTrailer");
   }

}