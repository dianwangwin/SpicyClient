package info.spicyclient.events.listeners;

import info.spicyclient.events.Event;

public class EventRenderLivingLabel extends Event {
	
	public EventRenderLivingLabel(String text, double x, double y, double z, int maxDistance) {
		
		this.text = text;
		this.x = x;
		this.y = y;
		this.z = z;
		this.maxDistance = maxDistance;
		
	}
	
	public String text;
	public double x, y, z;
	public int maxDistance;
	
}
