package com.glm.web.utils.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class MySQLHelper {
	private Connection mConnect;
	private Statement mStatement = null;
	private ResultSet mResultSet = null;
	
	public MySQLHelper(){
		try {
	        Class.forName("com.mysql.jdbc.Driver");	        
	    } catch (ClassNotFoundException ex) {
	        System.err.println("ClassNotFoundException Error Load MySQL Driver");
	        ex.printStackTrace();
	    } 	    
	   // ResultSet result = stmt.executeQuery("SELECT * FROM table1");
	}
	
	public boolean Connect(){
		try {	       
	        mConnect = DriverManager.getConnection("jdbc:mysql://localhost:3306/androidtrainer", "root", "androidtrainer");
	        mStatement = mConnect.createStatement();
	    }  catch (SQLException e) {
	    	System.err.println("SQLException Connection To MySQL Server");
			e.printStackTrace();
			return false;
		}        
		return true;
	}
	
	public boolean Disconnect(){
		try {	      
			mStatement.close();
	        mConnect.close();		      
	    }  catch (SQLException e) {
	    	System.err.println("SQLException Connection To MySQL Server");
			e.printStackTrace();
			return false;
		}        
		return true;
	}
	
	public boolean insertUpdateSQL(String sSQL){
		boolean bStatus=false;
		try {
			bStatus=mStatement.execute(sSQL);
		} catch (SQLException e) {
			System.err.println("SQLException SQL To MySQL Server");
			e.printStackTrace();
			bStatus=false;
		}
		
		return bStatus;
	}
}
