package de.uni_hamburg.informatik.swt.accessanalysis.logger;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * Creates a console and put out text to it.
 * 
 * Code based on the example at
 * http://www.eclipsezone.com/eclipse/forums/t52910.html.
 */
class ConsoleDisplayMgr
{
    private String _Title = null;
    private MessageConsole _MessageConsole = null;

    /**
     * Initializes an object of ConsoleDisplayMgr.
     * 
     * @param String
     *            messageTitle The name of the console
     */
    ConsoleDisplayMgr(String messageTitle)
    {
        _Title = messageTitle;
    }

    /**
     * Prints one line of the default message type.
     * 
     * @param String
     *            msg The text to be printed
     * @param LoggerMessageType
     *            type The type of message to be printed
     */
    void println(String msg, LoggerMessageType msgType)
    {
        if (msg == null)
            return;

        /*
         * if console-view in Java-perspective is not active, then show it and
         * then display the message in the console attached to it
         */
        if (!displayConsoleView())
        {
            /*
             * If an exception occurs while displaying in the console, then just
             * diplay atleast the same in a message-box
             */
            MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", msg);
            return;
        }

        /* display message on console */
        getNewMessageConsoleStream(msgType).println(msg);
    }

    private boolean displayConsoleView()
    {
        try
        {
            IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
            if (activeWorkbenchWindow != null)
            {
                IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
                if (activePage != null)
                    activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW, null, IWorkbenchPage.VIEW_VISIBLE);
            }

        }
        catch (PartInitException partEx)
        {
            return false;
        }

        return true;
    }

    /**
     * Returns a MessageConsoleStream
     * 
     * @param msgType
     *            The type of message
     * @return MessageConsoleStream
     */
    MessageConsoleStream getNewMessageConsoleStream(LoggerMessageType msgType)
    {
        MessageConsoleStream msgConsoleStream = getMessageConsole().newMessageStream();
        setColor(msgConsoleStream, msgType);
        return msgConsoleStream;
    }

    private void setColor(final MessageConsoleStream msgConsoleStream, final LoggerMessageType msgType)
    {
        Display.getDefault().syncExec(new Runnable()
        {
            @Override
            public void run()
            {
                msgConsoleStream.setColor(Display.getCurrent().getSystemColor(msgType.getColor()));
            }
        });
    }

    private MessageConsole getMessageConsole()
    {
        if (_MessageConsole == null)
        {
            makeMessageConsoleStream(_Title);
        }

        return _MessageConsole;
    }

    private void makeMessageConsoleStream(String title)
    {
        _MessageConsole = new MessageConsole(title, null);
        ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { _MessageConsole });
    }
}
