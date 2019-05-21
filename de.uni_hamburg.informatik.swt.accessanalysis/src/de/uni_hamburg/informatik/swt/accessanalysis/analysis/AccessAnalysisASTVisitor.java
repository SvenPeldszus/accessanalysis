package de.uni_hamburg.informatik.swt.accessanalysis.analysis;

import java.util.Stack;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MarkerAnnotation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WildcardType;

import de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators.ExpressionTypeDeterminator;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.printer.ElementPrinter;
import de.uni_hamburg.informatik.swt.accessanalysis.results.MethodResult;
import de.uni_hamburg.informatik.swt.accessanalysis.results.PackageResult;
import de.uni_hamburg.informatik.swt.accessanalysis.results.TypeResult;

class AccessAnalysisASTVisitor extends ASTVisitor {
    private final MethodHandler _methodHandler;
    private final TypeHandler _typeHandler;
    private final ElementPrinter _printer;
    private final PackageResult _packageResult;
    
    private final ExpressionTypeDeterminator _expressionTypeDeterminator;
    private final Stack<TypeResult> _typeResults;
    
    private boolean _localOrAnonymous;

    /**
     * Initializes an object of AccessAnalysisASTVisitor.
     * 
     * @param ElementPrinter
     *            printer The Printer that prints element information to the console
     */
    AccessAnalysisASTVisitor(PackageResult packageResult, TypeHandler typeHandler, MethodHandler methodHandler, ExpressionTypeDeterminator expressionTypeDeterminator, ElementPrinter printer)
    {
        _packageResult = packageResult;
        _typeHandler = typeHandler;
        _methodHandler = methodHandler;
        _expressionTypeDeterminator = expressionTypeDeterminator;
        _printer = printer;
        
        _typeResults = new Stack<TypeResult>();
        _localOrAnonymous = false;
    }

    /**
     * Sets the current type.
     * 
     * @param IType
     *            type The current type
     */
    private void setType(AbstractTypeDeclaration node)
    {
        _printer.typeBefore(node);
        TypeResult typeResult = new TypeResult(node, _packageResult);
        _typeResults.push(typeResult);
        _packageResult.addType(typeResult);
    }

    /**
     * Unsets the current type
     */
    private void unsetType()
    {
        _typeResults.pop();
        _printer.typeAfter();
    }

    /**
     * Returns the result of the current type
     * 
     * @return TypeResult the result of the current type
     */
    private TypeResult getTypeResult()
    {
        return _typeResults.peek();
    }

    @Override
    public boolean visit(CompilationUnit node)
    {
        _printer.compilationUnitBefore(node);
        _expressionTypeDeterminator.handle(node);
        _typeHandler.handle(node);
        return true;
    }

    @Override
    public void endVisit(CompilationUnit node)
    {
        _printer.compilationUnitAfter();
    }

    @Override
    public boolean visit(ImportDeclaration node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }
        return false;
    }

    @Override
    public boolean visit(TypeDeclaration node)
    {
        return visit((AbstractTypeDeclaration) node);
    }

    @Override
    public void endVisit(TypeDeclaration node)
    {
        endVisit((AbstractTypeDeclaration) node);
    }

    @Override
    public boolean visit(EnumDeclaration node)
    {
        return visit((AbstractTypeDeclaration) node);
    }

    @Override
    public void endVisit(EnumDeclaration node)
    {
        endVisit((AbstractTypeDeclaration) node);
    }

    @Override
    public boolean visit(AnnotationTypeDeclaration node)
    {
        return visit((AbstractTypeDeclaration) node);
    }

    @Override
    public void endVisit(AnnotationTypeDeclaration node)
    {
        endVisit((AbstractTypeDeclaration) node);
    }

    private boolean visit(AbstractTypeDeclaration node)
    {
        if (!node.isLocalTypeDeclaration())
        {
            setType(node);

            _expressionTypeDeterminator.handle(node);
            _methodHandler.handle(node);

            try
            {
                _typeHandler.handle(node);
            } catch (JavaModelException e)
            {
                _printer.error(e);
            }
        } else
        {
            _printer.typeBefore(node);
            _localOrAnonymous = true;
        }

        return true;
    }

    private void endVisit(AbstractTypeDeclaration node)
    {
        if (!node.isLocalTypeDeclaration())
        {
            _expressionTypeDeterminator.handleEnd(node);

            try
            {
                _methodHandler.handleEnd(node);
            } catch (JavaModelException e)
            {
                _printer.error(e);
            }

            _typeHandler.handleEnd(node);
            unsetType();
        } else
        {
            _printer.typeAfter();
            _localOrAnonymous = false;
        }
    }

    @Override
    public boolean visit(AnonymousClassDeclaration node)
    {
        _printer.typeBefore(node);
        _localOrAnonymous = true;
        return true;
    }

    @Override
    public void endVisit(AnonymousClassDeclaration node)
    {
        _printer.typeAfter();
        _localOrAnonymous = false;
    }

    @Override
    public boolean visit(MethodDeclaration node)
    {
        _printer.methodBefore(node);

        try
        {
            _methodHandler.handle(node);
            if (!_localOrAnonymous && node.resolveBinding() != null && node.resolveBinding().getJavaElement() != null)
            {
                getTypeResult().addMethodResults(new MethodResult(node, getTypeResult()));
            }

            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public void endVisit(MethodDeclaration node)
    {
        try
        {
            _methodHandler.handleEnd(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        _printer.methodAfter();
    }

    @Override
    public boolean visit(MethodInvocation node)
    {
        _printer.invocationBefore(node);
        try
        {
            _methodHandler.handle(node);
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }
        return true;
    }

    @Override
    public boolean visit(FieldAccess node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public boolean visit(ClassInstanceCreation node)
    {
        _printer.invocationBefore(node);
        try
        {
            _methodHandler.handle(node);
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public boolean visit(SuperMethodInvocation node)
    {
        _printer.invocationBefore(node);
        _methodHandler.handle(node);
        return true;
    }

    @Override
    public boolean visit(ConstructorInvocation node)
    {
        _printer.invocationBefore(node);
        _methodHandler.handle(node);
        return true;
    }

    @Override
    public boolean visit(SuperConstructorInvocation node)
    {
        _printer.invocationBefore(node);
        _methodHandler.handle(node);
        return true;
    }

    @Override
    public boolean visit(EnumConstantDeclaration node)
    {
        _printer.invocationBefore(node);
        try
        {
            _methodHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }
        return true;
    }

    @Override
    public boolean visit(FieldDeclaration node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public boolean visit(SingleVariableDeclaration node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public boolean visit(VariableDeclarationStatement node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public boolean visit(ParameterizedType node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public boolean visit(WildcardType node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public boolean visit(InstanceofExpression node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public boolean visit(CastExpression node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    private boolean visit(Annotation node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public boolean visit(MarkerAnnotation node)
    {
        return visit((Annotation) node);
    }

    @Override
    public boolean visit(NormalAnnotation node)
    {
        return visit((Annotation) node);
    }

    @Override
    public boolean visit(SingleMemberAnnotation node)
    {
        return visit((Annotation) node);
    }

    @Override
    public boolean visit(AnnotationTypeMemberDeclaration node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public boolean visit(QualifiedName node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }

    @Override
    public boolean visit(TypeLiteral node)
    {
        try
        {
            _typeHandler.handle(node);
        } catch (JavaModelException e)
        {
            _printer.error(e);
        }

        return true;
    }
}
