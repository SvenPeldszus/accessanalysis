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

import de.uni_hamburg.informatik.swt.accessanalysis.logger.Logger;
import de.uni_hamburg.informatik.swt.accessanalysis.logger.LoggerMessageType;


/**
 * Prints no information to analyzed elements to the console
 * except error messages.
 */
final class NullElementPrinter implements ElementPrinter
{
    @Override
    public void error(Throwable error)
    {
        Logger.println(error.getMessage(), LoggerMessageType.ERROR);
    }
 
    @Override
    public void message(String m)
    {
        Logger.println(m, LoggerMessageType.WARNING);
    }
    
    @Override
    public void analysisBefore()
    {
    }
    
    @Override
    public void analysisAfter()
    {
    }
    
    @Override
    public void compilationUnitAfter()
    {
    }

    @Override
    public void compilationUnitBefore(CompilationUnit node)
    {
    }

    @Override
    public void invocationAfter()
    {
    }

    @Override
    public void invocationBefore(MethodInvocation node)
    {
    }

    @Override
    public void invocationBefore(ClassInstanceCreation node)
    {
    }

    @Override
    public void invocationBefore(SuperMethodInvocation node)
    {
    }

    @Override
    public void invocationBefore(ConstructorInvocation node)
    {
    }

    @Override
    public void invocationBefore(SuperConstructorInvocation node)
    {
    }

    @Override
    public void invocationBefore(EnumConstantDeclaration node)
    {
    }

    @Override
    public void methodAfter()
    {
    }

    @Override
    public void methodBefore(MethodDeclaration node)
    {
    }

    @Override
    public void packageAfter()
    {
    }

    @Override
    public void packageBefore(IPackageFragment packageFragment)
    {
    }

    @Override
    public void sourceFolderAfter()
    {
    }

    @Override
    public void sourceFolderBefore(IPackageFragmentRoot packageFragment)
    {
    }

    @Override
    public void projectAfter()
    {
    }

    @Override
    public void projectBefore(IJavaProject project)
    {
    }

    @Override
    public void typeAfter()
    {
    }

    @Override
    public void typeBefore(AbstractTypeDeclaration node)
    {
    }

    @Override
    public void typeBefore(AnonymousClassDeclaration node)
    {
    }
}
