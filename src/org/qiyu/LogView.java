package org.qiyu;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogView extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ClientUtil.logInfo("Log Type:" + request.getParameter("type"));
		String type = request.getParameter("type");
		int t = Integer.parseInt(type);
		ClientUtil.logInfo("Look Log Type = " + t);
		response.setContentType("text/html;charset=UTF-8");
		ServletOutputStream ou = response.getOutputStream();
		switch(t)
		{
		case 1:
			//look log			
			{
				ou.write(ClientUtil.GetLogContent().getBytes("utf-8"));
				ou.flush();
				ou.close();
			}			
			
			break;
		case 2:
			//look log
			break;
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

	
}
