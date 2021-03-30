package info.spicyclient.chatCommands.commands;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;

public class Skin extends Command {
	
	public Skin() {
		super("skin", "skin <image url>", 1);
	}
	
	@Override
	public void commandAction(String message) {
		
		String[] splitMessage = message.split(" ");
		String newSkin = "";
		
		for (String s : splitMessage) {
			newSkin += s + " ";
		}
		
		newSkin = newSkin.replaceFirst(SpicyClient.commandManager.prefix + "skin ", "");
		newSkin = newSkin.substring(0, newSkin.length() - 1);
		
		if (newSkin.startsWith("https")) {
			Command.sendPrivateChatMessage("Try changing https to http if the skin fails to change");
		}
		
		if (!SpicyClient.config.skin.mode.modes.contains(newSkin))
			SpicyClient.config.skin.mode.modes.add(newSkin);
		
		SpicyClient.config.skin.mode.index = SpicyClient.config.skin.mode.modes.indexOf(newSkin);
		SpicyClient.config.skin.currentSkin = null;
		
		if (!SpicyClient.config.skin.isEnabled())
			SpicyClient.config.skin.toggle();
		
		NotificationManager.getNotificationManager().createNotification("Skin changed", "Changed skin to " + newSkin, true, 3000, Type.INFO, Color.PINK);
		
	}
	
}
