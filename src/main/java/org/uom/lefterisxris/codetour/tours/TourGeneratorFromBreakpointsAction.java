package org.uom.lefterisxris.codetour.tours;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.xdebugger.XDebuggerManager;
import com.intellij.xdebugger.XSourcePosition;
import com.intellij.xdebugger.breakpoints.XBreakpointManager;
import com.intellij.xdebugger.impl.breakpoints.XLineBreakpointImpl;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.domain.Tour;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Generates ToursState from existing Breakpoints, on groups starting with the identified 'NewTour'
 */
public class TourGeneratorFromBreakpointsAction extends AnAction {

   private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
   private final String TOUR_GROUP_PREFIX = "NewTour";

   @Override
   public void actionPerformed(@NotNull AnActionEvent e) {
      final Project project = e.getProject();
      if (isNull(project) || isNull(project.getBasePath()))
         return;

      final XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
      Map<String, Tour> tours = new TreeMap<>();
      Arrays.stream(breakpointManager.getAllBreakpoints())
            .filter(bp -> bp instanceof XLineBreakpointImpl)
            .filter(bp -> ((XLineBreakpointImpl<?>)bp).getGroup() != null)
            .forEach(bp -> {
               final String group = ((XLineBreakpointImpl<?>)bp).getGroup();

               if (nonNull(group) && group.startsWith(TOUR_GROUP_PREFIX)) {
                  final String groupName = group.replace(TOUR_GROUP_PREFIX, "");
                  System.out.printf("New Tour With Name: '%s' Requested!!!!%n", groupName);
                  final Tour tour = tours.computeIfAbsent(groupName,
                        s -> Tour.builder()
                              .title(groupName)
                              .steps(new ArrayList<>())
                              .description("Breakpoints Generated Tour")
                              .build());

                  final Step.TourStepBuilder stepBuilder = Step.builder()
                        .description("TODO: Add step description");
                  final XSourcePosition sourcePosition = bp.getSourcePosition();
                  if (isNull(sourcePosition))
                     return;
                  if (nonNull(sourcePosition.getFile()))
                     stepBuilder.file(sourcePosition.getFile().getPath());
                  stepBuilder.line(sourcePosition.getLine());

                  final Step step = stepBuilder.title("Step " + tour.getSteps().size()).build();
                  tour.getSteps().add(step);
               }
            });

      tours.values().forEach(tour -> {
         final String fileName = tour.getTitle() + ".tour";

         System.out.printf("Parsed BreakPointGroup '%s' (%s steps) and ready to persist it into file '%s'%n",
               tour.getTitle(), tour.getSteps().size(), fileName);
         // Persist the file
         try (FileWriter fileWriter = new FileWriter(Paths.get(project.getBasePath(), ".toursState", fileName).toString())) {
            GSON.toJson(tour, fileWriter);
         } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println(ex.getMessage());
         }

      });

   }

}