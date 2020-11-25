package info.spicyclient.chatCommands.commands;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Damage extends Command {

	public Damage() {
		super("damage", "damage", 0);
	}
	
	@Override
	public void commandAction(String message) {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		sendPrivateChatMessage("You have been damaged");
		
		if (mc.getCurrentServerData().serverIP.contains("hypixel")) {
			SpicyClient.config.fly.damage();
		}else {
			for (int index = 0; index < 70; index++) {
	            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.06D, mc.thePlayer.posZ, false));
	            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
	        }
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1D, mc.thePlayer.posZ, false));
		}
		
	}
	
	@Override
	public void incorrectParameters() {
		sendPrivateChatMessage("Please use .damage");
	}
	
}
