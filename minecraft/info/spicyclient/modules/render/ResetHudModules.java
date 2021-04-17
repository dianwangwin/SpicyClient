package info.spicyclient.modules.render;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.hudModules.HudModule;
import info.spicyclient.hudModules.HudModule.HudModuleConfig;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;

public class ResetHudModules extends Module {

	public ResetHudModules() {
		super("ResetHudModules", Keyboard.KEY_NONE, Category.RENDER);
	}
	
	@Override
	public void onEnable() {
		toggle();
	}
	
	@Override
	public void onDisable() {
    	HudModule.mods.clear();
    	SpicyClient.config.hudModConfig = new HudModuleConfig();
    	NotificationManager.getNotificationManager().createNotification("Hud Modules", "Reset hud modules", true, 5000, Type.INFO, Color.BLUE);
	}
	
}
