/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Hierarchy;

/**
 * Determines the types of expressions.
 */
public class ExpressionTypeDeterminator
{
    private final Stack<ITypeBinding> _currentTypes;

    private CompilationUnit _currentCompilationUnit;
    
    /**
     * Initializes an object of ExpressionTypeDeterminator. 
     */
    public ExpressionTypeDeterminator()
    {
        _currentTypes = new Stack<ITypeBinding>();
    }
    
    /**
     * Handles a CompilationUnit
     * 
     * @param CompilationUnit node
     */
    public void handle(CompilationUnit node)
    {
        _currentCompilationUnit = node;
    }
    
    /**
     * Handles a TypeDeclaration
     * 
     * @param TypeDeclaration node
     */
    public void handle(AbstractTypeDeclaration node)
    {
        ITypeBinding typeBinding = node.resolveBinding();
        setCurrentType(typeBinding);
    }
    
    /**
     * Handles the end of a TypeDeclaration
     * 
     * @param TypeDeclaration node
     */
    public void handleEnd(AbstractTypeDeclaration node)
    {
        unsetCurrentType();
    }
    
    /**
     * Determines the type of method invocation.
     * 
     * Returns not the return type of the invoked method but the expression where the method is called at. 
     * 
     * @param MethodInvocation node The method invocation to be examined.
     * @return ITypeBinding The type of the expression
     * @throws JavaModelException
     */
    public ITypeBinding getExpressionType(MethodInvocation node) throws JavaModelException
    {
        Expression expression = node.getExpression();
        if (expression != null)
        {
            ITypeBinding binding = expression.resolveTypeBinding();
            if (binding == null)
            {
                binding = node.resolveMethodBinding().getDeclaringClass();
            }
            while (binding.isTypeVariable())
            {
                ITypeBinding[] bounds = binding.getTypeBounds();
                if (bounds.length == 0)
                {
                    return null;
                }
                binding = bounds[0];
            }
            return binding.getTypeDeclaration();
        }
        
        IMethodBinding method = node.resolveMethodBinding();
        ITypeBinding enclosingType = getCurrentType();
        
        while (enclosingType != null)
        {
            if (Hierarchy.findOverriddenMethodInHierarchy(enclosingType, method) != null)
            {
                return enclosingType.getTypeDeclaration();
            }
            enclosingType = enclosingType.getDeclaringClass();
        }
        
        if (method.getJavaElement() != null && Flags.isStatic(((IMethod) method.getJavaElement()).getFlags()))
        {
            return getTypeOfStaticImportedMethods(method);
        }

        return null;
    }
    
    /**
     * Determines the type of field access.
     * 
     * Returns not the type of the field but the expression where the field is accessed at. 
     * 
     * @param FieldAccess node The field access to be examined.
     * @return ITypeBinding The type of the expression
     * @throws JavaModelException
     */
    public ITypeBinding getExpressionType(FieldAccess node) throws JavaModelException
    {
        Expression expression = node.getExpression();
        if (expression != null)
        {
            return expression.resolveTypeBinding().getTypeDeclaration();
        }
        
        IVariableBinding field = node.resolveFieldBinding();
        ITypeBinding enclosingType = getCurrentType();
        
        while (enclosingType != null)
        {
            if (Hierarchy.findFieldInHierarchy(enclosingType, field) != null)
            {
                return enclosingType.getTypeDeclaration();
            }
            enclosingType = enclosingType.getDeclaringClass();
        }

        return null;
    }
    
    private ITypeBinding getTypeOfStaticImportedMethods(IMethodBinding method)
    {
        ITypeBinding type = method.getDeclaringClass();
        
        for (ImportDeclaration importDeclaration : getStaticImports())
        {
            IBinding importedBinding = importDeclaration.resolveBinding();
            
            if (importedBinding.getKind() == IBinding.TYPE)
            {
                ITypeBinding importedType = (ITypeBinding) importedBinding;
                if (importedType.isSubTypeCompatible(type))
                {
                    return importedType.getTypeDeclaration();
                }
            }
            else if (importedBinding.getKind() == IBinding.METHOD)
            {
                IMethodBinding importedMethodBinding = (IMethodBinding) importedBinding;
                if (method.equals(importedMethodBinding))
                {
                    return type.getTypeDeclaration();
                }
            }
        }
        
        return null;
    }
    
    private void setCurrentType(ITypeBinding typeBinding)
    {
        _currentTypes.push(typeBinding);
    }
    
    private void unsetCurrentType()
    {
        if (! _currentTypes.empty())
        {
            _currentTypes.pop();
        }
    }
    
    private ITypeBinding getCurrentType()
    {
        if (_currentTypes.empty())
        {
            return null;
        }
        
        return _currentTypes.peek();
    }
    
    private Iterable<ImportDeclaration> getStaticImports()
    {
        Collection<ImportDeclaration> staticImports = new LinkedList<ImportDeclaration>();
        
        for (Object o : _currentCompilationUnit.imports())
        {
            ImportDeclaration importDeclaration = (ImportDeclaration) o;
            if (importDeclaration.isStatic())
            {
                staticImports.add(importDeclaration);
            }
        }
        
        return staticImports;
    }
}
