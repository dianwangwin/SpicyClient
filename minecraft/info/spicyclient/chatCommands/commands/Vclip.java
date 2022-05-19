package info.spicyclient.chatCommands.commands;

import java.text.DecimalFormat;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.chatCommands.CommandManager;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import net.minecraft.client.Minecraft;

public class Vclip extends Command {

	public Vclip() {
		super("vclip", "vclip <number of blocks>", 1);
	}
	
	@Override
	public void incorrectParameters() {
		NotificationManager.getNotificationManager().createNotification("vclip", "Please use " + SpicyClient.commandManager.prefix + "vclip <number of blocks>", true, 3000, Type.INFO, Color.GREEN);
	}
	
	@Override
	public void commandAction(String message) {
		
		try {
			double yOffset = Double.valueOf(message.split(" ")[1]);
			Minecraft mc = Minecraft.getMinecraft();
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + yOffset, mc.thePlayer.posZ);
			NotificationManager.getNotificationManager().createNotification("vclip", "Successfully vcliped to " + new DecimalFormat("#.##").format(mc.thePlayer.posY), true, 3000, Type.INFO, Color.GREEN);
		} catch (Exception e) {
			incorrectParameters();
		}
		
	}
	
}
