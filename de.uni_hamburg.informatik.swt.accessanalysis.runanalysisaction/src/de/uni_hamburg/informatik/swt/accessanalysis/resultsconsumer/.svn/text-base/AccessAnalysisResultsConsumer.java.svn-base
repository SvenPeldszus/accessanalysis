package de.uni_hamburg.informatik.swt.accessanalysis.resultsconsumer;

import java.util.List;

import org.eclipse.ui.IWorkbenchPage;

import de.uni_hamburg.informatik.swt.accessanalysis.results.Result;

public interface AccessAnalysisResultsConsumer {
	
	public static final String EXTENSION_POINT_ID = "de.uni_hamburg.informatik.swt.accessanalysis.results.consumer";
	
	public void setWorkbenchPage(IWorkbenchPage page);
	
	public void clear();
	
	public void takeResults(List<Result> results);
}
