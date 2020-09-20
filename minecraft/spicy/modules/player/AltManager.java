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
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.ui.NewAltManager;

public class AltManager extends Module {
	
	public AltManager() {
		super("Alt Manager", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	@Override
	public void toggle() {
		toggled = !toggled;
		if (toggled) {
			onEnable();
		}else {
			onDisable();
		}
	}
	
	public void onEnable() {
		mc.displayGuiScreen(new NewAltManager(null));
		this.toggle();
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
	}
	
}
