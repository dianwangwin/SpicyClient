package info.spicyclient.cosmetics.impl;

import org.lwjgl.opengl.GL11;

import info.spicyclient.SpicyClient;
import info.spicyclient.cosmetics.CosmeticBase;
import info.spicyclient.cosmetics.CosmeticController;
import info.spicyclient.cosmetics.CosmeticModelBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class FirstPerson extends CosmeticBase {
	
	private final lol modelTophat;
	private static final ResourceLocation texture = new ResourceLocation("spicy/hat.png");
	
	public FirstPerson(RenderPlayer renderPlayer) {
		
		super(renderPlayer);
		modelTophat = new lol(renderPlayer);
		
	}
	
	@Override
	public void render(AbstractClientPlayer player, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float headYaw, float headPitch, float scale) {
		
		
		if (SpicyClient.config.firstPerson.isEnabled()) {
			
			GlStateManager.pushMatrix();
			playerRenderer.bindTexture(texture);
			
			if (player.isSneaking()) {
				GL11.glTranslated(0D, 0.225D, 0D);
			}
			
			float[] color = CosmeticController.getTophatColor(player);
			GL11.glColor3f(1, 1, 1);
			modelTophat.render(player, limbSwing, limbSwingAmount, ageInTicks, headYaw, headPitch, scale);
			GL11.glColor3f(1, 1, 1);
			GlStateManager.popMatrix();
			
		}else {
			modelTophat.playerModel.bipedHead.isHidden = false;
		}
		
	}
	
	private class lol extends CosmeticModelBase{
		
		private ModelRenderer rim;
		private ModelRenderer hatBody;
		
		public lol(RenderPlayer player) {
			super(player);
			
		}
		
		@Override
		public void render(Entity player, float limbSwing, float limbSwingAmount, float ageInTicks, float headYaw,
				float headPitch, float scale) {
			
			if (player == Minecraft.getMinecraft().thePlayer && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
				playerModel.bipedHead.isHidden = true;
			}
			else if (player == Minecraft.getMinecraft().thePlayer) {
				playerModel.bipedHead.isHidden = false;
			}
			
		}
		
	}

}
