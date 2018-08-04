package save.sync.popup.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import save.sync.file.synctool.SyncTask;
import save.sync.file.synctool.SyncTool;

public class SaveSyncDirtyEditorsAction implements IObjectActionDelegate {

	
	/**
	 * Constructor for Action1.
	 */
	public SaveSyncDirtyEditorsAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
	
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
			}
		}
		SyncTool.SyncFiles(tasks);
		System.out.println("save and sync "+count+" files");
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
