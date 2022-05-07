package org.uom.lefterisxris.codetour.tours.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.domain.Tour;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * State Manager component to persist tours between IDE restarts
 *
 * @author Eleftherios Chrysochoidis
 * Date: 7/1/2022
 */
public class StateManager {

   private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final ToursState state = new ToursState();
   private Project project;

   public StateManager() {
   }

   public StateManager(Project project) {
      this.project = project;
   }

   public Tour createTour(Tour tour) {
      //TODO:
      // 1. Check that title is unique
      // 2. Persist to file
      // 3. Reload the StateManager
      final String fileName = tour.getTourFile();

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
      // 3. Reload the StateManager
      deleteTour(tourName);
      createTour(tour);
      return tour;
   }

   public void deleteTour(String tourName) {
      //TODO:
      // 1. Find the file corresponding to the given tourName
      // 2. Delete the file
      // 3. Reload the StateManager
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
      // 3. Reload the StateManager
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

      final String fileName = tour.getTourFile();

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
                        System.out.println("Reading (from Index) Tour from file: " + virtualFile.getName());
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
      final Optional<VirtualFile> toursDir = Arrays.stream(virtualFile.getChildren())
            .filter(file -> file.isDirectory() && file.getName().equals(".tours"))
            .findFirst();
      toursDir.ifPresent(dir -> Arrays.stream(dir.getChildren())
            .filter(file -> !file.isDirectory() && "tour".equals(file.getExtension()))
            .map(this::parse)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .forEach(tours::add));

      /*VfsUtilCore.iterateChildrenRecursively(virtualFile,
            null,
            fileOrDir -> {
               if (!fileOrDir.isDirectory() && "tour".equals(fileOrDir.getExtension()))
                  parse(fileOrDir).ifPresent(tours::add);
               return true;
            });*/

      return tours;
   }

   private Optional<Tour> parse(VirtualFile file) {
      if (file.isDirectory())
         return Optional.empty();

      try {
         System.out.println("Reading (from FS) Tour from file: " + file.getName());
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