package org.uom.lefterisxris.codetour.other;

import com.intellij.ide.favoritesTreeView.AbstractFavoritesListProvider;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.TreeState;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.ui.CheckedTreeNode;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.popup.util.DetailView;
import com.intellij.ui.popup.util.ItemWrapper;
import com.intellij.ui.treeStructure.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import com.intellij.xdebugger.breakpoints.ui.XBreakpointGroup;
import com.intellij.xdebugger.impl.breakpoints.ui.BreakpointItem;
import com.intellij.xdebugger.impl.breakpoints.ui.grouping.XBreakpointCustomGroup;
import com.intellij.xdebugger.impl.breakpoints.ui.tree.BreakpointItemNode;
import com.intellij.xdebugger.impl.breakpoints.ui.tree.BreakpointsGroupNode;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uom.lefterisxris.codetour.tours.domain.Tour;
import org.uom.lefterisxris.codetour.tours.domain.Step;
import org.uom.lefterisxris.codetour.tours.state.StateManager;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.*;

public class TourFavoritesListProvider extends AbstractFavoritesListProvider<String> {

   // final TourItemsTreeController treeController;
   final List<Tour> tours;

   protected TourFavoritesListProvider(@NotNull Project project) {
      super(project, "ToursState", "ToursState for Code Navigation");
      tours = new StateManager(myProject).getTours();

      // treeController = new TourItemsTreeController();
      // treeController.setTreeView(new ToursSimpleTree(project, treeController));
      // updateChildren();
      updateNodes();
   }

   private void updateNodes() {
      if (myProject.isDisposed())
         return;
      myChildren.clear();

      final Optional<Tour> tour = tours.stream().filter(t -> t.getTitle().startsWith("MyFirst")).findFirst();
      if (tour.isEmpty())
         return;
      tour.get().getSteps().forEach(step -> {
         myChildren.add(new TourNode(myProject, step.getTitle(), tour.get()));
      });


      /*new StateManager().getTours(myProject).stream()
            .map(Tour::getTitle)
            .map(t -> new FavoritesListNode(myProject, t))
            .forEach(myChildren::add);*/

      // new XBreakpointItem()
      // treeController.rebuildTree();

      /*new StateManager().getTours(myProject).forEach(tour -> {
         final FavoritesListNode tourNode = new FavoritesListNode(myProject, tour.getTitle());
         tour.getSteps().forEach(step -> {
            final List<FavoritesListNode> children = (ArrayList<FavoritesListNode>)tourNode.getChildren();
            children.add(new FavoritesListNode(myProject, step.getTitle()));
         });
         myChildren.add(tourNode);
      });*/

      /*for (int i = 0; i < 10; i++) {
         myChildren.add(new FavoritesListNode(myProject, "Yolo --- " + i));
      }*/
   }

   @Override
   public int getWeight() {
      return 0;
   }

   /*private void updateChildren() {
      if (myProject.isDisposed()) return;
      myChildren.clear();
      final List<TourItem> items = new StateManager().getTours(myProject).stream().map(t -> new TourItem(t))
            .collect(Collectors.toList());
      treeController.rebuildTree(items);


      CheckedTreeNode root = treeController.getRoot();
      for (int i = 0; i < root.getChildCount(); i++) {
         TreeNode child = root.getChildAt(i);
         if (child instanceof DefaultMutableTreeNode) {
            replicate((DefaultMutableTreeNode)child, myNode, myChildren);
         }
      }
      FavoritesManager.getInstance(myProject).fireListeners(getListName(myProject));
   }*/

   private void replicate(DefaultMutableTreeNode source, AbstractTreeNode<?> destination,
                          List<? super AbstractTreeNode<String>> destinationChildren) {
      List<AbstractTreeNode<String>> copyChildren = new ArrayList<>();
      AbstractTreeNode<String> copy = new AbstractTreeNode<String>(myProject, source.getUserObject().toString()) {
         @NotNull
         @Override
         public Collection<? extends AbstractTreeNode<?>> getChildren() {
            return copyChildren;
         }

         @Override
         protected void update(@NotNull PresentationData presentation) {
         }
      };

      for (int i = 0; i < source.getChildCount(); i++) {
         final TreeNode treeNode = source.getChildAt(i);
         if (treeNode instanceof DefaultMutableTreeNode) {
            final DefaultMutableTreeNode sourceChild = (DefaultMutableTreeNode)treeNode;
            replicate(sourceChild, copy, copyChildren);
         }
      }
      // if (checkNavigatable(copy)) {
      destinationChildren.add(copy);
      copy.setParent(destination);
      // }
   }

}

class TourItemsTreeController {
   private static final TreeNodeComparator COMPARATOR = new TreeNodeComparator();
   private final CheckedTreeNode myRoot;
   private final Map<TourItem, TourItemNode> myNodes = new HashMap<>();

   private JTree myTreeView;
   protected boolean myInBuild;

   public TourItemsTreeController() {
      myRoot = new CheckedTreeNode("root");
   }

   public JTree getTreeView() {
      return myTreeView;
   }

   public void setTreeView(JTree treeView) {
      myTreeView = treeView;
      myTreeView.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent event) {
            selectionChanged();
         }
      });
      /*if (treeView instanceof BreakpointsCheckboxTree) {
         ((BreakpointsCheckboxTree)treeView).setDelegate();
      }
      myTreeView.setShowsRootHandles(!myGroupingRules.isEmpty());*/
   }

   protected void selectionChanged() {
      if (myInBuild) return;
      selectionChangedImpl();
   }

   protected void selectionChangedImpl() {
   }

   /*private void setGroupingRulesInternal(final List<> groupingRules) {
      myGroupingRules = new ArrayList<>(groupingRules);
   }*/

   public void buildTree(@NotNull Collection<? extends TourItem> tourItems) {
      final TreeState state = TreeState.createOn(myTreeView, myRoot);
      state.setScrollToSelection(false);
      myRoot.removeAllChildren();
      myNodes.clear();
      for (TourItem tourItem : tourItems) {
         TourItemNode node = new TourItemNode(tourItem);
         CheckedTreeNode parent = getParentNode(tourItem);
         parent.add(node);
         myNodes.put(tourItem, node);
      }
      TreeUtil.sortRecursively(myRoot, COMPARATOR);
      myInBuild = true;
      ((DefaultTreeModel)(myTreeView.getModel())).nodeStructureChanged(myRoot);
      state.applyTo(myTreeView, myRoot);
      myInBuild = false;
   }


   @NotNull
   private CheckedTreeNode getParentNode(final TourItem breakpoint) {
      CheckedTreeNode parent = myRoot;
     /* for (int i = 0; i < myGroupingRules.size(); i++) {
         XBreakpointGroup group = myGroupingRules.get(i).getGroup(breakpoint.getBreakpoint(), Collections.emptyList());
         if (group != null) {
            parent = getOrCreateGroupNode(parent, group, i);
            if (breakpoint.isEnabled()) {
               parent.setChecked(true);
            }
         }
      }*/
      return parent;
   }

   /*private static Collection<XBreakpointGroup> getGroupNodes(CheckedTreeNode parent) {
      Collection<XBreakpointGroup> nodes = new ArrayList<>();
      Enumeration children = parent.children();
      while (children.hasMoreElements()) {
         Object element = children.nextElement();
         if (element instanceof BreakpointsGroupNode) {
            nodes.add(((BreakpointsGroupNode)element).getGroup());
         }
      }
      return nodes;
   }

   private static BreakpointsGroupNode getOrCreateGroupNode(CheckedTreeNode parent, final XBreakpointGroup group,
                                                            final int level) {
      Enumeration children = parent.children();
      while (children.hasMoreElements()) {
         Object element = children.nextElement();
         if (element instanceof BreakpointsGroupNode) {
            XBreakpointGroup groupFound = ((BreakpointsGroupNode)element).getGroup();
            if (groupFound.equals(group)) {
               return (BreakpointsGroupNode)element;
            }
         }
      }
      BreakpointsGroupNode groupNode = new BreakpointsGroupNode<>(group, level);
      parent.add(groupNode);
      return groupNode;
   }*/

   /*public void setGroupingRules(Collection<? extends XBreakpointGroupingRule> groupingRules) {
      setGroupingRulesInternal(groupingRules);
      rebuildTree(new ArrayList<>(myNodes.keySet()));
   }*/

   public void rebuildTree(Collection<? extends TourItem> items) {
      List<TourItem> selectedBreakpoints = getSelectedTours(false);
      TreePath path = myTreeView.getSelectionPath();
      buildTree(items);
      if (myTreeView.getRowForPath(path) == -1 && !selectedBreakpoints.isEmpty()) {
         selectBreakpointItem(selectedBreakpoints.get(0), path);
      } else {
         selectBreakpointItem(null, path);
      }
   }

   public List<TourItem> getSelectedTours(boolean traverse) {
      TreePath[] selectionPaths = myTreeView.getSelectionPaths();
      if (selectionPaths == null || selectionPaths.length == 0) return Collections.emptyList();

      final ArrayList<TourItem> list = new ArrayList<>();
      for (TreePath selectionPath : selectionPaths) {
         TreeNode startNode = (TreeNode)selectionPath.getLastPathComponent();
         if (traverse) {
            TreeUtil.traverseDepth(startNode, node -> {
               if (node instanceof TourItemNode) {
                  list.add(((TourItemNode)node).getTourItem());
               }
               return true;
            });
         } else {
            if (startNode instanceof TourItemNode) {
               list.add(((TourItemNode)startNode).getTourItem());
            }
         }
      }

      return list;
   }

   public void selectBreakpointItem(@Nullable final TourItem breakpoint, TreePath path) {
      TourItemNode node = myNodes.get(breakpoint);
      if (node != null) {
         path = TreeUtil.getPathFromRoot(node);
      }
      TreeUtil.selectPath(myTreeView, path, false);
   }

   public CheckedTreeNode getRoot() {
      return myRoot;
   }

   public void selectFirstTourItem() {
      TreeUtil.promiseSelectFirstLeaf(myTreeView);
   }

   public void removeSelectedTours(Project project) {
      final TreePath[] paths = myTreeView.getSelectionPaths();
      if (paths == null) return;
      final List<TourItem> breakpoints = getSelectedTours(true);
      for (TreePath path : paths) {
         Object node = path.getLastPathComponent();
         if (node instanceof TourItemNode) {
            final TourItem item = ((TourItemNode)node).getTourItem();
            if (!item.allowedToRemove()) {
               TreeUtil.unselectPath(myTreeView, path);
               breakpoints.remove(item);
            }
         }
      }
      if (breakpoints.isEmpty()) return;
      TreeUtil.removeSelected(myTreeView);
      for (TourItem breakpoint : breakpoints) {
         breakpoint.removed(project);
      }
   }

   private static class TreeNodeComparator implements Comparator<TreeNode> {
      @Override
      public int compare(final TreeNode o1, final TreeNode o2) {
         if (o1 instanceof TourItemNode && o2 instanceof TourItemNode) {
            TourItem b1 = ((TourItemNode)o1).getTourItem();
            TourItem b2 = ((TourItemNode)o2).getTourItem();
            return b1.compareTo(b2);
         }
         if (o1 instanceof BreakpointsGroupNode && o2 instanceof BreakpointsGroupNode) {
            final BreakpointsGroupNode group1 = (BreakpointsGroupNode)o1;
            final BreakpointsGroupNode group2 = (BreakpointsGroupNode)o2;
            if (group1.getLevel() != group2.getLevel()) {
               return group1.getLevel() - group2.getLevel();
            }
            return group1.getGroup().compareTo(group2.getGroup());
         }
         return o1 instanceof BreakpointsGroupNode ? -1 : 1;
      }
   }

}

class TourItem extends ItemWrapper implements Comparable<TourItem>, Navigatable {

   private final Tour tour;

   TourItem(Tour tour) {
      this.tour = tour;
   }

   public Tour getTour() {
      return tour;
   }

   @Override
   public void navigate(boolean requestFocus) {

   }

   @Override
   public boolean canNavigate() {
      return false;
   }

   @Override
   public boolean canNavigateToSource() {
      return false;
   }

   @Override
   public void setupRenderer(ColoredListCellRenderer renderer, Project project, boolean selected) {

   }

   @Override
   public void setupRenderer(ColoredTreeCellRenderer renderer, Project project, boolean selected) {

   }

   @Override
   public void updateAccessoryView(JComponent label) {

   }

   @Override
   public String speedSearchText() {
      return null;
   }

   @Override
   public @Nullable @Nls String footerText() {
      return null;
   }

   @Override
   protected void doUpdateDetailView(DetailView panel, boolean editorOnly) {

   }

   @Override
   public boolean allowedToRemove() {
      return false;
   }

   @Override
   public void removed(Project project) {

   }

   @Override
   public int compareTo(@NotNull TourItem o) {
      if (Objects.isNull(tour))
         return 1;
      if (Objects.isNull(o.tour))
         return -1;
      if (tour.equals(o.getTour()))
         return 0;
      return tour.getTitle().compareTo(o.getTour().getTitle());
   }
}

class TourItemNode extends CheckedTreeNode {
   private final TourItem tourItem;

   TourItemNode(TourItem tourItem) {
      super(tourItem);
      this.tourItem = tourItem;
   }

   public TourItem getTourItem() {
      return tourItem;
   }
}

class ToursSimpleTree extends Tree {
   public ToursSimpleTree(Project project, TourItemsTreeController controller) {
      super(controller.getRoot());
      setCellRenderer(new ToursSimpleTreeCellRenderer(project));
      setRootVisible(false);
   }
}

class ToursSimpleTreeCellRenderer extends ColoredTreeCellRenderer {
   private final Project myProject;
   private static final SimpleTextAttributes SIMPLE_CELL_ATTRIBUTES_BOLD =
         SimpleTextAttributes.SIMPLE_CELL_ATTRIBUTES.derive(SimpleTextAttributes.STYLE_BOLD, null, null, null);

   public ToursSimpleTreeCellRenderer(Project project) {
      myProject = project;
   }

   @Override
   public void customizeCellRenderer(@NotNull JTree tree,
                                     Object value,
                                     boolean selected,
                                     boolean expanded,
                                     boolean leaf,
                                     int row,
                                     boolean hasFocus) {
      customizeRenderer(myProject, value, selected, expanded, this);
   }

   private static void customizeRenderer(Project project,
                                         Object value,
                                         boolean selected,
                                         boolean expanded,
                                         ColoredTreeCellRenderer renderer) {
      if (value instanceof BreakpointItemNode) {
         BreakpointItemNode node = (BreakpointItemNode)value;
         BreakpointItem breakpoint = node.getBreakpointItem();
         breakpoint.setupRenderer(renderer, project, selected);
      } else if (value instanceof BreakpointsGroupNode) {
         XBreakpointGroup group = ((BreakpointsGroupNode)value).getGroup();
         renderer.setIcon(group.getIcon(expanded));
         if (group instanceof XBreakpointCustomGroup && ((XBreakpointCustomGroup)group).isDefault()) {
            renderer.append(group.getName(), SIMPLE_CELL_ATTRIBUTES_BOLD);
         } else {
            renderer.append(group.getName(), SimpleTextAttributes.SIMPLE_CELL_ATTRIBUTES);
         }
      }
   }
}

class TourNode extends AbstractTreeNode<String> {

   private final Tour tour;
   private final List<TourStepNode> children;

   protected TourNode(Project project, @NotNull String value, Tour tour) {
      super(project, value);
      this.tour = tour;
      this.children = new ArrayList<>();
      tour.getSteps().forEach(step -> children.add(new TourStepNode(project, step.getTitle(), step)));
   }

   @Override
   public @NotNull Collection<? extends AbstractTreeNode<?>> getChildren() {
      return children;
   }

   @Override
   protected void update(@NotNull PresentationData presentation) {

   }

   @Override
   public boolean canNavigate() {
      return false;
   }
}

class TourStepNode extends AbstractTreeNode<String> {
   private final Step step;

   protected TourStepNode(Project project, @NotNull String value,
                          Step step) {
      super(project, value);
      this.step = step;
   }

   @Override
   public @NotNull Collection<? extends AbstractTreeNode<?>> getChildren() {
      return Collections.emptyList();
   }

   @Override
   protected void update(@NotNull PresentationData presentation) {

   }

   @Override
   public boolean canNavigate() {
      return true;
   }

   @Override
   public void navigate(boolean requestFocus) {
      super.navigate(requestFocus);
   }
}