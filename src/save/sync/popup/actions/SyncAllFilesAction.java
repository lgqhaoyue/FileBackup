package save.sync.popup.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import save.sync.file.synctool.SyncTask;
import save.sync.file.synctool.SyncTool;

public class SyncAllFilesAction implements IObjectActionDelegate {

	private Shell shell;
	private ISelection currentSelection;
	
	/**
	 * Constructor for Action1.
	 */
	public SyncAllFilesAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
	
		if (this.currentSelection == null || this.currentSelection.isEmpty()) {
			return;
		}
		List<SyncTask> tasks = new ArrayList<SyncTask>();
		
		if(currentSelection instanceof IStructuredSelection)
		{
			Object obj = ((IStructuredSelection) currentSelection).getFirstElement();

            IResource resource = null;

            String path = null;

            // common resource file
            if (obj instanceof IProject || obj instanceof IFolder 
            		
            		|| obj instanceof IFile
            		) 
            {

                resource = (IResource) obj;

                path = resource.getLocation().toOSString();

            }
            
            if(path != null)
            {
                IProject  project  = resource.getProject();
                SyncTask task = new SyncTask(path,project);
                tasks.add(task);
            }
		}
		
		SyncTool.SyncFiles(tasks);
		
	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		
		this.currentSelection = selection;
	}

}
