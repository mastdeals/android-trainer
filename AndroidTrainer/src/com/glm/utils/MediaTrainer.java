package com.glm.utils;

import java.io.IOException;
import java.util.Vector;

import com.glm.bean.Music;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.util.Log;

public class MediaTrainer {
	private Context oContext;
	/**
	 * Media Player
	 * */
	private MediaPlayer 	oMediaPlayer=null;
	/**Lista delle canzoni su handset*/
	private static Vector<Music> 	vListOfMusic;
	private static int 		iMusicIndex=0;
	private static String sCurrentSong=""; 
	public MediaTrainer(Context context){
		oMediaPlayer= new MediaPlayer();
		
		oContext=context;
		//Sync Music
        vListOfMusic = ExerciseUtils.listRecursiveFile(oContext);	
		iMusicIndex=(int) (Math.random()*vListOfMusic.size());
		
		this.initPlayer();
	}
	
	private void initPlayer() {
		//Log.v(this.getClass().getCanonicalName(), "#################### initPlayer Play Music");	   		
   		
   		if(vListOfMusic!=null &&
   				vListOfMusic.size()>0){
   			try {   				
   				Music oMusic=(Music) vListOfMusic.get(iMusicIndex);
   				sCurrentSong=oMusic.getsTITLE();
				oMediaPlayer.setDataSource(oMusic.getsFileDATA());
				
				//Log.v(this.getClass().getCanonicalName(),"Now Play "+iMusicIndex+": "+oMusic.getsFileDATA()+" - Dur: "+oMusic.getiDURATION());
				oMusic=null;
				oMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer oCurrentPlayer) {
						//Rimuovo la canzone dalla lista
						try{
							vListOfMusic.remove(iMusicIndex);
						} catch (ArrayIndexOutOfBoundsException e) {
							Log.w(this.getClass().getCanonicalName(),"initPlayer->ArrayIndexOutOfBoundsException: "+e.getMessage());
							//Canzoni Finite
							return;
						}
						int iCurrentMusicIndex= (int) (Math.random()*vListOfMusic.size());
						
						if(iCurrentMusicIndex<vListOfMusic.size()){
							if(iCurrentMusicIndex!=iMusicIndex){
								iMusicIndex=iCurrentMusicIndex;
							}else{
								iMusicIndex=iCurrentMusicIndex++;
							}								
						}else{
							//Restart
							iMusicIndex=0;
						}
						
						try {
							oCurrentPlayer.reset(); 
							Music oMusic=(Music) vListOfMusic.get(iMusicIndex);
							sCurrentSong=oMusic.getsTITLE();
							//Log.v(this.getClass().getCanonicalName(),"Now Play: "+oMusic.getsFileDATA()+" - Dur: "+oMusic.getiDURATION());
							oMediaPlayer.setDataSource(oMusic.getsFileDATA());
							oMusic=null;								
							oCurrentPlayer.prepare();
							oCurrentPlayer.start();
						} catch (IllegalArgumentException e) {
							Log.e(this.getClass().getCanonicalName(),"initPlayer->IllegalArgumentException: "+e.getMessage());
						} catch (IllegalStateException e) {
							Log.e(this.getClass().getCanonicalName(),"initPlayer->IllegalStateException: "+e.getMessage());
						} catch (IOException e) {
							Log.e(this.getClass().getCanonicalName(),"initPlayer->IOException: "+e.getMessage());
						} catch (ArrayIndexOutOfBoundsException e) {
							Log.e(this.getClass().getCanonicalName(),"initPlayer->ArrayIndexOutOfBoundsException: "+e.getMessage());
						}
					}
				});
			} catch (IllegalArgumentException e) {
				Log.e(this.getClass().getCanonicalName(),"initPlayer->IllegalArgumentException: "+e.getMessage());
				
			} catch (IllegalStateException e) {
				Log.e(this.getClass().getCanonicalName(),"initPlayer->IllegalStateException: "+e.getMessage());
				
			} catch (IOException e) {
				Log.e(this.getClass().getCanonicalName(),"initPlayer->IOException: "+e.getMessage());				
			}
   		}	   		
	}

	public boolean play(boolean isResume){
		try{
			if(!isResume) oMediaPlayer.prepare();
			oMediaPlayer.start();
			return true;
		}catch (Exception e) {
			Log.e(this.getClass().getCanonicalName(),"Error Playing Media");
			e.printStackTrace();
			return false;
		}
		
	}
	public boolean pause(){
		try{
			if(this.isPlaying()){
				oMediaPlayer.pause();
			}
			return true;
		}catch (Exception e) {
			Log.e(this.getClass().getCanonicalName(),"Error Playing Media");
			return false;
		}
	}
	public boolean stop(){
		try{
			if(this.isPlaying()){
				oMediaPlayer.stop();
			}
			return true;
		}catch (Exception e) {
			Log.e(this.getClass().getCanonicalName(),"Error Stopping Media");
			return false;
		}
	}
	public boolean isPlaying(){
		return oMediaPlayer.isPlaying();
	}
	/**
	 * Ritorna la traccia corrente
	 * */
	public String getCurrentSong(){
		return sCurrentSong;
	}
	public boolean destroy(){
		try{
			this.stop();
			//oMediaPlayer.release();
			return true;
		}catch (Exception e) {
			Log.e(this.getClass().getCanonicalName(),"destroy MediaPlayer Error: "+e.getMessage());
			return false;
		}
	}
	public void SkipTrack(){
		if(this.isPlaying()){
			
			try {
				oMediaPlayer.stop();
				oMediaPlayer.reset();				
				iMusicIndex=(int) (Math.random()*vListOfMusic.size());			
				this.initPlayer();
				this.play(false);
			} catch (IllegalStateException e) {
				Log.e(this.getClass().getCanonicalName(),"IllegalStateException on SkipTrack");				
			}
			
		}
	}
}
