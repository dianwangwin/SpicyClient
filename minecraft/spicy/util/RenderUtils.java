package spicy.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;

public final class RenderUtils {
	
	public static void renderNametag(AxisAlignedBB bb) {
		
		Tessellator tessellator = Tessellator.getInstance();
	    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
	    worldRenderer.sVertexBuilder.beginAddVertex(worldRenderer);
	    int[] temp = {(int) bb.minX, (int) bb.minY, (int) bb.minZ};
	    worldRenderer.addVertexData(temp);
	    tessellator.draw();
	}
	
}