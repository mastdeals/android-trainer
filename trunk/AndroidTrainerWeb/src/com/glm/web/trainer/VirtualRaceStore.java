package com.glm.web.trainer;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.glm.web.utils.ServerUtilsHelper;

/**
 * Servlet implementation class VirtualRaceStore
 */
@WebServlet(description = "Ritorna le Virtual Race Disponibili in formato JSON", urlPatterns = { "/VirtualRaceStore" })
public class VirtualRaceStore extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VirtualRaceStore() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String  sJSonOut = "";
		String 	sGCMId 			= request.getParameter("gcmid");
		String 	sLocale			= request.getParameter("locale");
		
		ServerUtilsHelper oServer = new ServerUtilsHelper();
		
		sJSonOut = oServer.VirtualRaceStore(sGCMId,sLocale);
		
		response.getOutputStream().println(sJSonOut);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String  sJSonOut = "";
		String 	sGCMId 			= request.getParameter("gcmid");
		String 	sLocale			= request.getParameter("locale");
		
		ServerUtilsHelper oServer = new ServerUtilsHelper();
		
		sJSonOut = oServer.VirtualRaceStore(sGCMId,sLocale);
		
		response.getOutputStream().println(sJSonOut);
	}

}
