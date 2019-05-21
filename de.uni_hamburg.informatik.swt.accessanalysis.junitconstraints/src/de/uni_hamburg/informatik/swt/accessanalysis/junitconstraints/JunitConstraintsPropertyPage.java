package de.uni_hamburg.informatik.swt.accessanalysis.junitconstraints;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

import de.uni_hamburg.informatik.swt.accessanalysis.extensions.RcpExtension;

@RcpExtension
public class JunitConstraintsPropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {

	private JunitConstraintsProperties _properties;
	private Button _junit3Checkbox;
	private Button _junit4Checkbox;

	@Override
	protected Control createContents(Composite parent) {
		_properties = new JunitConstraintsProperties((IJavaProject) getElement());
		
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		
		Group group = new Group(main, SWT.NONE);
		group.setText("Enable JUnit Constraints");
		group.setLayout(new GridLayout());

		_junit3Checkbox = new Button(group, SWT.CHECK);
		_junit3Checkbox.setText("JUnit 3");
		_junit3Checkbox.setSelection(_properties.getProperty(JunitConstraintsProperties.Key.JUNIT3));
		
		_junit4Checkbox = new Button(group, SWT.CHECK);
		_junit4Checkbox.setText("JUnit 4");
		_junit4Checkbox.setSelection(_properties.getProperty(JunitConstraintsProperties.Key.JUNIT4));
		
		Label label = new Label(group, SWT.WRAP);
		label.setText("Sets the minimal access modifier of all JUnit related classes and methods to public, e.g. test methods.");
		
		return main;
	}
	
	@Override
	protected void performDefaults() {
		_junit3Checkbox.setSelection(_properties.getDefault(JunitConstraintsProperties.Key.JUNIT3));
		_junit4Checkbox.setSelection(_properties.getDefault(JunitConstraintsProperties.Key.JUNIT4));
		
		super.performDefaults();
	}
	
	
	
	@Override
	public boolean performOk() {
		
		try {
			_properties.setProperty(JunitConstraintsProperties.Key.JUNIT3, _junit3Checkbox.getSelection());
			_properties.setProperty(JunitConstraintsProperties.Key.JUNIT4, _junit4Checkbox.getSelection());
			_properties.saveProperties();
		} catch (BackingStoreException e) {
			showErrorMessage("Error while saving JUnit Constraints properties.", e);
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
}
