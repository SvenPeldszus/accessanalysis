package de.uni_hamburg.informatik.swt.accessanalysis.junitconstraints;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaProject;
import org.osgi.service.prefs.BackingStoreException;

class JunitConstraintsProperties {
	
	enum Key { JUNIT3, JUNIT4 }
	
	private static final String QUALIFIER = "de.uni_hamburg.informatik.swt.accessanalysis.junitconstraints";
	
	private static final boolean DEFAULT = true;

    private IEclipsePreferences _node;
	
	JunitConstraintsProperties(IJavaProject project) {
		IScopeContext context = new ProjectScope(project.getProject());
        _node = context.getNode(QUALIFIER);
	}
	
	boolean getDefault(Key key)
	{
		return DEFAULT;
	}
	
	
	boolean getProperty(Key key)
	{
		return _node.getBoolean(getQualifiedKey(key), DEFAULT);
	}
	
	void setProperty(Key key, boolean value)
	{
		_node.putBoolean(getQualifiedKey(key), value);
	}
	
	void saveProperties() throws BackingStoreException
	{
	    _node.flush();
	}
	
	String getQualifiedKey(Key key)
	{
	    return QUALIFIER + "." + key.name().toLowerCase();
	}
}
