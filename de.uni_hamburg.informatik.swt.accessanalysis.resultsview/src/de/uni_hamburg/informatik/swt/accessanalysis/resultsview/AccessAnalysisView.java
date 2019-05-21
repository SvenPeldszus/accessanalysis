package de.uni_hamburg.informatik.swt.accessanalysis.resultsview;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import de.uni_hamburg.informatik.swt.accessanalysis.extensions.RcpExtension;
import de.uni_hamburg.informatik.swt.accessanalysis.results.Result;


/**
 * View that shows the results of an AccessAnalysis.
 * 
 * Based on the SampleView in the "Plug-in with a view"-template
 * from the New-Plug-In-project-wizard. 
 */
@RcpExtension
public class AccessAnalysisView extends ViewPart
{
    private enum Level { ROOT, PROJECT, SOURCE_FOLDER, PACKAGE, TYPE, METHOD }
    
    
    /**
     * The ID of the view as specified by the extension.
     */
    public static final String ID = "de.uni_hamburg.informatik.swt.accessanalysis.resultsview";

    //The icons
    private static final ImageDescriptor IMG_EXPAND_ALL = ImageDescriptor.createFromFile(new AccessAnalysisView().getClass(), "/icons/expandall.png");
    private static final ImageDescriptor IMG_TYPE_LEVEL = ImageDescriptor.createFromFile(new AccessAnalysisView().getClass(), "/icons/typelevel.png");
    private static final ImageDescriptor IMG_PACKAGE_LEVEL = ImageDescriptor.createFromFile(new AccessAnalysisView().getClass(), "/icons/packagelevel.png");
    private static final ImageDescriptor IMG_SOURCE_FOLDER_LEVEL = ImageDescriptor.createFromFile(new AccessAnalysisView().getClass(), "/icons/sourcefolderlevel.png");
    private static final ImageDescriptor IMG_COLLAPSE_ALL = ImageDescriptor.createFromFile(new AccessAnalysisView().getClass(), "/icons/collapseall.png");

    private TreeViewer _viewer;
    private Action _expandAllAction;
    private Action _toSourceFolderLevelAction;
    private Action _toPackageLevelAction;
    private Action _toTypeLevelAction;
    private Action _collapseAllAction;
    private Action _doubleClickAction;

    @Override
    public void createPartControl(Composite parent)
    {
        makeTreeViewer(parent);
        makeActions();
        hookContextMenu();
        hookDoubleClickAction();
        contributeToActionBars();
        clear();
    }
    
    @Override
    public void setFocus()
    {
        _viewer.getControl().setFocus();
    }

    private void makeTreeViewer(Composite parent)
    {
        _viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
        makeTreeLayout(_viewer);        
        _viewer.setContentProvider(new AccessAnalysisViewContentProvider());
        getSite().setSelectionProvider(_viewer);
    }
    
    private void makeTreeLayout(TreeViewer viewer)
    {
        Tree tree = viewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        TableLayout layout = new TableLayout();

        TreeColumn treeColumn = new TreeColumn(tree, SWT.LEFT);
        treeColumn.setText("Element");
        TreeViewerColumn treeViewerColumn = new TreeViewerColumn(viewer, treeColumn);
        treeViewerColumn.setLabelProvider(new ElementLabelProvider());
        layout.addColumnData(new ColumnWeightData(100, 110));

        treeColumn = new TreeColumn(tree, SWT.LEFT);
        treeColumn.setText("IGAT");
        treeViewerColumn = new TreeViewerColumn(viewer, treeColumn);
        treeViewerColumn.setLabelProvider(new IGATLabelProvider());
        layout.addColumnData(new ColumnWeightData(10, 30));
        
        treeColumn = new TreeColumn(tree, SWT.LEFT);
        treeColumn.setText("IGAM");
        treeViewerColumn = new TreeViewerColumn(viewer, treeColumn);
        treeViewerColumn.setLabelProvider(new IGAMLabelProvider());
        layout.addColumnData(new ColumnWeightData(10, 30));
        
        treeColumn = new TreeColumn(tree, SWT.LEFT);
        treeColumn.setText("Minimal Access Modifier");
        treeViewerColumn = new TreeViewerColumn(viewer, treeColumn);
        treeViewerColumn.setLabelProvider(new MinimalAccessLabelProvider());
        layout.addColumnData(new ColumnWeightData(26, 80));
        
        treeColumn = new TreeColumn(tree, SWT.LEFT);
        treeColumn.setText("Actual Access Modifier");
        treeViewerColumn = new TreeViewerColumn(viewer, treeColumn);
        treeViewerColumn.setLabelProvider(new ActualAccessLabelProvider());
        layout.addColumnData(new ColumnWeightData(26, 80));

        tree.setLayout(layout);
    }
    
    private void hookContextMenu()
    {
        MenuManager menuMgr = new MenuManager("#PopupMenu");
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener()
        {
            public void menuAboutToShow(IMenuManager manager)
            {
                AccessAnalysisView.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(_viewer.getControl());
        _viewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, _viewer);
    }

    private void contributeToActionBars()
    {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalToolBar(bars.getToolBarManager());
    }

    private void fillMenu(IContributionManager manager)
    {
        manager.add(_collapseAllAction);
        manager.add(_toSourceFolderLevelAction);
        manager.add(_toPackageLevelAction);
        manager.add(_toTypeLevelAction);
        manager.add(_expandAllAction);
    }
    
    private void fillLocalToolBar(IToolBarManager manager)
    {
        fillMenu(manager);
    }

    private void fillContextMenu(IMenuManager manager)
    {
        fillMenu(manager);
        manager.add(new Separator());
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    }
    
    private void makeActions()
    {
        _expandAllAction = new ExpandAllAction();
        _toTypeLevelAction = new ToTypeLevelAction();
        _toPackageLevelAction = new ToPackageLevelAction();
        _toSourceFolderLevelAction = new ToSourceFolderLevelAction();
        _collapseAllAction = new CollapseAllAction();
        _doubleClickAction = new DoubleClickAction();
    }

    private void hookDoubleClickAction()
    {
        _viewer.addDoubleClickListener(new IDoubleClickListener()
        {
            public void doubleClick(DoubleClickEvent event)
            {
                _doubleClickAction.run();
            }
        });
    }
    
    private void setActionsEnabled(boolean enabled)
    {
        _expandAllAction.setEnabled(enabled);
        _toTypeLevelAction.setEnabled(enabled);
        _toPackageLevelAction.setEnabled(enabled);
        _toSourceFolderLevelAction.setEnabled(enabled);
        _collapseAllAction.setEnabled(enabled);
        _doubleClickAction.setEnabled(enabled);
    }
    
    private Result getSelectedResult()
    {
        ISelection candidate = _viewer.getSelection();
        
        if (!(candidate instanceof IStructuredSelection))
        {
            return null;
        }

        Object firstElement = ((IStructuredSelection) candidate).getFirstElement();
        if (!(firstElement instanceof Result))
        {
            return null;
        }
        
        return (Result) firstElement;
    }
    
    /**
     * Clears the view and disables the buttons.
     */
    void clear()
    {
    	Display.getDefault().asyncExec( 
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        _viewer.setInput(null);
                    }
                }
            );
        setActionsEnabled(false);
    }
    
    /**
     * Puts the given results on the view.
     * 
     * @param List<Result> results The results to be shown
     * @param Job reanalyzeJob The Job to be scheduled, when Reanalyze-Button is pressed.
     */
    void showResults(final List<Result> results)
    {
        Display.getDefault().asyncExec( 
            new Runnable()
            {
                @Override
                public void run()
                {
                    _viewer.setInput(results);
                }
            }
        );
        
        setActionsEnabled(true);
    }
    
    /**
     * Opens a dialog with a simple message.
     * 
     * @param String message The text to be shown
     */
    private void showMessage(final String message)
    {
        Display.getDefault().asyncExec( 
            new Runnable()
            {
                @Override
                public void run()
                {
                    MessageDialog.openInformation(_viewer.getControl().getShell(), "AccessAnalysis", message);
                }
            }
        );
    }
    
    private class ExpandAllAction extends Action
    {
        private ExpandAllAction()
        {
            setText("Expand All");
            setToolTipText("Expand to method level");
            setImageDescriptor(IMG_EXPAND_ALL);
        }
        
        public void run()
        {
            _viewer.expandAll();
        }
    }
    
    private class ToSourceFolderLevelAction extends Action
    {
        private ToSourceFolderLevelAction()
        {
            setText("Source Folder Level");
            setToolTipText("Expand/collapse to Source Folder Level");
            setImageDescriptor(IMG_SOURCE_FOLDER_LEVEL);
        }
        
        public void run()
        {
            _viewer.collapseAll();
            _viewer.expandToLevel(Level.SOURCE_FOLDER.ordinal());
        }
    }
    
    private class ToPackageLevelAction extends Action
    {
        private ToPackageLevelAction()
        {
            setText("Package Level");
            setToolTipText("Expand/collapse to Package Level");
            setImageDescriptor(IMG_PACKAGE_LEVEL);
        }
        
        public void run()
        {
            _viewer.collapseAll();
            _viewer.expandToLevel(Level.PACKAGE.ordinal());
        }
    }
    
    private class ToTypeLevelAction extends Action
    {
        private ToTypeLevelAction()
        {
            setText("Type Level");
            setToolTipText("Expand/collapse to type level");
            setImageDescriptor(IMG_TYPE_LEVEL);
        }
        
        public void run()
        {
            _viewer.collapseAll();
            _viewer.expandToLevel(Level.TYPE.ordinal());
        }
    }
    
    private class CollapseAllAction extends Action
    {
        private CollapseAllAction()
        {
            setText("Collapse All");
            setToolTipText("Collapse to project level");
            setImageDescriptor(IMG_COLLAPSE_ALL);
        }
        
        public void run()
        {
            _viewer.collapseAll();
        }
    }
    
    private class DoubleClickAction extends Action
    {
        public void run()
        {
            Result result = getSelectedResult();
            
            if (result.isElementEditable())
            {
                openElement(result.getJavaElement());
            }
            
            _viewer.expandToLevel(result, 1);
        }
        
        private void openElement(IJavaElement element)
        {
            try
            {
                IEditorPart editor = JavaUI.openInEditor(element);
                JavaUI.revealInEditor(editor, element);
            }
            catch (PartInitException e)
            {
                showMessage(e.getMessage());
            }
            catch (JavaModelException e)
            {
                showMessage(e.getMessage());
            }
        }
    }
    
    
}