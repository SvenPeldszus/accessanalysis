package de.uni_hamburg.informatik.swt.accessanalysis.analysis.printer;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import de.uni_hamburg.informatik.swt.accessanalysis.logger.Logger;
import de.uni_hamburg.informatik.swt.accessanalysis.logger.LoggerMessageType;


/**
 * Prints information to analyzed elements to the console.
 */
class RealElementPrinter implements ElementPrinter
{
    private static final String INDENT = "    ";
    
    private int _level = 0;
    
    private long _startTime = 0;
    
    @Override
    public void analysisBefore()
    {
        _startTime = System.currentTimeMillis();
        message("Run analysis");
    }
    
    @Override
    public void analysisAfter()
    {
        double duration = (System.currentTimeMillis() - _startTime) / 1000.0;
        message("Dauer: " + duration + " Sekunden");
        message("***************************");
    }
    
    @Override
    public void projectBefore(IJavaProject project)
    {
        print("<project name=\"" + project.getElementName() + "\">");
        _level++;
    }
    
    @Override
    public void projectAfter()
    {
        _level--;
        print("</project>");
    }
    
    @Override
    public void sourceFolderBefore(IPackageFragmentRoot packageFragmentRoot)
    {
        print("<sourceFolder name=\"" + packageFragmentRoot.getElementName() + "\">");
        _level++;
    }
    
    @Override
    public void sourceFolderAfter()
    {
        _level--;
        print("</project>");
    }
    
    @Override
    public void packageBefore(IPackageFragment packageFragment)
    {
        print("<package name=\"" + packageFragment.getElementName() + "\">");
        _level++;
    }
    
    @Override
    public void packageAfter()
    {
        _level--;
        print("</package>");
    }
    
    @Override
    public void compilationUnitBefore(CompilationUnit node)
    {
        print("<compilationUnit name=\"" + node.getTypeRoot().getElementName() + "\">");
        _level++;
    }
    
    @Override
    public void compilationUnitAfter()
    {
        _level--;
        print("</compilationUnit>");
    }
    
    @Override
    public void typeBefore(AbstractTypeDeclaration node)
    {
        String kind = "";
        switch (node.getNodeType())
        {
            case ASTNode.TYPE_DECLARATION :
            {
                if (((TypeDeclaration) node).isInterface())
                {
                    kind = "Interface";
                }
                else
                {
                    kind = "Class";
                }
                break;
            }
            case ASTNode.ENUM_DECLARATION :
            {
                kind = "Enum";
                break;
            }
            case ASTNode.ANNOTATION_TYPE_DECLARATION :
            {
                kind = "Annotation";
                break;
            }
        }
        
        print("<type kind=\"" + kind + "\" name=\"" + node.getName() + "\">");
        _level++;
    }
    
    @Override
    public void typeBefore(AnonymousClassDeclaration node)
    {
        print("<type kind=\"Anonymous\">");
        _level++;
    }
    
    @Override
    public void typeAfter()
    {
        _level--;
        print("</type>");
    }
    
    @Override
    public void methodBefore(MethodDeclaration node)
    {
        print("<method name=\"" + node.getName() + "\">");
        _level++;
    }
    
    @Override
    public void methodAfter()
    {
        _level--;
        print("</method>");
    }
    
    @Override
    public void invocationBefore(MethodInvocation node)
    {
        IMethodBinding binding = node.resolveMethodBinding();
        String expressionType = "";
        if (node.getExpression() != null)
        {
            expressionType = node.getExpression().resolveTypeBinding().getName();
        }
        
        print("<invocation method=\"" + binding.getName() + "\" expression_type=\"" + expressionType +"\" declaring_type=\"" + binding.getDeclaringClass().getName() + "\" />");
    }
    
    @Override
    public void invocationBefore(ClassInstanceCreation node)
    {
        IMethodBinding binding = node.resolveConstructorBinding();
        print("<invocation constructor=\"" + binding.getName() + "\" />");
    }
    
    @Override
    public void invocationBefore(SuperMethodInvocation node)
    {
        IMethodBinding binding = node.resolveMethodBinding();
        print("<invocation method=\"" + binding.getName() + "\" class=\"" + binding.getDeclaringClass().getName() + "\" />");
    }
    
    @Override
    public void invocationBefore(ConstructorInvocation node)
    {
        IMethodBinding binding = node.resolveConstructorBinding();
        print("<invocation constructor=\"" + binding.getName() + "\" />");
    }
    
    @Override
    public void invocationBefore(SuperConstructorInvocation node)
    {
        IMethodBinding binding = node.resolveConstructorBinding();
        print("<invocation constructor=\"" + binding.getName() + "\" />");
    }
    
    @Override
    public void invocationBefore(EnumConstantDeclaration node)
    {
        IMethodBinding binding = node.resolveConstructorBinding();
        print("<invocation enumconstructor=\"" + binding.getName() + "\" />");
    }
    
    @Override
    public void invocationAfter()
    {
    }

    @Override
    public void error(Throwable error)
    {
        print(error.getMessage(), LoggerMessageType.ERROR);
    }
    
    @Override
    public void message(String m)
    {
        Logger.println(m, LoggerMessageType.WARNING);
    }
    
    private void print(String text)
    {
        print(text, LoggerMessageType.INFO);
    }
    
    private void print(String text, LoggerMessageType type)
    {
        StringBuffer line = new StringBuffer();
        for (int i = 0; i < _level; i++)
        {
            line.append(INDENT);
        }
        line.append(text);
        
        Logger.println(line.toString(), type);
    }
}
