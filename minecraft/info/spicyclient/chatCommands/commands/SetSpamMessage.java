package info.spicyclient.chatCommands.commands;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;

public class SetSpamMessage extends Command {

	public SetSpamMessage() {
		super("SetSpamMessage", "SetSpamMessage (message) (you can put %r to use random characters)", 1);
	}
	
	@Override
	public void commandAction(String message) {
		
		String[] splitMessage = message.split(" ");
		String spam = "";
		
		for (String s : splitMessage) {
			spam += s + " ";
		}
		
		spam = spam.replaceFirst(SpicyClient.commandManager.prefix + "setspammessage ", "");
		spam = spam.substring(0, spam.length() - 1);
		
		SpicyClient.config.spammer.message = spam;
		
		NotificationManager.getNotificationManager().createNotification("Spammer", "Changed message to " + spam, true, 3000, Type.INFO, Color.PINK);
		
	}
	
}
