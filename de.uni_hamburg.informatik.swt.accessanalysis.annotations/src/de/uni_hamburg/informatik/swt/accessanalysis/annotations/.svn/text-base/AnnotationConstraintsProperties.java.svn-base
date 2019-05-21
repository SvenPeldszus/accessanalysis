package de.uni_hamburg.informatik.swt.accessanalysis.annotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.core.search.TypeNameMatchRequestor;
import org.osgi.service.prefs.BackingStoreException;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;
import de.uni_hamburg.informatik.swt.accessanalysis.annotations.AnnotationConstraint.ApplyTo;

class AnnotationConstraintsProperties {
	
    private static final String QUALIFIER = "de.uni_hamburg.informatik.swt.accessanalysis.annotations";
    private static final String SEPARATOR = ";";
    
    private final IJavaProject _project;
    private IEclipsePreferences _node;
	
	AnnotationConstraintsProperties(IJavaProject project) {
		_project = project;
		IScopeContext context = new ProjectScope(project.getProject());
        _node = context.getNode(QUALIFIER);
	}
	
	List<AnnotationConstraint> getConstraints()
    {
	    List<AnnotationConstraint> constraints = new ArrayList<AnnotationConstraint>();
	    for (Access access : Access.real())
	    {
	        constraints.addAll(getConstraints(access));
	    }
	    
	    Collections.sort(constraints);
	    
	    return constraints;
    }
	
	private List<AnnotationConstraint> getConstraints(Access access)
    {
	    List<AnnotationConstraint> constraints = new ArrayList<AnnotationConstraint>();
	    for (ApplyTo applyTo : ApplyTo.values())
        {
            constraints.addAll(getConstraints(access, applyTo));
        }
        return constraints;
    }
	
	private List<AnnotationConstraint> getConstraints(Access access, ApplyTo applyTo)
	{
	    List<AnnotationConstraint> constraints = new ArrayList<AnnotationConstraint>();
	    String property = getProperty(access, applyTo);
	    if (property != null)
        {
            for (String name : property.split(SEPARATOR))
            {
                IType type = null;
                try {
                    type = searchType(name);
                }
                catch (JavaModelException e) {
                }
                
                if (type != null)
                {
                    constraints.add(new AnnotationConstraint(type, access, applyTo));
                }
            }
        }
	    
	    return constraints;
	}
	
	void setConstraints(Collection<AnnotationConstraint> constraints) throws BackingStoreException
	{
	    Map<Access, Map<ApplyTo, Set<AnnotationConstraint>>> access2Constraints = createConstraintMap(constraints);
	    
	    for (Access access : Access.real())
        {
	        Map<ApplyTo, Set<AnnotationConstraint>> applyTo2Constraints = access2Constraints.get(access);
	        for (ApplyTo applyTo : ApplyTo.values())
            {
                StringBuilder valueString = new StringBuilder(); 
                for (AnnotationConstraint constraint : applyTo2Constraints.get(applyTo))
                {
                    valueString.append(constraint.getAnnotationType().getFullyQualifiedName());
                    valueString.append(SEPARATOR);
                }
                if (valueString.length() > 0)
                {
                    valueString.deleteCharAt(valueString.length() - 1);
                }
                
                setProperty(access, applyTo, valueString.toString());
            }
        }
	    
	    saveProperties();
	}

    private Map<Access, Map<ApplyTo, Set<AnnotationConstraint>>> createConstraintMap(Collection<AnnotationConstraint> constraints)
    {
        Map<Access, Map<ApplyTo, Set<AnnotationConstraint>>> access2Constraints = new HashMap<Access, Map<ApplyTo, Set<AnnotationConstraint>>>();
        for (Access access : Access.real())
        {
            Map<ApplyTo, Set<AnnotationConstraint>> applyTo2Constraints = new HashMap<AnnotationConstraint.ApplyTo, Set<AnnotationConstraint>>();
            for (ApplyTo applyTo : ApplyTo.values())
            {
                applyTo2Constraints.put(applyTo, new HashSet<AnnotationConstraint>());
            }
            access2Constraints.put(access, applyTo2Constraints);
        }
        
        for (AnnotationConstraint constraint : constraints)
        {
            access2Constraints.get(constraint.getMinimalAccess()).get(constraint.applyTo()).add(constraint);
        }
        
        return access2Constraints;
    }

	private String getProperty(Access access, ApplyTo applyTo)
	{
	    return _node.get(getQualifiedKey(access, applyTo), "");
	}
	
	private void setProperty(Access access, ApplyTo applyTo, String value)
	{
	    _node.put(getQualifiedKey(access, applyTo), value);
	}
	
	private String getQualifiedKey(Access key, ApplyTo applyTo)
	{
		return QUALIFIER + "." + key.name().toLowerCase() + "." + applyTo.name().toLowerCase();
	}
	
	private IType searchType(String qualifiedName) throws JavaModelException
	{
		Set<IJavaProject> projects = new HashSet<IJavaProject>();
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects())
		{
			projects.add(JavaCore.create((IProject) project));
		}

		SearchEngine search = new SearchEngine();
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] {_project});
		SearchRequestor requestor = new SearchRequestor();
		
		String packagePattern = "";
		String typePattern = qualifiedName;
		int lastDot = qualifiedName.lastIndexOf('.');
		if (lastDot >= 0)
		{
			packagePattern = qualifiedName.substring(0, lastDot);
			typePattern = qualifiedName.substring(lastDot + 1);
		}
		
		search.searchAllTypeNames(packagePattern.toCharArray(), SearchPattern.R_PATTERN_MATCH, typePattern.toCharArray(), SearchPattern.R_PATTERN_MATCH, IJavaSearchConstants.ANNOTATION_TYPE, scope, requestor, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, new NullProgressMonitor());
		
		return requestor.getResult();
	}
	
	private void saveProperties() throws BackingStoreException
	{
	    _node.flush();
	}
	
	private class SearchRequestor extends TypeNameMatchRequestor
	{
		private IType _result;
		
		@Override
		public void acceptTypeNameMatch(TypeNameMatch match) {
			_result = match.getType();
		}
		
		private IType getResult()
		{
			return _result;
		}
	}
}
