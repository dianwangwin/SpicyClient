package spicy.modules.player;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import spicy.events.Event;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.ModeSetting;

public class HideName extends Module {
	
	public ModeSetting mode = new ModeSetting("Name", "You", "You", "MooshroomMashUp", "Fox_of_floof", "lavaflowglow", "_Floofy_Fox_");
	
	public HideName() {
		super("HideName", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(mode);
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			this.additionalInformation = mode.getMode();
		}
		
		if (e instanceof EventPacket && e.isPre()) {
			
			EventPacket packetEvent = (EventPacket) e;
			if (packetEvent.packet instanceof S02PacketChat) {
				
				S02PacketChat packet = (S02PacketChat) packetEvent.packet;
				
				if (packet.getChatComponent().getUnformattedText().replaceAll("׼", "").contains(mc.getSession().getUsername())) {
					mc.thePlayer.addChatComponentMessage(new ChatComponentText(packet.getChatComponent().getFormattedText().replaceAll("׼", "").replaceAll(mc.getSession().getUsername(), mode.getMode())));
					e.setCanceled(true);
				}
				
			}
			
		}
		
	}
	
}
