package info.spicyclient.cosmetics.impl;

import org.lwjgl.opengl.GL11;

import info.spicyclient.cosmetics.CosmeticBase;
import info.spicyclient.cosmetics.CosmeticController;
import info.spicyclient.cosmetics.CosmeticModelBase;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class ProtogenMask extends CosmeticBase {
	
	private final ModelProtogenMask modelProtogenMask;
	private static final ResourceLocation texture = new ResourceLocation("spicy/splash/SpicyClient.png");
	
	public ProtogenMask(RenderPlayer renderPlayer) {
		
		super(renderPlayer);
		modelProtogenMask = new ModelProtogenMask(renderPlayer);
		
	}
	
	@Override
	public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float headYaw, float headPitch, float scale) {
		
		
		// if (CosmeticController.shouldRenderTophat(player)) {
		if (false) {
			
			GlStateManager.pushMatrix();
			playerRenderer.bindTexture(texture);
			
			if (player.isSneaking()) {
				GL11.glTranslated(0D, 0.225D, 0D);
			}
			
			float[] color = CosmeticController.getTophatColor(player);
			GL11.glColor3f(color[0], color[1], color[2]);
			modelProtogenMask.render(player, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale);
			GL11.glColor3f(1, 1, 1);
			GlStateManager.popMatrix();
			
		}
		
	}
	
	private class ModelProtogenMask extends CosmeticModelBase{
		
		private ModelRenderer rim, hatBody, obj3;
		
		public ModelProtogenMask(RenderPlayer player) {
			super(player);
			
			rim = new ModelRenderer(playerModel, 0, 0);
			rim.addBox(-4f, -8, -10f, 8, 8, 6);
			
			obj3 = new ModelRenderer(playerModel, 0, 0);
			obj3.addBox(-4f, -8, -10f, 8, 8, 6);
			
			hatBody = new ModelRenderer(playerModel, 0, 0);
			hatBody.addBox(-2f, -6, -12f, 4, 4, 2);
			
		}
		
		@Override
		public void render(Entity player, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw,
				float headPitch, float scale) {
			
			rim = new ModelRenderer(playerModel, 0, 0);
			rim.addBox(-4f, -8, -10f, 8, 8, 6);
			
			obj3 = new ModelRenderer(playerModel, 0, 0);
			obj3.addBox(-4f, -8, -10f, 8, 8, 2);
			
			hatBody = new ModelRenderer(playerModel, 0, 0);
			hatBody.addBox(-2f, -6, -12f, 4, 4, 2);
			
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
			
		}
		
	}

}
