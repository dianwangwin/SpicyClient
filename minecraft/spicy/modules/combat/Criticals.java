package spicy.modules.combat;

import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C03PacketPlayer;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventGetBlockReach;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventSendPacket;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.NumberSetting;
import spicy.util.Timer;

public class Criticals extends Module {
	
	public Criticals() {
		super("Criticals", Keyboard.KEY_NONE, Category.BETA);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public Timer timer = new Timer();
	
	public void onEvent(Event e) {
		
		if (e instanceof EventSendPacket && e.isPre()) {
			Packet packet = ((EventSendPacket)e).packet;
			
			if (packet instanceof C02PacketUseEntity) {
				
				if (!mc.thePlayer.onGround) {
					return;
				}
				
				C02PacketUseEntity attack = (C02PacketUseEntity) packet;
				
				if (attack.getAction() == Action.ATTACK) {
					
					Crit(new Double[] { 0.0, 0.419999986886978, 0.3331999936342235, 0.2481359985909455, 0.164773281826067, 0.083077817806467, 0.0, -0.078400001525879, -0.155232004516602, -0.230527368912964, -0.304316827457544, -0.376630498238655, -0.104080378093037 });
					
				}
				
			}
			
		}
		
	}
	
	// Found these methods on github
	public static void Crit(final Double[] value) {
        final Minecraft mc = Criticals.mc;
        final NetworkManager var1 = mc.thePlayer.sendQueue.getNetworkManager();
        final Minecraft mc2 = Criticals.mc;
        final Double curX = mc.thePlayer.posX;
        final Minecraft mc3 = Criticals.mc;
        Double curY = mc.thePlayer.posY;
        final Minecraft mc4 = Criticals.mc;
        final Double curZ = mc.thePlayer.posZ;
        final Double RandomY = 0.0;
        for (final Double offset : value) {
            curY += offset;
            var1.sendPacket((Packet)new C03PacketPlayer.C04PacketPlayerPosition((double)curX, curY + RandomY, (double)curZ, false));
        }
    }
	
    public static void Crit2(final Double[] value) {
        final Minecraft mc = Criticals.mc;
        final NetworkManager var1 = mc.thePlayer.sendQueue.getNetworkManager();
        final Minecraft mc2 = Criticals.mc;
        final Double curX = mc.thePlayer.posX;
        final Minecraft mc3 = Criticals.mc;
        final Double curY = mc.thePlayer.posY;
        final Minecraft mc4 = Criticals.mc;
        final Double curZ = mc.thePlayer.posZ;
        for (final Double offset : value) {
            var1.sendPacket((Packet)new C03PacketPlayer.C04PacketPlayerPosition((double)curX, curY + offset, (double)curZ, false));
        }
    }
	
}
