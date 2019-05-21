package de.uni_hamburg.informatik.swt.accessanalysis.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import de.uni_hamburg.informatik.swt.accessanalysis.analysis.determinators.ExpressionTypeDeterminator;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.printer.ElementPrinter;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.MethodAccessConstraintChecker;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.TypeAccessConstraintChecker;
import de.uni_hamburg.informatik.swt.accessanalysis.results.MethodResult;
import de.uni_hamburg.informatik.swt.accessanalysis.results.PackageResult;
import de.uni_hamburg.informatik.swt.accessanalysis.results.ProjectResult;
import de.uni_hamburg.informatik.swt.accessanalysis.results.Result;
import de.uni_hamburg.informatik.swt.accessanalysis.results.SourceFolderResult;
import de.uni_hamburg.informatik.swt.accessanalysis.results.TypeResult;

class AccessAnalysisResultCollector {

    private final List<IJavaProject> _projects;
    private final Set<TypeAccessConstraintChecker> _typeConstraints;
    private final Set<MethodAccessConstraintChecker> _methodConstraints;
    private final IProgressMonitor _monitor;
    private final ElementPrinter _printer;
    
    private List<ProjectResult> _projectResults;
    private List<Result> _mergedResults;
    private ExpressionTypeDeterminator _expressionTypeDeterminator;
    private TypeHandler _typeHandler;
    private MethodHandler _methodHandler;
    private ProjectResult _currentProjectResult;
    private SourceFolderResult _currentSourceFolderResult;
    private PackageResult _currentPackageResult;

    AccessAnalysisResultCollector(List<IJavaProject> projects, Set<TypeAccessConstraintChecker> typeConstraints,
            Set<MethodAccessConstraintChecker> methodConstraints, IProgressMonitor monitor, ElementPrinter printer)
    {
        _projects = projects;
        _typeConstraints = typeConstraints;
        _methodConstraints = methodConstraints;
        _monitor = monitor;
        _printer = printer;
    }
    
    void collect() throws JavaModelException, ProgressMonitorCanceledException
    {
        _projectResults = new ArrayList<ProjectResult>();
        _mergedResults = null;
        _expressionTypeDeterminator = new ExpressionTypeDeterminator();
        _typeHandler = new TypeHandler(_expressionTypeDeterminator, _typeConstraints);
        _methodHandler = new MethodHandler(_expressionTypeDeterminator, _methodConstraints);
        _currentProjectResult = null;
        _currentSourceFolderResult = null;
        _currentPackageResult = null;
        
        for (IJavaProject project : _projects)
        {
            analyzeProject(project);
        }
    }
    
    private void analyzeProject(IJavaProject project) throws JavaModelException, ProgressMonitorCanceledException
    {
        _printer.projectBefore(project);
        _currentProjectResult = new ProjectResult(project);
        _projectResults.add(_currentProjectResult);
        for (IPackageFragmentRoot packageFragmentRoot : project.getPackageFragmentRoots())
        {
            analyzeSourceFolder(packageFragmentRoot);
        }
        _printer.projectAfter();
        _currentProjectResult = null;
    }
    
    private void analyzeSourceFolder(IPackageFragmentRoot packageFragmentRoot) throws JavaModelException, ProgressMonitorCanceledException
    {
        if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE)
        {
            _printer.sourceFolderBefore(packageFragmentRoot);
            _currentSourceFolderResult = new SourceFolderResult(packageFragmentRoot, _currentProjectResult);
            _currentProjectResult.addSourceFolder(_currentSourceFolderResult);
            for (IJavaElement child : packageFragmentRoot.getChildren())
            {
                if (child instanceof IPackageFragment)
                {
                    analyzePackage((IPackageFragment) child);
                }
            }
            _printer.sourceFolderAfter();
            _currentSourceFolderResult = null;
        }
    }

    private void analyzePackage(IPackageFragment packageFragment) throws JavaModelException, ProgressMonitorCanceledException
    {
        if (packageFragment.getCompilationUnits().length > 0)
        {
            _printer.packageBefore(packageFragment);
            _currentPackageResult = new PackageResult(packageFragment, _currentSourceFolderResult);
            _currentSourceFolderResult.addPackage(_currentPackageResult);
            for (ICompilationUnit unit : packageFragment.getCompilationUnits())
            {
                analyzeCompilationUnit(unit);
            }
            _printer.packageAfter();
            _currentPackageResult = null;
        }
    }

    private void analyzeCompilationUnit(ICompilationUnit unit) throws ProgressMonitorCanceledException
    {
        ProgressMonitorCanceledException.checkMonitor(_monitor);
        _monitor.subTask("Read " + unit.getPath().toString());
        CompilationUnit ast = parse(unit);
        AccessAnalysisASTVisitor visitor = new AccessAnalysisASTVisitor(_currentPackageResult, _typeHandler, _methodHandler, _expressionTypeDeterminator, _printer);
        ast.accept(visitor);
        _monitor.worked(1);
    }
    
    /**
     * Creates the AST for a compilation unit.
     * 
     * @param ICompilationUnit
     *            unit The compilation unit
     * @return CompilationUnit The root element of the AST
     */
    private CompilationUnit parse(ICompilationUnit unit)
    {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(unit);
        parser.setResolveBindings(true);
        return (CompilationUnit) parser.createAST(null);
    }
    
    /**
     * Returns prepares and returns the results of last collect() call.
     * 
     * @return List<Result> All top level result elements (ProjectResults)
     *      or null if collect() haven't been called yet
     * @throws ProgressMonitorCanceledException if ProgressMonitor has canceled state.
     */
    List<Result> getResults(IProgressMonitor monitor) throws ProgressMonitorCanceledException
    {
        if (_projectResults != null && _mergedResults == null)
        {
            monitor.subTask("Merge results");
            mergeResults(monitor);
            monitor.worked(1);
        }
        
        return _mergedResults;
    }
    
    /**
     * Extracts the minimal access of types and methods from the maps 
     * and sets the results.
     * 
     * Starts a new progress monitor subtask and increments it by 1 when successful.
     * 
     * @param Collection<Result> projectResults The results to be set
     * @param MethodMap methodMap Map of minimal access of all methods
     * @param TypeMap typeMap Map of minimal access of all types  
     * 
     * @throws ProgressMonitorCanceledException if progress monitor get in canceled state
     */
    private void mergeResults(IProgressMonitor monitor) throws ProgressMonitorCanceledException
    {
        MethodMap methodMap = _methodHandler.getMethodMap();
        TypeMap typeMap = _typeHandler.getTypeMap();
        
        for (Result projectResult : _projectResults)
        {
            for (Result sourceFolderResult : projectResult.getChildren()) //Source Folder
            {
                for (Result packageResult : sourceFolderResult.getChildren()) //Packages
                {
                    for (Result typeResult : packageResult.getChildren()) //Types
                    {
                        ProgressMonitorCanceledException.checkMonitor(monitor);
                        
                        for (Result methodResult : typeResult.getChildren()) //Methods
                        {
                            ((MethodResult) methodResult).setMinimalAccess(methodMap.getAccess(((MethodResult) methodResult).getJavaElement()));
                        }
                        ((TypeResult) typeResult).setMinimalAccess(typeMap.getAccess(((TypeResult) typeResult).getJavaElement()));
                    }
                }
            }
        }
        
        _mergedResults = new ArrayList<Result>(_projectResults);
    }
}
