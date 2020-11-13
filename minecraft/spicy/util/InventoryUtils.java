package spicy.util;

import net.minecraft.client.Minecraft;

public class InventoryUtils {
	
    public static void shiftClick(int slot){
    	Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot, 0, 1, Minecraft.getMinecraft().thePlayer);
    }

    public static void drop(int slot){
    	Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, slot, 1, 4, Minecraft.getMinecraft().thePlayer);
    }
	
}
