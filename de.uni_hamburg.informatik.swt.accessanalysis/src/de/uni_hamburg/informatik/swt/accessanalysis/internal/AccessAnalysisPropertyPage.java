package de.uni_hamburg.informatik.swt.accessanalysis.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PropertyPage;

import de.uni_hamburg.informatik.swt.accessanalysis.extensions.RcpExtension;

@RcpExtension
public class AccessAnalysisPropertyPage extends PropertyPage implements
		IWorkbenchPropertyPage {


	@Override
	protected Control createContents(Composite parent) {
		Composite main = new Composite(parent, SWT.NONE);
		main.setLayout(new GridLayout());
		Label label = new Label(main, SWT.NONE);
		label.setText("Expand the tree to configure your AccessAnalysis project properties.");
		
		return main;
	}

}
