package spicy.modules.render;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C03PacketPlayer;
import spicy.events.Event;
import spicy.events.listeners.EventChatmessage;
import spicy.events.listeners.EventServerSettingWorldTime;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;

public class WorldTime extends Module {
	
	public NumberSetting time = new NumberSetting("Time", 1, 0, 24, 0.5);
	
	public WorldTime() {
		super("World Time", Keyboard.KEY_NONE, Category.RENDER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(time);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	
	public void onEvent(Event e) {
		
		if (e instanceof EventServerSettingWorldTime) {
			
			e.setCanceled(true);
			mc.theWorld.setWorldTime((long) (time.getValue() * 1000));
			
		}
		
	}
	
}
