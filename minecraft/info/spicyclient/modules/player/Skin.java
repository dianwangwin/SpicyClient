package info.spicyclient.modules.player;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.settings.SettingChangeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

public class Skin extends Module {

	public Skin() {
		super("Skin", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(mode);
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
		if (e.setting == mode) {
			currentSkin = null;
		}
		
	}
	
	public ModeSetting mode = new ModeSetting("Skin", "tf2 soldier", "tf2 soldier", "lavaflowglow", "puro", "when the skin is sus");
	
	public static transient ResourceLocation currentSkin = null;
	public static transient boolean isDownloading = false;
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			if (mode.getMode().length() <= 20) {
				this.additionalInformation = mode.getMode();
			}
		}
		
	}
	
	public ResourceLocation getSkin() {
        String url = mode.getMode();
        
        if (url.equals("tf2 soldier")) {
        	url = "http://spicyclient.info/api/skins/tf2%20soldier.png";
        }
        else if (url.equals("lavaflowglow")) {
        	url = "http://spicyclient.info/api/skins/lavaflowglow.png";
        }
        else if (url.equals("puro")) {
        	url = "http://spicyclient.info/api/skins/puro.png";
        }
        else if (url.equals("when the skin is sus")) {
        	url = "http://spicyclient.info/api/skins/when%20the%20skin%20is%20sus.png";
        }
        
        if (currentSkin == null) {
        	
        	try {
				currentSkin = mc.getTextureManager().getDynamicTextureLocation(url, new DynamicTexture(ImageIO.read(new URL(url))));
			} catch (IOException e) {
				NotificationManager.getNotificationManager().createNotification("Skin", "Failed to download skin", true, 3000, Type.WARNING, Color.RED);
				this.toggle();
				e.printStackTrace();
			}
			
		}else {
			return currentSkin;
		}
        
        return new ResourceLocation("spicy/skins/wolf3.png");
        
	}
	
}
