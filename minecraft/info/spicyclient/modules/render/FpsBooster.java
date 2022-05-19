package info.spicyclient.modules.render;

import java.text.DecimalFormat;

import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventRenderGUI;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.settings.BooleanSetting;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.Timer;

public class FpsBooster extends Module {

	public FpsBooster() {
		super("OptimizeSettings", Keyboard.KEY_NONE, Category.RENDER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(fps);
	}
	
	public NumberSetting fps = new NumberSetting("FPS", 20, 1, 120, 1);
	
	public static transient long lastFrame = System.currentTimeMillis();
	public static transient double framesPerSecond = 0;
	public static transient Timer lastChange = new Timer();
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventRenderGUI && e.isPre()) {
			framesPerSecond = 1000 / ((double)(System.currentTimeMillis() - lastFrame));
			lastFrame = System.currentTimeMillis();
		}
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			if (lastChange.hasTimeElapsed(2000, true)) {
				
				this.additionalInformation = new DecimalFormat("#.##").format(framesPerSecond) + "/" + fps.getValue() + " FPS";
				
				if (framesPerSecond <= fps.getValue()) {
					
					if (mc.gameSettings.ofFogType > 1) {
						mc.gameSettings.ofFogType = 1;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned on fast fog", true, 2000, Type.INFO, Color.PINK);
					}
					else if (mc.gameSettings.ofAnimatedExplosion) {
						mc.gameSettings.ofAnimatedExplosion = false;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned off animated explosion", true, 2000, Type.INFO, Color.PINK);
					}
					else if (mc.gameSettings.ofAnimatedFire) {
						mc.gameSettings.ofAnimatedFire = false;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned off animated fire", true, 2000, Type.INFO, Color.PINK);
					}
					else if (mc.gameSettings.ofAnimatedFlame) {
						mc.gameSettings.ofAnimatedFlame = false;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned off animated flame", true, 2000, Type.INFO, Color.PINK);
					}
					else if (mc.gameSettings.ofAnimatedPortal) {
						mc.gameSettings.ofAnimatedPortal = false;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned off animated portal", true, 2000, Type.INFO, Color.PINK);
					}
					else if (mc.gameSettings.ofAnimatedRedstone) {
						mc.gameSettings.ofAnimatedRedstone = false;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned off animated redstone", true, 2000, Type.INFO, Color.PINK);
					}
					else if (mc.gameSettings.ofAnimatedSmoke) {
						mc.gameSettings.ofAnimatedSmoke = false;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned off animated smoke", true, 2000, Type.INFO, Color.PINK);
					}
					else if (mc.gameSettings.ofAnimatedTerrain) {
						mc.gameSettings.ofAnimatedTerrain = false;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned off animated terrain", true, 2000, Type.INFO, Color.PINK);
					}
					else if (mc.gameSettings.ofAnimatedTextures) {
						mc.gameSettings.ofAnimatedTextures = false;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned off animated textures", true, 2000, Type.INFO, Color.PINK);
					}
					else if (mc.gameSettings.fancyGraphics) {
						mc.gameSettings.fancyGraphics = false;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned off fancy graphics", true, 2000, Type.INFO, Color.PINK);
					}
					else if (!mc.gameSettings.ofFastRender) {
						mc.gameSettings.ofFastRender = true;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned on fast render", true, 2000, Type.INFO, Color.PINK);
					}
					else if (!mc.gameSettings.ofSmoothFps) {
						mc.gameSettings.ofSmoothFps = true;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned on smooth fps", true, 2000, Type.INFO, Color.PINK);
					}
					else if (!mc.gameSettings.ofSmoothWorld) {
						mc.gameSettings.ofSmoothWorld = true;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned on smooth world", true, 2000, Type.INFO, Color.PINK);
					}
					else if (mc.gameSettings.renderDistanceChunks > 5) {
						mc.gameSettings.renderDistanceChunks--;
						NotificationManager.getNotificationManager().createNotification("FpsBooster",
								"Turned down render distance to " + mc.gameSettings.renderDistanceChunks, true, 2000, Type.INFO, Color.PINK);
					}
					
				}
				
			}
			
		}
		
	}
	
}
