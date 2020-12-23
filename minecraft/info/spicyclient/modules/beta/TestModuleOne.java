package info.spicyclient.modules.beta;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventMotion;
import info.spicyclient.events.listeners.EventPacket;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.combat.Killaura;
import info.spicyclient.modules.movement.Fly;
import info.spicyclient.notifications.Color;
import info.spicyclient.notifications.NotificationManager;
import info.spicyclient.notifications.Type;
import info.spicyclient.util.MovementUtils;
import info.spicyclient.util.RotationUtils;
import info.spicyclient.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class TestModuleOne extends Module {

	public TestModuleOne() {
		super("TestModuleOne", Keyboard.KEY_NONE, Category.BETA);
	}
	
	public static transient Timer timer = new Timer();
	
	public int status = 0, test = 0;
	public double dub = 0;
	public float flo = 0;
	public boolean bool1 = false, bool2 = true;
	
	@Override
	public void onEnable() {
		bool1 = false;
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			if (mc.thePlayer.isCollidedHorizontally) {
				bool1 = !bool1;
			}
			
			Killaura k = SpicyClient.config.killaura;
			
			if (k.target == null) {
				
			}else {
				
				float distance = 2;
				
				float f = (RotationUtils.getRotations(k.target)[0] + 180 + (bool1 ? -10 : 10)) * 0.017453292F;
				double x2 = k.target.posX, z2 = k.target.posZ;
	            x2 -= (double)(MathHelper.sin(f) * distance * -1);
	            z2 += (double)(MathHelper.cos(f) * distance * -1);
	            Command.sendPrivateChatMessage("t");
	            MovementUtils.setMotion(MovementUtils.getSpeed(), RotationUtils.getRotationFromPosition(x2, z2, mc.thePlayer.posY)[0]);
	            
				//mc.thePlayer.posX = x;
				//mc.thePlayer.posZ = z;
				
			}
			
		}
		
	}
	
	@Override
	public void onEventWhenDisabled(Event e) {
		
	}
	
}
