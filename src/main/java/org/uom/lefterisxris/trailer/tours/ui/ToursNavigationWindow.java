package org.uom.lefterisxris.trailer.tours.ui;

import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.tree.ui.DefaultTreeUI;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Navigation Window
 *
 * @author Eleftherios Chrysochoidis
 * Date: 10/4/2022
 */
public class ToursNavigationWindow {

   private JButton previousButton;
   private JButton nextButton;
   private JTree toursTree;
   private JPanel tourPanel;

   int activeRow = -1;

   public ToursNavigationWindow(ToolWindow toolWindow) {


      final DefaultMutableTreeNode root = new DefaultMutableTreeNode("Code ToursState");
      for (int i = 1; i <= 3; i++) {
         final DefaultMutableTreeNode tour = new DefaultMutableTreeNode("Sample Tour " + i);
         for (int j = 1; j <= 10; j++)
            tour.add(new DefaultMutableTreeNode(String.format("Sample Tour %s/%s", i, j)));

         root.add(tour);
      }

      toursTree = new Tree(root);
      // toursTree.add(root);

      previousButton.addActionListener(e -> {
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

      this.populate();
   }

   private void populate() {

      // new DefaultTreeUI()
   }

   public JPanel getContent() {
      return tourPanel;
   }


   /*private void createUIComponents() {
      // TODO: place custom component creation code here
      final JLabel root = new JLabel("Code ToursState");
      for (int i = 1; i <= 3; i++) {
         final JLabel tour = new JLabel("Sample Tour " + i);
         for (int j = 1; j <= 10; j++)
            tour.add(new JLabel(String.format("Sample Tour %s/%s", i, j)));

         root.add(tour);
      }

      toursTree.add(root);
   }*/
}