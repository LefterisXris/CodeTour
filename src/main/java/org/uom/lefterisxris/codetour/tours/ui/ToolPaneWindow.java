package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.Navigator;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.domain.Tour;
import org.uom.lefterisxris.codetour.tours.state.StateManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Class for Tours management
 *
 * @author Eleftherios Chrysochoidis
 * Date: 11/4/2022
 */
public class ToolPaneWindow {

   private final JPanel panel;
   private Tree toursTree;

   private Project project;
   private DefaultMutableTreeNode selectedNode;

   public ToolPaneWindow(@NotNull Project project, @NotNull ToolWindow toolWindow) {

      this.project = project;
      panel = new JPanel(new BorderLayout());
      panel.add(new JLabel("Tour Navigation UI"), BorderLayout.NORTH);

      createToursTee(project);

      createNavigationButtons();
   }

   private void createToursTee(Project project) {

      final List<Tour> tours = new StateManager(project).reloadTours();

      final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Code ToursState");

      tours.forEach(tour -> {
         final DefaultMutableTreeNode aTourNode = new DefaultMutableTreeNode(tour);
         tour.getSteps().forEach(step -> {
            aTourNode.add(new DefaultMutableTreeNode(step));
         });
         root.add(aTourNode);
      });
      toursTree = new Tree(root);

      toursTree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            // activeRow = -1;
            final int selectedRow = toursTree.getRowForLocation(e.getX(), e.getY());
            final TreePath pathSelected = toursTree.getPathForLocation(e.getX(), e.getY());

            if (selectedRow < 0 || pathSelected == null) {
               selectedNode = null;
               return;
            }

            // On right click, show a context menu
            if (e.getButton() == MouseEvent.BUTTON3) {
               // Delete
            }

            if (pathSelected.getLastPathComponent() instanceof DefaultMutableTreeNode) {
               final DefaultMutableTreeNode node = (DefaultMutableTreeNode)pathSelected.getLastPathComponent();
               if (node.getUserObject() instanceof Step) {
                  selectedNode = node;
                  final Step step = (Step)node.getUserObject();
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

   private void createNavigationButtons() {
      final JButton previousButton = new JButton("Previous Step");
      previousButton.addActionListener(e -> {
         System.out.println("Previous button pressed!");

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
         System.out.println("Next button pressed!");
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

         final Tour newTour = Tour.builder()
               .id(UUID.randomUUID().toString())
               .touFile("newTour.tour")
               .title("A New Tour")
               .description("A New Tour")
               .enabled(false)
               .steps(Arrays.asList(Step.builder()
                     .title("Step 1")
                     .description("Sample Description")
                     .file("")
                     .line(1)
                     .build()))
               .build();

         final StateManager stateManager = new StateManager(project);
         stateManager.createTour(newTour);
         createToursTee(project);

      });
      /*setActiveButton.addActionListener(e -> {
         // Check if there is any selected Tour
         // If so, deactivate the previously active
         // Activate the new one

         if (activeRow < 0) return;
         final TreePath path = toursTree.getPathForRow(activeRow);
         if (path == null) return;

         final StateManager stateManager = new StateManager(project);
         if (path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
            if (node.getUserObject() instanceof Tour) {
               final Optional<Tour> prev = stateManager.getActive();
               if (prev.isPresent()) {
                  System.out.println("De-activating Tour " + prev.get());
                  prev.get().setEnabled(false);
                  stateManager.updateTour(prev.get().getTitle(), prev.get());
               }
               final Tour active = (Tour)node.getUserObject();
               final Optional<Tour> tourToEnable = stateManager.getTours().stream()
                     .filter(tour -> tour.getTitle().equals(active.getTitle()))
                     .findFirst();
               if (tourToEnable.isPresent()) {
                  System.out.println("Activating Tour " + tourToEnable.get());
                  tourToEnable.get().setEnabled(true);
                  stateManager.updateTour(tourToEnable.get().getTitle(), tourToEnable.get());
               } else
                  System.out.println("Could not find the Tour to activate it");
            }
         }

      });*/

      final JButton reloadButton = new JButton("Reload");
      reloadButton.addActionListener(e -> {
         System.out.println("Re-creating the tree");
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

}