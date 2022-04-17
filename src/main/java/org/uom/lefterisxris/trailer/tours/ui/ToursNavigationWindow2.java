package org.uom.lefterisxris.trailer.tours.ui;

import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.treeStructure.Tree;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.trailer.tours.Navigator;
import org.uom.lefterisxris.trailer.tours.ToursStateComponent;
import org.uom.lefterisxris.trailer.tours.domain.Tour;
import org.uom.lefterisxris.trailer.tours.domain.TourStep;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This class ... TODO: Doc
 *
 * @author Eleftherios Chrysochoidis
 * Date: 11/4/2022
 */
public class ToursNavigationWindow2 {

   private JPanel panel;
   private Tree toursTree;
   private JButton previousButton;
   private JButton nextButton;
   private JButton reloadButton;

   Project project;
   int activeRow = -1;

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
            final int selectedRow = toursTree.getRowForLocation(e.getX(), e.getY());
            final TreePath selectedPath = toursTree.getPathForLocation(e.getX(), e.getY());
            if (selectedRow >= 0 && selectedPath != null) {
               System.out.println("Yes! " + selectedPath);
               if (selectedPath.getLastPathComponent() instanceof DefaultMutableTreeNode) {
                  final DefaultMutableTreeNode node = (DefaultMutableTreeNode)selectedPath.getLastPathComponent();
                  if (node.getUserObject() instanceof TourStep) {
                     final TourStep step = (TourStep)node.getUserObject();
                     Navigator.navigate(step, project);
                  }
               }
            }
         }
      });

      final JPanel treePanel = new JPanel();
      treePanel.add(toursTree);
      panel.add(treePanel, BorderLayout.CENTER);
   }

   private void createButtons() {
      previousButton = new JButton("Previous Step");
      previousButton.addActionListener(e -> {
         System.out.println("Previous button pressed!");

         // collapse All ToursState
         for (int i = 0; i < toursTree.getRowCount(); i++)
            toursTree.collapseRow(i);

         // expand Next/Active Tour
         activeRow++;
         if (activeRow > toursTree.getRowCount() - 1)
            activeRow = 0;

         toursTree.expandRow(activeRow);

         // select Next Step

      });
      nextButton = new JButton("Next Button");
      nextButton.addActionListener(e -> {
         System.out.println("Next button pressed!");
      });

      reloadButton = new JButton("Reload");
      reloadButton.addActionListener(e -> {
         System.out.println("Re-creating the tree");
         createToursTee(project);
      });

      final JPanel buttonsPanel = new JPanel();
      buttonsPanel.add(previousButton);
      buttonsPanel.add(nextButton);
      buttonsPanel.add(reloadButton);
      panel.add(buttonsPanel, BorderLayout.SOUTH);
   }


   public JPanel getContent() {
      return panel;
   }

}