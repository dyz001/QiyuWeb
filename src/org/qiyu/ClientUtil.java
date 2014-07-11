package org.qiyu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.UTF8JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;



public class ClientUtil
{	
	/**
	 * 
	 */	
	private static  String logPath = null;
	private static String osName = null;
	private static long configStaticLastModified = 0;
	private static long configDynamicLastModified = 0;
	private static String ChooseAssetURL;
	private static String exe_ver;
	private static String ChooseHostIP;
	private static String versionXMLPath = null;
	private static String globalXMLPath = null;
	private static String gameResXMLPath = null;
	
	private static Document versionXML = null;
	private static Document globalConfigXML = null;
	private static Document gameResXML = null;
	
	private static ObjectMapper mapper = new ObjectMapper();
	private static JsonGenerator jsonGenerator = null;
	private static StringWriter sw = new StringWriter();
	static
	{
		Properties prop = System.getProperties();

		osName = prop.getProperty("os.name");
		if(osName.startsWith("win") || osName.startsWith("Win"))
		{
			logPath = "d:\\WebExeError.txt";
		}
		else
		{
			logPath = "../logs/WebExeError.log";
		}
		loadXMLConfig();
	}
	
	public static String GetLogPath()
	{
		return logPath;
	}
	
	public static String GetLogContent() throws IOException
	{
		File f = new File(ClientUtil.GetLogPath());
		if(!f.exists())
		{
			return "";
		}
		FileInputStream fin = new FileInputStream(f);
		byte[] logBuf = new byte[fin.available()];
		fin.read(logBuf);
		fin.close();
		String logContent = new String(logBuf);
		logContent = logContent.replace(System.getProperty("line.separator"), "</br>");
		System.out.println(logContent);
		return logContent;
	}
	
	public static boolean ClearLog()
	{		
		File f = new File(ClientUtil.GetLogPath());
		return f.delete();		
	}
	
	private static void loadXMLConfig()
	{
		URL path = ClientUtil.class.getResource("/");
		System.out.println("PathURL:" + path.toString());
		String prePath = null;
		if(versionXMLPath == null)
		{			
			if(osName.startsWith("win") || osName.startsWith("Win"))
			{
				prePath = path.getFile().substring(1).replace("%20", " ");
				versionXMLPath = prePath + "../../" + ApplicationConfig.VersionXMLPath;
				globalXMLPath = prePath + "../../" + ApplicationConfig.GlobalXMLPath;
				gameResXMLPath = prePath + "../../" + ApplicationConfig.GameResXMLPath;
			}
			else
			{
				prePath = path.getFile().replace("%20", " ");
				versionXMLPath = prePath + "../../" + ApplicationConfig.VersionXMLPath;
				globalXMLPath = prePath + "../../" + ApplicationConfig.GlobalXMLPath;
				gameResXMLPath = prePath + "../../" + ApplicationConfig.GameResXMLPath;
			}
		}
		try
		{
			SAXReader reader = new SAXReader();
			reader.setEncoding("UTF-8");
			versionXML = reader.read(new File(versionXMLPath));			
			globalConfigXML = reader.read(new File(globalXMLPath));
			File gameResFile = new File(gameResXMLPath);
			if(gameResFile.exists())
			{
				gameResXML = reader.read(new File(gameResXMLPath));
				Element root = gameResXML.getRootElement();
				ApplicationConfig.GameResourceDir = root.elementText("path");
				ApplicationConfig.StopIfError = Integer.parseInt(root.elementText("stopIfError"));
				ApplicationConfig.PublishResName = root.elementText("PublishName");
			}
			else
			{
				ApplicationConfig.GameResourceDir = prePath + "../../";
			}
			
		}
		catch(DocumentException e)
		{
			logException(e);
		}
		catch(Exception e)
		{
			logException(e);
		}
		
	}
	
	public static String getResDir() throws IOException
	{
		File versionXMLFile = new File(versionXMLPath);
		if(configDynamicLastModified == 0 || versionXMLFile.lastModified() > configDynamicLastModified)
		{
			loadXMLConfig();
			configDynamicLastModified = versionXMLFile.lastModified();
		}
		Element root = versionXML.getRootElement();
		String version = root.elementText("version");
		if(version != null)
		{
			exe_ver = version;
		}
		else
		{
			logInfo("Version Num Not Found in XML");
		}
		return exe_ver;
	}
	
	public static String getHostIP()
	{
		//ÐÂÂß¼­
		File configStaticFile = new File(globalXMLPath);
		if(configStaticLastModified == 0 || configStaticFile.lastModified() > configStaticLastModified)
		{
			loadXMLConfig();
			configStaticLastModified = configStaticFile.lastModified();			
		}
		Element root = globalConfigXML.getRootElement();
		Element zoneList = root.element("zone_list");
		List zones = zoneList.elements("zone");
		if(zones.size() > 0)
		{
			Element firstZone = (Element)zones.get(0);
			String ip = firstZone.elementText("ip");
			String port = firstZone.elementText("port");
			ChooseHostIP = ip + ":" + port;
		}
		else
		{
			logInfo("Error, ZoneList is null");
		}
		return ChooseHostIP;
	}
	
	public static String getAssetUrl()
	{		
		File globalXMLFile = new File(globalXMLPath);
		if(configStaticLastModified == 0 || globalXMLFile.lastModified() > configStaticLastModified)
		{
			loadXMLConfig();
			configStaticLastModified = globalXMLFile.lastModified();
		}			
		Element root = globalConfigXML.getRootElement();
		Element zoneList = root.element("zone_list");
		List zones = zoneList.elements("zone");
		logInfo("zones size:" + zones.size());
		if(zones.size() > 0)
		{
			Element firstZone = (Element)zones.get(0);
			ChooseAssetURL  = firstZone.elementText("game_root_url");
		}
		else
		{
			logInfo("Error, ZoneList is null");
		}
		return ChooseAssetURL;
	}
	
	public static String getZoneList()
	{
		StringBuffer sb = sw.getBuffer();
		if(sb.length() > 0)
		{
			sb.delete(0, sb.length());
		}
		File configStaticFile = new File(globalXMLPath);
		if(configStaticLastModified == 0 || configStaticFile.lastModified() > configStaticLastModified)
		{
			loadXMLConfig();
			configStaticLastModified = configStaticFile.lastModified();			
		}
		
		Element root = globalConfigXML.getRootElement();
		Element zoneList = root.element("zone_list");
		List zones = zoneList.elements("zone");
		HashMap<Integer, ZoneBean> map = new HashMap<Integer, ZoneBean>();
		for(int i = 0; i < zones.size(); ++i)
		{
			Element zone = (Element)zones.get(i);
			ZoneBean zb = new ZoneBean();
			zb.setId(zone.elementText("id"));
			zb.setName(zone.elementText("name"));
			zb.setIp(zone.elementText("ip"));
			zb.setPort(zone.elementText("port"));
			zb.setName(zone.elementText("name"));
			map.put(Integer.parseInt(zone.elementText("id")), zb);
		}	    
	    try {
	    	if(jsonGenerator == null)
			{
	    		jsonGenerator = mapper.getFactory().createGenerator(sw);
			}		
			jsonGenerator.writeObject(map);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logException(e);
		}	
		
		return sw.toString();
	}
	
	public static void logInfo(String msg)
	{
		try {
			String className = Thread.currentThread().getStackTrace()[2].getClassName();
			int LineNum = Thread.currentThread().getStackTrace()[2].getLineNumber();
			String method = Thread.currentThread().getStackTrace()[2].getMethodName();
			FileWriter fw = new FileWriter(logPath, true);
			fw.write( new Date(System.currentTimeMillis()).toLocaleString() + " Class:" + className + ", LineNum:" +
			LineNum + ", MethodName: " + method + ",msg:" + msg + "\r\n");
			fw.flush();
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void logException(Exception e)
	{
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(logPath,true));
			e.printStackTrace(pw);
			pw.flush();
			pw.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
