package de.uni_hamburg.informatik.swt.accessanalysis.analysis;


import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IPackageBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators.ExpressionTypeDeterminator;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators.InvokedMethodAccessDeterminator;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators.InvokedMethodAccessDeterminatorForStaticContext;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators.OverriddenMethodAccessDeterminator;
import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Hierarchy;
import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Locals;
import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Nesting;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.MethodAccessConstraintChecker;


/**
 * Handles the declaration and invocation of methods for the access analysis
 */
class MethodHandler
{
    private final Stack<ITypeBinding> _currentTypes;
    private final MethodMap _methodMap;
    
    private final ExpressionTypeDeterminator _expressionTypeDeterminator;
    private final Set<MethodAccessConstraintChecker> _constraints;
    
    private InvokedMethodAccessDeterminator _invokedMethodAccessDeterminator;
    private InvokedMethodAccessDeterminatorForStaticContext _invokedMethodAccessDeterminatorForStaticContext;
    private boolean _constructorWithoutConstructorCall;
    
    /**
     * Initializes an object of MethodHandler. 
     * 
     * @param ExpressionTypeDeterminator expressionTypeDeterminator The ExpressionTypeDeterminator to be used
     */
    MethodHandler(ExpressionTypeDeterminator expressionTypeDeterminator, Set<MethodAccessConstraintChecker> constraints)
    {
    	_currentTypes = new Stack<ITypeBinding>();
    	_methodMap = new MethodMap();
    	
        _expressionTypeDeterminator = expressionTypeDeterminator;
		_constraints = constraints;
		
		_invokedMethodAccessDeterminator = null;
		_invokedMethodAccessDeterminatorForStaticContext = null;
        _constructorWithoutConstructorCall = false;
    }
    
    /**
     * Returns the method map with the determined minimal accesses.
     * 
     * @return MethodMap The map
     */
    MethodMap getMethodMap()
    {
        return _methodMap;
    }
    
    /**
     * Handles a MethodDeclaration
     * 
     * @param MethodDeclaration node
     */
    void handle(MethodDeclaration node) throws JavaModelException
    {
        IMethodBinding methodBinding = node.resolveBinding();
        setCurrentMethod(methodBinding);
        
        checkConstraints(methodBinding);
        
        IMethodBinding overriddenBinding = Hierarchy.findOverriddenMethod(methodBinding, true);
        if (overriddenBinding != null)
        {
            overriddenBinding = overriddenBinding.getMethodDeclaration();
            if (isLocalMethod(overriddenBinding))
            {
                _methodMap.putAccess(methodBinding, Access.NO_USE);
                _methodMap.putOverride(methodBinding, overriddenBinding);
                _methodMap.putAccess(overriddenBinding, OverriddenMethodAccessDeterminator.determineAccess(methodBinding, overriddenBinding));
            }
        }
        
        _constructorWithoutConstructorCall = node.isConstructor(); 
    }
    
    private void checkConstraints(IMethodBinding methodBinding) throws JavaModelException {
		for (MethodAccessConstraintChecker constraint : _constraints) {
        	Access access = constraint.checkMethodDeclaration(methodBinding);
        	if (access != null && access.compareTo(Access.NO_USE) > 0) {
        		_methodMap.putAccess(methodBinding, access);
        		if (access == Access.PUBLIC) {
        			break;
        		}
        	}
        }
	}
    
    /**
     * Handles the end of MethodDeclarartion
     * @throws JavaModelException 
     */
    void handleEnd(MethodDeclaration node) throws JavaModelException
    {
        if (_constructorWithoutConstructorCall)
        {
            handleImplicitConstructorInvocation();
        }
        
        unsetCurrentMethod();
    }
    
    /**
     * Handles a MethodInvocation
     * 
     * @param MethodInvocation node
     */
    void handle(MethodInvocation node) throws JavaModelException
    {
        IMethodBinding methodBinding = node.resolveMethodBinding().getMethodDeclaration();
        if (isLocalMethod(methodBinding))
        {
            ITypeBinding expressionType = _expressionTypeDeterminator.getExpressionType(node);
            _methodMap.putAccess(methodBinding, getAccessDeterminator().determineAccess(methodBinding, expressionType));
        }
    }
    
    /**
     * Handles a SuperMethodInvocation
     * 
     * @param SuperMethodInvocation node
     */
    void handle(SuperMethodInvocation node)
    {
        handleSuperMethod(node.resolveMethodBinding().getMethodDeclaration());
    }
    
    /**
     * Handles a ClassInstanceCreation
     * 
     * @param ClassInstanceCreation node
     */
    void handle(ClassInstanceCreation node) throws JavaModelException
    {
        IMethodBinding constructorBinding = node.resolveConstructorBinding().getMethodDeclaration();
        if (constructorBinding.getJavaElement() != null && isLocalMethod(constructorBinding))
        {
            _methodMap.putAccess(constructorBinding, getAccessDeterminator().determineAccess(constructorBinding, constructorBinding.getDeclaringClass()));
        }
    }
    
    /**
     * Handles a ConstructorInvocation
     * 
     * @param ConstructorInvocation node
     */
    void handle(ConstructorInvocation node)
    {
        IMethodBinding constructorBinding = node.resolveConstructorBinding().getMethodDeclaration();
        if (constructorBinding.getJavaElement() != null)
        {
            _methodMap.putAccess(constructorBinding, Access.PRIVATE);
        }
        
        _constructorWithoutConstructorCall = false;
    }
    
    /**
     * Handles a SuperConstructorInvocation
     * 
     * @param SuperConstructorInvocation node
     */
    void handle(SuperConstructorInvocation node)
    {
        IMethodBinding constructorBinding = node.resolveConstructorBinding().getMethodDeclaration();
        if (constructorBinding.getJavaElement() != null)
        {
            handleSuperMethod(constructorBinding);
        }
        
        _constructorWithoutConstructorCall = false;
    }
    
    /**
     * Handles a C
     * 
     * @param ClassInstanceCreation node
     */
    void handle(EnumConstantDeclaration node) throws JavaModelException
    {
        IMethodBinding constructorBinding = node.resolveConstructorBinding().getMethodDeclaration();
        if (constructorBinding.getJavaElement() != null && isLocalMethod(constructorBinding))
        {
            _methodMap.putAccess(constructorBinding, getAccessDeterminator().determineAccess(constructorBinding, constructorBinding.getDeclaringClass()));
        }
    }
    
    /**
     * Handles an AbstractTypeDeclaration
     * 
     * @param AbstractTypeDeclaration node
     */
    void handle(AbstractTypeDeclaration node)
    {
        ITypeBinding typeBinding = node.resolveBinding();
        setCurrentType(typeBinding);
        
        for (IMethodBinding method : Hierarchy.findAllInterfaceMethods(typeBinding))
        {
            if (Hierarchy.findOveridingMethodInType(typeBinding, method) == null)
            {
                IMethodBinding inheritedImplementation = Hierarchy.findMethodInSuperclasses(typeBinding, method, true);
                if (inheritedImplementation != null)
                {
                    _methodMap.putAccess(inheritedImplementation, Access.PUBLIC);
                }
            }
        }
    }
    
    /**
     * Handles the end of an AbstractTypeDeclaration
     * @throws JavaModelException 
     */
    void handleEnd(AbstractTypeDeclaration node) throws JavaModelException
    {
        if (defaultConstructorCallsSuperConstructor())
        {
            handleImplicitConstructorInvocation();
        }
        
        unsetCurrentType();
    }
    
    private void handleSuperMethod(IMethodBinding methodBinding)
    {
        if (isLocalMethod(methodBinding))
        {
            ITypeBinding typeBinding = methodBinding.getDeclaringClass();
            Access access = Access.PROTECTED;
            
            ITypeBinding topLevelType = Nesting.getTopLevelType(typeBinding);
            if (topLevelType.equals(Nesting.getTopLevelType(getCurrentType())))
            {
                access = Access.PRIVATE;
            }
            else
            {
                IPackageBinding packageBinding = typeBinding.getPackage();
                if (packageBinding.equals(getCurrentType().getPackage()))
                {
                    access = Access.DEFAULT;
                }
            }
            
            _methodMap.putAccess(methodBinding, access);
        }
    }
    
    private boolean defaultConstructorCallsSuperConstructor()
    {
        if (getCurrentType().isClass() && getCurrentType().getSuperclass() != null)
        {
            for (IMethodBinding method : getCurrentType().getDeclaredMethods())
            {
                if (method.isDefaultConstructor())
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private void handleImplicitConstructorInvocation() throws JavaModelException
    {
        for (IMethodBinding methodBinding : getCurrentType().getSuperclass().getDeclaredMethods())
        {
            if (methodBinding.isConstructor() && methodBinding.getParameterTypes().length == 0)
            {
                handleSuperMethod(methodBinding);
                break;
            }
        }
    }
       
    private boolean isLocalMethod(IMethodBinding methodBinding)
    { 
        return Locals.isLocalMethod(methodBinding);
    }
    
    private void setCurrentType(ITypeBinding typeBinding)
    {
        _currentTypes.push(typeBinding);
        _invokedMethodAccessDeterminatorForStaticContext = new InvokedMethodAccessDeterminatorForStaticContext(getCurrentType());
        _invokedMethodAccessDeterminator = getAccessDeterminatorForStaticContext();
    }
    
    private void unsetCurrentType()
    {
        _currentTypes.pop();
        _invokedMethodAccessDeterminatorForStaticContext = new InvokedMethodAccessDeterminatorForStaticContext(getCurrentType());
        _invokedMethodAccessDeterminator = getAccessDeterminatorForStaticContext();
    }
    
    private ITypeBinding getCurrentType()
    {
        if (_currentTypes.isEmpty())
        {
            return null;
        }
        
        return _currentTypes.peek();
    }
    
    private void setCurrentMethod(IMethodBinding methodBinding)
    {
        _invokedMethodAccessDeterminator = new InvokedMethodAccessDeterminator(methodBinding);
    }
    
    private void unsetCurrentMethod()
    {
        _invokedMethodAccessDeterminator = getAccessDeterminatorForStaticContext();
    }
    
    private InvokedMethodAccessDeterminatorForStaticContext getAccessDeterminatorForStaticContext()
    {
        return _invokedMethodAccessDeterminatorForStaticContext;
    }
    
    private InvokedMethodAccessDeterminator getAccessDeterminator()
    {
        return _invokedMethodAccessDeterminator;
    }
}
