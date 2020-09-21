package spicy.modules.render;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.network.play.server.S02PacketChat;
import spicy.events.Event;
import spicy.events.listeners.EventPacket;
import spicy.events.listeners.EventSwordBlockAnimation;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.ModeSetting;

public class OldHitting extends Module {
	
	public static ModeSetting animationSetting = new ModeSetting("Animation", "1.7", "1.7", "Spaz", "Spaz 2", "Jitter", "Tap", "Multi Tap", "Spin", "Scale");
	
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
				
				if (animationSetting.getMode() == "1.7") {
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(f, swingProgress);
				}
				else if (animationSetting.getMode() == "Spaz") {
					GlStateManager.translate(0.0f, 0.25f, 0.05f);
					ir.transformFirstPersonItem(0.0f, (0.3f * partialTicks) * swingProgress);
				}
				else if (animationSetting.getMode() == "Spaz 2") {
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(0, (swingProgress == 0) ? 0 : partialTicks);
				}
				else if (animationSetting.getMode() == "Jitter") {
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(0, (swingProgress == 0) ? 0 : partialTicks / 100);
				}
				else if (animationSetting.getMode() == "Multi Tap") {
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(0, -swingProgress * 2);
				}
				else if (animationSetting.getMode() == "Tap") {
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(0, -swingProgress);
				}
				else if (animationSetting.getMode() == "Spin") {
					
					ir.renderItem(mc.thePlayer, mc.thePlayer.getHeldItem(), TransformType.FIRST_PERSON);
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(0, 0.3f);
					GlStateManager.rotate(swingProgress * 360, 1, 0, -1);
					
				}
				else if (animationSetting.getMode() == "Scale") {
					GlStateManager.translate(-0.15f, 0.15f, -0.2f);
					ir.transformFirstPersonItem(0, 0);
					GlStateManager.scale(1.2, 1.2, 1.2);
					GlStateManager.scale(1 / (swingProgress + 1.4), 1 / (swingProgress + 1.4) , 1 / (swingProgress + 1.4));
				}
				
				ir.func_178103_d();
				
			}
			
		}
		
	}
	
}
