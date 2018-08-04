package save.sync.file.synctool;

import org.eclipse.core.resources.IProject;

public class SyncTask
{
    protected String srcFile;
    protected IProject project;
    
    public SyncTask(String srcFile,IProject project)
    {
        this.srcFile = srcFile;
        this.project = project;
    }
    
}
