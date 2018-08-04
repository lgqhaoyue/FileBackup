package save.sync.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import save.sync.file.synctool.SyncTask;
import save.sync.file.synctool.SyncTool;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SaveSyncAllFilesHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SaveSyncAllFilesHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);

		IEditorPart[] parts = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getDirtyEditors();
		List<SyncTask> tasks = new ArrayList<SyncTask>();
		
		for(IEditorPart part:parts)
		{
			part.getSite().getPage().saveEditor(part, false);
			IEditorInput editorInput = part.getEditorInput();
			if(editorInput instanceof FileEditorInput)
			{
				FileEditorInput fileEditor = (FileEditorInput)editorInput;
				IFile file  = fileEditor.getFile();
				IProject project = file.getProject();
				
				SyncTask task = new SyncTask(file.getLocation().toString(),project);
				tasks.add(task);
			}
			
		}
		SyncTool.SyncFiles(tasks);
		
		//force
		//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().saveAllEditors(false);
		//editor.get().getSite().getPage().saveEditor(editor.get(), false);
		
		return null;
	}
}
