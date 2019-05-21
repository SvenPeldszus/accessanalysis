package de.uni_hamburg.informatik.swt.accessanalysis.runanalysisaction;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jdt.core.IJavaProject;

import de.uni_hamburg.informatik.swt.accessanalysis.Analysis;
import de.uni_hamburg.informatik.swt.accessanalysis.AnalysisException;
import de.uni_hamburg.informatik.swt.accessanalysis.AnalysisFactory;
import de.uni_hamburg.informatik.swt.accessanalysis.results.Result;
import de.uni_hamburg.informatik.swt.accessanalysis.resultsconsumer.AccessAnalysisResultsConsumer;
import de.uni_hamburg.informatik.swt.accessanalysis.runanalysisaction.internal.RunAnalysisActionActivator;


/**
 * Runs the analysis.
 */
class AccessAnalysisJob extends WorkspaceJob
{
    private List<IJavaProject> _projects;
    private Analysis _analysis;
    private Collection<AccessAnalysisResultsConsumer> _resultsConsumer;
    
    /**
     * Initializes an object of AnalysisJob. 
     * 
     * @param IJavaProject projects The projects to be analyzed
     * @param AccessAnalysisView view The view that shows the results
     */
    AccessAnalysisJob(List<IJavaProject> projects, Collection<AccessAnalysisResultsConsumer> resultsConsumer)
    {
        super("AccessAnalysis");
        
        _projects = projects;
        _analysis = AnalysisFactory.analyzer(_projects);
        _resultsConsumer = resultsConsumer;
        
        setUser(true);
        try {
            setRule(createModifyRule(_analysis.filesToBeLocked()));
        }
        catch (AnalysisException exc) {
        	RunAnalysisActionActivator.showErrorMessage("Error while listing files!", exc);
        }
    }
    
    @Override
    public IStatus runInWorkspace(IProgressMonitor monitor)
    {
        try
        {
        	clearResultsConsumer();
            monitor.beginTask("", 1000);
            _analysis.run(new SubProgressMonitor(monitor, 990, SubProgressMonitor.PREPEND_MAIN_LABEL_TO_SUBTASK));
            if (! monitor.isCanceled())
            {
                monitor.subTask("Show results");
                fillResultsConsumer(_analysis.getResults());
                monitor.worked(10);
            }
        }
        catch (AnalysisException exc)
        {
        	RunAnalysisActionActivator.showErrorMessage(null, exc);
            return Status.CANCEL_STATUS;
        }
        finally 
        {
            monitor.done();
        }
        return Status.OK_STATUS;
    }
    
    private void clearResultsConsumer() {
		for (AccessAnalysisResultsConsumer resultsConsumer : _resultsConsumer) {
			resultsConsumer.clear();
		}
	}
    
    private void fillResultsConsumer(List<Result> results) {
		for (AccessAnalysisResultsConsumer resultsConsumer : _resultsConsumer) {
			resultsConsumer.takeResults(results);
		}
	}
    
	private ISchedulingRule createModifyRule(IFile[] files) 
    {
        ISchedulingRule combinedRule = null;
        IResourceRuleFactory ruleFactory = ResourcesPlugin.getWorkspace().getRuleFactory();
        for (int i = 0; i < files.length; i++) 
        {
            ISchedulingRule rule = ruleFactory.modifyRule(files[i]);
            combinedRule = MultiRule.combine(rule, combinedRule);
        }
        return combinedRule;
    }
}