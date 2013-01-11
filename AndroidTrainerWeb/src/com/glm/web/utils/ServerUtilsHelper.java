package com.glm.web.utils;

import com.glm.web.utils.db.MySQLHelper;

public class ServerUtilsHelper {
	/**registra un nuovo device*/
	public boolean registerNewDevices(String sGCMId, String sAge, String sWeight, String sGender, String sName, String sNick){
		MySQLHelper oMySql = new MySQLHelper();
		if(oMySql.Connect()){
			//Prima verifico se esiste e poi eventualmente ricreo
			if(oMySql.insertUpdateSQL("DELETE FROM users WHERE gcmid='"+sGCMId+"'")){
				if(oMySql.insertUpdateSQL("INSERT INTO users (gcmid,age,weight,gender,name,nick) values ('"+sGCMId+"','"+sAge+"','"+sWeight+"','"+sGender+"','"+sName+"','"+sNick+"')")){
					System.out.println("registered ner device");
				}else{
					return false;
				}
			}
			oMySql.Disconnect();
		}else{
			if(oMySql.isConected()) oMySql.Disconnect();
			return false;
		}
		return true;
	}
	/**registra un nuovo partecimante alla VirtualRace*/
	public boolean newWatchPointForVirtualRace(String sGCMId,int iVirtualRace,
			double dLatidute,double dLongitude,float fAlt,float fSpeed,double dDistance,float fTime){
		MySQLHelper oMySql = new MySQLHelper();
		
		if(oMySql.Connect()){
			if(oMySql.insertUpdateSQL("INSERT INTO virtualrace (gcmid,id_virtual_race,latidute,longitude,alt,pace,distance,time) values" +
					" ('"+sGCMId+"',"+iVirtualRace+","+dLatidute+","+dLongitude+","+fAlt+","+fSpeed+
							","+dDistance+","+fTime+")")){
				System.out.println("registered ner device");
			}else{
				return false;
			}
			oMySql.Disconnect();
		}else{
			if(oMySql.isConected()) oMySql.Disconnect();
			return false;
		}
		return true;
	}
}
