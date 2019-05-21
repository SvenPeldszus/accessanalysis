package de.uni_hamburg.informatik.swt.accessanalysis.runanalysisaction.internal;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.uni_hamburg.informatik.swt.accessanalysis.resultsconsumer.AccessAnalysisResultsConsumer;

/**
 * The activator class controls the plug-in life cycle
 */
public class RunAnalysisActionActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.uni_hamburg.informatik.swt.accessanalysis.runanalysisaction"; //$NON-NLS-1$

	public static Collection<AccessAnalysisResultsConsumer> getResultsConsumer() throws CoreException {
		IWorkbenchPage page = null;
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			page = window.getActivePage();
		}
		
    	IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(AccessAnalysisResultsConsumer.EXTENSION_POINT_ID);
    	
    	Collection<AccessAnalysisResultsConsumer> consumers = new ArrayList<AccessAnalysisResultsConsumer>(config.length);
		for (IConfigurationElement e : config) {
			final Object extension = e.createExecutableExtension("class");
			if (extension instanceof AccessAnalysisResultsConsumer) {
				AccessAnalysisResultsConsumer consumer = (AccessAnalysisResultsConsumer) extension;
				if (page != null) {
					consumer.setWorkbenchPage(page);
				}
				consumers.add(consumer);
			}
		}
		
		return consumers;
    }
	
	public static void showErrorMessage(String message, Exception exception) {
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
		
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", errorMessage.toString());
			}
		});
	}
}
