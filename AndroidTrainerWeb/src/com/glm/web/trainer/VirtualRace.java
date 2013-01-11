package com.glm.web.trainer;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.glm.web.utils.ServerUtilsHelper;

/**
 * Servlet implementation class VirtualRace
 */
@WebServlet("/VirtualRace")
public class VirtualRace extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VirtualRace() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getOutputStream().print(request.getParameter("test"));
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sGCMId 		= request.getParameter("gcmid");
		int sVirtualRace 	= Integer.parseInt(request.getParameter("virtualrace"));
		double dLatidute 	= Double.parseDouble(request.getParameter("latidute"));
		double dLongitude 	= Double.parseDouble(request.getParameter("logitude"));
		float  fAlt 		= Float.parseFloat(request.getParameter("alt"));
		float  fSpeed 		= Float.parseFloat(request.getParameter("speed"));
		double dDistance 	= Double.parseDouble( request.getParameter("distance"));
		float  fTime 		= Float.parseFloat(request.getParameter("time"));
		
		ServerUtilsHelper oServer = new ServerUtilsHelper();
		oServer.newWatchPointForVirtualRace(sGCMId,sVirtualRace,dLatidute,dLongitude,fAlt,fSpeed,dDistance,fTime);
	}

}
