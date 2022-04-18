package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.Navigator;
import org.uom.lefterisxris.codetour.tours.ToursStateComponent;
import org.uom.lefterisxris.codetour.tours.domain.Tour;
import org.uom.lefterisxris.codetour.tours.domain.TourStep;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

/**
 * This class ... TODO: Doc
 *
 * @author Eleftherios Chrysochoidis
 * Date: 11/4/2022
 */
public class ToursNavigationWindow2 {

   private final JPanel panel;
   private Tree toursTree;

   private Project project;
   private int activeRow = -1;

   public ToursNavigationWindow2(@NotNull Project project, @NotNull ToolWindow toolWindow) {

      this.project = project;
      panel = new JPanel(new BorderLayout());
      panel.add(new JLabel("Tour Navigation UI"), BorderLayout.NORTH);

      createToursTee(project);

      createButtons();
   }

   private void createToursTee(Project project) {

      final List<Tour> tours = new ToursStateComponent(project).reloadTours();

      final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Code ToursState");

      tours.forEach(tour -> {
         final DefaultMutableTreeNode aTourNode = new DefaultMutableTreeNode(tour);
         tour.getSteps().forEach(step -> {
            aTourNode.add(new DefaultMutableTreeNode(step));
         });
         root.add(aTourNode);
      });
      /*for (int i = 1; i <= 3; i++) {
         final DefaultMutableTreeNode tour = new DefaultMutableTreeNode("Sample Tour " + i);
         for (int j = 1; j <= 10; j++) {
            final DefaultMutableTreeNode step = new DefaultMutableTreeNode(String.format("Sample Tour %s/%s", i, j));
            step.setUserObject(TourStep.builder().title("LeC Tour Step : " + j).build());
            tour.add(step);
         }
         root.add(tour);
      }*/
      toursTree = new Tree(root);

      toursTree.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            activeRow = -1;
            final int selectedRow = toursTree.getRowForLocation(e.getX(), e.getY());
            final TreePath selectedPath = toursTree.getPathForLocation(e.getX(), e.getY());
            if (selectedRow >= 0 && selectedPath != null) {
               if (selectedPath.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                  final DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
                  if (node.getUserObject() instanceof Tour) {
                     activeRow = selectedRow;
                  } else if (node.getUserObject() instanceof TourStep) {
                     final TourStep step = (TourStep)node.getUserObject();
                     Navigator.navigate(step, project);
                  }
               }
            }
         }
      });

      final JPanel treePanel = new JPanel();
      treePanel.setName("treePanel");
      treePanel.add(toursTree);
      for (int i = 0; i < panel.getComponentCount(); i++) {
         if ("treePanel".equals(panel.getComponent(i).getName())) {
            panel.remove(i);
            break;
         }
      }
      panel.add(treePanel, BorderLayout.CENTER);
   }

   private void createButtons() {
      final JButton previousButton = new JButton("Previous Step");
      previousButton.addActionListener(e -> {
         System.out.println("Previous button pressed!");

         // collapse All ToursState
         for (int i = 0; i < toursTree.getRowCount(); i++)
            toursTree.collapseRow(i);

         // expand Next/Active Tour
         activeRow++;
         if (activeRow > toursTree.getRowCount() - 1) activeRow = 0;

         toursTree.expandRow(activeRow);

         // select Next Step

      });
      final JButton nextButton = new JButton("Next Button");
      nextButton.addActionListener(e -> {
         System.out.println("Next button pressed!");
      });

      final JButton setActiveButton = new JButton("Set Active");
      setActiveButton.addActionListener(e -> {
         // Check if there is any selected Tour
         // If so, deactivate the previously active
         // Activate the new one

         if (activeRow < 0) return;
         final TreePath path = toursTree.getPathForRow(activeRow);
         if (path == null) return;

         final ToursStateComponent state = new ToursStateComponent(project);
         if (path.getLastPathComponent() instanceof DefaultMutableTreeNode) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
            if (node.getUserObject() instanceof Tour) {
               final Optional<Tour> prev = state.getActive();
               if (prev.isPresent()) {
                  System.out.println("De-activating Tour " + prev.get());
                  prev.get().setEnabled(false);
                  state.updateTour(prev.get().getTitle(), prev.get());
               }
               final Tour active = (Tour)node.getUserObject();
               final Optional<Tour> tourToEnable = state.getTours().stream()
                     .filter(tour -> tour.getTitle().equals(active.getTitle()))
                     .findFirst();
               if (tourToEnable.isPresent()) {
                  System.out.println("Activating Tour " + tourToEnable.get());
                  tourToEnable.get().setEnabled(true);
                  state.updateTour(tourToEnable.get().getTitle(), tourToEnable.get());
               } else
                  System.out.println("Could not find the Tour to activate it");
            }
         }

      });

      final JButton reloadButton = new JButton("Reload");
      reloadButton.addActionListener(e -> {
         System.out.println("Re-creating the tree");
         createToursTee(project);
      });

      final JPanel buttonsPanel = new JPanel();
      buttonsPanel.add(previousButton);
      buttonsPanel.add(nextButton);
      buttonsPanel.add(setActiveButton);
      buttonsPanel.add(reloadButton);
      panel.add(buttonsPanel, BorderLayout.SOUTH);
   }


   public JPanel getContent() {
      return panel;
   }

}