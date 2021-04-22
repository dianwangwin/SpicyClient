package info.spicyclient.modules.player;

import org.lwjgl.input.Keyboard;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.spicyMessageClient.Messenger;

public class IrcChat extends Module {

	public IrcChat() {
		super("IRC", Keyboard.KEY_NONE, Category.PLAYER);
	}
	
	public static transient boolean autoEnable = false;
	public static transient Messenger messenger = new Messenger();
	
	@Override
	public void onEnable() {
		
		if (messenger.isConnected()) {
			try {
				messenger.closeConnection();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		messenger = new Messenger();
		
		new Thread("Connection Thread") {
			public void run() {
				try {
					messenger.openConnection("167.114.36.140", 6969);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		
	}
	
	@Override
	public void onDisable() {
		try {
			messenger.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre() && mc.thePlayer.ticksExisted % 40 == 0) {
			if (messenger == null || (!messenger.isConnected())) {
				NotificationManager.getNotificationManager().createNotification("IRC", "You were disconnected from the server", true, 20000, Type.WARNING, Color.RED);
				toggle();
			}
		}
		
	}
	
	@Override
	public void onEventWhenDisabled(Event e) {
		
		if (e instanceof EventUpdate && e.isPre() && !autoEnable) {
			toggle();
			autoEnable = true;
		}
		
	}
	
}
