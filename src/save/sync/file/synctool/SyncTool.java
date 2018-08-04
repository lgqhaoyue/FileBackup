package save.sync.file.synctool;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import save.sync.properties.SyncFilesPropertyPage;

public class SyncTool {
    
    private  static class SyncJob extends  Job
    {
        List<SyncTask> tasks;
        public SyncJob(String name)
        {
            super(name);
        }
        
        public SyncJob(String name,List<SyncTask> tasks)
        {
            super(name);
            this.tasks = tasks;
        }

        @Override
        protected IStatus run(IProgressMonitor monitor)
        {
            monitor.beginTask("Start SyncFiles", tasks.size());  
            
            Iterator<SyncTask> iter = tasks.iterator();
            while(iter.hasNext())
            {
                SyncTask task = iter.next();
                SyncTool.Sync(task.srcFile,task.project);
                monitor.worked(1);  
            }
              
            monitor.done();  
            return Status.OK_STATUS;  
         }  
        
    }
    
    public static void SyncFiles(List<SyncTask> tasks)
    {
        Job job = new SyncJob("SyncFiles",tasks);  
        job.schedule(); 
    }
    
	
	public static void Sync(String srcFile, IProject project) {

		String destPath = null;
		try {
			destPath = project.getPersistentProperty(SyncFilesPropertyPage.destPathQualified);
			
		} catch (CoreException e) {

		}
		if(destPath == null || destPath.isEmpty())
		{
			return;
		}
		
		String currentProjectPath = project.getLocation().toString().replace('\\', '/');
		
		CopyFiles(srcFile,currentProjectPath,destPath);

	}
	
	
	public static void CopyFiles(String srcPath,String syncSrcRootPath,String syncDestRootPath) {
        if ( null == srcPath || srcPath.isEmpty()) {
            return;
        }
       
        File file = new File(srcPath);
        if (file.isFile()) {
        	fileChannelCopy(srcPath,syncSrcRootPath,syncDestRootPath);
        	return;
        }
        
        mkDestDir(srcPath,syncSrcRootPath,syncDestRootPath);
        
        File[] fileList = file.listFiles();
        if (fileList.length == 0) {
            return;
        }

        LinkedList<File> dirList = new LinkedList<File>();
    
        for (File curFile : fileList) {
            if (curFile.isDirectory()) {
                dirList.add(curFile);
                mkDestDir(curFile.getAbsolutePath(),syncSrcRootPath,syncDestRootPath);
            }
            else
            {
            	fileChannelCopy(curFile.getAbsolutePath(),syncSrcRootPath,syncDestRootPath);
            }
        }

        while (!dirList.isEmpty()) {
            File removeDir = dirList.removeFirst();//移除首位的目录         
            File[] removeDirFileList = removeDir.listFiles();
            if (removeDirFileList.length ==  0) {
                continue;
            }
            //将找出的所有的文件和目录加入到总集合中
            for (File curFile : removeDirFileList) {
                if (curFile.isDirectory()) {
                    dirList.add(curFile);
                    mkDestDir(curFile.getAbsolutePath(),syncSrcRootPath,syncDestRootPath);
                }
                else
                {
                	fileChannelCopy(curFile.getAbsolutePath(),syncSrcRootPath,syncDestRootPath);
                }
            }
        }
        return ;
    }
	
	public static void mkDestDir(String srcPath,String syncSrcRootPath, String syncDestRootPath)
	{
		if(srcPath == null  || srcPath.isEmpty())
		{
			return;
		}
		srcPath  = srcPath.replace('\\', '/');
		String  destDir = syncDestRootPath + "/" +  srcPath.substring(syncSrcRootPath.length());
		File dir =  new File(destDir);
		if(!dir.exists())
		{
			dir.mkdirs();
		}
		
	}
	
	public static void fileChannelCopy(String src,String syncSrcRootPath, String syncDestRootPath) {
		FileInputStream fi = null;
		FileOutputStream fo = null;
		FileChannel in = null;
		FileChannel out = null;
		File  srcFile =  null;
		File  destFile =  null;
		src  = src.replace('\\', '/');
		String  dest = syncDestRootPath + "/" +  src.substring(syncSrcRootPath.length());
		File file = new File(dest);
		if(!file.exists())
		{
			File  parentDir = file.getParentFile();
			if(!parentDir.exists())
			{
				parentDir.mkdirs();
			}
			
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			
			srcFile =  new File(src);
			destFile = new File(dest);
			if(srcFile.lastModified() == destFile.lastModified())
			{
				return;
			}
			fi = new FileInputStream(src);
			fo = new FileOutputStream(destFile);
			in = fi.getChannel();// 得到对应的文件通道
			out = fo.getChannel();// 得到对应的文件通道
			in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			try {
				if(fi !=  null)
					fi.close();
				
				if(in != null)
					in.close();
				
				if(fo  !=   null)
					fo.close();
				
				if(out  != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		long s = srcFile.lastModified();
		boolean falg = destFile.setLastModified(srcFile.lastModified());
		System.out.println("falg:"+falg);
	}
}
