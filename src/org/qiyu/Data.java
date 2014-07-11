package org.qiyu;

import java.util.ArrayList;
import java.util.HashMap;


public class Data {
	
	public static HashMap<String, HashMap<String, byte[]>> fileList = new HashMap<String, HashMap<String,byte[]>>();
	public static ArrayList<VersionFiles> versionFilesArray = new ArrayList<VersionFiles>();
	public static ArrayList<DownLoadFileInfo> FileInfoArray = new ArrayList<DownLoadFileInfo>();
	public static long LastSortTime = System.currentTimeMillis();
	public static long CurrentUsedMem = 0;
	public static DownLoadFileInfo findFileInfo(String dir, String fn)
	{
		for(int i = 0; i < FileInfoArray.size(); ++i)
		{
			DownLoadFileInfo temp = FileInfoArray.get(i);
			if(temp.getDirectory().compareTo(dir) == 0 && temp.getFileName().compareTo(fn) == 0)
			{
				return temp;
			}
		}
		return null;
	}
	
	public static void CheckAndReleaseMemery()
	{		
		while(CurrentUsedMem > ApplicationConfig.MaxMem)
		{
			int lastIndex = FileInfoArray.size();
			DownLoadFileInfo dFI = FileInfoArray.get(lastIndex);
			byte[] temp = fileList.get(dFI.getDirectory()).get(dFI.getFileName());
			CurrentUsedMem -= temp.length;
			fileList.get(dFI.getDirectory()).remove(dFI.getFileName());
		}
	}
}
