package info.spicyclient.notifications;

public enum Type {
	
	INFO("info"),
	WARNING("warning"),
	DEBUG("debug");
	
	public String filePrefix;
	
	Type(String filePrefix) {
		
		this.filePrefix = filePrefix;
		
	}
	
}
