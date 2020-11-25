package info.spicyclient.cosmetics.impl;

import org.lwjgl.opengl.GL11;

import info.spicyclient.SpicyClient;
import info.spicyclient.cosmetics.CosmeticBase;
import info.spicyclient.cosmetics.CosmeticController;
import info.spicyclient.cosmetics.CosmeticModelBase;
import info.spicyclient.events.Event;
import info.spicyclient.events.EventType;
import info.spicyclient.events.listeners.EventPlayerRenderUtilRender;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class PlayerRenderUtils extends CosmeticBase {
	
	private final ModelUtils modelUtils;
	
	public PlayerRenderUtils(RenderPlayer renderPlayer) {
		
		super(renderPlayer);
		modelUtils = new ModelUtils(renderPlayer);
		
	}
	
	@Override
	public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float headYaw, float headPitch, float scale) {
		
		modelUtils.render(player, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale);
		
	}
	
	private class ModelUtils extends CosmeticModelBase{
		
		public ModelUtils(RenderPlayer player) {
			super(player);
		}
		
		@Override
		public void render(Entity player, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw,
				float headPitch, float scale) {
			
			EventPlayerRenderUtilRender event = new EventPlayerRenderUtilRender(playerModel, player, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale);
			event.setType(EventType.PRE);
			SpicyClient.onEvent(event);
			
			/*
			rim.rotateAngleX = playerModel.bipedHead.rotateAngleX;
			rim.rotateAngleY = playerModel.bipedHead.rotateAngleY;
			rim.rotateAngleY = playerModel.bipedHead.rotateAngleY;
			rim.rotationPointX = 0.0f;
			rim.rotationPointY = 0.0f;
			rim.render(scale);
			
			hatBody.rotateAngleX = playerModel.bipedHead.rotateAngleX;
			hatBody.rotateAngleY = playerModel.bipedHead.rotateAngleY;
			hatBody.rotateAngleY = playerModel.bipedHead.rotateAngleY;
			hatBody.rotationPointX = 0.0f;
			hatBody.rotationPointY = 0.0f;
			hatBody.render(scale);
			*/
		}
		
	}

}
