package de.uni_hamburg.informatik.swt.accessanalysis.junitconstraints;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;
import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Hierarchy;

class Junit3AccessDeterminator implements AccessDeterminator {
	
	private static final String SUPER_CLASS = "junit.framework.TestCase";
	private static final String TEST_METHOD_PREFIX = "test";
	private static final String TEST_SUITE_METHOD_NAME = "suite";
	
	private final Map<IJavaProject, Boolean> _enabled = new HashMap<IJavaProject, Boolean>();

	public Access determineAccess(ITypeBinding typeBinding) throws JavaModelException
	{
		if (!constraintEnabled(typeBinding))
		{
			return Access.NO_USE;
		}
		
		if (isJunitClass(typeBinding) && containsJunitMethod(typeBinding))
		{
			return Access.PUBLIC;
		}
				
		return Access.NO_USE;
	}
	
	private boolean containsJunitMethod(ITypeBinding typeBinding) throws JavaModelException
	{
		for (IMethodBinding methodBinding : typeBinding.getDeclaredMethods())
		{
			if (isJunitMethod(methodBinding)) {
				return true;
			}
		}
		
		return false;
	}
	
	public Access determineAccess(IMethodBinding methodBinding) throws JavaModelException
	{
		if (!constraintEnabled(methodBinding))
		{
			return Access.NO_USE;
		}
		
		if (isJunitConstructor(methodBinding) && isJunitClass(methodBinding.getDeclaringClass()) && containsJunitTestMethod(methodBinding.getDeclaringClass()))
		{
			return Access.PUBLIC;
		}
		
		if (isJunitMethod(methodBinding) && isJunitClass(methodBinding.getDeclaringClass()))
		{
			return Access.PUBLIC;
		}

		return Access.NO_USE;
	}
	
	private boolean containsJunitTestMethod(ITypeBinding typeBinding) throws JavaModelException
	{
		for (IMethodBinding methodBinding : typeBinding.getDeclaredMethods())
		{
			if (isJunitTestMethod(methodBinding)) {
				return true;
			}
		}
		
		return false;
	}

	private boolean isJunitClass(ITypeBinding typeBinding) 
	{
		return Hierarchy.isSuperType(SUPER_CLASS, typeBinding);
	}

	private boolean isJunitConstructor(IMethodBinding methodBinding) 
	{
		return methodBinding.isConstructor() && methodBinding.getParameterTypes().length == 0;
	}

	private boolean isJunitMethod(IMethodBinding methodBinding) throws JavaModelException
	{
		return isJunitTestMethod(methodBinding) || isJunitTestSuiteMethod(methodBinding);
	}
	
	private boolean isJunitTestMethod(IMethodBinding methodBinding)
	{
		return methodBinding.getName().startsWith(TEST_METHOD_PREFIX) && "void".equals(methodBinding.getReturnType().getQualifiedName());
	}
	
	private boolean isJunitTestSuiteMethod(IMethodBinding methodBinding) throws JavaModelException
	{
		return methodBinding.getName().equals(TEST_SUITE_METHOD_NAME)
				&& Access.fromFlags((IMember) methodBinding.getJavaElement()) == Access.PUBLIC;
	}
	
	private boolean constraintEnabled(IBinding binding)
	{
		IJavaProject project = binding.getJavaElement().getJavaProject();
		
		if (!_enabled.containsKey(project))
		{
			JunitConstraintsProperties properties = new JunitConstraintsProperties(project);
			boolean enabled = properties.getProperty(JunitConstraintsProperties.Key.JUNIT3);
			_enabled.put(project, enabled);
		}
		
		return _enabled.get(project);
	}
}
