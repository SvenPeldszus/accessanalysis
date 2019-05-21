package de.uni_hamburg.informatik.swt.accessanalysis.annotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.MethodAccessConstraintChecker;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.TypeAccessConstraintChecker;
import de.uni_hamburg.informatik.swt.accessanalysis.extensions.RcpExtension;

@RcpExtension
public class AnnotationConstraintChecker implements TypeAccessConstraintChecker, MethodAccessConstraintChecker {

    private Map<IJavaProject, Map<IType, Access>> _typeConstraintTables;
    private Map<IJavaProject, Map<IType, Access>> _methodsConstraintTables;

    @RcpExtension
    public AnnotationConstraintChecker()
    {
        _typeConstraintTables = new HashMap<IJavaProject, Map<IType, Access>>();
        _methodsConstraintTables = new HashMap<IJavaProject, Map<IType, Access>>();
    }

    @Override
    public Access checkTypeDeclaration(ITypeBinding typeBinding) throws JavaModelException
    {
        Access access = Access.NO_USE;

        Map<IType, Access> constraintTable = getConstraintTable(typeBinding);
        for (IAnnotationBinding annotation : typeBinding.getAnnotations())
        {
            IJavaElement annotationType = annotation.getAnnotationType().getJavaElement();
            if (constraintTable.containsKey(annotationType))
            {
                access = Access.max(access, constraintTable.get(annotationType));
                if (access == Access.PUBLIC)
                {
                    break;
                }
            }
        }

        if (typeBinding.isTopLevel())
        {
            if (access == Access.PROTECTED)
            {
                access = Access.PUBLIC;
            } else if (access == Access.PRIVATE)
            {
                access = Access.DEFAULT;
            }
        }

        return access;
    }

    @Override
    public Access checkMethodDeclaration(IMethodBinding methodBinding) throws JavaModelException
    {
        Access access = Access.NO_USE;

        Map<IType, Access> constraintTable = getConstraintTable(methodBinding);
        for (IAnnotationBinding annotation : methodBinding.getAnnotations())
        {
            IJavaElement annotationType = annotation.getAnnotationType().getJavaElement();
            if (constraintTable.containsKey(annotationType))
            {
                access = Access.max(access, constraintTable.get(annotationType));
                if (access == Access.PUBLIC)
                {
                    break;
                }
            }
        }

        return access;
    }

    private Map<IType, Access> getConstraintTable(ITypeBinding typeBinding) throws JavaModelException
    {
        IJavaProject project = typeBinding.getJavaElement().getJavaProject();

        if (!_typeConstraintTables.containsKey(project))
        {
            fillConstraintTables(project);
        }

        return _typeConstraintTables.get(project);
    }

    private Map<IType, Access> getConstraintTable(IMethodBinding methodBinding) throws JavaModelException
    {
        IJavaProject project = methodBinding.getJavaElement().getJavaProject();

        if (!_methodsConstraintTables.containsKey(project))
        {
            fillConstraintTables(project);
        }

        return _methodsConstraintTables.get(project);
    }

    private void fillConstraintTables(IJavaProject project) throws JavaModelException
    {
        AnnotationConstraintsProperties properties = new AnnotationConstraintsProperties(project);
        Collection<AnnotationConstraint> constraints = new ArrayList<AnnotationConstraint>();
        constraints = properties.getConstraints();

        Map<IType, Access> typeConstraintTable = new HashMap<IType, Access>();
        Map<IType, Access> methodConstraintTable = new HashMap<IType, Access>();
        for (AnnotationConstraint constraint : constraints)
        {
            IType type = constraint.getAnnotationType();
            Access access = constraint.getMinimalAccess();

            if (constraint.applyTo().types())
            {
                typeConstraintTable.put(type, access);
            }

            if (constraint.applyTo().methods())
            {
                methodConstraintTable.put(type, access);
            }
        }

        _typeConstraintTables.put(project, typeConstraintTable);
        _methodsConstraintTables.put(project, methodConstraintTable);
    }
}
