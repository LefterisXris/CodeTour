package org.uom.lefterisxris.trailer.tours;

import com.google.gson.Gson;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.FilenameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.trailer.tours.domain.Tour;
import org.uom.lefterisxris.trailer.tours.domain.Tours;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ToursStateComponent implements PersistentStateComponent<Tours> {

   final Tours tours = new Tours();

   @Override
   public @Nullable Tours getState() {
      return tours;
   }

   @Override
   public void loadState(@NotNull Tours state) {
      tours.getTours().clear();
      tours.getTours().addAll(state.getTours());
   }

   public List<Tour> getTours(Project project) {
      if (tours.getTours().isEmpty()) {
         final List<Tour> loadedTours = ReadAction.compute(() -> FilenameIndex.getAllFilesByExt(project, "tour")
               .stream().map(virtualFile -> {
                  Tour tour;
                  try {
                     tour = new Gson().fromJson(new InputStreamReader(virtualFile.getInputStream()), Tour.class);
                  } catch (IOException e) {
                     e.printStackTrace();
                     System.err.println("Skipping file: " + virtualFile.getName());
                     return null;
                  }
                  tour.setTitle(virtualFile.getName());
                  return tour;
               }).filter(Objects::nonNull)).collect(Collectors.toList());
         tours.getTours().addAll(loadedTours);
      }
      return tours.getTours();
   }

   public boolean shouldNotify(Project project) {
      return true;
   }
}