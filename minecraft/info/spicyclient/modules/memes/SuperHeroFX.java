package info.spicyclient.modules.memes;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.render.SkyColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.util.ResourceLocation;

public class SuperHeroFX extends Module {

	public SuperHeroFX() {
		super("SuperHeroFX", Keyboard.KEY_NONE, Category.MEMES);
	}
	
	public CopyOnWriteArrayList<FX> effects = new CopyOnWriteArrayList<>();
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventRender3D && e.isPre()) {
			
			EventRender3D render3d = ((EventRender3D)e);
			
			for (FX f : effects) {
				
				if (System.currentTimeMillis() > f.ttl) {
					effects.remove(f);
				}else {
					
					GlStateManager.pushMatrix();
		            Tessellator tessellator = Tessellator.getInstance();
		            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
		            this.mc.getTextureManager().bindTexture(new ResourceLocation("spicy/SpicyClientBlack.png"));
		            int imageWidth = 500, imageHeight = 122;
					imageWidth /= 6;
					imageHeight /= 6;
					imageWidth = 0;
					imageHeight = 0;
					/*
		            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
		            worldrenderer.pos(f.x + imageWidth, f.y, f.z).tex(1.0, 0).normal(0, 0, -1).endVertex();
		            worldrenderer.pos(f.x + imageWidth, f.y + imageHeight, f.z).tex(1.0, 1.0).normal(0, 0, -1).endVertex();
		            worldrenderer.pos(f.x, f.y + imageHeight, f.z).tex(0, 1.0).normal(0, 0, -1).endVertex();
		            worldrenderer.pos(f.x, f.y, f.z).tex(0, 0).normal(0, 0, -1).endVertex();
		            worldrenderer.pos(f.x + imageWidth, f.y, f.z).tex(1.0, 0).normal(0, 0, 1).endVertex();
		            worldrenderer.pos(f.x + imageWidth, f.y + imageHeight, f.z).tex(1.0, 1.0).normal(0, 0, 1).endVertex();
		            worldrenderer.pos(f.x, f.y + imageHeight, f.z).tex(0, 1.0).normal(0, 0, 1).endVertex();
		            worldrenderer.pos(f.x, f.y, f.z).tex(0, 0).normal(0, 0, 1).endVertex();
		            */
					worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
	                worldrenderer.pos(-100.0D, -100.0D, -100.0D).tex(0.0D, 0.0D).color(40, 40, 40, 255).endVertex();
	                worldrenderer.pos(-100.0D, -100.0D, 100.0D).tex(0.0D, 16.0D).color(40, 40, 40, 255).endVertex();
	                worldrenderer.pos(100.0D, -100.0D, 100.0D).tex(16.0D, 16.0D).color(40, 40, 40, 255).endVertex();
	                worldrenderer.pos(100.0D, -100.0D, -100.0D).tex(16.0D, 0.0D).color(40, 40, 40, 255).endVertex();
	                tessellator.draw();
	                GlStateManager.popMatrix();
					
				}
				
			}
			
		}
		
		if (e instanceof EventSendPacket && ((EventSendPacket)e).packet instanceof C02PacketUseEntity) {
			C02PacketUseEntity packet = ((C02PacketUseEntity)((EventSendPacket)e).packet);
			effects.add(new FX(5000, packet.getEntityFromWorld(mc.theWorld).posX - mc.getRenderManager().renderPosX,
					packet.getEntityFromWorld(mc.theWorld).posY - mc.getRenderManager().renderPosY,
					packet.getEntityFromWorld(mc.theWorld).posZ - mc.getRenderManager().renderPosZ));
		}
		
	}
	
	public class FX {
		
		public FX(long ttl, double x, double y, double z) {
			this.ttl = System.currentTimeMillis() + ttl;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		public long ttl;
		public double x, y, z;
		
	}
	
}
