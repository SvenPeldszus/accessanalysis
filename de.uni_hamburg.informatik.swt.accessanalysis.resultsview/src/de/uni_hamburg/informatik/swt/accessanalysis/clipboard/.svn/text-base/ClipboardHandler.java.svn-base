package de.uni_hamburg.informatik.swt.accessanalysis.clipboard;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.EditorInputTransfer;

import de.uni_hamburg.informatik.swt.accessanalysis.extensions.RcpExtension;
import de.uni_hamburg.informatik.swt.accessanalysis.results.Result;


/**
 * Copies selected results from the AccessAnalysisView to the clipboard.
 */
@RcpExtension
public class ClipboardHandler extends AbstractHandler
{
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Clipboard clipboard = new Clipboard(HandlerUtil.getActiveShell(event).getDisplay());
        try
        {
            copyResults(event, clipboard);
            return null;
        }
        finally
        {
            clipboard.dispose();
        }
    }

    private void copyResults(ExecutionEvent event, Clipboard clipboard) throws ExecutionException
    {
        ISelection selection = HandlerUtil.getCurrentSelection(event);
        if (selection instanceof IStructuredSelection)
        {
            Object[] objects = ((IStructuredSelection) selection).toArray();
            if (objects.length > 0)
            {
                try
                {
                    Object[] contentObjects = new Object[] { asResource(objects), asText(objects) };
                    Transfer[] transfers = new Transfer[] { EditorInputTransfer.getInstance(), TextTransfer.getInstance() };
                    clipboard.setContents(contentObjects, transfers);
                }
                catch (SWTError error) { }
            }
        }
        
    }

    private IResource[] asResource(Object[] objects)
    {
        Collection<IResource> resources = new HashSet<IResource>(objects.length);
        
        for (Object object : objects)
        {
            if (object instanceof Result)
            {
                Result result = (Result) object;
                if (result.isElementEditable())
                {
                    try
                    {
                        resources.add(result.getJavaElement().getUnderlyingResource());
                    }
                    catch (JavaModelException e)
                    {
                    }
                }
            }
        }
        return resources.toArray(new IResource[resources.size()]);
    }

    private String asText(Object[] objects)
    {
        StringBuffer buf = new StringBuffer();
        for (Object object : objects)
        {
            buf.append(object.toString());
            buf.append(System.getProperty("line.separator"));
        }
        return buf.toString();
    }
}