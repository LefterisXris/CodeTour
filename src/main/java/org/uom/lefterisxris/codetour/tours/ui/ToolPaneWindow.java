package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.JBPopupMenu;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.Navigator;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.domain.Tour;
import org.uom.lefterisxris.codetour.tours.state.StateManager;
import org.uom.lefterisxris.codetour.tours.state.TourUpdateNotifier;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Class for Tours management
 *
 * @author Eleftherios Chrysochoidis
 * Date: 11/4/2022
 */
public class ToolPaneWindow {

   private static final Logger LOG = Logger.getInstance(ToolPaneWindow.class);

   private final JPanel panel;
   private Tree toursTree;

   private final Project project;
   private final StateManager stateManager;
   private DefaultMutableTreeNode selectedNode;

   public ToolPaneWindow(@NotNull Project project, @NotNull ToolWindow toolWindow) {

      this.project = project;
      this.stateManager = new StateManager(project);
      panel = new JPanel(new BorderLayout());
      panel.add(new JLabel("Tour Navigation UI"), BorderLayout.NORTH);

      createToursTee(project);

      createNavigationButtons();

      initMessageBus();
   }

   private void createToursTee(Project project) {

      final List<Tour> tours = stateManager.getTours();

      final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Code ToursState");

      final String activeId = StateManager.getActiveTour().map(tour -> tour.getId()).orElse("Null");
      tours.forEach(tour -> {
         final DefaultMutableTreeNode aTourNode = new DefaultMutableTreeNode(tour);
         LOG.info(String.format("Rendering Tour '%s' with %s steps%n", tour.getTitle(), tour.getSteps().size()));
         tour.getSteps().forEach(step -> aTourNode.add(new DefaultMutableTreeNode(step)));

         if (tour.getId().equals(activeId)) {
            //TODO: Set active in bold
         }

         root.add(aTourNode);
      });
      toursTree = new Tree(root);

      toursTree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            final int selectedRow = toursTree.getRowForLocation(e.getX(), e.getY());
            final TreePath pathSelected = toursTree.getPathForLocation(e.getX(), e.getY());

            if (selectedRow < 0 || pathSelected == null) {
               selectedNode = null;
               return;
            }


            if (pathSelected.getLastPathComponent() instanceof DefaultMutableTreeNode) {
               final DefaultMutableTreeNode node = (DefaultMutableTreeNode)pathSelected.getLastPathComponent();
               if (node.getUserObject() instanceof Tour) {
                  final Tour tour = (Tour)node.getUserObject();
                  // On Tour right click, show a context menu (Delete, Edit)
                  if (e.getButton() == MouseEvent.BUTTON3) {
                     final JBPopupMenu menu = new JBPopupMenu("Tour Context Menu");

                     // Add new Step
                     final JMenuItem newStepAction = new JMenuItem("Add new Step", AllIcons.Actions.AddFile);
                     newStepAction.addActionListener(d -> addNewStepOnTourListener(tour));

                     // Edit Action
                     final JMenuItem editAction = new JMenuItem("Edit Tour", AllIcons.Actions.Edit);
                     editAction.addActionListener(d -> editTourListener(tour));

                     // Delete Action
                     final JMenuItem deleteAction = new JMenuItem("Delete Tour", AllIcons.Actions.DeleteTag);
                     deleteAction.addActionListener(d -> deleteTourListener(tour));

                     Arrays.asList(newStepAction, editAction, deleteAction).forEach(menu::add);
                     menu.show(toursTree, e.getX(), e.getY());
                     return;
                  }
               }
               if (node.getUserObject() instanceof Step) {
                  selectedNode = node;
                  final Step step = (Step)node.getUserObject();
                  final DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)node.getParent();
                  final Tour tour = (Tour)parentNode.getUserObject();
                  StateManager.setActiveTour(tour);

                  // On Tour right click, show a context menu (Delete, Edit)
                  if (e.getButton() == MouseEvent.BUTTON3) {
                     final JBPopupMenu menu = new JBPopupMenu("Tour Context Menu");

                     // Edit Title Action
                     final JMenuItem editTitleAction = new JMenuItem("Edit Title", AllIcons.Actions.Edit);
                     editTitleAction.addActionListener(d -> editStepTitleListener(step, tour));

                     // Edit Description Action
                     final JMenuItem editDescriptionAction = new JMenuItem("Edit Description", AllIcons.Actions.Edit);
                     editDescriptionAction.addActionListener(d -> editStepDescriptionListener(step, tour));

                     // Delete Action
                     final JMenuItem deleteAction = new JMenuItem("Delete Step", AllIcons.Actions.DeleteTag);
                     deleteAction.addActionListener(d -> deleteStepListener(step, tour));

                     Arrays.asList(editTitleAction, editDescriptionAction, deleteAction).forEach(menu::add);
                     menu.show(toursTree, e.getX(), e.getY());
                     return;
                  }

                  Navigator.navigate(step, project);
               }
            }
         }
      });

      final JPanel treePanel = new JPanel(new BorderLayout());
      treePanel.setName("treePanel");
      final JBScrollPane scrollPane = new JBScrollPane(toursTree);
      treePanel.add(scrollPane, BorderLayout.CENTER);
      for (int i = 0; i < panel.getComponentCount(); i++) {
         if ("treePanel".equals(panel.getComponent(i).getName())) {
            panel.remove(i);
            break;
         }
      }
      panel.add(treePanel, BorderLayout.CENTER);
   }

   private void selectTourLastStep(Tour tour) {
      // Expand and select the last Step of the active Tour on the tree
      for (int i = 0; i < toursTree.getRowCount(); i++) {
         if (!toursTree.getPathForRow(i).getLastPathComponent().toString().equals(tour.getTitle())) continue;

         final Object component = toursTree.getPathForRow(i).getLastPathComponent();
         if (component instanceof DefaultMutableTreeNode) {
            final DefaultMutableTreeNode pNode = (DefaultMutableTreeNode)component;
            if (pNode.getUserObject() instanceof Tour) {
               toursTree.expandPath(new TreePath(pNode.getPath()));
               toursTree.getSelectionModel().setSelectionPath(new TreePath(pNode.getLastLeaf().getPath()));
            }
         }
      }
   }

   private void createNavigationButtons() {
      final JButton previousButton = new JButton("Previous Step");
      previousButton.addActionListener(e -> {
         LOG.info("Previous button pressed!");

         if (selectedNode == null || selectedNode.getPreviousSibling() == null) return;
         final DefaultMutableTreeNode previousNode = selectedNode.getPreviousSibling();
         if (previousNode.getUserObject() instanceof Step) {
            selectedNode = previousNode;
            toursTree.getSelectionModel().setSelectionPath(new TreePath(selectedNode.getPath()));
            final Step step = (Step)previousNode.getUserObject();
            Navigator.navigate(step, project);
         }

      });

      final JButton nextButton = new JButton("Next Step");
      nextButton.addActionListener(e -> {
         LOG.info("Next button pressed!");
         if (selectedNode == null || selectedNode.getNextSibling() == null) return;
         final DefaultMutableTreeNode nextNode = selectedNode.getNextSibling();
         if (nextNode.getUserObject() instanceof Step) {
            selectedNode = nextNode;
            toursTree.getSelectionModel().setSelectionPath(new TreePath(selectedNode.getPath()));
            final Step step = (Step)nextNode.getUserObject();
            Navigator.navigate(step, project);
         }
      });

      final JButton createNewButton = new JButton("Create New Tour");
      createNewButton.addActionListener(e -> {

         final long index = stateManager.getTours().stream()
               .map(tour -> tour.getTourFile())
               .filter(tourFile -> tourFile != null)
               .filter(tourFile -> tourFile.startsWith("newTour") && tourFile.endsWith(".tour"))
               .count();
         final String newTourFileName = String.format("newTour%s.tour", (index > 0 ? index : ""));

         final Tour newTour = Tour.builder()
               .id(UUID.randomUUID().toString())
               .touFile(newTourFileName)
               .title("A New Tour")
               .description("A New Tour")
               .steps(Arrays.asList(Step.builder()
                     .title("Step 1")
                     .description("Sample Description")
                     .file("")
                     .line(1)
                     .build()))
               .build();

         stateManager.createTour(newTour);
         createToursTee(project);
         CodeTourNotifier.notifyTourAction(project, newTour, "Creation",
               String.format("Tour '%s' (file %s) has been created", newTour.getTitle(), newTour.getTourFile()));

      });

      final JButton reloadButton = new JButton("Reload");
      reloadButton.addActionListener(e -> {
         LOG.info("Re-creating the tree");
         stateManager.reloadState();
         StateManager.setActiveTour(null); // reset the activeTour
         createToursTee(project);
      });

      final JPanel buttonsPanel = new JPanel();
      buttonsPanel.add(previousButton);
      buttonsPanel.add(nextButton);
      buttonsPanel.add(createNewButton);
      buttonsPanel.add(reloadButton);
      panel.add(buttonsPanel, BorderLayout.SOUTH);
   }

   public JPanel getContent() {
      return panel;
   }

   public void initMessageBus() {
      project.getMessageBus().connect().subscribe(TourUpdateNotifier.TOPIC, (tour) -> {
         stateManager.reloadState();
         createToursTee(project);
         selectTourLastStep(tour);
      });
   }

   private void addNewStepOnTourListener(Tour tour) {
      final long index = tour.getSteps().stream()
            .filter(step -> step.getTitle().startsWith("New Step"))
            .count();

      final String stepTitle = String.format("New Step%s", (index > 0) ? index : "");
      tour.getSteps().add(Step.builder()
            .title(stepTitle)
            .description("Step Description")
            .file("")
            .line(1)
            .build());
      stateManager.updateTour(tour);
      createToursTee(project);
      CodeTourNotifier.notifyTourAction(project, tour, "New Step",
            String.format("A New Step added on Tour '%s'", tour.getTitle()));

      // Expand and select the last Step of the active Tour on the tree
      selectTourLastStep(tour);
   }

   private void editTourListener(Tour tour) {

      final String updatedTitle = Messages.showInputDialog(project, "Edit Tour's title",
            "Edit Tour", AllIcons.Actions.Edit, tour.getTitle(), null);
      if (updatedTitle == null || updatedTitle.equals(tour.getTitle())) return;

      tour.setTitle(updatedTitle);
      stateManager.updateTour(tour);

      LOG.info("Active Tour: " + tour.getTitle());
      createToursTee(project);
      StateManager.setActiveTour(tour);
      CodeTourNotifier.notifyTourAction(project, tour, "Tour Update",
            String.format("Tour's '%s' Title has been updated", tour.getTitle()));

      // Expand and select the last Step of the active Tour on the tree
      selectTourLastStep(tour);
   }

   private void deleteTourListener(Tour tour) {
      stateManager.deleteTour(tour);
      createToursTee(project);
      CodeTourNotifier.notifyTourAction(project, tour, "Deletion", String.format("Tour " +
            "'%s' (file %s) has been deleted", tour.getTitle(), tour.getTourFile()));
   }

   private void editStepTitleListener(Step step, Tour tour) {
      final String updatedTitle = Messages.showInputDialog(project, "Edit Step's title",
            "Edit Step", AllIcons.Actions.Edit, step.getTitle(), null);
      if (updatedTitle == null || updatedTitle.equals(step.getTitle())) return;

      final Optional<Step> origStep = tour.getSteps().stream()
            .filter(s -> s.getTitle().equals(step.getTitle()))
            .findFirst();
      if (origStep.isEmpty()) {
         CodeTourNotifier.warn(project, String.format("Could not find Step '%s'. Edit failed", step.getTitle()));
         return;
      }
      origStep.get().setTitle(updatedTitle);

      stateManager.updateTour(tour);
      createToursTee(project);
      CodeTourNotifier.notifyTourAction(project, tour, "Step Update",
            String.format("Step's '%s' Title has been updated", step.getTitle()));

      // Expand and select the last Step of the active Tour on the tree
      selectTourLastStep(tour);
   }

   private void editStepDescriptionListener(Step step, Tour tour) {
      final String updatedDescription = Messages.showMultilineInputDialog(project, "Edit Step's description",
            "Edit Step", step.getDescription(), AllIcons.Actions.Edit, null);
      if (updatedDescription == null || updatedDescription.equals(step.getDescription())) return;

      final Optional<Step> origStep = tour.getSteps().stream()
            .filter(s -> s.getTitle().equals(step.getTitle()))
            .findFirst();
      if (origStep.isEmpty()) {
         CodeTourNotifier.warn(project, String.format("Could not find Step '%s'. Edit failed", step.getTitle()));
         return;
      }
      origStep.get().setDescription(updatedDescription);

      stateManager.updateTour(tour);
      createToursTee(project);
      CodeTourNotifier.notifyTourAction(project, tour, "Step Update",
            String.format("Step's '%s' Description has been updated", step.getTitle()));

      // Expand and select the last Step of the active Tour on the tree
      selectTourLastStep(tour);
   }

   private void deleteStepListener(Step step, Tour tour) {
      tour.getSteps().removeIf(tourStep -> tourStep.getTitle().equals(step.getTitle()));
      stateManager.updateTour(tour);
      createToursTee(project);
      CodeTourNotifier.notifyTourAction(project, tour, "Step Deletion", String.format("Step " +
            "'%s' has been removed from Tour '%s'", step.getTitle(), tour.getTitle()));

      // Expand and select the last Step of the active Tour on the tree
      selectTourLastStep(tour);
   }

}