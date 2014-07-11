package org.qiyu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FileHelper extends HttpServlet {
	protected static String csvFilePath = "CSVTables.unity3d";
	protected static char hexDigits[] = { '0', '1','2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f'};
	protected static MessageDigest messagedigest = null;
	static{
	  try {
	  messagedigest = MessageDigest.getInstance("MD5");
	  } catch (NoSuchAlgorithmException e) {
	  }
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ClientUtil.logInfo("Request Type:" + request.getParameter("type") + ", remoteIp:" + request.getRemoteAddr() +
				", remoteHost:" + request.getRemoteHost());
		String type = request.getParameter("type");
		int t = Integer.parseInt(type);
		ClientUtil.logInfo("Look Log Type = " + t);
		response.setContentType("text/html;charset=UTF-8");
		ServletOutputStream ou = response.getOutputStream();
		switch(t)
		{
		case 1://Get md5 code
			//look log			
			{
				ClientUtil.logInfo("Get Hash Code:" + getMD5String(csvFilePath));
				ou.write(getMD5String(csvFilePath).getBytes());
				ou.flush();
				ou.close();
			}			
			
			break;
		case 2://get file
		{
			ClientUtil.logInfo("Get file");
			RequestDispatcher rd = request.getRequestDispatcher(csvFilePath);
			rd.forward(request, response);
			break;
		}
		case 3:
			//look log
			break;
		case 4:
			//del log
			if(ClientUtil.ClearLog())
			{
				ClientUtil.logInfo("Delete Log Success");
				ou.write(ClientUtil.GetLogContent().getBytes("utf-8"));
				ou.flush();
				ou.close();
			}
			break;
//			ou.write(ClientUtil.GetLogContent().getBytes("utf-8"));
//			ou.close();
//			{
//				RequestDispatcher rd = request.getRequestDispatcher("/0.0.85.540/Publish.unity3d");
//				rd.forward(request, response);
//				break;
//			}
			
		}
		
	}	
	
	public String getFileMD5String(File file) throws IOException{
		  FileInputStream in = new FileInputStream(file);
		  FileChannel ch =in.getChannel();
		  MappedByteBuffer byteBuffer =ch.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
		  messagedigest.update(byteBuffer);
		  return bufferToHex(messagedigest.digest());
		}
	
	public String getMD5String(String s){
		  return getMD5String(s.getBytes());
		}

		public String getMD5String(byte[] bytes){
		  messagedigest.update(bytes);
		  return bufferToHex(messagedigest.digest());
		}

		private String bufferToHex(byte bytes[]){
		  return bufferToHex(bytes, 0,bytes.length);
		}

		private String bufferToHex(byte bytes[], int m, int n){
		  StringBuffer stringbuffer =new StringBuffer(2 * n);
		  int k = m + n;
		  for (int l = m; l< k; l++) {
		  appendHexPair(bytes[l], stringbuffer);
		  }
		  return stringbuffer.toString();
		}


		private void appendHexPair(byte bt, StringBuffer stringbuffer) {
		  char c0 = hexDigits[(bt& 0xf0) >> 4];
		  char c1 = hexDigits[bt& 0xf];
		  stringbuffer.append(c0);
		  stringbuffer.append(c1);
		}

		public boolean checkPassword(String password, String md5PwdStr) {
		  String s =getMD5String(password);
		  return s.equals(md5PwdStr);
		}

//		public void main(String[] args) throws IOException{
//		  long begin =System.currentTimeMillis();
//
//		  File big = new File("/home/xxx/xxx/dell.txt");
//		  String md5=getFileMD5String(big);
//
//		  long end =System.currentTimeMillis();
//		  System.out.println("md5:"+md5);
//		  System.out.println("time:"+((end-begin)/1000)+"s");
//		 
//		}

	
}
