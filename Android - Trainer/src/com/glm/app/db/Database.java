package com.glm.app.db;

import java.io.File;
import com.glm.app.db.Database.MetaData.TrainerExerciseDett;
import com.glm.app.db.Database.MetaData.TrainerPref;
import com.glm.app.db.Database.MetaData.TrainerVersion;
import com.glm.bean.ConfigTrainer;
import com.glm.trainer.R;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
/**
 * DATABASE STRUCTURE
 * 
-- -----------------------------------------------------
-- Table trainer_users
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS trainer_users (
  id_users INT NOT NULL AUTOINCREMENT ,
  name VARCHAR(45) NOT NULL ,
  nick VARCHAR(45) NULL ,
  enabled TINYINT(1)  NULL DEFAULT 1 ,
  deleted TINYINT(1)  NULL DEFAULT 0 ,
  creation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (id_users) );


-- -----------------------------------------------------
-- Table trainer_type_exercise
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS trainer_type_exercise (
  id_type_exercise INT NOT NULL AUTOINCREMENT ,
  desc VARCHAR(45) NOT NULL ,
  long_desc VARCHAR(255) NOT NULL ,
  PRIMARY KEY (id_type_exercise) );


-- -----------------------------------------------------
-- Table trainer_exercise
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS trainer_exercise (
  id_exercise INT NOT NULL AUTOINCREMENT ,
  id_users INT NOT NULL ,
  id_type_exercise INT NOT NULL ,
  creation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  start_date DATETIME NULL ,
  end_date DATETIME NULL ,
  PRIMARY KEY (id_exercise, id_users, id_type_exercise) );


-- -----------------------------------------------------
-- Table trainer_exercise_dett
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS trainer_exercise_dett (
  id_watch_point INT NOT NULL AUTOINCREMENT ,
  id_exercise INT NOT NULL ,
  id_type_exercise INT NOT NULL ,
  id_users INT NOT NULL ,
  long DOUBLE NOT NULL ,
  watch_point_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ,
  lat DOUBLE NOT NULL ,
  PRIMARY KEY (id_watch_point, id_exercise, id_type_exercise, id_users) );


-- -----------------------------------------------------
-- Table trainer_pref
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS trainer_pref (
  id_pref INT NOT NULL ,
  type VARCHAR(1) NOT NULL ,
  name VARCHAR(45) NOT NULL ,
  value VARCHAR(255) NOT NULL ,
  PRIMARY KEY (id_pref) );


-- -----------------------------------------------------
-- Table trainer_social_account
-- -----------------------------------------------------
CREATE  TABLE IF NOT EXISTS trainer_social_account (
  id_social_account INT NOT NULL AUTOINCREMENT ,
  id_users INT NOT NULL ,
  name VARCHAR(45) NULL ,
  desc VARCHAR(255) NULL ,
  login VARCHAR(45) NULL ,
  password VARCHAR(45) NULL ,
  creation_date DATETIME NULL DEFAULT CURRENT_TIMESTAMP ,
  PRIMARY KEY (id_social_account, id_users) );

 * 
 * 
 * 
 * **/
public class Database {
    private SQLiteDatabase oDatabase;
    private String sVersionNumber;
    private String sVersionDesc;
    
    private boolean bLicence=false;
    
    private DbHelper oDbHelper;
    private Context oContext;
    private static String DB_PATH = "/data/data/%PACKAGE%/databases/";
    //private static String DB_NAME = "Maps4Strategy1";//"4cbase3";
    private static String DB_NAME = "trainer";
    private static final int DB_VERSION = 804;

    static class MetaData {
    	
    	static class TrainerVersion{
			 static final String tablename = "trainer_version";
			    
			 static final String key_id 			= "id_ver";			 	
			 static final String key_version 		= "version_number";
			 static final String key_version_desc 	= "version_desc";
			 static final String licence 			= "licence";
		}
    	
    	/**
    	 * 	-- -----------------------------------------------------
		 *	-- Table trainer_users
		 *	-- -----------------------------------------------------
		 	CREATE  TABLE IF NOT EXISTS trainer_users (
			  id_users INT NOT NULL AUTOINCREMENT ,
			  name VARCHAR(45) NOT NULL ,
			  nick VARCHAR(45) NULL ,
			  enabled TINYINT(1)  NULL DEFAULT 1 ,
			  deleted TINYINT(1)  NULL DEFAULT 0 ,
			  creation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ,
			  PRIMARY KEY (id_users) );
    	 * */
		static class TrainerUsers {
		    static final String tablename = "trainer_users";
		    
		    static final String key_id = "id_users";
		    static final String key_name = "name";
		    static final String key_nick = "nick";
		    static final String key_enabled = "enabled";
		    static final String key_deleted = "deleted";
		    static final String key_creation_date = "creation_date";
		    static final String key_weight = "weight";
		    static final String key_age = "age";
		    static final String key_gender = "gender";
		    static final String key_facebook = "facebook";
		    static final String key_buzz = "buzz";
		    static final String key_twitter = "twitter";
		   
		}
		/**
		 *  -- -----------------------------------------------------
			-- Table trainer_type_exercise
			-- -----------------------------------------------------
			CREATE  TABLE IF NOT EXISTS trainer_type_exercise (
			  id_type_exercise INT NOT NULL AUTOINCREMENT ,
			  desc VARCHAR(45) NOT NULL ,
			  long_desc VARCHAR(255) NOT NULL ,
			  PRIMARY KEY (id_type_exercise) );
		 * 
		 * **/
		static class TrainerTypeEcercise {
		    static final String tablename = "trainer_type_exercise";
		    
		    static final String key_id = "id_type_exercise";
		    static final String key_desc = "desc";
		    static final String key_long_desc = "long_desc";
		}
		/***
		 *  -- -----------------------------------------------------
			-- Table trainer_exercise
			-- -----------------------------------------------------
			CREATE  TABLE IF NOT EXISTS trainer_exercise (
			  id_exercise INT NOT NULL AUTOINCREMENT ,
			  id_users INT NOT NULL ,
			  id_type_exercise INT NOT NULL ,
			  creation_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ,
			  start_date DATETIME NULL ,
			  end_date DATETIME NULL ,
			  PRIMARY KEY (id_exercise, id_users, id_type_exercise) );
		 * 
		 * */
		static class TrainerExercise{
			 static final String tablename = "trainer_exercise";
			    
			 static final String key_id = "id_exercise";
			 static final String key_id_user = "id_users";
			 static final String key_id_type_exercise = "id_type_exercise";
			 static final String key_creation_date = "creation_date";
			 static final String key_start_date = "start_date";
			 static final String key_end_date = "end_date";
			 static final String key_note = "note";
			 static final String key_calorie = "calorie_burn";
			 static final String key_total_distance = "distance";
			 static final String key_avg_speed = "avg_speed";
			 static final String key_total_time = "total_time";
			 static final String key_steps = "steps_count";
			 static final String key_weight = "weight";
		}
		/**
		 * -- -----------------------------------------------------
		-- Table trainer_exercise_dett
		-- -----------------------------------------------------
		CREATE  TABLE IF NOT EXISTS trainer_exercise_dett (
		  id_watch_point INT NOT NULL AUTOINCREMENT ,
		  id_exercise INT NOT NULL ,
		  id_type_exercise INT NOT NULL ,
		  id_users INT NOT NULL ,
		  long DOUBLE NOT NULL ,		  
		  lat DOUBLE NOT NULL ,
		  watch_point_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ,
		  PRIMARY KEY (id_watch_point, id_exercise, id_type_exercise, id_users) );
		*/
		static class TrainerExerciseDett{
			 static final String tablename = "trainer_exercise_dett";
			    
			 static final String key_id = "id_watch_point";			 	
			 static final String key_id_exercise = "id_exercise";
			 static final String key_id_type_exercise = "id_type_exercise";
			 static final String key_id_user = "id_users";			 			 
			 static final String key_long = "long";
			 static final String key_lat = "lat";
			 static final String key_alt = "alt";
			 static final String key_distance = "distance";
			 static final String key_watch_point_name = "watch_point_name";
			 static final String key_watch_point_date = "watch_point_date";
			 static final String key_watch_point_date_prev = "watch_point_date_prev";
		     static final String key_watch_point_song = "watch_point_song";
			
		}
		/**
		-- -----------------------------------------------------
		-- Table trainer_pref
		-- -----------------------------------------------------
		CREATE  TABLE IF NOT EXISTS trainer_pref (
		  id_pref INT NOT NULL ,
		  type VARCHAR(1) NOT NULL ,
		  name VARCHAR(45) NOT NULL ,
		  value VARCHAR(255) NOT NULL ,
		  PRIMARY KEY (id_pref) );
		 **/
		static class TrainerPref{
			 static final String tablename = "trainer_pref";
			    
			 static final String key_id = "id_pref";			 	
			 static final String key_type = "type";
			 static final String key_name = "name";
			 static final String key_desc = "desc";
			 static final String key_value = "value";			 			 						
		}
		/**
		 * -- -----------------------------------------------------
		
		-- Table trainer_social_account
		-- -----------------------------------------------------
		CREATE  TABLE IF NOT EXISTS trainer_social_account (
		  id_social_account INT NOT NULL AUTOINCREMENT ,
		  id_users INT NOT NULL ,
		  name VARCHAR(45) NULL ,
		  desc VARCHAR(255) NULL ,
		  login VARCHAR(45) NULL ,
		  password VARCHAR(45) NULL ,
		  creation_date DATETIME NULL DEFAULT CURRENT_TIMESTAMP ,
		  PRIMARY KEY (id_social_account, id_users) );
		 */
		static class TrainerSocialAccount{
			 static final String tablename = "trainer_social_account";
			    
			 static final String key_id = "id_social_account";	
			 static final String key_id_user = "id_users";				 
			 static final String key_name = "name";
			 static final String key_desc = "desc";
			 static final String key_login = "login";		
			 static final String key_password = "password";	
			 static final String key_creation_date = "creation_date";	
		}
    }
    
    
    
    public Database(Context ctx){
		oContext=ctx;
		DB_PATH = DB_PATH.replace("%PACKAGE%", ctx.getPackageName());	
		
		oDbHelper=new DbHelper(ctx, DB_NAME, null, DB_VERSION); 		
    }

    public void open() {
    	////Log.v(getClass().getCanonicalName(), "createDataBase()");
    	String myPath = DB_PATH + DB_NAME;
    	if(checkDataBase()){
    	    ////Log.v(getClass().getName(), "Database already exists. OK");
    	    try{
    	    	oDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    	    }catch(SQLiteException e){
    	    	Log.e(this.getClass().getCanonicalName(), "createDataBase() "+e.getMessage());
    	    }
    	} else {
    		
    	    try {
    	    	oDatabase = oDbHelper.getReadableDatabase();	//accede una prima volta al file (ancora inesistente)	//copyDataBase();
    	    	createDataBase();
    	    } catch (Exception e) {
    	    	Log.e(this.getClass().getCanonicalName(), "createDataBase() "+e.getMessage());
    	    }
    	}    	    	    		
		//oDatabase=oDbHelper.getWritableDatabase();
		////Log.v(this.getClass().getCanonicalName(), "open(): "+oDatabase.isOpen());
    	
    }

    public void close() {
    	if(oDatabase!=null) oDatabase.close();
    }


    public Cursor fetchAll (String table,String sSelection, String sOrderBy) {
    	try{
    		return oDatabase.query(table, null,sSelection,null,null,null,sOrderBy);
    	}catch (SQLiteException e) {
			return null;
		}
    	
    	               
    }
    public Cursor rawQuery(String sSQL, String[] sSelArgs){
    	if(oDatabase!=null)
    		return oDatabase.rawQuery(sSQL, sSelArgs);
    	else
    		return null;
    }
    /**
     * Crea il DataBase per l'applicativo
     * 
     * **/
    public boolean createDataBase(){
    	try {        	        		        		
    		oDatabase.execSQL("CREATE TABLE trainer_version (" +
    				" 	id_ver         INTEGER         PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
    				" 	version_number VARCHAR( 5 )    NOT NULL, " + 
    				" 	version_desc   VARCHAR( 255 )  NOT NULL, " + 
    				" 	licence        VARCHAR( 1 )    NOT NULL DEFAULT 'N')");

    		oDatabase.execSQL("INSERT INTO trainer_version (id_ver, version_number, version_desc, licence) VALUES (1, '2.6', 'Ver. 2.6', 'N')");


    		oDatabase.execSQL("CREATE TABLE trainer_users (" +
    				" 	id_users      INTEGER        PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
    				" 	name          VARCHAR( 45 )  NOT NULL, " + 
    				" 	nick          VARCHAR( 45 )  NULL, " + 
    				" 	enabled       BOOL           NULL DEFAULT 1, " + 
    				" 	deleted       BOOL           NULL DEFAULT 0, " + 
    				" 	creation_date DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP, " + 
    				" 	age           INTEGER        NOT NULL DEFAULT 0, " + 
    				" 	weight        INTEGER        NOT NULL DEFAULT 0, " + 
    				" 	gender        VARCHAR( 1 )   NOT NULL DEFAULT 'M', " + 
    				" 	facebook      BOOL           NULL DEFAULT 0, " + 
    				" 	buzz          BOOL           NULL DEFAULT 0, " + 
    				" 	twitter       BOOL           NULL DEFAULT 0, " + 
    				" 	height        INT            DEFAULT 0)");

    		oDatabase.execSQL("CREATE TABLE trainer_type_exercise (" +
    				" 	id_type_exercise INTEGER         PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
    				" 	DESC           VARCHAR( 45 )   NOT NULL, " + 
    				" 	long_desc        VARCHAR( 255 )  NOT NULL)");
    		oDatabase.execSQL("INSERT INTO trainer_type_exercise (id_type_exercise, DESC, long_desc) VALUES (0, 'Bicicletta', 'Bicicletta')");
    		oDatabase.execSQL("INSERT INTO trainer_type_exercise (id_type_exercise, DESC, long_desc) VALUES (1, 'sci', 'sci')");
    		oDatabase.execSQL("INSERT INTO trainer_type_exercise (id_type_exercise, DESC, long_desc) VALUES (2, 'Corsa', 'Corsa')");

    		oDatabase.execSQL("CREATE TABLE trainer_exercise (" +
    				" 	id_exercise      INTEGER          PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
    				" 	id_users         INTEGER          NOT NULL, " + 
    				" 	id_type_exercise INTEGER          NOT NULL, " + 
    				" 	creation_date    DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP, " + 
    				" 	start_date       DATETIME         NULL DEFAULT CURRENT_TIMESTAMP, " + 
    				" 	end_date         DATETIME         NULL, " + 
    				" 	note             VARCHAR( 2000 )  NULL, " + 
    				" 	calorie_burn     VARCHAR( 50 )    NULL, " + 
    				" 	distance         DOUBLE           NULL, " + 
    				" 	avg_speed        DOUBLE           NULL, " + 
    				" 	total_time       VARCHAR( 50 )    NULL, " + 
    				" 	steps_count      INTEGER          NOT NULL DEFAULT 0, " + 
    				" 	weight           DOUBLE           NOT NULL DEFAULT 0, " + 
    				" 	bmi              DOUBLE           DEFAULT 0, " + 
    				" 	kalories         DOUBLE           DEFAULT 0)");

    		oDatabase.execSQL("CREATE TABLE trainer_exercise_dett (" +
    				" 	id_watch_point        INTEGER         PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
    				" 	id_exercise           INTEGER         NOT NULL, " + 
    				" 	id_type_exercise      INTEGER         NOT NULL, " + 
    				" 	id_users              INTEGER         NOT NULL, " + 
    				" 	long                  DOUBLE          NOT NULL, " + 
    				" 	lat                   DOUBLE          NOT NULL, " + 
    				" 	alt                   DOUBLE          NOT NULL, " + 
    				" 	distance              DOUBLE          NOT NULL  DEFAULT 0, " + 
    				" 	watch_point_name      VARCHAR( 255 ), " + 
    				" 	watch_point_date      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP, " + 
    				" 	watch_point_date_prev DATETIME        DEFAULT CURRENT_TIMESTAMP, " + 
    				" 	watch_point_song      VARCHAR( 300 ), " + 
    				" 	speed                 DOUBLE          DEFAULT 0, " + 
    				" 	accurancy             DOUBLE          DEFAULT 0, " + 
    				" 	gpstime               DOUBLE          DEFAULT 0, " + 
    				" 	bpm                   INT             DEFAULT 0)");

    		oDatabase.execSQL("CREATE TABLE trainer_pref (" +
    				" 	id_pref  INTEGER         PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
    				" 	type     VARCHAR( 1 )    NOT NULL, " + 
    				" 	name     VARCHAR( 45 )   NOT NULL, " + 
    				" 	desc   VARCHAR( 255 )  NOT NULL, " + 
    				" 	value    VARCHAR( 255 )  NOT NULL, " + 
    				" 	order_id INT             DEFAULT 0, " + 
    				" 	visible  INT             DEFAULT 1)");

    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (1, 'B', '"+oContext.getString(R.string.show_map).replaceAll("'", "''")+"', '"+oContext.getString(R.string.show_map_desc).replaceAll("'", "''")+"', 1, 1, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (2, 'B', '"+oContext.getString(R.string.show_notification).replaceAll("'", "''")+"', '"+oContext.getString(R.string.show_notification_desc).replaceAll("'", "''")+"', 1, 2, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (3, 'B', '"+oContext.getString(R.string.show_music).replaceAll("'", "''")+"', '"+oContext.getString(R.string.show_music_desc).replaceAll("'", "''")+"o', 1, 3, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (4, 'B', '"+oContext.getString(R.string.show_motivator).replaceAll("'", "''")+"', '"+oContext.getString(R.string.show_motivator_desc).replaceAll("'", "''")+"', 1, 4, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (6, 'L', '"+oContext.getString(R.string.motovatortime).replaceAll("'", "''")+"', 'motovatortime', 1, 6, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (7, 'B', '"+oContext.getString(R.string.distance).replaceAll("'", "''")+"', '"+oContext.getString(R.string.say_distance).replaceAll("'", "''")+"', 1, 7, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (8, 'B', '"+oContext.getString(R.string.time).replaceAll("'", "''")+"', '"+oContext.getString(R.string.say_time).replaceAll("'", "''")+"', 1, 8, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (9, 'B', '"+oContext.getString(R.string.kalories).replaceAll("'", "''")+"', '"+oContext.getString(R.string.say_kalories).replaceAll("'", "''")+"', 1, 9, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (10, 'B', '"+oContext.getString(R.string.pace).replaceAll("'", "''")+"', '"+oContext.getString(R.string.say_pace).replaceAll("'", "''")+"', 1, 10, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (11, 'B', '"+oContext.getString(R.string.show_autopause).replaceAll("'", "''")+"', '"+oContext.getString(R.string.show_autopause_desc).replaceAll("'", "''")+"', 1, 12, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (13, 'B', '"+oContext.getString(R.string.interactive_trainer).replaceAll("'", "''")+"', '"+oContext.getString(R.string.interactive_trainer_desc).replaceAll("'", "''")+"', 1, 14, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (14, 'L', '"+oContext.getString(R.string.show_units).replaceAll("'", "''")+"', 'units', 0, 15, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (15, 'B', '"+oContext.getString(R.string.track_weight).replaceAll("'", "''")+"', '"+oContext.getString(R.string.track_weight_desc).replaceAll("'", "''")+"', 1, 16, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (16, 'L', '"+oContext.getString(R.string.autopause_time).replaceAll("'", "''")+"', 'autopausetime', 1, 13, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (17, 'B', '"+oContext.getString(R.string.inclination).replaceAll("'", "''")+"', '"+oContext.getString(R.string.say_inclination).replaceAll("'", "''")+"', 1, 11, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (18, 'B', '"+oContext.getString(R.string.target).replaceAll("'", "''")+"', '"+oContext.getString(R.string.target_desc).replaceAll("'", "''")+"', 1, 17, 1)");
    		oDatabase.execSQL("INSERT INTO trainer_pref (id_pref, type, name, DESC, value, ORDER_ID, VISIBLE) VALUES (19, 'B', '"+oContext.getString(R.string.cardio).replaceAll("'", "''")+"', '"+oContext.getString(R.string.cardio_desc).replaceAll("'", "''")+"', 1, 18, 1)");


    		oDatabase.execSQL("CREATE TABLE trainer_social_account (" +
    				" 	id_social_account INTEGER         PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
    				" 	id_users          INTEGER         NOT NULL, " + 
    				" 	name              VARCHAR( 45 )   NULL, " + 
    				" 	DESC            VARCHAR( 255 )  NULL, " + 
    				" 	login             VARCHAR( 45 )   NULL, " + 
    				" 	password          VARCHAR( 45 )   NULL, " + 
    				" 	creation_date     DATETIME        NULL DEFAULT CURRENT_TIMESTAMP)");


    		oDatabase.execSQL("CREATE TABLE STORE_ORDERS (" +
    				" 	store_id     INTEGER         PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
    				" 	state        INTEGER         NOT NULL DEFAULT 0, " + 
    				" 	product_id   VARCHAR( 45 )   NOT NULL, " + 
    				" 	product_desc VARCHAR( 255 )  NOT NULL, " + 
    				" 	order_time   DATETIME        NULL DEFAULT CURRENT_TIMESTAMP)");
    		oDatabase.execSQL("INSERT INTO STORE_ORDERS (store_id, state, product_id, product_desc, order_time) VALUES (0, 0, 'polarcardio', 'polarcardio', '2012-04-19 06:28:31')");


    		oDatabase.execSQL("CREATE TABLE TRAINER_CONFIG (" +
    				" 	cfg_id    INTEGER        PRIMARY KEY AUTOINCREMENT NOT NULL, " + 
    				" 	cfg_desc  VARCHAR( 45 )  NOT NULL, " + 
    				" 	cfg_value INTEGER        NOT NULL DEFAULT 0, " + 
    				" 	cfg_time  DATETIME       NULL DEFAULT CURRENT_TIMESTAMP)");
    		oDatabase.execSQL("INSERT INTO TRAINER_CONFIG (cfg_id, cfg_desc, cfg_value, cfg_time) VALUES (1, 'first_boot', 1, '2012-04-19 06:28:30')");

    		oDatabase.execSQL("CREATE INDEX exercise_index ON trainer_exercise (" +
    				" 	id_exercise)");

    		oDatabase.execSQL("CREATE INDEX exercise_index_dett ON trainer_exercise_dett (" +
    				" 	id_exercise)");

    		oDatabase.execSQL("CREATE INDEX exercise_type ON trainer_exercise (" +
    				" 	id_type_exercise)");
    	
    		oDatabase.close();
    		/**DB Version**/
    		Log.i(this.getClass().getCanonicalName(), "create DB");
    		return true;
    	} catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(), "ERROR create DB");
    		return false;
    	}catch (NullPointerException e1){
    		Log.e(this.getClass().getCanonicalName(), "Null ERROR create DB");
    		return false;
    	}
    }
    
    /**
     * Passato il path dove risiede il database nel device, proviamo ad aprirlo in sola lettura.
     * Se riusciamo ad effettuare l'operazione, il datbase esiste e ritorniamo true altrimenti
     * viene lanciata un'eccezione.
     * 
     * Controlla anche la release e aggiorna la struttura se necessario
     */
    private boolean checkDataBase (){
    	
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    		//Conrollo la versione del DB
    		if(getDataBaseVersionAndLicence(checkDB)!=null){
    			//Log.v(this.getClass().getCanonicalName(),"DB Version Number "+sVersionNumber);
    			if(sVersionNumber.compareToIgnoreCase("1.2")==0){
    				Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 1.4");
    				upgradeDB1_2To1_4();				
    			}
    			if(sVersionNumber.compareToIgnoreCase("1.4")==0){
					Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 1.5");
    				upgradeDB1_4To1_5();
				}
    			if(sVersionNumber.compareToIgnoreCase("1.5")==0){
					Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 1.7");
					upgradeDB1_5To1_7();
				}
    			if(sVersionNumber.compareToIgnoreCase("1.7")==0){
					Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 1.8");
					upgradeDB1_7To1_8();
				}
    			if(sVersionNumber.compareToIgnoreCase("1.8")==0){
					Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 1.9");
					upgradeDB1_8To1_9();
				}
    			if(sVersionNumber.compareToIgnoreCase("1.9")==0){
					Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 1.9.5");
					upgradeDB1_9To1_9_5();
				}
    			if(sVersionNumber.compareToIgnoreCase("1.9.5")==0){
					Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 2.0.0");
					upgradeDB1_9To2_0();
				}
    			if(sVersionNumber.compareToIgnoreCase("2.0.0")==0){
					Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 2.2");
					upgradeDB2_0To2_2();
				}
    			if(sVersionNumber.compareToIgnoreCase("2.2")==0){
					Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 2.3");
					upgradeDB2_2To2_3();
				}
    			if(sVersionNumber.compareToIgnoreCase("2.3")==0){
					Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 2.4");
					upgradeDB2_3To2_4();
				}
    			if(sVersionNumber.compareToIgnoreCase("2.4")==0){
					Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 2.5");
					upgradeDB2_4To2_5();
				}
    			if(sVersionNumber.compareToIgnoreCase("2.5")==0){
					Log.i(this.getClass().getCanonicalName(),"Upgrade DB Version From "+sVersionNumber+" TO 2.6");
					upgradeDB2_5To2_6();
				}
    			
    			//manualDB();
    		}
    		
    	}catch(SQLiteException e){
 
    		//TODO il database non esiste createDataBase()
    		createDataBase();
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
    
    /**
     * ONE SHOOT SCRIPT FOR UPGRADE DB VERSION
     * 
     * 
		DELETE FROM TRAINER_PREF WHERE id_pref=5
		
		alter table TRAINER_PREF ADD COLUMN ORDER_ID INT DEFAULT 0
		
		alter table TRAINER_PREF ADD COLUMN VISIBLE INT DEFAULT 1
		
		UPDATE TRAINER_PREF SET id_pref=15 WHERE id_pref=14
		
		UPDATE TRAINER_PREF SET id_pref=14 WHERE id_pref=13
		
		UPDATE TRAINER_PREF SET id_pref=13 WHERE id_pref=12
		
		UPDATE TRAINER_PREF SET ORDER_ID=id_pref
		
		insert INTO TRAINER_PREF (type,name,desc,value,order_id) VALUES ('L','Tempo','autopausetime',1,12)				
		
		UPDATE TRAINER_VERSION SET version_number='1.4',version_desc='Ver. 1.4'
     * 
     * */
    private void upgradeDB1_2To1_4() {
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
		//Step 1
    	oDB.execSQL("DELETE FROM TRAINER_PREF WHERE id_pref=5");
    	//Step 1
    	oDB.execSQL("alter table TRAINER_PREF ADD COLUMN ORDER_ID INT DEFAULT 0");
    	//Step 1
    	oDB.execSQL("alter table TRAINER_PREF ADD COLUMN VISIBLE INT DEFAULT 1");
    	//Step 1
    	oDB.execSQL("UPDATE TRAINER_PREF SET id_pref=15 WHERE id_pref=14");
    	//Step 1
    	oDB.execSQL("UPDATE TRAINER_PREF SET id_pref=14 WHERE id_pref=13");
    	//Step 1
    	oDB.execSQL("UPDATE TRAINER_PREF SET id_pref=13 WHERE id_pref=12");
    	//Step 1
    	oDB.execSQL("UPDATE TRAINER_PREF SET ORDER_ID=id_pref");
    	//Step 1
    	oDB.execSQL("INSERT INTO "
                    + TrainerPref.tablename
                    + " ("+TrainerPref.key_type+", "+TrainerPref.key_name+", "+TrainerPref.key_desc+", "+TrainerPref.key_value+",order_id)"
                    + " VALUES ('L', '"+oContext.getString(R.string.autopause_time).replaceAll("'", "''")+"', 'autopausetime', '1','12');");
    		
    	//Step 1
    	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='1.4',version_desc='Ver. 1.4'");
    	oDB.close();   	
	}
    
    private void upgradeDB1_4To1_5() {
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{
	    	//Step 1
	    	oDB.execSQL("update TRAINER_PREF set ORDER_ID=ORDER_ID+1 where id_pref>10");
	    	
	    	//Step 2
	    	oDB.execSQL("INSERT INTO "
	                    + TrainerPref.tablename
	                    + " ("+TrainerPref.key_type+", "+TrainerPref.key_name+", "+TrainerPref.key_desc+", "+TrainerPref.key_value+",order_id,visible)"
	                    + " VALUES ('B', '"+oContext.getString(R.string.inclination).replaceAll("'", "''")+"', '"+oContext.getString(R.string.say_inclination).replaceAll("'", "''")+"', '1','11','1');");
	    		
	    	//Step 1
	    	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='1.5',version_desc='Ver. 1.5'");
    	}catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(),"Error upgrade to 1.7");
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null; 	
    	
    }
   
    /**
     * Aggiorna la strittura del DB alla release 2.0 con in app store e tabella allenamenti programmati.
     * 
     * 
     * */
    private void upgradeDB1_5To1_7(){
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{
	    	oDB.execSQL("CREATE TABLE STORE_ORDERS (" +
	    			"store_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL," +
	    			"state INTEGER NOT NULL DEFAULT 0," +
	    			"product_id VARCHAR(45) NOT NULL, " +
	    			"product_desc VARCHAR(255) NOT NULL," +
	    			"order_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP)");
	    	
	    	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='1.7',version_desc='Ver. 1.7'");
    	
    	}catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(),"Error upgrade to 1.7");
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null;
    }
    
    /**
     * Aggiorna la strittura del DB alla release 2.0 con in app store e tabella allenamenti programmati.
     * 
     * 
     * */
    private void upgradeDB1_7To1_8(){
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{
    		
        	
        	/*oDB.execSQL("DROP TABLE TRAINER_CONFIG");
        	
        	oDB.execSQL("DELETE FROM TRAINER_PREF where name='"+oContext.getString(R.string.target).replaceAll("'", "''")+"'");
        	
        	
        	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='1.7',version_desc='Ver. 1.7'");
        	*/
        	
    		oDB.execSQL("CREATE TABLE TRAINER_CONFIG (" +
        			"cfg_id INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL," +
        			"cfg_desc VARCHAR(45) NOT NULL," +
        			"cfg_value INTEGER NOT NULL DEFAULT 0, " +
        			"cfg_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP)");
        
        	
        	oDB.execSQL("INSERT INTO TRAINER_CONFIG"
                    + " (cfg_desc, cfg_value)"
                    + " VALUES ('first_boot', 1)");
        	
        	
        	oDB.execSQL("insert into TRAINER_PREF ( type, name, desc,  value, ORDER_ID, VISIBLE) " + 
    		    	" values " +
    		    	" ('B','"+oContext.getString(R.string.target).replaceAll("'", "''")+"','"+oContext.getString(R.string.target_desc).replaceAll("'", "''")+"',1,17,1)");
        	
        	oDB.execSQL("alter table TRAINER_EXERCISE_DETT add column speed double default 0;");

        	oDB.execSQL("alter table TRAINER_EXERCISE_DETT add column accurancy double default 0;");

        	oDB.execSQL("alter table TRAINER_EXERCISE_DETT add column gpstime double default 0;");
        	
        	oDB.execSQL("update TRAINER_EXERCISE_DETT set speed=round(((distance*1000)/abs(((strftime('%S',watch_point_date)-strftime('%S',watch_point_date_prev))))),10)"); 
        	
        	
        	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='1.8',version_desc='Ver. 1.8'");
    	}catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(),"Error upgrade to 1.8");
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null;
    }
    
    /**
     * Aggiorna la strittura del DB alla release 2.0 con in app store e tabella allenamenti programmati.
     * 
     * 
     * */
    private void upgradeDB1_8To1_9(){
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{
    		oDB.execSQL("alter table TRAINER_EXERCISE add column bmi double default 0;");
    		
        	oDB.execSQL("alter table trainer_users add column height int default 0;");
        	
        	oDB.execSQL("UPDATE TRAINER_CONFIG"
                    + " set cfg_value=1 WHERE "
                    + " cfg_desc='first_boot'");
        	
        	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='1.9',version_desc='Ver. 1.9'");
    	}catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(),"Error upgrade to 1.9");
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null;
    }
    
    /**
     * Aggiorna la strittura del DB alla release 2.0 con in app store e tabella allenamenti programmati.
     * 
     * 
     * */
    private void upgradeDB1_9To1_9_5(){
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{
    		       	
        	oDB.execSQL("UPDATE TRAINER_CONFIG"
                    + " set cfg_value=1 WHERE "
                    + " cfg_desc='first_boot'");
        	
        	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='1.9.5',version_desc='Ver. 1.9.5'");
    	}catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(),"Error upgrade to 1.9.5");
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null;
    }
    /**
     * Aggiorna la strittura del DB alla release 2.0 con in app store e tabella allenamenti programmati.
     * 
     * 
     * */
    private void upgradeDB1_9To2_0(){
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{   		
    		
    		oDB.execSQL("alter table TRAINER_EXERCISE add column kalories DOUBLE default 0;");
    		
    		oDB.execSQL("update trainer_exercise set kalories=CAST(replace(calorie_burn,',','.') as double)");    		
    		
    		oDB.execSQL("alter table TRAINER_EXERCISE_DETT add column bpm INT default 0;");
    		
    		oDB.execSQL("insert into TRAINER_PREF ( type, name, desc,  value, ORDER_ID, VISIBLE) " + 
    		    	" values " +
    		    	" ('B','"+oContext.getString(R.string.cardio).replaceAll("'", "''")+"','"+oContext.getString(R.string.cardio_desc).replaceAll("'", "''")+"',1,18,1)");
        
    		
    		oDB.execSQL("CREATE INDEX exercise_index_dett ON "+TrainerExerciseDett.tablename+" ("+TrainerExerciseDett.key_id_exercise+")");
    		       	
    		oDB.execSQL("CREATE INDEX exercise_type ON trainer_exercise ( id_type_exercise )");
    		
        	oDB.execSQL("UPDATE TRAINER_CONFIG"
                    + " set cfg_value=1 WHERE "
                    + " cfg_desc='first_boot'");
        	
        	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='2.0.0',version_desc='Ver. 2.0.0'");
    	}catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(),"Error upgrade to 2.0.0");
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null;
    }
    /**
     * Aggiorna la strittura del DB alla release 2.0 con in app store e tabella allenamenti programmati.
     * 
     * 
     * */
    private void upgradeDB2_0To2_2(){
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{   		
    		oDB.execSQL("INSERT INTO STORE_ORDERS (store_id, state, product_id, product_desc) VALUES ('0','0','polarcardio','polarcardio')");		
        	
    		oDB.execSQL("UPDATE TRAINER_CONFIG"
                    + " set cfg_value=1 WHERE "
                    + " cfg_desc='first_boot'");
        	
        	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='2.2',version_desc='Ver. 2.2'");
    	}catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(),"Error upgrade to 2.2");
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null;
    }
    
    /**
     * Aggiorna la strittura del DB alla release 2.0 con in app store e tabella allenamenti programmati.
     * 
     * 
     * */
    private void upgradeDB2_2To2_3(){
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{   		
    		oDB.execSQL("update trainer_exercise_dett set distance=distance/0.88 where watch_point_date<'2012-05-01'");		
        	
    		oDB.execSQL("UPDATE TRAINER_CONFIG"
                    + " set cfg_value=1 WHERE "
                    + " cfg_desc='first_boot'");
        	
        	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='2.3',version_desc='Ver. 2.3'");
    	}catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(),"Error upgrade to 2.3");
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null;
    }
    
    /**
     * Aggiorna la strittura del DB alla release 2.0 con in app store e tabella allenamenti programmati.
     * 
     * 
     * */
    private void upgradeDB2_3To2_4(){
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{   		
    		
    		oDB.execSQL("UPDATE TRAINER_CONFIG"
                    + " set cfg_value=1 WHERE "
                    + " cfg_desc='first_boot'");
        	
        	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='2.4',version_desc='Ver. 2.4'");
    	}catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(),"Error upgrade to 2.3");
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null;
    }
    
    /**
     * Aggiorna la strittura del DB alla release 2.0 con in app store e tabella allenamenti programmati.
     * 
     * 
     * */
    private void upgradeDB2_4To2_5(){
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{   		
    		oDB.execSQL("update trainer_exercise_dett set distance=distance*0.86");
    		
    		oDB.execSQL("update trainer_exercise set distance=distance*0.86");
    		
    		
    		oDB.execSQL("UPDATE TRAINER_CONFIG"
                    + " set cfg_value=1 WHERE "
                    + " cfg_desc='first_boot'");
        	
        	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='2.5',version_desc='Ver. 2.5'");
    	}catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(),"Error upgrade to 2.4");
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null;
    }
    /**
     * Aggiorna la strittura del DB alla release 2.0 con in app store e tabella allenamenti programmati.
     * 
     * 
     * */
    private void upgradeDB2_5To2_6(){
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{   		    	
    		oDB.execSQL("UPDATE TRAINER_CONFIG"
                    + " set cfg_value=1 WHERE "
                    + " cfg_desc='first_boot'");
    		
        	oDB.execSQL("UPDATE TRAINER_VERSION SET version_number='2.6',version_desc='Ver. 2.6'");
    	}catch (SQLException e) {
    		Log.e(this.getClass().getCanonicalName(),"Error upgrade to 2.4");
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null;
    }
    /**
     * Ony for DEVELOPMENT
     * 
     * **/
    private void manualDB(){
    	SQLiteDatabase oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
    	try{   		
    		
    		oDB.execSQL("INSERT INTO STORE_ORDERS (store_id, state, product_id, product_desc,order_time) VALUES ('100','ok','polarcardio','polar','')");

    			
    		/*oDB.execSQL("insert into TRAINER_PREF ( type, name, desc,  value, ORDER_ID, VISIBLE) " + 
    		    	" values " +
    		    	" ('B','"+oContext.getString(R.string.cardio).replaceAll("'", "''")+"','"+oContext.getString(R.string.cardio_desc).replaceAll("'", "''")+"',1,18,1)");
           */	
    	}catch (SQLException e) {
    		oDB.close();   
        	oDB=null;
        	return;
		}   	
    	oDB.close();   
    	oDB=null;
    }
    
    /**
     * Ritorna la versione del DB edell'applicazione
     * 
     * */
    public String getDataBaseVersionAndLicence(SQLiteDatabase oDBase) {
		String sSQL ="SELECT "+TrainerVersion.key_version+", "
				+TrainerVersion.key_version_desc+", "+TrainerVersion.licence+" from "+TrainerVersion.tablename;
		Cursor oCursor;
		SQLiteDatabase oDB=null;
		if(oDBase==null){
			oDB=SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
			oCursor = oDB.rawQuery(sSQL,null);
		}else{
			oCursor = oDBase.rawQuery(sSQL,null);
		}
		
		if(oCursor!=null){      				   		
   			int iTrainerVersionNumber 	= oCursor.getColumnIndex(TrainerVersion.key_version);
   			int iTrainerVersionDesc 	= oCursor.getColumnIndex(TrainerVersion.key_version_desc);
   			int iTrainerLicence			= oCursor.getColumnIndex(TrainerVersion.licence);
   			
	   		while(oCursor.moveToNext()){ 
	   			sVersionNumber=oCursor.getString(iTrainerVersionNumber);
	   			sVersionDesc=oCursor.getString(iTrainerVersionDesc);
	   			if(oCursor.getString(iTrainerLicence).compareToIgnoreCase("S")==0){
	   				bLicence=true;	   				
	   			}else{
	   				bLicence=false;
	   			}
	   		}
	   		oCursor.close();
		}
		if(oDBase==null){
			if(oDB!=null )oDB.close();
		}else{
			oDBase.close();
		}
				
		return sVersionNumber;
	}

	public void openDataBase() throws SQLException{
    	 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
        oDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
     }
    /**
     * Restituisce l'oggetto DQLiteDataBase
     * */
    public SQLiteDatabase getOpenedDatabase () {
    	return oDatabase;
    }
 
    
    

    /**
     * Helper di upgrade
     * @author norick
     *
     */
    private class DbHelper extends SQLiteOpenHelper {

	public DbHelper(Context context, String name, CursorFactory factory,int version) {
	    super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase _db) {
	    Log.i(this.getClass().getCanonicalName(),"Database.onCreate()");
	    
	    //_db.execSQL(PRODUCTS_TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
	}

    }



	public synchronized boolean isbLicence() {
		return bLicence;
	}
	
	/**
	 * METODO PRINCIPALE CHE VERIFICA SE ESISTE IL DB LO CREA E NE AGGIORNA LA 
	 * STRUTTURA.
	 * 
	 * VIENE CHIAMATO AD OGNI AVVIO DELL'APP DALL'ACTIVITY PRICIPALE.
	 * */
	public synchronized boolean init(){
		try{
			File oDBFile = new File(DB_PATH+DB_NAME);
			//Verifico se il DB Esiste
			if(oDBFile.exists()){
				Log.i(this.getClass().getCanonicalName(),"DB Exist UPGRADE PROCESS");
				return checkDataBase();
			}else{
				if(oDbHelper!=null){
					oDbHelper.close();
				}
				oDatabase = oDbHelper.getReadableDatabase();
				if(createDataBase()){
					return checkDataBase();
				}else{
					return false;
				}
			}	
		}catch(SQLException e){
			Log.e(this.getClass().getCanonicalName(),"Error init DB");
			if(oDatabase!=null){
				if(oDatabase.isOpen()) oDatabase.close();
			}
			return false;
		}catch (Exception e) {
			Log.e(this.getClass().getCanonicalName(),"Error init DB");
			if(oDatabase!=null){
				if(oDatabase.isOpen()) oDatabase.close();
			}
			return false;
		}			
	}
}
