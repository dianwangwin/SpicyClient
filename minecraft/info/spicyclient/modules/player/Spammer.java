package info.spicyclient.modules.player;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.util.Timer;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class Spammer extends Module {
	
	public Spammer() {
		super("Spammer", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	public NumberSetting delay = new NumberSetting("Delay", 3100, 100, 10000, 100);
	public String message = "%r%r%r%r%r%r%r%rSpicyClient%r.%rinfo";
	public static transient Timer timer = new Timer();
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(delay);
	}
	
	@Override
	public void onEnable() {
		timer.reset();
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			if (timer.hasTimeElapsed((long) delay.getValue(), true)) {
				
				String message = this.message;
				
				while (!message.equalsIgnoreCase(message.replaceAll("%r", "-"))) {
					
					message = message.replaceFirst("%r", getBypassString());
					
				}
				
				while (!message.equalsIgnoreCase(message.replaceAll("%R", "-"))) {
					
					message = message.replaceFirst("%R", getBypassString());
					
				}
				
				mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C01PacketChatMessage(message));
				
			}
			
		}
		
	}
	
	public String getBypassString() {
		
		String bypass = "⛍⛗⛌⛗⛘⛉⛡⛍⛗⛉⛍⛘⛜⛍⛠⛘⛟⛏⛡⛏⛗⛏⛍⛉⛋׼⛑⛒⛓⛔⛕⛖";
		return String.valueOf(bypass.toCharArray()[new Random().nextInt(bypass.length())]);
		
	}
	
}
