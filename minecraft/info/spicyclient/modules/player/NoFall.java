package info.spicyclient.modules.player;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.bypass.Hypixel;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.settings.ModeSetting;
import info.spicyclient.util.InventoryUtils;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RenderUtils;
import info.spicyclient.util.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0CPacketBoatInput;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class NoFall extends Module {
	
	public ModeSetting noFallMode = new ModeSetting("NoFall Mode", "Vanilla", "Vanilla", "Packet", "Hypixel");
	
	public NoFall() {
		super("No Fall", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(noFallMode);
	}
	
	public void onEnable() {
		hasLandedAfterFly = true;
	}
	
	public void onDisable() {
		
	}
	
	public static transient boolean hasLandedAfterFly = true;
	
	public void onEvent(Event e) {
		
		if (e instanceof EventMotion && e.isPre() && noFallMode.is("Vanilla") && mc.thePlayer.fallDistance > 3) {
			
			EventMotion event = (EventMotion) e;
			event.onGround = true;
			
		}
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				this.additionalInformation = noFallMode.getMode();
				
				if (mc.thePlayer.fallDistance >= 3 && noFallMode.is("Packet") && !SpicyClient.config.fly.isEnabled()) {
					
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
					
				}
				if (mc.thePlayer.fallDistance >= 2 && noFallMode.is("Hypixel") && !isOverVoid()) {
					
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
					
				}
				
//				if (SpicyClient.config.fly.isEnabled()) {
//					hasLandedAfterFly = false;
//				}
//				
//				if (MovementUtils.isOnGround(0.0001)) {
//					hasLandedAfterFly = true;
//				}
				
			}
			
		}
		
	}
	
	private boolean isOverVoid() {
		
		boolean isOverVoid = true;
		BlockPos block = mc.thePlayer.getPosition();
		
		for (double i = mc.thePlayer.posY + 1; i > 0; i -= 0.5) {
			
			if (isOverVoid) {
				
				try {
					if (mc.theWorld.getBlockState(block).getBlock() != Blocks.air) {
						
						isOverVoid = false;
						break;
						
					}
				} catch (Exception e) {
					
				}
				
			}
			
			block = block.add(0, -1, 0);
			
		}
		
		for (double i = 0; i < 10; i += 0.1) {
			if (MovementUtils.isOnGround(i) && isOverVoid) {
				isOverVoid = false;
				break;
			}
		}
		
		return isOverVoid;
	}
	
}
