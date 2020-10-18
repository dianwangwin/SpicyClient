package spicy.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;

public class RenderUtils {
	
	public static Minecraft mc = Minecraft.getMinecraft();
	public static WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
	public static Tessellator tessellator = Tessellator.getInstance();
	
}
