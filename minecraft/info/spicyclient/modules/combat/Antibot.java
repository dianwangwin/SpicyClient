package info.spicyclient.modules.combat;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.util.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.util.MathHelper;

public class Antibot extends Module {
	
	public static EntityLivingBase target = null;
	
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
		
		if (e instanceof EventPacket && AntibotMode.is("Advanced")) {
			
			if (e.isIncoming() && e.isPre()) {
				
				EventPacket packet = (EventPacket) e;
				
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
				else if (packet.packet instanceof S0CPacketSpawnPlayer && !mc.isSingleplayer() && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {

					//hypixelAntibot();
					/*
					S0CPacketSpawnPlayer p = (S0CPacketSpawnPlayer) packet.packet;
					Entity entity = mc.theWorld.getEntityByID(p.getEntityID());
					
					if (entity == null) {
						return;
					}
					
	                if (entity.getDisplayName().getFormattedText().startsWith("\u00a7") && !entity.isInvisible() && !entity.getDisplayName().getFormattedText().toLowerCase().contains("npc")) {
	                	
	                }else {
	                	Command.sendPrivateChatMessage("The " + entity.getDisplayName().getFormattedText() + " bot was removed from your game");
	                	packet.setCanceled(true);
	                }
					*/
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
			else if (!mc.isSingleplayer() && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
				
				/*
				S0CPacketSpawnPlayer p = (S0CPacketSpawnPlayer) packet.packet;
				Entity entity = mc.theWorld.getEntityByID(p.getEntityID());
				
				if (entity == null) {
					return;
				}
				
                if (entity.getDisplayName().getFormattedText().startsWith("\u00a7") && !entity.isInvisible() && !entity.getDisplayName().getFormattedText().toLowerCase().contains("npc")) {
                	
                }else {
                	Command.sendPrivateChatMessage("The " + entity.getDisplayName().getFormattedText() + " bot was removed from your game");
                	packets.add(p);
                	packet.setCanceled(true);
                }
                */
			}
			
		}
		
	}
	
}
