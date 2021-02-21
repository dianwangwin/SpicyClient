package info.spicyclient.autoUpdater;

import java.io.File;

/**
 * @author xTrM_
 */
public class Referances {

//	CHANGE THESE VARS:
	public static String ClientName = "SpicyClient";
	public static String JarLink = "https://spicyclient.info/Downloads/Jars/SpicyClient.jar";
	
	
//	DONT CHANGE THESE VARS!!!!!
	public static final String APPDATA = System.getenv("APPDATA");
	
	public static final String MINECRAFT_FOLDER = APPDATA + "\\" + ".minecraft";
	public static final String VERSIONS_FOLDER = new File("versions").getAbsolutePath();	

	public static final String CLIENT_FOLDER = VERSIONS_FOLDER + "\\" + ClientName;
	
	public static final String JSON_FILE = Referances.CLIENT_FOLDER + "\\" + Referances.ClientName + ".json";
	public static final String JSON_LINK = "http://spicyclient.info/api/client_json"; // Used to be https://pastebin.com/raw/DhUyKWta
	
	public static final String JAR_FILE = Referances.CLIENT_FOLDER + "\\" + Referances.ClientName + ".jar";

}
