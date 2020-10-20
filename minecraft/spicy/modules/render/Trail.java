package spicy.modules.render;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventPlayerRender;
import spicy.events.listeners.EventRender3D;
import spicy.modules.Module;
import spicy.util.RenderUtils;
import spicy.util.Timer;

public class Trail extends Module {
	
	private transient ArrayList<EventRender3D> trailList = new ArrayList<EventRender3D>();
	private transient Timer timer = new Timer();
	
	public Trail() {
		super("Trail", Keyboard.KEY_NONE, Category.BETA);
	}
	
	@Override
	public void onDisable() {
		trailList.clear();
	}
	
	@Override
	public void onEvent(Event e) {
		
		
		if (e instanceof EventPlayerRender) {
			
			EventPlayerRender event = (EventPlayerRender) e;
			
			AbstractClientPlayer player = event.entity;
			
			if (player == mc.thePlayer) {
				
				return;
				
			}
			
			RenderUtils.drawLine(mc.thePlayer.boundingBox.minX, mc.thePlayer.boundingBox.minY, mc.thePlayer.boundingBox.minZ, player.boundingBox.minX, player.boundingBox.minY, player.boundingBox.minZ);
			
			/*
			//((EventRender3D) e).reset();
			EventRender3D render3d = (EventRender3D) e;
			
			if (timer.hasTimeElapsed(500, true)) {
				
				//Command.sendPrivateChatMessage(trailList + "");
				trailList.add(render3d);
				
			}
			
			EventRender3D lastLoc = null;
			
			for (EventRender3D Loc: trailList) {
				
				if (lastLoc == null) {
					lastLoc = Loc;
				}else {
					lastLoc.reset();
					Loc.reset();
					//Command.sendPrivateChatMessage(lastLoc.getX() + lastLoc.getY() + lastLoc.getZ() + Loc.getX() + Loc.getY() + Loc.getZ() + "");
					RenderUtils.drawLine(lastLoc.getX(), lastLoc.getY(), lastLoc.getZ(), Loc.getX(), Loc.getY(), Loc.getZ());
					lastLoc = Loc;
				}
				
			}
			
			//RenderUtils.drawLine(render3d.getX(), render3d.getY(), render3d.getZ(), render3d.getX(), render3d.getY() + 10, render3d.getZ());
			//RenderUtils.drawLine(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.posX, mc.thePlayer.posY + 100000, mc.thePlayer.posZ);
			*/
		}
		
	}
	
}
