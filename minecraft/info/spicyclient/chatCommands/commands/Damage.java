package info.spicyclient.chatCommands.commands;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.MathHelper;

public class Damage extends Command {

	public Damage() {
		super("damage", "damage", 0);
	}
	
	@Override
	public void commandAction(String message) {
		
		Minecraft mc = Minecraft.getMinecraft();
		
		sendPrivateChatMessage("You have been damaged");
		
    	int damage = 1;
		if (damage > MathHelper.floor_double(mc.thePlayer.getMaxHealth()))
			damage = MathHelper.floor_double(mc.thePlayer.getMaxHealth());

		double offset = 0.0625;
		if (mc.thePlayer != null && mc.getNetHandler() != null && mc.thePlayer.onGround) {
			for (int i = 0; i <= ((3 + damage) / offset); i++) {
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY + offset, mc.thePlayer.posZ, false));
				mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
						mc.thePlayer.posY, mc.thePlayer.posZ, (i == ((3 + damage) / offset))));
			}
		}
		
		/*
		if (mc.getCurrentServerData().serverIP.contains("hypixel")) {
			SpicyClient.config.fly.damage();
		}else {
			for (int index = 0; index < 70; index++) {
	            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.06D, mc.thePlayer.posZ, false));
	            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
	        }
			mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1D, mc.thePlayer.posZ, false));
		}
		*/
	}
	
	@Override
	public void incorrectParameters() {
		sendPrivateChatMessage("Please use .damage");
	}
	
}
