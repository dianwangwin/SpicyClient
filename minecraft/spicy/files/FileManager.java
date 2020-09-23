package spicy.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;

import com.google.gson.Gson;

import net.minecraft.client.Minecraft;
import spicy.SpicyClient;
import spicy.modules.Module;

/*
 * THIS IS NOT MY CODE, I FOUND IT ON A TUTORIAL VIDEO ON YOUTUBE
 */

public class FileManager {
	
	private static Gson gson = new Gson();
	
	public static File ROOT_DIR = new File("SpicyClient_V1");
	public static File configs = new File(ROOT_DIR, "configs");
	public static File skins = new File(ROOT_DIR, "skins");
	
	public static void init() {
		
		if (!ROOT_DIR.exists()) {
			
			ROOT_DIR.mkdirs();
			
		}
		if (!configs.exists()) {
			
			configs.mkdirs();
			
		}
		if (!skins.exists()) {
			
			skins.mkdirs();
			
		}
	}

	public static Gson getGson() {
		return gson;
	}

	public static File getROOT_DIR() {
		return ROOT_DIR;
	}

	public static File getConfigs() {
		return configs;
	}
	
	public static File getSkins() {
		return skins;
	}
	
	public static boolean writeJsonToFile(File file, Object obj) throws IOException {
		
		if (!file.exists()) {
			
			file.createNewFile();
			
			FileOutputStream outputStream = new FileOutputStream(file);
			outputStream.write(gson.toJson(obj).getBytes());
			outputStream.close();
			
		}
		
		FileOutputStream outputStream;
		outputStream = new FileOutputStream(file);
		outputStream.write(gson.toJson(obj).getBytes());
		outputStream.close();
		return true;
	}
	
	public static Object readFromJson(File file, Object c) throws IOException {
		
		FileInputStream fileInputStream = new FileInputStream(file);
		InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
		BufferedReader b = new BufferedReader(inputStreamReader);
		
		StringBuilder builder = new StringBuilder();
		
		String line;
		
		while ((line = b.readLine()) != null) {
			
			builder.append(line);
			
		}
		b.close();
		inputStreamReader.close();
		fileInputStream.close();
		
		return builder.toString();
		
	}
	
	public static boolean save_config(String name) throws IOException {
		
		SpicyClient.config.saveConfig();
		
		File file = new File(getROOT_DIR(), "configs");
		if (!file.exists()) {
			file.mkdirs();
		}
		
		try {
			writeJsonToFile(new File(file, name + ".SpicyClientConfig"), SpicyClient.config);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
		
	}
	
	public static boolean load_config(String name) throws IOException {
		
		Config temp = new Config("temp");
		
		File file = new File(getROOT_DIR(), "configs");
		if (!file.exists()) {
			return false;
		}
		
		try {
			
			Gson g = new Gson();
			temp = g.fromJson((String) readFromJson(new File(file, name + ".SpicyClientConfig"), temp), Config.class);
			
			temp.loadConfig();
			return true;
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public static boolean saveAltInfo(AltInfo obj) throws IOException {
		
		File file = new File(getROOT_DIR(), "");
		if (!file.exists()) {
			file.mkdirs();
		}
		
		try {
			writeJsonToFile(new File(file, "Accounts.alts"), obj);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return true;
		
	}
	
	public static Object loadAltInfo(AltInfo obj) throws IOException {
		
		File file = new File(getROOT_DIR(), "");
		if (!file.exists()) {
			return null;
		}
		
		try {
			
			Gson g = new Gson();
			AltInfo p = g.fromJson((String) readFromJson(new File(file, "Accounts.alts"), obj), AltInfo.class);
			
			return p;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
