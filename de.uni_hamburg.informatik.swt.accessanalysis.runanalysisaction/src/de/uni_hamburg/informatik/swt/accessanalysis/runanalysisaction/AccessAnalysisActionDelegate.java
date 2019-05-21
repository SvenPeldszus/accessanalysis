package de.uni_hamburg.informatik.swt.accessanalysis.runanalysisaction;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.actions.ActionDelegate;

import de.uni_hamburg.informatik.swt.accessanalysis.extensions.RcpExtension;
import de.uni_hamburg.informatik.swt.accessanalysis.resultsconsumer.AccessAnalysisResultsConsumer;
import de.uni_hamburg.informatik.swt.accessanalysis.runanalysisaction.internal.RunAnalysisActionActivator;


/**
 * Starts the analysis job.
 */
@RcpExtension
public class AccessAnalysisActionDelegate extends ActionDelegate implements IViewActionDelegate
{
    private ISelection _selection;
    
    @Override
    public void init(IViewPart view)
    {
    }

    @Override
    public void selectionChanged(IAction ignored, ISelection selection)
    {
        _selection = selection;
    }
    
    @Override
    public void run(IAction ignored)
    {
        if (_selection != null)
        {
            scheduleJob(getProjects());
        }
    }
    
    private List<IJavaProject> getProjects()
    {
        LinkedList<IJavaProject> projectList = new LinkedList<IJavaProject>();
        
        if (_selection instanceof IStructuredSelection)
        {
            Set<IJavaProject> projectSet = new HashSet<IJavaProject>();
            for (Object element : ((IStructuredSelection) _selection).toArray())
            {
                if (element instanceof IJavaElement)
                {
                    projectSet.add(((IJavaElement) element).getJavaProject());
                }
            }
            projectList.addAll(projectSet);
        }
        
        return projectList;
    }
    
    private void scheduleJob(List<IJavaProject> projects)
    {
        if (! projects.isEmpty())
        {
        	try {
    			Collection<AccessAnalysisResultsConsumer> resultsConsumer = RunAnalysisActionActivator.getResultsConsumer();
    			Job job = new AccessAnalysisJob(projects, resultsConsumer);
    			job.schedule();
    		} 
            catch (CoreException e) {
    			RunAnalysisActionActivator.showErrorMessage(null, e);
    			e.printStackTrace();
    		}
        }
        else
        {
            MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "AccessAnalysis", "No Project selected!");
        }
    }
}
