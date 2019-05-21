/**
 * 
 */
package de.uni_hamburg.informatik.swt.accessanalysis.analysis;

import java.util.Set;
import java.util.Stack;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IAnnotationBinding;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WildcardType;

import de.uni_hamburg.informatik.swt.accessanalysis.access.Access;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators.ExpressionTypeDeterminator;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators.StaticImportedTypeAccessDeterminator;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators.TypeAccessDeterminator;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators.UsedTypeAccessDeterminator;
import de.uni_hamburg.informatik.swt.accessanalysis.bindings.Locals;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.TypeAccessConstraintChecker;


/**
 * Handles the usage of types for the access analysis.
 */
class TypeHandler
{
    private final TypeMap _typeMap = new TypeMap();

    private final ExpressionTypeDeterminator _expressionTypeDeterminator;
    private final Set<TypeAccessConstraintChecker> _constraints;
    
    private Stack<ITypeBinding> _currentTypes;
    private Stack<TypeAccessDeterminator> _typeAccessDeterminators;
    
    
    /**
     * Initializes an object of TypeHandler. 
     * 
     * @param ExpressionTypeDeterminator expressionTypeDeterminator The ExpressionTypeDeterminator to be used
     */
    TypeHandler(ExpressionTypeDeterminator expressionTypeDeterminator, Set<TypeAccessConstraintChecker> constraints)
    {
        _expressionTypeDeterminator = expressionTypeDeterminator;
        _constraints = constraints;
        
        _currentTypes = null;
        _typeAccessDeterminators = null;
    }
    
    /**
     * Returns the type map with the determined minimal accesses.
     * 
     * @return TypeMap The map
     */
    TypeMap getTypeMap()
    {
        return _typeMap;
    }
    
    /**
     * Handles a CompilationUnit
     * 
     * @param CompilationUnit node
     */
    void handle(CompilationUnit node)
    {
        setCurrentCompilationUnit(node);
    }
    
    /**
     * Handles a ImportDeclaration
     * 
     * @param ImportDeclaration node
     */
    void handle(ImportDeclaration node) throws JavaModelException
    {
        if (node.isStatic())
        {
            handleUsage(resolveTypeDeclaration(node.resolveBinding()));
        }
    }
    
    /**
     * Handles a TypeDeclaration
     * 
     * @param TypeDeclaration node
     */
    void handle(AbstractTypeDeclaration node) throws JavaModelException
    {
        ITypeBinding typeBinding = node.resolveBinding();
        setCurrentType(typeBinding);
        
        ITypeBinding superclassBinding = typeBinding.getSuperclass();
        if (superclassBinding != null)
        {
            handleUsage(resolveTypeDeclaration(typeBinding.getSuperclass()));
        }
        
        for (ITypeBinding interfaceBinding : typeBinding.getInterfaces())
        {
            handleUsage(resolveTypeDeclaration(interfaceBinding));
        }
        
        checkConstraints(typeBinding);
    }

	private void checkConstraints(ITypeBinding typeBinding) throws JavaModelException {
		for (TypeAccessConstraintChecker constraint : _constraints) {
        	Access access = constraint.checkTypeDeclaration(typeBinding);
        	if (access != null && access.compareTo(Access.NO_USE) > 0) {
        		_typeMap.putAccess(typeBinding, access);
        		if (access == Access.PUBLIC) {
        			break;
        		}
        	}
        }
	}
    
    /**
     * Handles the end of a TypeDeclaration
     * 
     * @param TypeDeclaration node
     */
    void handleEnd(AbstractTypeDeclaration node)
    {
        unsetCurrentType();
    }
    
    void handle(ClassInstanceCreation node) throws JavaModelException
    {
        handleUsage(resolveTypeDeclaration(node.resolveConstructorBinding().getDeclaringClass()));
    }
    
    void handle(FieldDeclaration node) throws JavaModelException
    {
        handleUsage(resolveTypeDeclaration(node.getType()));
    }
    
    void handle(SingleVariableDeclaration node) throws JavaModelException
    {
        handleUsage(resolveTypeDeclaration(node.getType()));
    }
    
    void handle(VariableDeclarationStatement node) throws JavaModelException
    {
        handleUsage(resolveTypeDeclaration(node.getType()));
    }
    
    void handle(ParameterizedType node) throws JavaModelException
    {
        for (ITypeBinding typeArgumentBinding : node.resolveBinding().getTypeArguments())
        {
            handleUsage(resolveTypeDeclaration(typeArgumentBinding));
        }
    }
    
    void handle(WildcardType node) throws JavaModelException
    {
        Type boundType = node.getBound();
        
        if (boundType != null)
        {
            handleUsage(resolveTypeDeclaration(boundType));
        }
    }
    
    void handle(MethodDeclaration node) throws JavaModelException
    {
        IMethodBinding binding = node.resolveBinding();
        
        if (binding.getJavaElement() != null && ((IMethod) binding.getJavaElement()).isMainMethod())
        {
            _typeMap.putAccess(getCurrentType(), Access.DEFAULT);
        }
        
        Type returnType = node.getReturnType2();
        if (returnType != null)
        {
            handleUsage(resolveTypeDeclaration(returnType));
        }
        
        for (ITypeBinding exceptionBinding : node.resolveBinding().getExceptionTypes())
        {
            handleUsage(resolveTypeDeclaration(exceptionBinding));
        }
    }
    
    void handle(InstanceofExpression node) throws JavaModelException
    {
        handleUsage(resolveTypeDeclaration(node.getRightOperand()));
    }
    
    void handle(CastExpression node) throws JavaModelException
    {
        handleUsage(resolveTypeDeclaration(node.getType()));
    }
    
    void handle(Annotation node) throws JavaModelException
    {
        handleUsage(resolveTypeDeclaration(node.resolveAnnotationBinding().getAnnotationType()));
    }
    
    void handle(MethodInvocation node) throws JavaModelException
    {
        ITypeBinding expressionBinding = _expressionTypeDeterminator.getExpressionType(node);
        
        if (expressionBinding != null)
        {
            handleUsage(resolveTypeDeclaration(expressionBinding));
        }
    }
    
    void handle(FieldAccess node) throws JavaModelException
    {
        ITypeBinding expressionBinding = _expressionTypeDeterminator.getExpressionType(node);
        
        if (expressionBinding != null)
        {
            handleUsage(resolveTypeDeclaration(expressionBinding));
        }
    }
    
    void handle(AnnotationTypeMemberDeclaration node) throws JavaModelException
    {
        handleUsage(resolveTypeDeclaration(node.getType()));
    }
    
    void handle(QualifiedName node) throws JavaModelException
    {
        ITypeBinding typeBinding = node.getQualifier().resolveTypeBinding();
        
        if (typeBinding != null)
        {           
            handleUsage(resolveTypeDeclaration(typeBinding));
        }
    }
    
    void handle(TypeLiteral node) throws JavaModelException
    {
        Type type = node.getType();
        
        if (type != null)
        {
            handleUsage(resolveTypeDeclaration(type));
        }
    }
    
    private void handleUsage(ITypeBinding binding) throws JavaModelException
    {
        if (isLocalType(binding))
        {
            _typeMap.putAccess(binding, getAccessDeterminator().determineAccess(binding));
            
            if (binding.isNested())
            {
                handleUsage(resolveTypeDeclaration(binding.getDeclaringClass()));
            }
        }
    }
    
    private boolean isLocalType(ITypeBinding typeBinding)
    { 
        return Locals.isLocalType(typeBinding);
    }
    
    private void setCurrentCompilationUnit(CompilationUnit compilationUnit)
    {
        _currentTypes = new Stack<ITypeBinding>();
        _typeAccessDeterminators = new Stack<TypeAccessDeterminator>();
        _typeAccessDeterminators.push(new StaticImportedTypeAccessDeterminator(compilationUnit));
    }
    
    private void setCurrentType(ITypeBinding typeBinding)
    {
        _currentTypes.push(typeBinding);
        _typeAccessDeterminators.push(new UsedTypeAccessDeterminator(typeBinding));
    }
    
    private void unsetCurrentType()
    {
        _currentTypes.pop();
        _typeAccessDeterminators.pop();
    }
    
    private ITypeBinding getCurrentType()
    {
        return _currentTypes.peek();
    }

    /**
     * @return the _usedTypeAccessDeterminator
     */
    private TypeAccessDeterminator getAccessDeterminator()
    {
        return _typeAccessDeterminators.peek();
    }
    
    private ITypeBinding resolveTypeDeclaration(Type type)
    {
        return resolveTypeDeclaration(type.resolveBinding());
    }
    
    private ITypeBinding resolveTypeDeclaration(ITypeBinding binding)
    {
        if (binding.isArray())
        {
            binding = binding.getElementType();
        }
        
        return binding.getTypeDeclaration();
    }
    
    private ITypeBinding resolveTypeDeclaration(IMethodBinding binding)
    {
        return resolveTypeDeclaration(binding.getDeclaringClass());
    }
    
    private ITypeBinding resolveTypeDeclaration(IVariableBinding binding)
    {
        return resolveTypeDeclaration(binding.getDeclaringClass());
    }
    
    private ITypeBinding resolveTypeDeclaration(IAnnotationBinding binding)
    {
        return resolveTypeDeclaration(binding.getAnnotationType());
    }

    /**
     * Findet zu einem <code>binding</code> den Typen, der hierfür behandelt werden muss. 
     * @param IBinding binding Ausschließlich erlaubt sind Bindings von 
     *  Typen, Methoden, Variablen und Annotations. 
     * @return ITypeBinding Das Binding des zu behandelnden Typen
     * 
     * @throws IllegalArgumentException wenn ein Binding einer anderen als die genannten
     * übergeben wird.
     */
    private ITypeBinding resolveTypeDeclaration(IBinding binding)
    {
        switch (binding.getKind())
        {
            case IBinding.TYPE       : return resolveTypeDeclaration((ITypeBinding) binding);
            case IBinding.METHOD     : return resolveTypeDeclaration((IMethodBinding) binding);
            case IBinding.VARIABLE   : return resolveTypeDeclaration((IVariableBinding) binding);
            case IBinding.ANNOTATION : return resolveTypeDeclaration((IAnnotationBinding) binding);
            default : throw new IllegalArgumentException("Binding not supported.");
        }
    }
}
