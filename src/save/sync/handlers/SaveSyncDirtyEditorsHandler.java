package save.sync.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;

import save.sync.file.synctool.SyncTask;
import save.sync.file.synctool.SyncTool;
import save.sync.properties.SyncFilesPropertyPage;

import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SaveSyncDirtyEditorsHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SaveSyncDirtyEditorsHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		IWorkbenchPage  page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart[] parts = page.getDirtyEditors();
		int count = 0;
		List<SyncTask> tasks = new ArrayList<SyncTask>();
		for(IEditorPart part:parts)
		{
            part.getSite().getPage().saveEditor(part, false);//save 
            
		    IEditorInput editorInput = part.getEditorInput();
			Object o = editorInput.getAdapter(IFile.class);
			if(o instanceof IFile)
			{
			    IFile file  = (IFile)o;
                IProject project = file.getProject();
                SyncTask task = new SyncTask(file.getLocation().toString(),project);
                tasks.add(task);
                count++;
			}	
		}
		SyncTool.SyncFiles(tasks);

		System.out.println("save and sync "+count+" files");
				
		//force
		//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(false);
		//editor.get().getSite().getPage().saveEditor(editor.get(), false);
		
		return null;
	}
}
