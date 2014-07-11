package org.qiyu;

public class DownLoadFileInfo implements Comparable<DownLoadFileInfo>
{
	
	String directory;	
	String fileName;
	long lastDownLoad;
	long downLoadNum;
	
	public DownLoadFileInfo(String dir, String fn)
	{
		directory=dir;
		fileName=fn;
		lastDownLoad=System.currentTimeMillis();
		downLoadNum = 0;
	}
	
	public String getDirectory() {
		return directory;
	}
	public void setDirectory(String directory) {
		this.directory = directory;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getLastDownLoad() {
		return lastDownLoad;
	}
	public void setLastDownLoad(long lastDownLoad) {
		this.lastDownLoad = lastDownLoad;
	}
	public long getDownLoadNum() {
		return downLoadNum;
	}
	public void setDownLoadNum(long downLoadNum) {
		this.downLoadNum = downLoadNum;
	}

	//make array sort desc order
	@Override
	public int compareTo(DownLoadFileInfo o) {
		// TODO Auto-generated method stub
		
		return (lastDownLoad > o.getLastDownLoad() ? -1 : (lastDownLoad < o.getLastDownLoad() ? 1 : 0));
	}

}
