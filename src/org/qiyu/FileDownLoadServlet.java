package org.qiyu;

import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



/**
 * Servlet implementation class MyProcessorSecond
 */
public class FileDownLoadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FileDownLoadServlet() {
        super();
        // TODO Auto-generated constructor stub
        
    }

    public void init()
    {
    	boolean loadFile = false;
    	String latestResVersion = getLatestResVersion();
    	if(ApplicationConfig.CurVersion != null)
    	{    		
    		if(ApplicationConfig.CurVersion.compareTo(latestResVersion) != 0)
    		{
    			loadFile = true;    			
    		}
    	}
    	else
    	{
    		loadFile = true;
    	}
    	if(loadFile)
    	{
    		checkReserveResNum();
    		ApplicationConfig.CurVersion = latestResVersion;    		
    		loadVersionFile(latestResVersion);			
			//loadResFiles(Data.versionFilesArray.get(Data.versionFilesArray.size() - 1));
    	}
    }
    
    private void sendFileBytesToClient(HttpServletResponse response, byte[] fileContent, String fileName) throws Exception
    {
    	//response.setContentType("application/x-msdownload");
    	if(fileContent == null)
    	{
    		response.sendError(HttpServletResponse.SC_NO_CONTENT, "File Not Found, Please Check Url Path!");
    		return;
    	}
    	response.setContentType("text/html;charset=UTF-8");
    	  response.setContentType("application/x-msdownload");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		  response.setHeader("Content-Length", fileContent.length + "");
		javax.servlet.ServletOutputStream ou = response.getOutputStream();		
		ou.write(fileContent);		
		ou.flush();
		ou.close();
		response.setStatus(HttpServletResponse.SC_OK);
		response.flushBuffer();
    }
    
    private void loadFile(String directory, String path)
    {
    	if(!Data.fileList.containsKey(directory))
    	{
    		Data.fileList.put(directory, new HashMap<String, byte[]>());
    	}
    	HashMap<String, byte[]> files = Data.fileList.get(directory);    	
    	if(files.containsKey(path))
    	{
    		return;
    	}
    	try
    	{
    		FileInputStream fin = new FileInputStream(ApplicationConfig.GameResourceDir + "/" + directory + "/" + path);
    		int fileLength = fin.available();
    		byte[] buf = new byte[fileLength];
    		fin.read(buf);
    		fin.close();
    		files.put(path, buf);
    		Data.CurrentUsedMem += fileLength;
    	}
    	catch(IOException e)
    	{
    		logError(e.getMessage());
    		return;
    	}
    	
    }
    
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub		
		String uri = request.getRequestURI();
		logInfo("URI:" + uri);
		int assetURLStartIndex = uri.indexOf("AssetBundles");
		int publishURLStartIndex = uri.lastIndexOf("/");
		String fileName = null;
		if(assetURLStartIndex != -1)
		{
			fileName = uri.substring(assetURLStartIndex - 1);
		}
		else if(publishURLStartIndex != -1)
		{
			fileName = uri.substring(publishURLStartIndex);
		}
		else
		{
			logError("File Name Not Found:" + fileName);
			return;
		}
		//String sendToClientFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		logInfo("Request File:" + fileName + ",mver:" + request.getParameter("mver") + ", ver:" + request.getParameter("ver"));
		RequestDispatcher rd = null;
		if(fileName.contains(ApplicationConfig.VerionFileName) || fileName.contains(ApplicationConfig.PublishResName))
		{
			String latestResVersion = getLatestResVersion();
			if(latestResVersion.compareTo(ApplicationConfig.CurVersion) != 0)
			{				
	    		ApplicationConfig.CurVersion = latestResVersion;
	    		loadVersionFile(latestResVersion);	    		
				checkReserveResNum();
				logInfo("CheckVersionNum, versionSize:" + Data.versionFilesArray.size());
			}
			rd = request.getRequestDispatcher("/" + ApplicationConfig.CurVersion + fileName);
			logInfo("/" + ApplicationConfig.CurVersion + fileName);
		}
		else
		{
			String verNum = request.getParameter("ver");
			Integer versionNum = Integer.parseInt(verNum);
			String directory = getDirectory(versionNum, fileName);
			logInfo("VersionNum:" + verNum + ", directory:" + directory);
			rd = request.getRequestDispatcher("/" + directory + "/" + uri.substring(assetURLStartIndex));
			logInfo("/" + directory + "/" + uri.substring(assetURLStartIndex));
		}
		rd.forward(request, response);
	}

	
	private void logError(String msg)
	{
		if((ApplicationConfig.logLevel & LogLevel.Error) == 0)return;
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		int LineNum = Thread.currentThread().getStackTrace()[2].getLineNumber();
		String method = Thread.currentThread().getStackTrace()[2].getMethodName();
		getServletContext().log("Error:"+ className + ">>" + method + ">>" + LineNum +":" + msg);
	}
	
	private void logInfo(String msg)
	{
		if((ApplicationConfig.logLevel & LogLevel.Info) == 0)return;
		String className = Thread.currentThread().getStackTrace()[2].getClassName();
		int LineNum = Thread.currentThread().getStackTrace()[2].getLineNumber();
		String method = Thread.currentThread().getStackTrace()[2].getMethodName();
		getServletContext().log("Info:" + className + ">>" + method + ">>" + LineNum +":" + msg);
	}
	
	private String getDirectory(int versionNum, String fileName)
	{
		String result = null;
		String result2 = null;
		for(VersionFiles files : Data.versionFilesArray)
		{
			result2 = files.getDirectoryOfFile(fileName, versionNum);
			if(result == null) result = result2;
			else if(result2.compareTo(result) > 0)result = result2;
		}
		return result;		
	}	
	
	
	
	public String getLatestResVersion()
	{
		String result = null;		
		try {
			result = ClientUtil.getResDir();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logError("Exception:"+e.getMessage());
			if(ApplicationConfig.StopIfError == 1)
			{
				System.exit(0);
			}
		}
		return result;
	}
	
	private void loadVersionFile(String directory)
	{		
		if(ApplicationConfig.GameResourceDir == null)
		{
			ClientUtil.getHostIP();
		}
		String path = ApplicationConfig.GameResourceDir + "/" + directory + "/AssetBundles/version.ini";
		logInfo("version File:" + path);
		try
		{
			FileReader fr = new FileReader(path);
			BufferedReader br = new BufferedReader(fr);
			String line = null;
			int VersionNum = Integer.parseInt(directory.substring(directory.lastIndexOf('.') + 1));
			VersionFiles versionFiles = new VersionFiles(directory, VersionNum);
			while((line = br.readLine())!=null)
			{
				String[] temp = line.split("=");
				if(temp == null || temp.length != 2)
				{
					logError("Split Error, line:" + line);
					continue;
				}
				versionFiles.getFileList().put("/AssetBundles/" + temp[0], Integer.parseInt(temp[1]));
			}
			br.close();
			Data.versionFilesArray.add(versionFiles);
		}
		catch(IOException e)
		{
			logError("Exception:"+e.getMessage());
			if(ApplicationConfig.StopIfError == 1)
			{
				System.exit(0);
			}
		}
	}
	
	private void loadResFiles(VersionFiles versionFiles)
	{
		if(Data.fileList.containsKey(versionFiles.getDirName()))
		{
			logError("DirName:" + versionFiles.getDirName() + " Exist, Load Error");
			return;
		}
		HashMap<String, byte[]> Files = new HashMap<String, byte[]>();		
		FileInputStream fin;
		byte[] buf;
		
		try
		{
			logInfo("Load Publish unity3d file:" + ApplicationConfig.PublishResName);
			fin = new FileInputStream(ApplicationConfig.GameResourceDir + "/" + versionFiles.getDirName() + "/" + ApplicationConfig.PublishResName);			
			buf = new byte[fin.available()];
			fin.read(buf);
			Files.put("/AssetBundles/" + ApplicationConfig.PublishResName, buf);
			fin.close();
			logInfo("Load Version.ini file:" + ApplicationConfig.VerionFileName);
			fin = new FileInputStream(ApplicationConfig.GameResourceDir + "/" + 
					versionFiles.getDirName() + "/AssetBundles/" + ApplicationConfig.VerionFileName);
			buf = new byte[fin.available()];
			fin.read(buf);
			fin.close();
			Files.put("/AssetBundles/" + ApplicationConfig.VerionFileName, buf);
		}
		catch (FileNotFoundException e) 
		{
			// TODO Auto-generated catch block
			logError("File Not Found, Message:" + e.getMessage());			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logError("IOException, Message:" + e.getMessage());			
		}
		int index = 0;
		for(String path : versionFiles.getFileList().keySet())
		{
			try 
			{
				logInfo("Index:" + (index++) + ",Loading File:" + path);
				fin = new FileInputStream(ApplicationConfig.GameResourceDir + "/" + versionFiles.getDirName() + path);
				buf = new byte[fin.available()];
				fin.read(buf);
				fin.close();
				Files.put(path, buf);
			}
			catch (FileNotFoundException e) 
			{
				// TODO Auto-generated catch block
				logError("File Not Found, name:" + path + ", Message:" + e.getMessage());
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logError("File Read Error, Message:" + e.getMessage());
				continue;
			}
		}
		Data.fileList.put(versionFiles.getDirName(), Files);
	}
	
	private void checkReserveResNum()
	{
		if(Data.versionFilesArray.size() == ApplicationConfig.ReserveVersionNum)
		{
			//String directory = Data.versionFilesArray.get(0).getDirName();
			//Data.fileList.remove(directory);
			Data.versionFilesArray.remove(0);
		}
	}

}
