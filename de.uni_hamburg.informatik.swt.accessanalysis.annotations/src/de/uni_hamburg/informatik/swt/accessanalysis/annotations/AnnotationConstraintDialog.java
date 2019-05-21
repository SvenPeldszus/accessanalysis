package de.uni_hamburg.informatik.swt.accessanalysis.annotations;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.SelectionDialog;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;
import de.uni_hamburg.informatik.swt.accessanalysis.annotations.AnnotationConstraint.ApplyTo;

class AnnotationConstraintDialog extends TitleAreaDialog {

    private final IJavaProject _project;
    private Text _annotationTypeTextField;
    private ComboViewer _accessComboViewer;
    private Button _methodsCheckbox;
    private Button _typesCheckbox;
    
    private IType _annotationType;
    private AnnotationConstraint _inputConstraint;
    private AnnotationConstraint _resultConstraint;
    private Button _bothCheckbox;

    AnnotationConstraintDialog(Shell parentShell, IJavaProject project)
    {
        super(parentShell);
        setHelpAvailable(false);

        _project = project;
        _annotationType = null;
        _inputConstraint = null;
        _resultConstraint = null;
    }
    
    AnnotationConstraintDialog(Shell parentShell, IJavaProject project, AnnotationConstraint constraintToEdit)
    {
        this(parentShell, project);

        _inputConstraint = constraintToEdit;
    }

    @Override
    protected Control createDialogArea(Composite parent)
    {
        setTitle("Annotation Constraint");
        setMessage("Types and/or methods with the choosen Annotation will have\nat least the select Minimal Access Modifier.");

        Label titleBarSeparator = new Label(parent, SWT.HORIZONTAL | SWT.SEPARATOR);
        titleBarSeparator.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(GridLayoutFactory.swtDefaults().numColumns(5).spacing(5, 10).margins(5, 10).create());
        main.setLayoutData(GridDataFactory.swtDefaults().grab(true, true).align(SWT.FILL, SWT.BEGINNING).create());

        createAnnotationTypeField(main);
        createAppliesToFields(main);
        createLeastMinimalAccessModifierField(main);
        
        if (_inputConstraint != null)
        {
            initFields();
        }

        return main;
    }
    
    @Override
    protected Control createButtonBar(Composite parent)
    {
        Control control = super.createButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        
        updateTextFieldAndOkButton();
        
        return control;
    }

    private void createAnnotationTypeField(Composite parent)
    {
        Label label = new Label(parent, SWT.NONE);
        label.setText("Annotation:");

        _annotationTypeTextField = new Text(parent, SWT.BORDER);
        _annotationTypeTextField.setLayoutData(GridDataFactory.swtDefaults().minSize(120, SWT.DEFAULT).span(3, 1).grab(true, false)
                .align(SWT.FILL, SWT.CENTER).create());
        _annotationTypeTextField.setEditable(false);

        Button browseButton = new Button(parent, SWT.PUSH);
        browseButton.setText("Browse");
        browseButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event)
            {
                try
                {
                    IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { _project });

                    SelectionDialog dialog = JavaUI.createTypeDialog(getShell(), new ProgressMonitorDialog(getShell()),
                            scope, IJavaElementSearchConstants.CONSIDER_ANNOTATION_TYPES, false);
                    dialog.open();

                    if (dialog.getReturnCode() == SelectionDialog.OK)
                    {
                        for (Object result : dialog.getResult())
                        {
                            if (result instanceof IType && ((IType) result).isAnnotation())
                            {
                                _annotationType = (IType) result;
                                updateTextFieldAndOkButton();
                            }
                        }
                    }
                } catch (JavaModelException e)
                {
                    showErrorMessage("Error while looking for Annotation Types.", e);
                }
            }
        });
    }

    private void createAppliesToFields(Composite parent)
    {
        Label applyLabel = new Label(parent, SWT.NONE);
        applyLabel.setText("Applies to:");
    
        _typesCheckbox = new Button(parent, SWT.RADIO);
        _typesCheckbox.setText("Types");
    
        _methodsCheckbox = new Button(parent, SWT.RADIO);
        _methodsCheckbox.setText("Methods");
        
        _bothCheckbox = new Button(parent, SWT.RADIO);
        _bothCheckbox.setText("both");
        _bothCheckbox.setSelection(true);
    }
    
    private void initFields()
    {
        assert _inputConstraint != null : "Precondition failed: _inputConstraint != null";
        
        _annotationType = _inputConstraint.getAnnotationType();
        
        ApplyTo applyTo = _inputConstraint.applyTo();
        _typesCheckbox.setSelection(applyTo == ApplyTo.TYPES);
        _methodsCheckbox.setSelection(applyTo == ApplyTo.METHODS);
        _bothCheckbox.setSelection(applyTo == ApplyTo.BOTH);
        
        _accessComboViewer.setSelection(new StructuredSelection(_inputConstraint.getMinimalAccess()));
    }

    private void createLeastMinimalAccessModifierField(Composite parent)
    {
        Composite main = new Composite(parent, SWT.NONE);
        main.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).margins(0, 0).create());
        main.setLayoutData(GridDataFactory.swtDefaults().span(5, 1).create());
    
        Label accessLabel = new Label(main, SWT.NONE);
        accessLabel.setText("Minimal Access Modifier at least:");
    
        _accessComboViewer = new ComboViewer(main, SWT.DROP_DOWN | SWT.READ_ONLY);
        _accessComboViewer.setContentProvider(new ArrayContentProvider());
        _accessComboViewer.setInput(new Access[] { Access.PUBLIC, Access.PROTECTED, Access.DEFAULT, Access.PRIVATE });
        _accessComboViewer.setSelection(new StructuredSelection(Access.PUBLIC));
    }

    private void updateTextFieldAndOkButton()
    {
        String text = "";
        if (_annotationType != null)
        {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
            text = _annotationType.getFullyQualifiedName();
        }
        _annotationTypeTextField.setText(text);
    }

    @Override
    protected void okPressed()
    {
        assert _annotationType != null : "Precondition failed: OK button not clickable as long as no Annotation is selected";
        assert !_accessComboViewer.getSelection().isEmpty() : "Precondition failed: OK button not clickable as long as no Minimal Access Modifier is selected";
        
        Access access = (Access) ((IStructuredSelection) _accessComboViewer.getSelection()).getFirstElement();
        ApplyTo applyTo = ApplyTo.BOTH;
        if (_typesCheckbox.getSelection())
        {
            applyTo = ApplyTo.TYPES;
        }
        else if (_methodsCheckbox.getSelection())
        {
            applyTo = ApplyTo.METHODS;
        }
        
        _resultConstraint = new AnnotationConstraint(_annotationType, access, applyTo);
        
        super.okPressed();
    }

    AnnotationConstraint getResult()
    {
        return _resultConstraint;
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
}
