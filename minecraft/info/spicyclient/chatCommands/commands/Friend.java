package info.spicyclient.chatCommands.commands;

import java.util.concurrent.CopyOnWriteArrayList;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

public class Friend extends Command {
	
	public Friend() {
		super("friend", "friend <add/remove/list> <name>", 1);
	}
	
	public static transient CopyOnWriteArrayList<String> friends = new CopyOnWriteArrayList<String>();
	
	@Override
	public void commandAction(String message) {
		
		String[] splitMessage = message.split(" ");
		String friendName = "";;
		for (short i = 0; i < splitMessage.length; i++) {
			if (i >= 2) {
				friendName += splitMessage[i] + " ";
			}
		}
		
		if (friendName != "") {
			friendName = friendName.replaceFirst(".friend ", "");
			friendName = friendName.substring(0, friendName.length() - 1);
		}
		
		friendName = friendName.toLowerCase();
		
		if (splitMessage[1].equalsIgnoreCase("add") && friendName != "") {
			
			if (!friends.contains(friendName)) {
				friends.add(friendName);
				NotificationManager.getNotificationManager().createNotification("Friends", "Successfully added " + friendName + " to your friends list", true, 5000, Type.INFO, Color.GREEN);
			}else {
				NotificationManager.getNotificationManager().createNotification("Friends", friendName + " is already on your friends list", true, 5000, Type.INFO, Color.RED);
			}
			
		}
		else if (splitMessage[1].equalsIgnoreCase("remove") && friendName != "") {
			
			if (friends.contains(friendName)) {
				friends.remove(friendName);
				NotificationManager.getNotificationManager().createNotification("Friends", "Successfully removed " + friendName + " from your friends list", true, 5000, Type.INFO, Color.GREEN);
			}else {
				NotificationManager.getNotificationManager().createNotification("Friends", friendName + " is not on your friends list", true, 5000, Type.INFO, Color.RED);
			}
			
		}
		else if (splitMessage[1].equalsIgnoreCase("list")) {
			
			ChatStyle style = new ChatStyle();
			
			for (String name : friends) {
				
				style.setChatHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ChatComponentText("Click to remove " + name + " from your friends list")));
		    	style.setChatClickEvent(new ClickEvent(net.minecraft.event.ClickEvent.Action.RUN_COMMAND, ".friend remove " + name));
		    	Command.sendPrivateChatMessage(SpicyClient.config.clientName + SpicyClient.config.clientVersion, false, " - " + name, style);
				
			}
			
			Command.sendPrivateChatMessage("You have " + friends.size() + " people on your friends list, the list will wipe next time you start you game");
			
		}else {
			incorrectParameters();
		}
		
	}
	
	@Override
	public void incorrectParameters() {
		sendPrivateChatMessage("Please use .friend <add/remove/list> <name>");
	}
	
}
