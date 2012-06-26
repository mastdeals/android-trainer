package com.glm.bean;

import java.text.NumberFormat;
import java.util.ArrayList;

import android.util.Log;

/**
 * Dati dell'utente che vengono letti dal DB
 * nel caso non è definito un'utente verrà visualizzata una popup dall'Activity
 * con la creazione dell'utente ed eventualmente connessione ad uno o più social
 * 
 * **/
public class User {
	private static int iID;

	private static String sName;
	
	private static String sNick;
	
	private static boolean bEnabled;
	
	private static boolean bDeleted;
		
	private static String sGender;
	private static int iAge; 	
	private static int iWeight; 	  
	private static int iHeight;
	private static int iFB; 	 
	private static int iBuzz; 	 
	private static int iTwitter;  
	
	private static String sBMI=" - ";
	private static ArrayList<SocialAccount> aSocialAccount;

	
	public static synchronized String getsName() {
		return sName;
	}

	public static synchronized void setsName(String sName) {
		User.sName = sName;
	}

	public static synchronized String getsNick() {
		return sNick;
	}

	public static synchronized void setsNick(String sNick) {
		User.sNick = sNick;
	}

	public synchronized boolean isbEnabled() {
		return bEnabled;
	}

	public synchronized void setbEnabled(boolean bEnabled) {
		User.bEnabled = bEnabled;
	}

	public synchronized boolean isbDeleted() {
		return bDeleted;
	}

	public synchronized void setbDeleted(boolean bDeleted) {
		User.bDeleted = bDeleted;
	}

	public synchronized ArrayList<SocialAccount> getaSocialAccount() {
		return aSocialAccount;
	}

	public synchronized void setaSocialAccount(
			ArrayList<SocialAccount> aSocialAccount) {
		User.aSocialAccount = aSocialAccount;
	}
	
	public static synchronized int getID() {
		return iID;
	}

	public static synchronized void setID(int iID) {
		User.iID = iID;
	}
	
	/**
	 * @return the iGender
	 */
	public static synchronized String getsGender() {
		return sGender;
	}

	/**
	 * @param iGender the iGender to set
	 */
	public static synchronized void setsGender(String sGender) {
		User.sGender = sGender;
	}

	/**
	 * @return the iAge
	 */
	public static synchronized int getiAge() {
		return iAge;
	}

	/**
	 * @param iAge the iAge to set
	 */
	public static synchronized void setiAge(int iAge) {
		User.iAge = iAge;
	}

	/**
	 * @return the iWeight
	 */
	public static synchronized int getiWeight() {
		return iWeight;
	}

	/**
	 * @param iWeight the iWeight to set
	 */
	public static synchronized void setiWeight(int iWeight) {
		User.iWeight = iWeight;
	}

	/**
	 * @return the iFB
	 */
	public static synchronized int getiFB() {
		return iFB;
	}

	/**
	 * @param iFB the iFB to set
	 */
	public static synchronized void setiFB(int iFB) {
		User.iFB = iFB;
	}

	/**
	 * @return the iBuzz
	 */
	public static synchronized int getiBuzz() {
		return iBuzz;
	}

	/**
	 * @param iBuzz the iBuzz to set
	 */
	public static synchronized void setiBuzz(int iBuzz) {
		User.iBuzz = iBuzz;
	}

	/**
	 * @return the iTwitter
	 */
	public static synchronized int getiTwitter() {
		return iTwitter;
	}

	/**
	 * @param iTwitter the iTwitter to set
	 */
	public static synchronized void setiTwitter(int iTwitter) {
		User.iTwitter = iTwitter;
	}

	public static synchronized int getiHeight() {
		return iHeight;
	}
	public static synchronized void setiHeight(int iHeight) {
		User.iHeight = iHeight;
	}

	/**
	 * iUnit 0 KM/Kg 1 Mi/Lbs/
	 * 
	 * 
	 * */
	public static synchronized String getsBMI(int iUnit) {
		try{
			if(iUnit==0){
				//Unità metriche: BMI = Peso (kg) / (Altezza (m) x Altezza (m))
				float fBMI=0;	
				float fHeight=((float) User.iHeight)/100;
				fBMI=User.iWeight/(fHeight*fHeight);
				NumberFormat oNFormat = NumberFormat.getNumberInstance();
				oNFormat.setMaximumFractionDigits(2);
				sBMI = oNFormat.format(fBMI);
				//Log.i(User.class.getCanonicalName(),"User BMI (Peso/(Altezza m)*(Altezza m)) "+User.iWeight+"/("+fHeight+"*"+fHeight+")"+": "+sBMI);
			}else{
				//Unità Inglesi: BMI = Peso (lb) / (Altezza (in) x Altezza (in)) x 703
				float fBMI=0;	
				float fHeight=((float) User.iHeight);
				fBMI=(float) (((User.iWeight)/(fHeight*fHeight))*703);
				NumberFormat oNFormat = NumberFormat.getNumberInstance();
				oNFormat.setMaximumFractionDigits(2);
				sBMI = oNFormat.format(fBMI);
				//Log.i(User.class.getCanonicalName(),"User BMI (Peso/(Altezza m)*(Altezza m)) "+(User.iWeight)+"/("+(fHeight)+"*"+(fHeight)+")"+": "+sBMI);
			}
			
			
		}catch (Exception e) {
			Log.e(User.class.getCanonicalName(),"Error Calculate BMI");
			return sBMI;
		}
		
		return sBMI;
	}
}
