package de.uni_hamburg.informatik.swt.accessanalysis.resultsview;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

import de.uni_hamburg.informatik.swt.accessanalysis.extensions.RcpExtension;
import de.uni_hamburg.informatik.swt.accessanalysis.results.Result;
import de.uni_hamburg.informatik.swt.accessanalysis.resultsconsumer.AccessAnalysisResultsConsumer;

@RcpExtension
public class AccessAnalysisViewFiller implements AccessAnalysisResultsConsumer {

	private AccessAnalysisView _view;

	@Override
	public void setWorkbenchPage(IWorkbenchPage page) {
		try
    	{
    		_view = (AccessAnalysisView) page.showView(AccessAnalysisView.ID);
    		page.activate(_view);
    	}
    	catch (PartInitException e)
    	{
    		MessageDialog.openError(Display.getCurrent().getActiveShell(), "Error", e.getMessage());
    		e.printStackTrace();
    	}
	}
	
	@Override
	public void clear() {
		if (_view != null) {
			_view.clear();
		}
	}

	@Override
	public void takeResults(List<Result> results) {
		if (_view != null) {
			_view.showResults(results);
		}
	}
}
