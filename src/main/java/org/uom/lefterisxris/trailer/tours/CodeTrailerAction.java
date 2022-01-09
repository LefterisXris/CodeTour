package org.uom.lefterisxris.trailer.tours;

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
import org.uom.lefterisxris.trailer.tours.domain.Tour;
import org.uom.lefterisxris.trailer.tours.domain.TourStep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Generates Tours from existing Breakpoints, on groups starting with the identified 'NewTour'
 */
public class CodeTrailerAction extends AnAction {

   private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

   @Override
   public void actionPerformed(@NotNull AnActionEvent e) {
      final Project project = e.getProject();
      if (isNull(project))
         return;

      final XBreakpointManager breakpointManager = XDebuggerManager.getInstance(project).getBreakpointManager();
      Map<String, Tour> tours = new TreeMap<>();
      Arrays.stream(breakpointManager.getAllBreakpoints())
            .filter(bp -> bp instanceof XLineBreakpointImpl)
            .filter(bp -> ((XLineBreakpointImpl<?>)bp).getGroup() != null)
            .forEach(bp -> {
               System.out.println("Got a BP: Check details");
               final String group = ((XLineBreakpointImpl<?>)bp).getGroup();

               if (nonNull(group) && group.startsWith("NewTour")) {
                  final String groupName = group.replace("NewTour", "");
                  System.out.printf("New Tour With Name: '%s' Requested!!!!%n", groupName);
                  final Tour tour = tours.computeIfAbsent(groupName,
                        s -> Tour.builder()
                              .title(groupName)
                              .steps(new ArrayList<>())
                              .description("Breakpoints Generated Tour")
                              .build());

                  final TourStep.TourStepBuilder stepBuilder = TourStep.builder()
                        .description("TODO: Add step description");
                  final XSourcePosition sourcePosition = bp.getSourcePosition();
                  if (isNull(sourcePosition))
                     return;
                  if (nonNull(sourcePosition.getFile()))
                     stepBuilder.file(sourcePosition.getFile().getPath());
                  stepBuilder.line(sourcePosition.getLine());

                  final TourStep step = stepBuilder.title("Step " + tour.getSteps().size()).build();
                  tour.getSteps().add(step);
               }
               // bp.getType().getId() == java-line
               // bp.getSourcePosition().getFile().getPath()
               // bp.getSourcePosition().getLine()
            });
      tours.values().forEach(tour -> {
         final String tourJson = GSON.toJson(tour);
         final String fileName = tour.getTitle() + ".tour";
         // TODO: Persist the file
         System.out.printf("Parsed BreakPointGroup '%s' (%s steps) and ready to persist it into file '%s' " +
               "with content '%s'%n", tour.getTitle(), tour.getSteps().size(), fileName, tourJson);
      });

      // myType.myId = java-exception or myState.myTypeId
      // myState.myGroup

   }

}