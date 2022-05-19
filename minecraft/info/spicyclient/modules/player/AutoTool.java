package info.spicyclient.modules.player;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.util.InventoryUtils;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class AutoTool extends Module {

	public AutoTool() {
		super("AutoTool", Keyboard.KEY_NONE, Category.PLAYER);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventSendPacket && e.isPre()) {
			
			Packet p = ((EventSendPacket)e).packet;
			
			if (true) {
				
				try {
					if (p instanceof C07PacketPlayerDigging && mc.gameSettings.keyBindAttack.isKeyDown() && mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
						autotool(mc.objectMouseOver.getBlockPos());
					}
				} catch (Exception e2) {
					
				}
				
				if (p instanceof C02PacketUseEntity && ((C02PacketUseEntity)p).getAction() == Action.ATTACK) {
					
			        int bestWeapon = -1;
			        
			        for (int i = 0; i < 9; i++) {
			        	
			            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
			            
			            if (itemStack != null && itemStack.getItem() != null && InventoryUtils.isBestWeapon(itemStack)) {
			            	
			                bestWeapon = i;
			                
			            }
			            
			        }
			        
			        if (bestWeapon < 0) {
			            return;
			        }
			        
			        if (mc.thePlayer.inventory.currentItem == bestWeapon) {
			        	return;
			        }
			        
			        mc.thePlayer.inventory.currentItem = bestWeapon;
			        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(bestWeapon));
			        
				}
				
			}
			
		}
		
	}
	
    private static void autotool(BlockPos position) {
    	
        Block block = mc.theWorld.getBlockState(position).getBlock();
        
        int item = AutoTool.getStrongestItem(block);
        if (item < 0) {
            return;
        }
        float strength = AutoTool.getStrengthAgainstBlock(block, mc.thePlayer.inventory.mainInventory[item]);
        if (mc.thePlayer.getHeldItem() != null && AutoTool.getStrengthAgainstBlock(block, mc.thePlayer.getHeldItem()) >= strength) {
            return;
        }
        
        if (mc.thePlayer.inventory.currentItem == item) {
        	return;
        }
        
        mc.thePlayer.inventory.currentItem = item;
        mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(item));
        
    }

    private static int getStrongestItem(Block block) {
    	
        float strength = Float.NEGATIVE_INFINITY;
        int strongest = -1;
        
        for (int i = 0; i < 9; i++) {
        	
            float itemStrength;
            
            ItemStack itemStack = mc.thePlayer.inventory.mainInventory[i];
            
            if (itemStack != null && itemStack.getItem() != null && (itemStrength = AutoTool.getStrengthAgainstBlock(block, itemStack)) > strength && itemStrength != 1.0f) {
            	
                strongest = i;
                strength = itemStrength;
                
            }
            
        }
        
        return strongest;
        
    }

    public static float getStrengthAgainstBlock(Block block, ItemStack item) {
        float strength = item.getStrVsBlock(block);
        if (!EnchantmentHelper.getEnchantments(item).containsKey(Enchantment.efficiency.effectId) || strength == 1.0f) {
            return strength;
        }
        int enchantLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.efficiency.effectId, item);
        return strength + (float)(enchantLevel * enchantLevel + 1);
    }
	
}
