package com.example.elevation;

public class SessionData {
	public static double ascent;
	public static double descent;
	public static double distance;
	public static double netElevation;
	
	public SessionData (double ascent, double descent, double distance, double netElevation) {
		this.ascent = ascent;
		this.descent = descent;
		this.distance = distance;
		this.netElevation = netElevation;
	}
}
