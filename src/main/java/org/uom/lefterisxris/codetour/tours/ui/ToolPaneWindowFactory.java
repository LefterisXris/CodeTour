package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class to generate the related Tool Pane Window
 *
 * @author Eleftherios Chrysochoidis
 * Date: 10/4/2022
 */
public class ToolPaneWindowFactory implements ToolWindowFactory {


   @Override
   public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

      final ToolPaneWindow toursNavigationWindow = new ToolPaneWindow(project, toolWindow);
      final ContentFactory contentFactory = ContentFactory.getInstance();
      final Content content =
            contentFactory.createContent(toursNavigationWindow.getContent(), null, false);
      toolWindow.getContentManager().addContent(content);
   }
}