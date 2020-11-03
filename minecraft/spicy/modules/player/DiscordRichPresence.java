package spicy.modules.player;

import org.lwjgl.input.Keyboard;

import spicy.DiscordRP;
import spicy.SpicyClient;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.SettingChangeEvent;

public class DiscordRichPresence extends Module {

	public DiscordRichPresence() {
		super("Discord Rich Presence", Keyboard.KEY_NONE, Category.PLAYER);
		this.toggled = true;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onEnable() {
		
	}
	
	@Override
	public void onDisable() {
		if (SpicyClient.discord != null) {
			SpicyClient.discord.shutdown();
		}
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre() && SpicyClient.discord != null && !SpicyClient.discord.running) {
			
			SpicyClient.discord.start();
			SpicyClient.discord.running = true;
			SpicyClient.discord.refresh();
			
		}
		
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
		if (SpicyClient.discord != null && SpicyClient.discord.running) {
			
			SpicyClient.discord.refresh();
			
		}
		
	}
	
}
