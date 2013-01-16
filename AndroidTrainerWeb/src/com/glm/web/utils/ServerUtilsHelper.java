package com.glm.web.utils;


import java.util.Date;

import com.glm.web.bean.VirtualRace;
import com.glm.web.utils.db.MySQLHelper;
import com.google.gson.*;
public class ServerUtilsHelper {
	/**registra un nuovo device*/
	public boolean registerNewDevices(String sGCMId, String sAge, String sWeight, String sGender, String sName, String sNick){
		MySQLHelper oMySql = new MySQLHelper();
		if(oMySql.Connect()){
			//Prima verifico se esiste e poi eventualmente ricreo
			oMySql.insertUpdateSQL("DELETE FROM androidtrainer.users WHERE gcmid='"+sGCMId+"'");
			if(oMySql.insertUpdateSQL("INSERT INTO androidtrainer.users (gcmid,age,weight,gender,name,nick) values ('"+sGCMId+"','"+sAge+"','"+sWeight+"','"+sGender+"','"+sName+"','"+sNick+"')")){
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
	/**registra un nuovo partecimante alla VirtualRace*/
	public boolean newWatchPointForVirtualRace(String sGCMId,int iVirtualRace,
			double dLatidute,double dLongitude,float fAlt,float fSpeed,double dDistance,Long lTime,String sLocale){
		MySQLHelper oMySql = new MySQLHelper();
		
		if(oMySql.Connect()){
			if(oMySql.insertUpdateSQL("INSERT INTO androidtrainer.virtualrace (gcmid,id_virtual_race,latidute,longitude,alt,pace,distance,time,locale) values" +
					" ('"+sGCMId+"',"+iVirtualRace+","+dLatidute+","+dLongitude+","+fAlt+","+fSpeed+
							","+dDistance+","+lTime+",'"+sLocale+"')")){
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
	/**
	 * Ritorna un JSon con le Virtual Race
	 * 
	 * */
	public String VirtualRaceStore(String sGCMId, String sLocale) {
		String sJsonOut="";
		//if(sGCMId.compareToIgnoreCase("APA91bFsgVvBjhTd6cvFeYMpYdwaHw5Pns84asKfRFCe5zpOOcEqhpVvF8AFZpQAuimLGUvTRR5ulK3l_FugWny3egaO1HtJcDWygCfAfJUXiceIRfLAwKd0PJ_DSf-LQAFWXdhJgwOUAqTB2D3b8OTv5fooYnTp2Q")==0){
			Gson gson = new Gson();
			VirtualRace oVirtualRace = new VirtualRace();
			oVirtualRace.setdStart(new Date());
			oVirtualRace.setfPrice(0.0f);
			oVirtualRace.setiDifficult(0);
			oVirtualRace.setiType(0);
			oVirtualRace.setiVirtualRaceID(0);
			oVirtualRace.setsDesc("First Beta");
			oVirtualRace.setsDesc1("BETA");
			oVirtualRace.setsMsKu("BETA");
			oVirtualRace.setsName("BETA RACE");
			sJsonOut= gson.toJson(oVirtualRace);
		//}	
		return sJsonOut;
	}
}
