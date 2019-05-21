package de.uni_hamburg.informatik.swt.accessanalysis.analysis.printer;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;

/**
 * Prints information to analyzed elements to the console.
 */
public interface ElementPrinter
{
    /**
     * A printer that actually prints nothing except of error messages
     */
    public static final ElementPrinter QUIET = new NullElementPrinter();
    
    /**
     * A printer that prints only the duration of an analysis
     */
    public static final ElementPrinter TIME = new TimeOnlyPrinter();
    
    /**
     * A printer that really prints some information
     */
    public static final ElementPrinter VERBOSE = new RealElementPrinter();
    
    public void analysisBefore();
    
    public void analysisAfter();

    public void projectBefore(IJavaProject project);

    public void projectAfter();
    
    public void sourceFolderBefore(IPackageFragmentRoot packageFragment);
    
    public void sourceFolderAfter();

    public void packageBefore(IPackageFragment packageFragment);

    public void packageAfter();

    public void compilationUnitBefore(CompilationUnit node);

    public void compilationUnitAfter();

    public void typeBefore(AbstractTypeDeclaration node);

    public void typeBefore(AnonymousClassDeclaration node);

    public void typeAfter();

    public void methodBefore(MethodDeclaration node);

    public void methodAfter();

    public void invocationBefore(MethodInvocation node);

    public void invocationBefore(ClassInstanceCreation node);

    public void invocationBefore(SuperMethodInvocation node);

    public void invocationBefore(ConstructorInvocation node);

    public void invocationBefore(SuperConstructorInvocation node);

    public void invocationBefore(EnumConstantDeclaration node);

    public void invocationAfter();

    public void error(Throwable error);
    
    public void message(String message);

}