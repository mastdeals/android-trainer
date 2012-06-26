package com.glm.bean;

public class DistancePerExercise {
	/***
	 * 0=running
	 * 1=biking
	 * 100=walking
	 * 2=skiing
	 * 
	 * */
	private int iTypeExercise;
	private String sDistance;
	public synchronized int getiTypeExercise() {
		return iTypeExercise;
	}
	public synchronized void setiTypeExercise(int iTypeExercise) {
		this.iTypeExercise = iTypeExercise;
	}
	public synchronized String getsDistance() {
		return sDistance;
	}
	public synchronized void setsDistance(String sDistance) {
		this.sDistance = sDistance;
	}
}
