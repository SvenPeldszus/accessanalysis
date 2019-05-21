package de.uni_hamburg.informatik.swt.accessanalysis.internal;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The activator class controls the plug-in life cycle
 */
public class AccessAnalysisActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.uni_hamburg.informatik.swt.accessanalysis";

	public static ImageDescriptor imageDescriptorFromPlugin(String imageFilePath)
	{
	    return imageDescriptorFromPlugin(PLUGIN_ID, imageFilePath);
	}
}
