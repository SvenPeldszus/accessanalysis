package de.uni_hamburg.informatik.swt.accessanalysis;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import de.uni_hamburg.informatik.swt.accessanalysis.results.Result;


/**
 * Analyses some files
 */
public interface Analysis
{
    /**
     * Returns the files that will be read for the analysis.
     * 
     * @return IFile[] The files
     */
    public IFile[] filesToBeLocked() throws AnalysisException;
    
    /**
     * Runs the analysis.
     * 
     * @param IProgressMonitor monitor The progress monitor of choice
     *
     * @throws AnalysisException if an error occurs during the analysis
     */
    public void run(IProgressMonitor monitor) throws AnalysisException;
    
    /**
     * Returns the results of the last analysis run.
     * 
     * @return List<Result> The results of all projects or null if the analysis has not run yet
     */
    public List<Result> getResults();
    
    /**
     * Returns the results of the last analysis run for a given project.
     * 
     * @param String name The name of the project 
     * @return Result The result of the given project or null if there is no such result
     */
    public Result getResult(String name);
}
