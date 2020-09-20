package spicy.modules.player;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.block.BlockAir;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import spicy.events.Event;
import spicy.events.listeners.EventChatmessage;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventServerSetYawAndPitch;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.ui.NewAltManager;

public class NoRotate extends Module {
	
	BooleanSetting notify = new BooleanSetting("Notify", false);
	
	public NoRotate() {
		super("NoRotate", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(notify);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventServerSetYawAndPitch) {
			
			if (e.isPre()) {
				
				EventServerSetYawAndPitch event = (EventServerSetYawAndPitch) e;
				
				event.setCanceled(true);
				event.notify = notify.enabled;
				
			}
			
		}
		
	}
	
}
