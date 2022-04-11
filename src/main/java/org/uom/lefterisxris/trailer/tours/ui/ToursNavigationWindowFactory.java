package org.uom.lefterisxris.trailer.tours.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Factory class to generate the ToursNavigationWindow2
 *
 * @author Eleftherios Chrysochoidis
 * Date: 10/4/2022
 */
public class ToursNavigationWindowFactory implements ToolWindowFactory {


   @Override
   public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {

      final ToursNavigationWindow2 toursNavigationWindow = new ToursNavigationWindow2(toolWindow);
      final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
      final Content content =
            contentFactory.createContent(toursNavigationWindow.getContent(), "Tours Nav Window", false);
      toolWindow.getContentManager().addContent(content);
   }
}