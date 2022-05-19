package info.spicyclient.modules.player;

import org.lwjgl.input.Keyboard;

import info.spicyclient.DiscordRP;
import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.SettingChangeEvent;

public class DiscordRichPresence extends Module {

	public DiscordRichPresence() {
		super("Discord Rich Presence", Keyboard.KEY_NONE, Category.PLAYER);
		this.toggled = !SpicyClient.discordFailedToStart;
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
		
		if (e instanceof EventUpdate && e.isPre() && !SpicyClient.discordFailedToStart && SpicyClient.discord != null && !SpicyClient.discord.running) {
			
			try {
				SpicyClient.discord.start();
			} catch (Exception e1) {
				Command.sendPrivateChatMessage("Discord failed to start when you first started the client, this module is unavailable");
				toggle();
				e1.printStackTrace();
				return;
			}
			SpicyClient.discord.running = true;
			SpicyClient.discord.refresh();
			
		}
		else if (e instanceof EventUpdate && e.isPre() && SpicyClient.discordFailedToStart) {
			Command.sendPrivateChatMessage("Discord failed to start when you first started the client, this module is unavailable");
			toggle();
			return;
		}
		
	}
	
	@Override
	public void onSettingChange(SettingChangeEvent e) {
		
		try {
			
			if (SpicyClient.discord != null && SpicyClient.discord.running) {
				
				SpicyClient.discord.refresh();
				
			}
			
		} catch (ExceptionInInitializerError e2) {
			e2.printStackTrace();
		}
		
	}
	
}
