package org.qiyu;

import java.util.HashMap;

public class VersionFiles {
	private String dirName;

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public int getVersionNum() {
		return versionNum;
	}

	public void setVersionNum(int versionNum) {
		this.versionNum = versionNum;
	}

	private int versionNum;
	private HashMap<String, Integer> fileList;

	public HashMap<String, Integer> getFileList() {
		return fileList;
	}

	public void setFileList(HashMap<String, Integer> fileList) {
		this.fileList = fileList;
	}

	public VersionFiles(String directory, int version) {
		dirName = directory;
		versionNum = version;
		fileList = new HashMap<String, Integer>();
	}

	public String getDirectoryOfFile(String fileName, int versionNum) {
		String result = null;
		if (fileList.containsKey(fileName)
				&& fileList.get(fileName) == versionNum) {
			result = dirName;
		}
		return result;
	}


}
