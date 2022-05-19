package info.spicyclient.modules.memes;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.events.listeners.EventSendPacket;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.render.SkyColor;
import info.spicyclient.settings.NumberSetting;
import info.spicyclient.ui.fonts.FontUtil;
import info.spicyclient.ui.fonts.JelloFontRenderer;
import info.spicyclient.util.RandomObjectArraylist;
import info.spicyclient.util.RotationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

public class SuperHeroFX extends Module {

	public SuperHeroFX() {
		super("SuperHeroFX", Keyboard.KEY_NONE, Category.MEMES);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(scale, amount, ttl);
	}
	
	public NumberSetting scale = new NumberSetting("Scale", 0.03, 0.01, 0.1, 0.005),
			amount = new NumberSetting("Amount", 1, 1, 10, 1),
			ttl = new NumberSetting("Time to live", 2000, 500, 10000, 200);
	
	private static transient CopyOnWriteArrayList<FX> effects = new CopyOnWriteArrayList<>();
	private static transient RandomObjectArraylist<String> strings = new RandomObjectArraylist<String>("Boom", "Kaboom",
			"Pow", "Wam", "Zap", "Bam", "Zap", "Slap", "Kapow", "Wow", "BOOM", "KABOOM", "POW", "WAM", "ZAP", "BAM",
			"ZAP", "SLAP", "KAPOW", "WOW");
	private static transient RandomObjectArraylist<Integer> colors = new RandomObjectArraylist<Integer>(0xff0000,
			0x00ff00, 0x0000ff, 0xffff00, 0xff00ff, 0x00ffff, 0xffffff);
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			this.additionalInformation = "Best module";
		}
		
		if (e instanceof EventRender3D && e.isPre()) {
			
			EventRender3D render3d = ((EventRender3D)e);
			
			for (FX f : effects) {
				
				if (System.currentTimeMillis() > f.ttl && f.opacity <= 5) {
					effects.remove(f);
				}else {
					
					if (System.currentTimeMillis() > f.ttl) {
						f.opacity -= f.opacity/200;
					}
					
					f.x += (f.motX / 10);
					f.y += (f.motY / 10);
					f.z += (f.motZ / 10);
					f.motX -= f.motX / 20;
					f.motY -= f.motY / 20;
					f.motZ -= f.motZ / 20;
					
					String text = f.text;
					GlStateManager.pushMatrix();
					RenderHelper.enableStandardItemLighting();
					GlStateManager.enablePolygonOffset();
					GL11.glPolygonOffset(1.0f, -1100000.0f);
					GlStateManager.disableLighting();
					GlStateManager.disableDepth();
					GlStateManager.enableBlend();
					double scale = f.scale;
					GlStateManager.scale(1/scale, 1/scale, 1/scale);
					GlStateManager.rotate(180, 1, 0, 0);
					GlStateManager.enableTexture2D();
					GlStateManager.translate((f.x - mc.getRenderManager().renderPosX) * scale,
							(f.y - mc.getRenderManager().renderPosY) * -scale, (f.z - mc.getRenderManager().renderPosZ) * -scale);
					GlStateManager.rotate((float) f.yaw, 0, 1, 0);
					GlStateManager.rotate((float) f.pitch, 1, 0, 0);
					JelloFontRenderer fr = FontUtil.superherofx1;
					GlStateManager.color(1, 1, 1, (float) (f.opacity/100));
					fr.drawString(text, (float) (f.x - (mc.fontRendererObj.getStringWidth(text) / 2)
							- mc.getRenderManager().renderPosX), 0f, f.color);
					GlStateManager.rotate(180, 0, 1, 0);
					GlStateManager.color(1, 1, 1, 1);
					GlStateManager.color(1, 1, 1, (float) (f.opacity/100));
					fr.drawString(text, (float) (f.x - (mc.fontRendererObj.getStringWidth(text) / 2)
							- mc.getRenderManager().renderPosX), 0f, f.color);
					GlStateManager.color(1, 1, 1, 1);
					GlStateManager.disableTexture2D();
					GlStateManager.disableBlend();
					GlStateManager.enableDepth();
					GlStateManager.enableLighting();
					GlStateManager.disablePolygonOffset();
					RenderHelper.disableStandardItemLighting();
					GlStateManager.popMatrix();
					
				}
				
			}
			
		}
		
		if (e instanceof EventSendPacket && ((EventSendPacket)e).packet instanceof C0APacketAnimation) {
			
			try {
				if (mc.objectMouseOver.typeOfHit == MovingObjectType.ENTITY) {
					
					for (short i = 0; i < amount.getValue(); i++) {
						effects.add(new FX((long) ttl.getValue(),
								mc.objectMouseOver.entityHit.posX + new Random().nextInt(3) + new Random().nextDouble()
										- 2,
								mc.objectMouseOver.entityHit.posY + new Random().nextInt(2) + new Random().nextDouble()
										- 0.5,
								mc.objectMouseOver.entityHit.posZ + new Random().nextInt(3) + new Random().nextDouble()
										- 2,
								new Random().nextInt(360), new Random().nextInt(180) - 90, 1 / scale.getValue(),
								strings.getRandomObject(), colors.getRandomObject()));
					}
					
				}
			} catch (Exception e2) {
				// TODO: handle exception
			}
			
		}
		
	}
	
	public class FX {
		
		public FX(long ttl, double x, double y, double z, double yaw, double pitch, double scale, String text, int color) {
			this.ttl = System.currentTimeMillis() + ttl;
			this.x = x;
			this.y = y;
			this.z = z;
			this.motX = ((double)(new Random().nextInt(3))) - 1.0;
			this.motY = new Random().nextDouble();
			this.motZ = ((double)(new Random().nextInt(3))) - 1.0;
			this.yaw = yaw;
			this.pitch = pitch;
			this.scale = scale;
			this.text = text;
			this.color = color;
			this.opacity = 100;
		}
		
		public long ttl;
		public double x, y, z, yaw, pitch, scale, motX, motY, motZ, opacity;
		public String text;
		public int color;
		
	}
	
}
