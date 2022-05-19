package info.spicyclient.chatCommands;

import info.spicyclient.SpicyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;

public class Command {
	
	public Command(String command, String helpText, int parameters) {
		this.command = command;
		this.helpText = helpText;
		this.parameters = parameters;
	}
	
	// You would need to type .example for this command to work
	public String command = "example";
	
	// This would be displayed as ".example <ModuleName>" when you type ".help"
	public String helpText = "example <ModuleName>";
	
	// you would only need to type '.example' for this command to work, if this was set to 1 you would need to type '.example [foo]' for it to work
	public int parameters = 0;
	
	// This is what the command would do
	public void commandAction(String message) {
		
	}
	
	// Called when a player types the wrong amount of parameters
	public void incorrectParameters() {
		
	}
	
	// This shows a chat message to the player
	public static void sendPrivateChatMessage(Object text) {
		
		String prefix = SpicyClient.config.clientName + SpicyClient.config.clientVersion;
		
		if (SpicyClient.config.clientVersion != SpicyClient.config.version) {
			prefix = SpicyClient.config.clientName;
		}
		
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("§s[ §s" + prefix + " §s] §f" + text.toString()));
	}
	
	// This shows a chat message to the player
	public static void sendPrivateChatMessage(Object text, ChatStyle style) {
		
		String prefix = SpicyClient.config.clientName + SpicyClient.config.clientVersion;
		
		if (SpicyClient.config.clientVersion != SpicyClient.config.version) {
			prefix = SpicyClient.config.clientName;
		}
		
		ChatComponentText message = new ChatComponentText("§s[ §s" + prefix + " §s] §f" + text.toString());
		message.setChatStyle(style);
		
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(message);
	}
	
	// This shows a chat message to the player
	public static void sendPrivateChatMessage(String prefix, boolean useExactPrefix, Object text) {
		
		if (!useExactPrefix) {
			prefix = "§s[ §s" + prefix + " §s] §f";
		}
		
		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(prefix + text.toString()));
	}
	
	// This shows a chat message to the player
	public static void sendPrivateChatMessage(String prefix, boolean useExactPrefix, Object text, ChatStyle style) {

		if (!useExactPrefix) {
			prefix = "§s[ §s" + prefix + " §s] §f";
		}

		ChatComponentText message = new ChatComponentText(prefix + text.toString());
		message.setChatStyle(style);

		Minecraft.getMinecraft().thePlayer.addChatComponentMessage(message);
	}
	
	// This shows a chat message to the everyone
	public static void sendPublicChatMessage(Object text) {
		Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(text.toString()));
	}
	
}
