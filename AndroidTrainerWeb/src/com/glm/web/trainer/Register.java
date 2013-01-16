package com.glm.web.trainer;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.glm.web.utils.ServerUtilsHelper;

import java.sql.*;
/**
 * Servlet implementation class register
 */
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;  
	private ResultSet resultSet = null;  
	private Statement statement = null;  
	private File oFile = new File(".");
	private String sCurrentPath=oFile.getAbsolutePath(); 
    /**
     * Default constructor. 
     */
    public Register() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String sGCMId	= request.getParameter("gcmid");
		String sAge		= request.getParameter("age");
		String sWeight	= request.getParameter("weight");
		String sGender	= request.getParameter("gender");
		String sName	= request.getParameter("name");
		String sNick	= request.getParameter("nick");
		
		
		ServerUtilsHelper oServer = new ServerUtilsHelper();
		oServer.registerNewDevices(sGCMId,sAge,sWeight,sGender,sName,sNick);
	}

}
