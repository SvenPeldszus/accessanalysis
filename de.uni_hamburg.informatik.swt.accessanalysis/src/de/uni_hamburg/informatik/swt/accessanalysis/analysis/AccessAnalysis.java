package de.uni_hamburg.informatik.swt.accessanalysis.analysis;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import de.uni_hamburg.informatik.swt.accessanalysis.Analysis;
import de.uni_hamburg.informatik.swt.accessanalysis.AnalysisException;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.constraints.ConstraintsCollector;
import de.uni_hamburg.informatik.swt.accessanalysis.analysis.printer.ElementPrinter;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.MethodAccessConstraintChecker;
import de.uni_hamburg.informatik.swt.accessanalysis.constraints.TypeAccessConstraintChecker;
import de.uni_hamburg.informatik.swt.accessanalysis.results.Result;


/**
 * Extracts all packages, types and methods from given projects,
 * determines the minimal access for all types and methods and 
 * generates a result tree.
 */
public class AccessAnalysis implements Analysis
{
	private final List<IJavaProject> _projects;
    private final ElementPrinter _printer;

    private List<Result> _projectResults = null;
    private Set<ICompilationUnit> _compilationUnits = null;
    
    /**
     * Initializes an object of AccessAnalysis.
     * 
     * @param List<IJavaProject> projects The projects to be analyzed
     * @param boolean verbose Should some information be written to console during analysis?
     */
    public AccessAnalysis(List<IJavaProject> projects, boolean verbose)
    {
        _projects = projects;
        
        if (verbose)
        {
            _printer = ElementPrinter.VERBOSE;
        }
        else
        {
            _printer = ElementPrinter.QUIET;
        }
    }
    
    @Override
    public IFile[] filesToBeLocked() throws AnalysisException
    {
        Set<IFile> files = new HashSet<IFile>();
        
        try
        {
            for (ICompilationUnit compilationUnit : getCompilationUnits())
            {
                files.add((IFile) compilationUnit.getCorrespondingResource());
            }
        }
        catch (JavaModelException e)
        {
            throw new AnalysisException(e);
        }
        
        return files.toArray(new IFile[files.size()]);
    }

    @Override
    public void run(IProgressMonitor monitor) throws AnalysisException
    {
        try
        {
            createResults(monitor);
        }
        catch (ProgressMonitorCanceledException cancelExc)
        {
            _printer.message("Canceled by user.");
        }
        catch (CoreException coreExc)
        {
            _printer.error(coreExc);
            throw new AnalysisException(coreExc);
        }
        finally
        {
            monitor.done();
        }
    }
    
    /**
     * Returns all compilation units of all projects
     * 
     * @return Set<ICompilationUnit> All compilation units
     * 
     * @throws JavaModelException if something goes wrong
     */
    private Set<ICompilationUnit> getCompilationUnits() throws JavaModelException
    {
        if (_compilationUnits == null)
        {
            _compilationUnits = new HashSet<ICompilationUnit>();
            
            for (IJavaProject project : _projects)
            {
                for (IPackageFragment packageFragment : project.getPackageFragments())
                {
                    if (packageFragment.getKind() == IPackageFragmentRoot.K_SOURCE)
                    {
                        _compilationUnits.addAll(Arrays.asList(packageFragment.getCompilationUnits()));
                    }
                }
            }
        }
        
        return _compilationUnits;
    }
    
    /**
     * Runs the actual analysis for all projects, 
     * prints some basic information, 
     * forwards some information to the progress monitor and
     * puts the results to the fields when finished. 
     * 
     * Starts the progress monitor and increments it to the end when successful, but doesn't stop it.
     * 
     * @param IProgressMonitor monitor The current progress monitor
     * 
     * @throws AnalysisException if a project can't be analyzed 
     * @throws CoreException if something goes wrong
     * @throws ProgressMonitorCanceledException if the progress monitor is in canceled state
     */
    private void createResults(IProgressMonitor monitor) throws AnalysisException, CoreException, ProgressMonitorCanceledException
    {
        monitor.beginTask("", getCompilationUnits().size() + 2);
        
        checkAllReadable(_projects, monitor);
        
        _printer.analysisBefore();
        Set<TypeAccessConstraintChecker> typeConstraints = ConstraintsCollector.getTypeConstraints();
        Set<MethodAccessConstraintChecker> methodConstraints = ConstraintsCollector.getMethodConstraints();
        AccessAnalysisResultCollector resultCollector = new AccessAnalysisResultCollector(_projects, typeConstraints, methodConstraints, monitor, _printer);
        resultCollector.collect();
        _projectResults = resultCollector.getResults(monitor);
        _printer.analysisAfter();
    }
    
	/**
     * Checks whether some given projects are readable for the analysis. 
     * If this is not the case an exception is thrown.
     * 
     * Increments the progress monitor by 1 when successful.
     * 
     * @param Collection<IJavaProject> projects The projects to be checked
     * @param IProgressMonitor monitor The current progress monitor
     * 
     * @throws CoreException if something goes wrong
     * @throws AnalysisException AnalysisException if a project can't be analyzed
     */
    private void checkAllReadable(Collection<IJavaProject> projects, IProgressMonitor monitor) throws CoreException, AnalysisException, ProgressMonitorCanceledException
    {
        monitor.subTask("Check projects");
        for (IJavaProject project : projects)
        {
            ProgressMonitorCanceledException.checkMonitor(monitor);
            checkReadable(project);
        }
        monitor.worked(1);
    }
    
    /**
     * Checks whether a single given project is readable for the analysis. 
     * If this is not the case an exception is thrown.
     * 
     * @param IJavaProject project The project to be checked
     * 
     * @throws CoreException if something goes wrong
     * @throws AnalysisException AnalysisException if the project can't be analyzed
     */
    private void checkReadable(IJavaProject project) throws CoreException, AnalysisException
    {
        if (hasError(project, IJavaModelMarker.BUILDPATH_PROBLEM_MARKER) || 
            hasError(project, IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER))
        {
            throw new AnalysisException("There are some errors in project \"" + project.getElementName() + "\". Analysis impossible!");
        }           
        if (project.hasUnsavedChanges())
        {
            throw new AnalysisException("There are unsaved changes in project \"" + project.getElementName() + "\". Analysis impossible!");
        }
    }
    
    /**
     * Checks whether a project contains an error of a specific type
     * 
     * @param IJavaProject project The project to be checked
     * @param String errorType The type of error
     * 
     * @throws CoreException if something goes wrong
     */
    private boolean hasError(IJavaProject project, String errorType) throws CoreException
    {
        return IMarker.SEVERITY_ERROR == project.getUnderlyingResource().findMaxProblemSeverity(errorType, true, IResource.DEPTH_INFINITE);
    }  

    @Override
    public List<Result> getResults()
    {
        return _projectResults;
    }

    @Override
    public Result getResult(String name)
    {
        if (_projectResults != null && ! _projectResults.isEmpty())
        {
            for (Result result : _projectResults)
            {
                if (result.getFormatter().name().equals(name))
                {
                    return result;
                }
            }
        }
        
        return null;
    }
}
