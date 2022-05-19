package info.spicyclient.modules.combat;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.util.PlayerUtils;
import info.spicyclient.util.ServerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.util.MathHelper;

public class Antibot extends Module {
	
	private ModeSetting AntibotMode = new ModeSetting("Antibot Mode", "Advanced", "Advanced");
	
	public Antibot() {
		super("Antibot", Keyboard.KEY_NONE, Category.COMBAT);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(AntibotMode);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				if (!mc.isSingleplayer() && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
					this.additionalInformation = "Hypixel";
				}else {
					this.additionalInformation = "Advanced";
				}
				
			}
			
		}
		
		if (e instanceof EventReceivePacket && AntibotMode.is("Advanced")) {
			
			if (e.isIncoming() && e.isPre()) {
				
				EventReceivePacket packet = (EventReceivePacket) e;
				
				if (packet.packet instanceof S0CPacketSpawnPlayer && !(mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel"))) {
					
	                S0CPacketSpawnPlayer p = (S0CPacketSpawnPlayer) packet.packet;
	                double entX = p.getX() / 32D;
	                double entY = p.getY() / 32D;
	                double entZ = p.getZ() / 32D;
	                double diffX = mc.thePlayer.posX - entX;
	                double diffY = mc.thePlayer.posY - entY;
	                double diffZ = mc.thePlayer.posZ - entZ;
	                
	                float distance = MathHelper.sqrt_double(diffX * diffX + diffY * diffY + diffZ * diffZ);
	                
	                if (distance <= 17 && entY > mc.thePlayer.posY + 1 && (entX != mc.thePlayer.posX && entY != mc.thePlayer.posY && entZ != mc.thePlayer.posZ)) {
	                	//Entity entity = mc.theWorld.getEntityByID(p.getEntityID());
	                	//Command.sendPrivateChatMessage("The " + entity.getDisplayName().getFormattedText() + " bot was removed from your game");
	                	packet.setCanceled(true);
	                	Command.sendPrivateChatMessage("A bot was removed from your game");
	                }
					
				}
				else if (packet.packet instanceof S0CPacketSpawnPlayer && !mc.isSingleplayer() && ServerUtils.isOnHypixel()) {
					
					S0CPacketSpawnPlayer p = (S0CPacketSpawnPlayer) packet.packet;
					Entity entity = mc.theWorld.getEntityByID(p.getEntityID());
					
//					new Thread("Bot checker thread") {
//                		public void run() {
//                			
//                			try {
//								Thread.sleep(3500);
//							} catch (InterruptedException e) {
//								e.printStackTrace();
//							}
//                			
//                			Entity entity = null;
//                			
//                			try {
//                				entity = mc.theWorld.getEntityByID(p.getEntityID());
//							} catch (Exception e2) {
//								
//							}
//                			
//                			try {
//    	                        if (mc.getNetHandler().getPlayerInfo(((EntityPlayer)entity).getUniqueID()).responseTime > 1) {
//    	                        	//Command.sendPrivateChatMessage("A watchdog bot was removed from your game (ping check)");
//    	                        	mc.theWorld.removeEntity(entity);
//    	                        	return;
//    	                        }
//							} catch (Exception e2) {
//								
//							}
//            				
//            				try {
//    	                        if (mc.getNetHandler()
//										.getPlayerInfo(((EntityPlayer) entity).getUniqueID()) == null) {
//									//Command.sendPrivateChatMessage(
//											//"A watchdog bot was removed from your game (null npi check)");
//									mc.theWorld.removeEntity(entity);
//									return;
//								}
//							} catch (Exception e2) {
//								
//							}
//            				
//            				try {
//    	                        if (mc.getNetHandler()
//										.getPlayerInfo(((EntityPlayer) entity).getUniqueID())
//										.getGameProfile() == null) {
//									//Command.sendPrivateChatMessage(
//											//"A watchdog bot was removed from your game (null game profile check)");
//									mc.theWorld.removeEntity(entity);
//									return;
//								}
//							} catch (Exception e2) {
//								
//							}
//
//                		};
//                	}.start();
					
				}
				
			}
			
		}
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre() && AntibotMode.is("Advanced") && !mc.isSingleplayer() && !(mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel"))) {
				
				for (Object entity : mc.theWorld.loadedEntityList) {
					
					if (((Entity)entity).isInvisible() && entity != mc.thePlayer) {
						
						//Command.sendPrivateChatMessage("a bot was removed from your game");
						Command.sendPrivateChatMessage("A bot was removed from your game");
						mc.theWorld.removeEntity(((Entity)entity));
						
					}
					
				}
				
			}
			
		}
		
	}
	
}
