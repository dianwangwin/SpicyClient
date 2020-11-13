package spicy.modules.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.util.MathHelper;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.ModeSetting;
import spicy.util.PlayerUtils;

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
		
		for (S0CPacketSpawnPlayer packet : packets) {
			
			try {
				packet.processPacket(mc.getNetHandler());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		packets.clear();
		
		for (Entity entity : entities) {
			
			try {
				mc.theWorld.spawnEntityInWorld(entity);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		entities.clear();
	}
	
	public static transient ArrayList<S0CPacketSpawnPlayer> packets = new ArrayList<S0CPacketSpawnPlayer>();
	public static transient ArrayList<Entity> entities = new ArrayList<Entity>();
	
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
				else if (packet.packet instanceof S0CPacketSpawnPlayer && mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
					
					hypixelAntibot();
					
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
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre() && AntibotMode.is("Advanced") && !(mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel"))) {
				
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
	
	// Prevents non bots from being removed
	private static List<EntityPlayer> dontRemove = new ArrayList<>();
	
	private void hypixelAntibot() {
		
		CopyOnWriteArrayList<EntityPlayer> removeThese = new CopyOnWriteArrayList<EntityPlayer>();
		
		for (Object o : mc.theWorld.getLoadedEntityList()) {
			
            if (o instanceof EntityPlayer) {
            	
                EntityPlayer ent = (EntityPlayer) o;
                
                if (ent != mc.thePlayer && !dontRemove.contains(ent)) {
                	
                	String customName = ent.getCustomNameTag();
                	String formattedName = ent.getDisplayName().getFormattedText();
                    String name = ent.getName();
                    
                    if(ent.isInvisible() && !formattedName.startsWith("§c") && formattedName.endsWith("§r") && customName.equals(name)){
                    	
    					double diffX = Math.abs(ent.posX - mc.thePlayer.posX);
    					double diffY = Math.abs(ent.posY - mc.thePlayer.posY);
    					double diffZ = Math.abs(ent.posZ - mc.thePlayer.posZ);
    					double diffH = Math.sqrt(diffX * diffX + diffZ * diffZ);
    					
    					if(diffY < 13 && diffY > 10 && diffH < 3){
    						
    						List<EntityPlayer> list = PlayerUtils.getTabPlayerList();
    						
    						if(!list.contains(ent)){
    							
    							Command.sendPrivateChatMessage("The bot " + name + " bot was removed from your game");
                          		removeThese.add(ent);
                          		
    						}
    						
    					}
    				
    				}
                    
                    if(ent.isInvisible()){
                    	
                    	if(!customName.equalsIgnoreCase("") && customName.toLowerCase().contains("§c§c") && name.contains("§c")){
                    		Command.sendPrivateChatMessage("The bot " + name + " bot was removed from your game");
                    		removeThese.add(ent);
                    	}
                    	
                    }
                    
	                if (formattedName.startsWith("\u00a7") && !ent.isInvisible() && !formattedName.toLowerCase().contains("npc")) {
	                	
	                }else {
	                	Command.sendPrivateChatMessage("The " + ent.getDisplayName().getFormattedText() + " bot was removed from your game");
	                	removeThese.add(ent);
	                }
                    
                    // Watchdog bots
                    if(!customName.equalsIgnoreCase("") && customName.toLowerCase().contains("§c") && customName.toLowerCase().contains("§r")){
                    	Command.sendPrivateChatMessage("The bot " + name + " bot was removed from your game");
                    	removeThese.add(ent);
                    }
                    
                    // npcs
                    if(formattedName.contains("§8[NPC]")){
                    	
                    	dontRemove.add(ent);
                    	
                    }
                    
                    if(!formattedName.contains("§c") && !customName.equalsIgnoreCase("")){

                    	dontRemove.add(ent);
                    }
                    
                    // bedwars shop
                    if(!formattedName.startsWith("§") && formattedName.endsWith("§r")){
                    	dontRemove.add(ent);
                    }
                    
                }
                
            }
            
        }
		
		for (EntityPlayer ent : removeThese) {
			
			mc.theWorld.removeEntity(ent);
			
		}
		
	}
	
}
