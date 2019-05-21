package de.uni_hamburg.informatik.swt.accessanalysis.junitconstraints;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;

class Junit4AccessDeterminator implements AccessDeterminator {
	
	private static final String RUN_WITH_ANNOTATION = "org.junit.runner.RunWith";
	private static final String TEST_ANNOTATION = "org.junit.Test";
		
	private static final Set<String> METHOD_ANNOTATIONS = new HashSet<String>(Arrays.asList(
		TEST_ANNOTATION,
		"org.junit.Before",
		"org.junit.BeforeClass",
		"org.junit.After",
		"org.junit.AfterClass"
	));
	
	private static final Set<String> FIELD_ANNOTATIONS = new HashSet<String>(Arrays.asList(
		"org.junit.Rule",
		"org.junit.ClassRule"
	));
	
	private final Map<IJavaProject, Boolean> _enabled = new HashMap<IJavaProject, Boolean>();
	
	public Access determineAccess(ITypeBinding typeBinding)
	{
		if (!constraintEnabled(typeBinding))
		{
			return Access.NO_USE;
		}
		
		if (containsJunitMember(typeBinding))
		{
			return Access.PUBLIC;
		}
				
		if (typeBinding.isTopLevel())
		{
			for (IAnnotationBinding annotationBinding : typeBinding.getAnnotations())
			{
				if (RUN_WITH_ANNOTATION.equals(annotationBinding.getAnnotationType().getQualifiedName()))
				{
					return Access.DEFAULT;
				}
			}
		}
		
		return Access.NO_USE;
	}
	
	private boolean containsJunitMember(ITypeBinding typeBinding)
	{
		for (IMethodBinding methodBinding : typeBinding.getDeclaredMethods())
		{
			if (isJunitMethod(methodBinding)) {
				return true;
			}
		}

		for (IVariableBinding variableBinding : typeBinding.getDeclaredFields())
		{
			if (isJunitField(variableBinding)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean containsTestMethod(ITypeBinding typeBinding)
	{
		for (IMethodBinding methodBinding : typeBinding.getDeclaredMethods())
		{
			if (isJunitTestMethod(methodBinding)) {
				return true;
			}
		}

		return false;
	}
	
	public Access determineAccess(IMethodBinding methodBinding)
	{
		if (!constraintEnabled(methodBinding))
		{
			return Access.NO_USE;
		}
		
		if (isJunitConstructor(methodBinding) && containsTestMethod(methodBinding.getDeclaringClass()))
		{
			return Access.PUBLIC;
		}
		
		if (isJunitMethod(methodBinding))
		{
			return Access.PUBLIC;
		}

		return Access.NO_USE;
	}
	
	private boolean isJunitConstructor(IMethodBinding methodBinding)
	{
		return methodBinding.isConstructor() && methodBinding.getParameterTypes().length == 0;
	}
	
	private boolean isJunitMethod(IMethodBinding methodBinding)
	{
		for (IAnnotationBinding annotationBinding : methodBinding.getAnnotations())
		{
			if (METHOD_ANNOTATIONS.contains(annotationBinding.getAnnotationType().getQualifiedName()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isJunitTestMethod(IMethodBinding methodBinding)
	{
		for (IAnnotationBinding annotationBinding : methodBinding.getAnnotations())
		{
			if (annotationBinding.getAnnotationType().getQualifiedName().equals(TEST_ANNOTATION))
			{
				return true;
			}
		}
		
		return false;
	}
	
	private boolean isJunitField(IVariableBinding fieldBinding)
	{
		for (IAnnotationBinding annotationBinding : fieldBinding.getAnnotations())
		{
			if (FIELD_ANNOTATIONS.contains(annotationBinding.getAnnotationType().getQualifiedName()))
			{
				return true;
			}
		}
		
		return false;
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
