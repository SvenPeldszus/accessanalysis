package de.uni_hamburg.informatik.swt.accessanalysis.annotations;

import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

import de.uni_hamburg.informatik.swt.accessanalysis.extensions.RcpExtension;

@RcpExtension
public class AnnotationConstraintsPropertyPage extends PropertyPage implements IWorkbenchPropertyPage {

    private List<AnnotationConstraint> _constraints;
    private AnnotationConstraintsProperties _properties;
    private TableViewer _constraintTable;

    @Override
    protected Control createContents(final Composite parent)
    {
        _properties = new AnnotationConstraintsProperties((IJavaProject) getElement());
        _constraints = _properties.getConstraints();

        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());
        main.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());

        createTable(main);
        createButtons(main);

        return main;
    }

    private void createTable(Composite parent)
    {
        _constraintTable = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
        _constraintTable.getTable().setLayoutData(
                GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).create());
        _constraintTable.getTable().setHeaderVisible(true);
        _constraintTable.getTable().setLinesVisible(true);
        _constraintTable.setContentProvider(new ArrayContentProvider());

        TableViewerColumn nameColumn = new TableViewerColumn(_constraintTable, SWT.LEFT);
        nameColumn.getColumn().setText("Annotation Type");
        nameColumn.getColumn().setWidth(110);
        nameColumn.setLabelProvider(new AnnotationConstraintNameLabelProvider());

        TableViewerColumn packageColumn = new TableViewerColumn(_constraintTable, SWT.LEFT);
        packageColumn.getColumn().setText("From Package");
        packageColumn.getColumn().setWidth(180);
        packageColumn.setLabelProvider(new AnnotationConstraintPackageLabelProvider());
        
        TableViewerColumn accessColumn = new TableViewerColumn(_constraintTable, SWT.LEFT);
        accessColumn.getColumn().setText("Minimal Access");
        accessColumn.getColumn().setWidth(100);
        accessColumn.setLabelProvider(new AnnotationConstraintAccessLabelProvider());
        
        TableViewerColumn applyToColumn = new TableViewerColumn(_constraintTable, SWT.LEFT);
        applyToColumn.getColumn().setText("Apply To");
        applyToColumn.getColumn().setWidth(100);
        applyToColumn.setLabelProvider(new AnnotationConstraintApplyToLabelProvider());
        
        _constraintTable.setInput(_constraints);
    }

    private void createButtons(Composite parent)
    {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(GridLayoutFactory.swtDefaults().numColumns(1).margins(0, 0).create());
        main.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).create());

        createAddButton(main);
        final Button editButton = createEditButton(main);
        final Button removeButton = createRemoveButton(main);

        _constraintTable.addSelectionChangedListener(new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event)
            {
                boolean hasSelection = !_constraintTable.getSelection().isEmpty();
                editButton.setEnabled(hasSelection);
                removeButton.setEnabled(hasSelection);
            }
        });
    }

    private void createAddButton(Composite parent)
    {
        Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).create());
        button.setText("Add...");
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                AnnotationConstraintDialog dialog = new AnnotationConstraintDialog(getShell(),
                        (IJavaProject) getElement());
                dialog.open();
                if (dialog.getReturnCode() == AnnotationConstraintDialog.OK)
                {
                    AnnotationConstraint newConstraint = dialog.getResult();
                    if (!_constraints.contains(newConstraint))
                    {
                        _constraints.add(newConstraint);
                    }
                    _constraintTable.refresh();
                    _constraintTable.setSelection(new StructuredSelection(newConstraint));
                }
            }
        });
    }

    private Button createEditButton(Composite parent)
    {
        final Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).create());
        button.setText("Edit...");
        button.setEnabled(false);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e)
            {
                ISelection selection = _constraintTable.getSelection();
                if (!selection.isEmpty())
                {
                    AnnotationConstraint selectedConstraint = (AnnotationConstraint) ((IStructuredSelection) selection).getFirstElement();
                    AnnotationConstraintDialog dialog = new AnnotationConstraintDialog(getShell(),
                            (IJavaProject) getElement(), selectedConstraint);
                    dialog.open();
                    if (dialog.getReturnCode() == AnnotationConstraintDialog.OK)
                    {
                        int index = _constraints.indexOf(selectedConstraint);
                        _constraints.remove(selectedConstraint);
                        AnnotationConstraint newConstraint = dialog.getResult();
                        if (!_constraints.contains(newConstraint))
                        {
                            _constraints.add(index, newConstraint);
                        }
                        _constraintTable.refresh();
                        _constraintTable.setSelection(new StructuredSelection(newConstraint));
                    }
                }
            }
        });
        return button;
    }

    private Button createRemoveButton(Composite parent)
    {
        final Button button = new Button(parent, SWT.PUSH);
        button.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).create());
        button.setText("Remove");
        button.setEnabled(false);
        button.addSelectionListener(new SelectionAdapter() {
            
            @Override
            public void widgetSelected(SelectionEvent e)
            {
                ISelection selection = _constraintTable.getSelection();
                if (!selection.isEmpty())
                {
                    _constraints.remove(((IStructuredSelection) selection).getFirstElement());
                    _constraintTable.refresh();
                }
            }
        });
        return button;
    }

    @Override
    public boolean performOk()
    {
        try
        {
            _properties.setConstraints(new HashSet<AnnotationConstraint>(_constraints));
        } catch (BackingStoreException e)
        {
            showErrorMessage("Error while saving Annotation Constraints.", e);
        }

        return super.performOk();
    }
    
    private void showErrorMessage(String message, Exception exception) {
        final StringBuilder errorMessage = new StringBuilder();
        if (message != null) {
            errorMessage.append(message);
        }
        if (message != null && exception != null) {
            errorMessage.append(" (");
        }
        if (exception != null) {
            errorMessage.append(exception.getMessage());
        }
        if (message != null && exception != null) {
            errorMessage.append(")");
        }
        
        getShell().getDisplay().asyncExec(new Runnable() {
            
            @Override
            public void run() {
                MessageDialog.openError(getShell(), "Error", errorMessage.toString());
            }
        });
    }
    
    private abstract class AnnotationConstraintLabelProvider extends ColumnLabelProvider 
    {
        @Override
        public String getText(Object element)
        {
            if (element instanceof AnnotationConstraint)
            {
                return getText((AnnotationConstraint) element);
            }
            
            return super.getText(element);
        }
        
        abstract String getText(AnnotationConstraint constraint);
        
        @Override
        public Image getImage(Object element)
        {
            if (element instanceof AnnotationConstraint)
            {
                return getImage((AnnotationConstraint) element);
            }
            
            return super.getImage(element);
        }
        
        Image getImage(AnnotationConstraint constraint)
        {
            return super.getImage((Object) constraint);
        }
    }
    

    private class AnnotationConstraintNameLabelProvider extends AnnotationConstraintLabelProvider
    {
        private JavaElementLabelProvider _javaElementLabelProvider = new JavaElementLabelProvider();
        
        @Override
        String getText(AnnotationConstraint constraint)
        {
            return _javaElementLabelProvider.getText(constraint.getAnnotationType());
        }
        
        @Override
        Image getImage(AnnotationConstraint constraint)
        {
            return _javaElementLabelProvider.getImage(constraint.getAnnotationType());
        }
    }
    
    private class AnnotationConstraintPackageLabelProvider extends AnnotationConstraintLabelProvider
    {
        @Override
        String getText(AnnotationConstraint constraint)
        {
            return constraint.getAnnotationType().getPackageFragment().getElementName();
        }
    }
    
    private class AnnotationConstraintAccessLabelProvider extends AnnotationConstraintLabelProvider
    {
        @Override
        String getText(AnnotationConstraint constraint)
        {
            return constraint.getMinimalAccess().toString();
        }
        
        //Does not work. Image will be stretched in an ugly way.
//        @Override
//        protected Image getImage(AnnotationConstraint constraint)
//        {
//            return constraint.getMinimalAccess().getImage();
//        }
    }
    
    private class AnnotationConstraintApplyToLabelProvider extends AnnotationConstraintLabelProvider
    {
        @Override
        String getText(AnnotationConstraint constraint)
        {
            return constraint.applyTo().toString();
        }
    }

}
