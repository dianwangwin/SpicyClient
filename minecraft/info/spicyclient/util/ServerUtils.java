package info.spicyclient.util;

import net.minecraft.client.Minecraft;

public class ServerUtils {
	
	public static boolean isOnHypixel() {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		if (!mc.isSingleplayer() && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel.net")) {
			return true;
		}
		
		return false;
		
	}
	
}
