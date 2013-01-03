package com.glm.web.utils;

import com.glm.web.utils.db.MySQLHelper;

public class ServerUtilsHelper {
	
	public boolean registerNewDevices(String sGCMId, String sAge, String sWeight, String sGender, String sName, String sNick){
		MySQLHelper oMySql = new MySQLHelper();
		if(oMySql.Connect()){
			//Prima verifico se esiste e poi eventualmente ricreo
			
			if(oMySql.insertUpdateSQL("INSERT INTO users (gcmid,age,weight,gender,name,nick) values ('"+sGCMId+"','"+sAge+"','"+sWeight+"','"+sGender+"','"+sName+"','"+sNick+"')")){
				System.out.println("registered ner device");
			}else{
				return false;
			}
			oMySql.Disconnect();
		}else{
			return false;
		}
		return true;
	}
}
