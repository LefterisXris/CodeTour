package org.uom.lefterisxris.trailer.tours;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.trailer.tours.domain.TourStep;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

/**
 * This class ... TODO: Doc
 *
 * @author Eleftherios Chrysochoidis
 * Date: 16/4/2022
 */
public class Navigator {

   public static void navigate(@NotNull TourStep step, @NotNull Project project) {
      if (project.getBasePath() == null) return;

      final Collection<VirtualFile> virtualFiles = FilenameIndex.getVirtualFilesByName(project, step.getFile(),
            GlobalSearchScope.projectScope(project));
      if (!virtualFiles.isEmpty())
      new OpenFileDescriptor(project, new ArrayList<>(virtualFiles).get(0)).navigate(true);
   }

}