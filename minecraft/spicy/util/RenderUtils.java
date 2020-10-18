package spicy.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class RenderUtils {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
	public static Tessellator tessellator = Tessellator.getInstance();
	
	public static void drawPlayerBox(Double posX, Double posY, Double posZ, AbstractClientPlayer player){
		double x =
			posX - 0.5
				- Minecraft.getMinecraft().getRenderManager().renderPosX;
		double y =
			posY
				- Minecraft.getMinecraft().getRenderManager().renderPosY;
		double z =
			posZ - 0.5
				- Minecraft.getMinecraft().getRenderManager().renderPosZ;
		GL11.glBlendFunc(770, 771);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glLineWidth(100.0F);
		GL11.glColor4d(0, 1, 0, 0.15F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(false);
		//drawColorBox(new AxisAlignedBB(x, y, z, x + 1.0, y + 1.0, z + 1.0));
		GL11.glColor4d(1, 1, 1, 0.5F);
		RenderGlobal.func_181561_a(new AxisAlignedBB(x, y, z,
			x + 1.0, y + 2.0, z + 1.0));
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthMask(true);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glColor4f(1, 1, 1, 1);
	}
	
}
