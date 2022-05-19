package info.spicyclient.modules.render;

import java.util.Random;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import info.spicyclient.chatCommands.Command;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventReceivePacket;
import info.spicyclient.events.listeners.EventSwordBlockAnimation;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import info.spicyclient.settings.ModeSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.MathHelper;

public class OldHitting extends Module {
	
	public ModeSetting animationSetting = new ModeSetting("Animation", "1.7", "1.7", "Spaz", "Spaz 2", "Jitter", "Tap", "Multi Tap", "Spin", "Scale", "Spicy", "Astolfo", "Test");
	
	public OldHitting() {
		super("OldHitting", Keyboard.KEY_NONE, Category.RENDER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		super.addSettings(animationSetting);
	}
	
	public void onEnable() {
		ItemRenderer.customSwordBlockAnimation = true;
	}
	
	public void onDisable() {
		ItemRenderer.customSwordBlockAnimation = false;
	}
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				this.additionalInformation = animationSetting.getMode();
				
			}
			
		}
		
		if (e instanceof EventSwordBlockAnimation) {
			
			if (e.isPre()) {
				
				ItemRenderer ir = mc.getItemRenderer();
				float partialTicks = ir.partTicks;
				
				float f = 1.0F - (mc.getItemRenderer().prevEquippedProgress + (ir.equippedProgress - ir.prevEquippedProgress) * partialTicks);
				float swingProgress = mc.thePlayer.getSwingProgress(partialTicks);
				float swingProgressReversed = 1.0f - swingProgress;
				if (this.animationSetting.getMode() == "1.7" || this.animationSetting.is("1.7")) {
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(f, swingProgress);
					ir.func_178103_d();
				}
				else if (this.animationSetting.getMode() == "Spaz" || this.animationSetting.is("Spaz")) {
					if (swingProgress != 0.0f) {
						GlStateManager.translate(0.0f, 0.25f, 0.05f);
						ir.transformFirstPersonItem(swingProgress >= 0.45f ? swingProgressReversed : swingProgress, partialTicks / 20);
					}else {
						ir.transformFirstPersonItem(swingProgress / 20, 0);
					}
					ir.func_178103_d();
				}
				else if (this.animationSetting.getMode() == "Spaz 2" || this.animationSetting.is("Spaz 2")) {
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(0, (swingProgress == 0) ? 0 : partialTicks);
					ir.func_178103_d();
				}
				else if (this.animationSetting.getMode() == "Jitter" || this.animationSetting.is("Jitter")) {
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(0, (swingProgress == 0) ? 0 : partialTicks / 100);
					ir.func_178103_d();
				}
				else if (this.animationSetting.getMode() == "Multi Tap" || this.animationSetting.is("Multi Tap")) {
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(0, swingProgress - 2.5f);
					ir.func_178103_d();
				}
				else if (this.animationSetting.getMode() == "Tap" || this.animationSetting.is("Tap")) {
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(0, swingProgress - 1);
					ir.func_178103_d();
				}
				else if (this.animationSetting.getMode() == "Spin" || this.animationSetting.is("Spin")) {
					
					GlStateManager.translate(0.2f, 0.1f, -0.4f);
					ir.transformFirstPersonItem(0, 0);
					GlStateManager.rotate(-8, 0, 0, 1);
					GL11.glTranslatef(-1.0f, 0.4f, 0);
					GlStateManager.rotate(swingProgress * 360, 1, 0, -1);
					GL11.glTranslatef(1.0f, -0.4f, 0);
					ir.func_178103_d();
					
				}
				else if (this.animationSetting.getMode() == "Scale" || this.animationSetting.is("Scale")) {
					
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(0, 0);
					GlStateManager.scale(1.2, 1.2, 1.2);
					GlStateManager.scale(1 / (swingProgress + 1.4), 1 / (swingProgress + 1.4) , 1 / (swingProgress + 1.4));
					ir.func_178103_d();
					
				}
				else if (animationSetting.getMode() == "Spicy" || this.animationSetting.is("Spicy")) {
					
					GlStateManager.translate(0.56F, -0.32F, -0.71999997F);
					GlStateManager.scale(0.4F, 0.4F, 0.4F);
					GlStateManager.rotate(90, 1, 1, -4);
					GlStateManager.rotate(90, -1, 0, 0);
					GlStateManager.rotate((swingProgress < 0.5 ? swingProgress : swingProgressReversed) * -200, 2, 0, 0);
					
				}
				else if (animationSetting.getMode() == "Astolfo" || this.animationSetting.is("Astolfo")) {
					
					astolfoCircle(mc.thePlayer.getSwingProgress(partialTicks));
					ir.func_178103_d();
					
				}
				else if (animationSetting.is("Test")) {
					
					GlStateManager.translate(0.56F, -0.32F, -0.71999997F);
					GlStateManager.scale(0.4F, 0.4F, 0.4F);
					GlStateManager.rotate(90, 1, 1, -4);
					GlStateManager.rotate(90, -1, 0, 0);
					GlStateManager.rotate((swingProgress < 0.5 ? swingProgress : swingProgressReversed) * -250, 2, 0, 0);
					
				}
				
				//ir.func_178103_d();
				
			}
			
		}
		
	}
	
	private static transient int astolfoTicks = 0;
	
	private void astolfoCircle(float swingProgress) {
        float f1 = MathHelper.sin((float) (MathHelper.sqrt_float(swingProgress) * Math.PI));
        this.astolfoTicks++;
        GlStateManager.translate(-0.0F, -0.2F, -0.6F);
        GlStateManager.rotate(-this.astolfoTicks * 0.07F * 30.0F, 0.0F, 0.0F, -1.0F);
        GlStateManager.rotate(44.0F, 0.0F, 1.0F, 0.6F);
        GlStateManager.rotate(44.0F, 1.0F, 0.0F, -0.6F);
        GlStateManager.translate(1.0F, -0.2F, 0.5F);
        GlStateManager.rotate(-44.0F, 1.0F, 0.0F, -0.6F);
        GlStateManager.scale(0.5D, 0.5D, 0.5D);
      }
	
}
