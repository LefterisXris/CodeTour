package org.uom.lefterisxris.codetour.tours.state;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.diagnostic.PluginException;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.domain.Props;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.domain.Tour;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * State Manager component to read/write the Tours on the project
 *
 * @author Eleftherios Chrysochoidis
 * Date: 7/1/2022
 */
public class StateManager {

   private static Optional<Tour> activeTour = Optional.empty();
   private static Optional<Integer> activeStepIndex = Optional.empty();
   private static final Logger LOG = Logger.getInstance(StateManager.class);

   private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final ToursState state = new ToursState();
   private final Project project;

   public StateManager(Project project) {
      this.project = project;
   }

   public Tour createTour(Tour tour) {
      //TODO:
      // 1. Check that title is unique
      // 2. Persist to file
      // 3. Reload the StateManager
      if (project.getBasePath() == null) return null;
      final String fileName = tour.getTourFile();

      LOG.info(String.format("Saving Tour '%s' (%s steps) into file '%s'%n",
            tour.getTitle(), tour.getSteps().size(), fileName));

      WriteAction.runAndWait(() -> {
         Optional<VirtualFile> toursDir = getToursDir();
         if (toursDir.isEmpty()) {
            toursDir = createToursDir();
            if (toursDir.isEmpty())
               throw new PluginException("Could not find or creat '.tours' directory. Tour creation failed",
                     PluginId.findId("org.uom.lefterisxris.codetour"));
         }
         // Persist the file
         try {
            final VirtualFile newTourVfile = toursDir.get().createChildData(this, fileName);
            newTourVfile.setBinaryContent(GSON.toJson(tour).getBytes(StandardCharsets.UTF_8));
            reloadState();
         } catch (IOException e) {
            LOG.error("Failed to create tour file: " + e.getMessage(), e);
         }
      });
      return tour;
   }

   public Tour updateTour(String tourId, Tour tour) {
      //TODO:
      // 1. Find Tour with given name
      // 2. Check whether the title has changed. If so, delete and create the new tour
      // 2. Persist the new object to file
      // 3. Reload the StateManager
      deleteTour(tourId);
      createTour(tour);
      return tour;
   }

   public Tour updateTour(Tour tour) {
      //TODO:
      // 1. Find Tour with given name
      // 2. Check whether the title has changed. If so, delete and create the new tour
      // 2. Persist the new object to file
      // 3. Reload the StateManager
      deleteTour(tour);
      createTour(tour);
      return tour;
   }

   public void deleteTour(String tourId) {
      //TODO:
      // 1. Find the file corresponding to the given tourId
      // 2. Delete the file
      // 3. Reload the StateManager
      findTourFile(tourId).ifPresent(virtualFile -> {
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
      findTourFile(tour).ifPresent(virtualFile -> {
         WriteAction.runAndWait(() -> {
            try {
               virtualFile.delete(this);
               reloadState();
            } catch (IOException e) {
               e.printStackTrace();
            }
         });
      });
      return tour;
   }

   public List<Tour> reloadState() {
      state.clear();
      return getTours();
   }

   public List<Tour> getTours() {
      if (state.getTours().isEmpty())
         state.getTours().addAll(loadTours(project));
      return state.getTours();
   }

   public boolean shouldNotify(Project project) {
      return true;
   }

   private List<Tour> loadTours(@NotNull Project project) {
      return project.getBasePath() == null ? loadFromIndex(project) : loadFromFS();
   }

   private List<Tour> loadFromIndex(@NotNull Project project) {
      return ReadAction.compute(() -> FilenameIndex.getAllFilesByExt(project, Props.TOUR_EXTENSION).stream()
                  .map(virtualFile -> {
                     Tour tour;
                     try {
                        LOG.info("Reading (from Index) Tour from file: " + virtualFile.getName());
                        tour = new Gson().fromJson(new InputStreamReader(virtualFile.getInputStream()), Tour.class);
                     } catch (IOException e) {
                        LOG.error("Skipping file: " + virtualFile.getName(), e);
                        return null;
                     }
                     tour.setTitle(virtualFile.getName());
                     return tour;
                  })
                  .filter(Objects::nonNull))
            .collect(Collectors.toList());
   }

   private List<Tour> loadFromFS() {
      final List<Tour> tours = new ArrayList<>();
      final Optional<VirtualFile> toursDir = getToursDir();
      if (toursDir.isEmpty()) return new ArrayList<>();

      VfsUtilCore.iterateChildrenRecursively(toursDir.get(),
            null,
            fileOrDir -> {
               if (!fileOrDir.isDirectory() && Props.TOUR_EXTENSION.equals(fileOrDir.getExtension()))
                  parse(fileOrDir).ifPresent(tours::add);
               return true;
            });
      return tours;
   }

   private Optional<Tour> parse(VirtualFile file) {
      if (file.isDirectory())
         return Optional.empty();

      try {
         LOG.info("Reading (from FS) Tour from file: " + file.getName());
         return Optional.of(
               new Gson().fromJson(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8), Tour.class));
      } catch (IOException e) {
         e.printStackTrace();
         LOG.error("Skipping file: " + file.getName());
      }
      return Optional.empty();
   }

   private Optional<VirtualFile> findTourFile(Tour tour) {
      final Optional<VirtualFile> toursDir = getToursDir();
      if (toursDir.isEmpty()) return Optional.empty();

      final List<VirtualFile> virtualFiles = new ArrayList<>();
      VfsUtilCore.iterateChildrenRecursively(toursDir.get(),
            null,
            fileOrDir -> {
               if (!fileOrDir.isDirectory() && tour.getTourFile().equals(fileOrDir.getName()))
                  virtualFiles.add(fileOrDir);
               return true;
            });

      return virtualFiles.isEmpty() ? Optional.empty() : Optional.of(virtualFiles.get(0));
   }

   private Optional<VirtualFile> findTourFile(String tourId) {
      return getTours().stream()
            .filter(tour -> tourId.equals(tour.getId()))
            .findFirst()
            .flatMap(tour -> findTourFile(tour));
   }

   private Optional<VirtualFile> getToursDir() {
      final VirtualFile virtualFile = ProjectUtil.guessProjectDir(project);
      if (virtualFile == null) return Optional.empty();

      return Arrays.stream(virtualFile.getChildren())
            .filter(file -> file.isDirectory() && file.getName().equals(Props.TOURS_DIR))
            .findFirst();
   }

   private Optional<VirtualFile> createToursDir() {
      final VirtualFile virtualFile = ProjectUtil.guessProjectDir(project);
      if (virtualFile == null) return Optional.empty();

      try {
         virtualFile.createChildDirectory(this, Props.TOURS_DIR);
      } catch (IOException e) {
         LOG.error("Failed to create .tours Directory: " + e.getMessage(), e);
         return Optional.empty();
      }

      return getToursDir();
   }

   public static Optional<Tour> getActiveTour() {
      return activeTour;
   }

   public static Optional<Integer> getActiveStepIndex() {
      return activeStepIndex;
   }

   /**
    * Retrieves the Next Step of the currently active Tour. Also updates the activeStepIndex
    */
   public static Optional<Step> getNextStep() {
      return getNextOrPrevStep(true);
   }

   /**
    * Retrieves the Previous Step of the currently active Tour. Also updates the activeStepIndex
    */
   public static Optional<Step> getPrevStep() {
      return getNextOrPrevStep(false);
   }

   private static Optional<Step> getNextOrPrevStep(boolean next) {
      final Optional<Tour> activeTour = getActiveTour();
      if (activeTour.isEmpty()) return Optional.empty();

      final Optional<Integer> activeIndex = getActiveStepIndex();
      if (activeIndex.isEmpty()) return Optional.empty();

      final int candidate = next ? activeIndex.get() + 1 : activeIndex.get() - 1;
      if (candidate >= 0 && activeTour.get().getSteps().size() > candidate) {
         setActiveStepIndex(candidate);
         return Optional.of(activeTour.get().getSteps().get(candidate));
      }

      return Optional.empty();
   }

   public static void setActiveTour(Tour aTour) {
      activeTour = Optional.ofNullable(aTour);
   }

   public static void setActiveStepIndex(Integer index) {
      activeStepIndex = Optional.ofNullable(index);
   }
}