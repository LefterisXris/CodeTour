package org.uom.lefterisxris.trailer.tours;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.trailer.tours.domain.Tour;
import org.uom.lefterisxris.trailer.tours.domain.ToursState;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ToursStateComponent implements PersistentStateComponent<ToursState> {

   private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final ToursState state = new ToursState();
   private Project project;

   public ToursStateComponent() {
   }

   public ToursStateComponent(Project project) {
      this.project = project;
   }

   @Override
   public @Nullable ToursState getState() {
      return state;
   }

   @Override
   public void loadState(@NotNull ToursState state) {
      this.state.clear();
      this.state.getTours().addAll(state.getTours());
   }

   public Tour createTour(Tour tour) {
      //TODO:
      // 1. Check that title is unique
      // 2. Persist to file
      // 3. Reload the State
      final String fileName = tour.getTitle() + ".tour";

      System.out.printf("Saving Tour '%s' (%s steps) into file '%s'%n",
            tour.getTitle(), tour.getSteps().size(), fileName);

      // Persist the file
      try (FileWriter fileWriter = new FileWriter(
            Paths.get(project.getBasePath(), ".tours", fileName).toString())) {
         GSON.toJson(tour, fileWriter);
      } catch (IOException ex) {
         ex.printStackTrace();
         System.err.println(ex.getMessage());
      }
      reloadState();
      return tour;
   }

   public Tour updateTour(String tourName, Tour tour) {
      //TODO:
      // 1. Find Tour with given name
      // 2. Check whether the title has changed. If so, delete and create the new tour
      // 2. Persist the new object to file
      // 3. Reload the State
      deleteTour(tourName);
      createTour(tour);
      return tour;
   }

   public void deleteTour(String tourName) {
      //TODO:
      // 1. Find the file corresponding to the given tourName
      // 2. Delete the file
      // 3. Reload the State
      findTourFile(tourName).ifPresent(virtualFile -> {
         try {
            virtualFile.delete(this);
         } catch (IOException e) {
            e.printStackTrace();
         }
      });
      reloadState();
   }

   public Tour deleteTour(Tour tour) {
      //TODO:
      // 1. Find the file corresponding to the given tour
      // 2. Delete the file
      // 3. Reload the State
      findTourFile(tour.getTitle()).ifPresent(virtualFile -> {
         try {
            virtualFile.delete(this);
         } catch (IOException e) {
            e.printStackTrace();
         }
      });
      reloadState();
      return tour;
   }

   public List<Tour> reloadState() {
      state.clear();
      return getTours(project);
   }

   public List<Tour> getTours(Project project) {
      if (state.getTours().isEmpty())
         state.getTours().addAll(loadTours(project));
      return state.getTours();
   }

   public List<Tour> getTours() {
      if (state.getTours().isEmpty())
         state.getTours().addAll(loadTours(project));
      return state.getTours();
   }

   public List<Tour> reloadTours() {
      state.clear();
      state.getTours().addAll(loadTours(project));
      return state.getTours();
   }

   public List<Tour> persist(Project project, Tour tour) {
      if (project == null || project.getBasePath() == null) return getTours(project);

      final String fileName = tour.getTitle() + ".tour";

      System.out.printf("Saving Tour '%s' (%s steps) into file '%s'%n",
            tour.getTitle(), tour.getSteps().size(), fileName);

      // Persist the file
      try (FileWriter fileWriter = new FileWriter(
            Paths.get(project.getBasePath(), ".toursState", fileName).toString())) {
         GSON.toJson(tour, fileWriter);
      } catch (IOException ex) {
         ex.printStackTrace();
         System.err.println(ex.getMessage());
      }

      state.getTours().clear();
      return getTours(project);
   }

   public Optional<Tour> getActive() {
      return getTours().stream()
            .filter(tour -> tour.getEnabled())
            .findFirst();
   }

   public Tour setActive(Project project, String id) {
      final Optional<Tour> aTour = getTours().stream()
            .filter(tour -> tour.getId().equals(id))
            .findFirst();

      // Persist it
      aTour.ifPresent(tour -> {
         tour.setEnabled(true);
         persist(project, tour);
      });

      return getActive()
            .orElseThrow(() -> new RuntimeException("Could not find Enabled Tour"));
   }

   public boolean shouldNotify(Project project) {
      return true;
   }

   private List<Tour> loadTours(@NotNull Project project) {
      return project.getBasePath() == null ? loadFromIndex(project) : loadFromFS(project);
   }

   private List<Tour> loadFromIndex(@NotNull Project project) {
      return ReadAction.compute(() -> FilenameIndex.getAllFilesByExt(project, "tour").stream()
                  .map(virtualFile -> {
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
                  })
                  .filter(Objects::nonNull))
            .collect(Collectors.toList());
   }

   private List<Tour> loadFromFS(@NotNull Project project) {
      final VirtualFile virtualFile = ProjectUtil.guessProjectDir(project);
      if (virtualFile == null)
         return new ArrayList<>();

      List<Tour> tours = new ArrayList<>();
      VfsUtilCore.iterateChildrenRecursively(virtualFile,
            null,
            fileOrDir -> {
               if (!fileOrDir.isDirectory() && "tour".equals(fileOrDir.getExtension()))
                  parse(fileOrDir).ifPresent(tours::add);
               return true;
            });

      return tours;
   }

   private Optional<Tour> parse(VirtualFile file) {
      if (file.isDirectory())
         return Optional.empty();

      try {
         return Optional.of(new Gson().fromJson(new InputStreamReader(file.getInputStream()), Tour.class));
      } catch (IOException e) {
         e.printStackTrace();
         System.err.println("Skipping file: " + file.getName());
      }
      return Optional.empty();
   }


   private Optional<VirtualFile> findTourFile(String tourName) {
      return FilenameIndex.getAllFilesByExt(project, "tour").stream()
            .filter(file -> file.getName().equals(tourName))
            .findFirst();
   }
}