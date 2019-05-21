package de.uni_hamburg.informatik.swt.accessanalysis.analysis;

import org.eclipse.core.runtime.IProgressMonitor;

@SuppressWarnings("serial") 
class ProgressMonitorCanceledException extends Exception 
{ 
    static void checkMonitor(IProgressMonitor monitor) throws ProgressMonitorCanceledException
    {
        if (monitor.isCanceled())
        {
            throw new ProgressMonitorCanceledException();
        }
    }
}