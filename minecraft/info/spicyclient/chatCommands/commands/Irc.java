package info.spicyclient.chatCommands.commands;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.chatCommands.CommandManager;
import info.spicyclient.modules.player.IrcChat;
import info.spicyclient.spicyMessageClient.networking.packets.SpicyPacket;
import info.spicyclient.spicyMessageClient.networking.packets.SpicyPacket.type;

public class Irc extends Command {

	public Irc() {
		super("irc", "irc <chat message>", 1);
	}
	
	@Override
	public void commandAction(String message) {
		
		if (!IrcChat.messenger.isConnected()) {
			if (!SpicyClient.config.ircChat.isEnabled()) {
				SpicyClient.config.ircChat.toggle();
				Command.sendPrivateChatMessage("Enabled the irc chat module, please try again");
			}else {
				Command.sendPrivateChatMessage("There was a problem sending your message");
			}
		}
		
		String ircMessage = message.substring(4 + SpicyClient.commandManager.prefix.length());
		if (ircMessage.equalsIgnoreCase("/list")) {
			IrcChat.messenger.sendPacket(new SpicyPacket(type.LIST, null, null, null, null));
		}else {
			IrcChat.messenger.sendPacket(new SpicyPacket(type.MESSAGE, ircMessage, null, null, null));
		}
		
	}
	
}
