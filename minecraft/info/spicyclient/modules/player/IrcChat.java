package info.spicyclient.modules.player;

import org.lwjgl.input.Keyboard;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;

public class IrcChat extends Module {

	public IrcChat() {
		super("IRC", Keyboard.KEY_NONE, Category.BETA);
	}
	
	public static transient boolean autoEnable = false;
	public static transient info.spicyclient.irc.IrcChat chat;
	
	@Override
	public void onEnable() {
		chat = new info.spicyclient.irc.IrcChat();
		chat.start();
	}
	
	@Override
	public void onDisable() {
		chat.quit();
	}
	
	@Override
	public void onEventWhenDisabled(Event e) {
		
		if (e instanceof EventUpdate && e.isPre() && !autoEnable) {
			//toggle();
			autoEnable = true;
		}
		
	}
	
}
