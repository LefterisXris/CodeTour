package org.uom.lefterisxris.codetour.tours.ui;

import com.intellij.ui.render.LabelBasedRenderer;
import org.jetbrains.annotations.NotNull;
import org.uom.lefterisxris.codetour.tours.domain.Props;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.domain.Tour;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * Handles the rendering of each tree item. It is currently used for defining icons
 *
 * @author Eleftherios Chrysochoidis
 * Date: 10/5/2022
 */
public class TreeRenderer extends LabelBasedRenderer.Tree {

   private String selectedTourId;

   public TreeRenderer(String selectedTourId) {this.selectedTourId = selectedTourId;}


   @Override
   public @NotNull Component getTreeCellRendererComponent(@NotNull JTree tree, Object value, boolean sel,
                                                          boolean expanded, boolean leaf,
                                                          int row, boolean hasFocus) {

      final Component component = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      if (value instanceof DefaultMutableTreeNode) {
         final DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
         if (node.getUserObject() instanceof Tour) {
            final Tour tour = (Tour)node.getUserObject();
            if (tour.getId() != null && tour.getId().equals(selectedTourId))
               setIcon(Props.ICON_XS);
         } else if (node.getUserObject() instanceof Step) {
            setIcon(Props.STEP);
         }
      }

      return component;
   }

   public void setSelectedTourId(String selectedTourId) {
      this.selectedTourId = selectedTourId;
   }
}