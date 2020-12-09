package info.spicyclient.autoUpdater;

import info.spicyclient.files.FileManager;

/**
 * @author xTrM_
 */
public class Referances {

	//	CHANGE THESE VARS:
	public static String ClientName = "Updater";
	public static String JarLink = "http://spicyclient.info/Downloads/Jars/Updater.jar";
	
	
	//	DONT CHANGE THESE VARS!!!!!
	public static final String APPDATA = System.getenv("APPDATA");
	
	public static final String MINECRAFT_FOLDER = APPDATA + "\\" + ".minecraft";
	public static final String VERSIONS_FOLDER = MINECRAFT_FOLDER + "\\" + "SpicyClient_V1";	

	public static final String CLIENT_FOLDER = VERSIONS_FOLDER;
	
	//public static final String JSON_FILE = Referances.CLIENT_FOLDER + "\\" + Referances.ClientName + ".json";
	//public static final String JSON_LINK = "https://pastebin.com/raw/DhUyKWta"; // DO NOT TOUCH THIS! It will automaticly update itself with the CLIENT_NAME var
	
	public static final String JAR_FILE = FileManager.assets.getAbsolutePath() + "\\Updater.jar";

}
