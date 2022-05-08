package org.uom.lefterisxris.codetour.tours;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.ui.CodeTourNotifier;
import org.uom.lefterisxris.codetour.tours.ui.StepDocumentationRenderer;

import java.util.ArrayList;
import java.util.Collection;

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

      final Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName(project, step.getFile(),
            GlobalSearchScope.projectScope(project));
      if (!virtualFiles.isEmpty())
         new OpenFileDescriptor(project, new ArrayList<>(virtualFiles).get(0), step.getLine(), 1).navigate(true);

      CodeTourNotifier.notifyStepDescription(project, step.getDescription());

      //TODO: It would be nice to provide the Tour as well, for info like 1/5 steps etc
      new StepDocumentationRenderer(step, project)
            .showDoc();
   }

}