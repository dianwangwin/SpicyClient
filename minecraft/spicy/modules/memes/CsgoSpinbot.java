package spicy.modules.memes;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook;
import spicy.events.Event;
import spicy.events.listeners.EventMotion;
import spicy.events.listeners.EventSendPacket;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.BooleanSetting;
import spicy.settings.ModeSetting;
import spicy.settings.NumberSetting;
import spicy.util.Timer;

public class CsgoSpinbot extends Module {
	
	private float yaw = 0;
	private float lastYaw = 0;
	
	ModeSetting mode = new ModeSetting("Mode", "Slow Smooth", "Slow Smooth", "Fast Smooth", "Random");
	BooleanSetting lookDown = new BooleanSetting("Look Down", true);
	
	public CsgoSpinbot() {
		super("CS:GO Spinbot", Keyboard.KEY_NONE, Category.BETA);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(mode, lookDown);
	}
	
	@Override
	public void onEnable() {
		
		yaw = mc.thePlayer.rotationYaw;
		lastYaw = mc.thePlayer.rotationYaw;
	}
	
	private static Timer timer = new Timer();
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				if (timer.hasTimeElapsed(1, true)) {
					
					mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C05PacketPlayerLook(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, mc.thePlayer.onGround));
					
				}
				
			}
			
		}
		
		if (e instanceof EventSendPacket) {
			
			if (e.isPre()) {
				
				if (((EventSendPacket) e).packet instanceof C03PacketPlayer || ((EventSendPacket) e).packet instanceof C03PacketPlayer.C04PacketPlayerPosition || ((EventSendPacket) e).packet instanceof C03PacketPlayer.C05PacketPlayerLook || ((EventSendPacket) e).packet instanceof C03PacketPlayer.C06PacketPlayerPosLook){
					
					lastYaw += 0.1;
					((C03PacketPlayer)((EventSendPacket) e).packet).setYaw(lastYaw);
					
					if (lookDown.enabled) {
						((C03PacketPlayer)((EventSendPacket) e).packet).setPitch(90f);
					}
					
				}
				
			}
			
		}
		
	}
	
}
