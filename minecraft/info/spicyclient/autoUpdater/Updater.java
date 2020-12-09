package info.spicyclient.autoUpdater;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import info.spicyclient.SpicyClient;
import info.spicyclient.files.FileManager;

public class Updater {
	
	public static Updater updater;
	
	public static Updater getUpdater() {
		
		if (updater == null) {
			updater = new Updater();
		}
		
		return updater;
		
	}
	
	public boolean updaterFound = false, checkedForUpdate = false;
	
	public boolean ClientOutdated() {
		
		return false;
		
	}
	
	public void start() {
		
		if (!AutoUpdaterExists()) {
			
			updaterFound = false;
			new Thread() {
				
				@Override
				public void run() {
					
					try {
						Installer.saveFile(Referances.JarLink, Referances.JAR_FILE);
						updaterFound = true;
						start();
					} catch (IOException e){
						e.printStackTrace();
					}
					
				}
				
			}.start();
			
		}else {
			
			new Thread() {
				
				@Override
				public void run() {
					
					try {
						ProcessBuilder builder = new ProcessBuilder("cmd.exe", "dir", "cd " + FileManager.assets.getAbsolutePath(), "java -Xms128M -Xmx4096M -jar Updater.jar");
					        builder.redirectErrorStream(true);
					        builder.redirectOutput();
					        builder.redirectInput();
					        Process p = builder.start();
					        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
					        String line;
					        while (true) {
					            line = r.readLine();
					            System.out.println(line);
					        }
						
					        /*
						Process cmd = Runtime.getRuntime().exec("cd " + FileManager.assets.getAbsolutePath());
						Process proc = Runtime.getRuntime().exec("java -Xms128M -Xmx4096M -jar " + "Updater.jar");
						System.err.println("java -Xms128M -Xmx4096M -jar " + (new File(FileManager.assets, "Updater.jar").toURI().toString().replaceFirst("file:/", "")));
						*/
					} catch (IOException e) {	
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}.start();
			
		}
		
	}
	
	public boolean AutoUpdaterExists() {
		
		return new File(FileManager.assets, "Updater.jar").exists();
		
	}
	
	public void DownloadAutoUpdater() {
		
		
		
	}
	
	public int getCurrentVersion() throws Exception {
		
	     String url = "http://spicyclient.info/api/Updater.php";
	     URL obj = new URL(url);
	     HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	     // optional default is GET
	     con.setRequestMethod("GET");
	     //add request header
	     con.setRequestProperty("User-Agent", "Mozilla/5.0");
	     int responseCode = con.getResponseCode();
	     //System.out.println("\nSending 'GET' request to URL : " + url);
	     //System.out.println("Response Code : " + responseCode);
	     BufferedReader in = new BufferedReader(
	             new InputStreamReader(con.getInputStream()));
	     String inputLine;
	     StringBuffer response = new StringBuffer();
	     while ((inputLine = in.readLine()) != null) {
	     	response.append(inputLine);
	     }
	     in.close();
	     //print in String
	     //System.out.println(response.toString());
	     //Read JSON response and print
	     JSONObject myResponse = new JSONObject(response.toString());
	     
	     return myResponse.getInt("currentVersion");
	     
	}
	
}
