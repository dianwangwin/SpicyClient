package info.spicyclient.ui.Jello;

import java.awt.Color;
import java.awt.Robot;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import info.spicyclient.SpicyClient;
import info.spicyclient.events.EventType;
import info.spicyclient.events.listeners.EventRenderGUI;
import info.spicyclient.modules.Module;
import info.spicyclient.modules.Module.Category;
import info.spicyclient.ui.fonts.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

public class JelloHud extends GuiScreen {

	public Timer timer;

	public info.spicyclient.util.Timer timer2 = new info.spicyclient.util.Timer();
	
	Robot r;
	public static FontRenderer fr;
	public double arrayTrans;
	public double leftHudTrans;
	public boolean transOver;
	public boolean timerStarted;
	public Random rand = new Random();
	public int tRed, tGreen, tBlue;
	public int lasttRed, lasttGreen, lasttBlue;

	public int nRed, nGreen, nBlue;
	public int lastnRed, lastnGreen, lastnBlue;

	public int nbRed, nbGreen, nbBlue;
	public int lastnbRed, lastnbGreen, lastnbBlue;

	public int bRed, bGreen, bBlue;
	public int lastbRed, lastbGreen, lastbBlue;
	public Color top = new Color(255, 255, 255, 255);
	public Color bottom = new Color(255, 255, 255, 255);
	public Color notif = new Color(255, 255, 255, 255);
	Thread colorThread;

	int colorTop, colorTopRight, colorBottom, colorBottomRight, colorNotification = 0, colorNotificationBottom = 0;

	public JelloHud() {
		this.mc = Minecraft.getMinecraft();
		this.fr = mc.fontRendererObj;
		this.timer = new Timer();

	}

	public void renderScreen() {

		this.calculateTransitions();

		if (!this.transOver) {
			GL11.glPushMatrix();
			// GL11.glTranslated(0-this.leftHudTrans, 0, 0);
		}
		this.renderHud();
		if (!this.transOver) {
			GL11.glPopMatrix();
			GL11.glPushMatrix();
		}
		if (!this.transOver) {
			// GL11.glTranslated(this.arrayTrans, 0, 0);
		}

		this.renderArraylist();

		if (!this.transOver) {
			GL11.glPopMatrix();
		}
		
		ScaledResolution sr = new ScaledResolution(this.mc);

		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();
		
	}
	
	public void calculateTransitions() {
		if (arrayTrans > 0 && timerStarted && timer2.hasTimeElapsed(1100, false)) {
			arrayTrans -= (arrayTrans / (8 * Minecraft.getMinecraft().getDebugFPS() * 0.04)) + 0.01;
		} else if (arrayTrans <= 1 && leftHudTrans <= 1) {
			transOver = true;
			timerStarted = false;
		}
		if (leftHudTrans > 0 && timerStarted && timer2.hasTimeElapsed(1100, false)) {
			leftHudTrans -= (leftHudTrans / (8 * Minecraft.getMinecraft().getDebugFPS() * 0.04)) + 0.01;
		} else if (leftHudTrans <= 1 && arrayTrans <= 1) {
			transOver = true;
			timerStarted = false;
		}
	}
	
	public void onTick() {

		lasttRed = tRed;
		lasttGreen = tGreen;
		lasttBlue = tBlue;

		lastnRed = nRed;
		lastnGreen = nGreen;
		lastnBlue = nBlue;

		lastnbRed = nbRed;
		lastnbGreen = nbGreen;
		lastnbBlue = nbBlue;

		lastbRed = bRed;
		lastbGreen = bGreen;
		lastbBlue = bBlue;
		
		bRed += ((bottom.getRed() - bRed) / (5)) + 0.1;
		bGreen += ((bottom.getGreen() - bGreen) / (5)) + 0.1;
		bBlue += ((bottom.getBlue() - bBlue) / (5)) + 0.1;

		tRed += ((top.getRed() - tRed) / (5)) + 0.1;
		tGreen += ((top.getGreen() - tGreen) / (5)) + 0.1;
		tBlue += ((top.getBlue() - tBlue) / (5)) + 0.1;

		nRed += ((ColorUtil.colorFromInt(colorNotification).getRed() - nRed) / (5)) + 0.1;
		nGreen += ((ColorUtil.colorFromInt(colorNotification).getGreen() - nGreen) / (5)) + 0.1;
		nBlue += ((ColorUtil.colorFromInt(colorNotification).getBlue() - nBlue) / (5)) + 0.1;

		nbRed += ((ColorUtil.colorFromInt(colorNotificationBottom).getRed() - nbRed) / (5)) + 0.1;
		nbGreen += ((ColorUtil.colorFromInt(colorNotificationBottom).getGreen() - nbGreen) / (5)) + 0.1;
		nbBlue += ((ColorUtil.colorFromInt(colorNotificationBottom).getBlue() - nbBlue) / (5)) + 0.1;

		tRed = Math.min((int) tRed, 255);
		tGreen = Math.min((int) tGreen, 255);
		tBlue = Math.min((int) tBlue, 255);
		tRed = Math.max((int) tRed, 0);
		tGreen = Math.max((int) tGreen, 0);
		tBlue = Math.max((int) tBlue, 0);

		nRed = Math.min((int) nRed, 255);
		nGreen = Math.min((int) nGreen, 255);
		nBlue = Math.min((int) nBlue, 255);
		nRed = Math.max((int) nRed, 0);
		nGreen = Math.max((int) nGreen, 0);
		nBlue = Math.max((int) nBlue, 0);

		nbRed = Math.min((int) nbRed, 255);
		nbGreen = Math.min((int) nbGreen, 255);
		nbBlue = Math.min((int) nbBlue, 255);
		nbRed = Math.max((int) nbRed, 0);
		nbGreen = Math.max((int) nbGreen, 0);
		nbBlue = Math.max((int) nbBlue, 0);

		bRed = Math.min((int) bRed, 255);
		bGreen = Math.min((int) bGreen, 255);
		bBlue = Math.min((int) bBlue, 255);
		bRed = Math.max((int) bRed, 0);
		bGreen = Math.max((int) bGreen, 0);
		bBlue = Math.max((int) bBlue, 0);
	}

	public void renderHud() {

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		ScaledResolution sr = new ScaledResolution(this.mc);

		if (timer.hasReach(50)) {
			int p_148259_2_ = 0, p_148259_3_ = 0;
			IntBuffer pixelBuffer = null;
			int[] pixelValues = null;

			if (OpenGlHelper.isFramebufferEnabled()) {
				p_148259_2_ = 180;
				p_148259_3_ = 280;
			}

			int var6 = p_148259_2_ * p_148259_3_;

			if (pixelBuffer == null || pixelBuffer.capacity() < var6) {
				pixelBuffer = BufferUtils.createIntBuffer(var6);
				pixelValues = new int[var6];
			}

			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			pixelBuffer.clear();

			GL11.glReadPixels(0, sr.getScaledHeight() - (p_148259_3_ - sr.getScaledHeight())/* 728 */, p_148259_2_,
					p_148259_3_, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);

			pixelBuffer.get(pixelValues);
			//TextureUtil.func_147953_a(pixelValues, p_148259_2_, p_148259_3_);
			TextureUtil.copyToBufferPos(pixelValues, p_148259_2_, p_148259_3_);

			// if(!(mc.currentScreen instanceof GuiChat)){
			colorTop = pixelValues[(45 * sr.getScaleFactor()) * p_148259_2_ + 10];
			colorTopRight = pixelValues[(45 * sr.getScaleFactor()) * p_148259_2_ + 130];

			colorBottom = pixelValues[((45 + 77) * sr.getScaleFactor()) * p_148259_2_ + 10];
			colorBottomRight = pixelValues[((45 + 77) * sr.getScaleFactor()) * p_148259_2_ + 130];
			// }

			p_148259_2_ = 0;
			p_148259_3_ = 0;
			pixelBuffer = null;
			pixelValues = null;

			if (OpenGlHelper.isFramebufferEnabled()) {
				p_148259_2_ = 280;
				p_148259_3_ = 150;
			}

			var6 = p_148259_2_ * p_148259_3_;

			if (pixelBuffer == null || pixelBuffer.capacity() < var6) {
				pixelBuffer = BufferUtils.createIntBuffer(var6);
				pixelValues = new int[var6];
			}

			GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
			GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
			pixelBuffer.clear();

			GL11.glReadPixels(sr.getScaledWidth() - (p_148259_2_ - sr.getScaledWidth()),
					sr.getScaledHeight() - (p_148259_3_ - sr.getScaledHeight())/* 728 */, p_148259_2_, p_148259_3_,
					GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);

			pixelBuffer.get(pixelValues);
			//TextureUtil.func_147953_a(pixelValues, p_148259_2_, p_148259_3_);
			TextureUtil.copyToBufferPos(pixelValues, p_148259_2_, p_148259_3_);

			colorNotification = pixelValues[(10) * p_148259_2_ + 270];
			colorNotificationBottom = pixelValues[(77) * p_148259_2_ + 270];

			timer.reset();
		}
		
		EventRenderGUI event = new EventRenderGUI();
		event.setType(EventType.PRE);
		
		info.spicyclient.SpicyClient.onEvent(event);
		
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableBlend();
		GlStateManager.disableAlpha();

		//this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/Jello.png"));
		this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/JelloForSpicy.png"));
		Gui.drawModalRectWithCustomSizedTexture(0.5 - 1, 2 - 2.5, 0, 0, 86, 49, 86, 49);
		
		int yOff = 0;

		GlStateManager.disableAlpha();
		GlStateManager.enableBlend();
		
		int tR = smoothAnimation(tRed, lasttRed);
		int tG = smoothAnimation(tGreen, lasttGreen);
		int tB = smoothAnimation(tBlue, lasttBlue);

		int bR = smoothAnimation(bRed, lastbRed);
		int bG = smoothAnimation(bGreen, lastbGreen);
		int bB = smoothAnimation(bBlue, lastbBlue);
		
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}

	public int smoothAnimation(double current, double last) {
		return (int) (current * mc.timer.renderPartialTicks + (last * (1.0f - mc.timer.renderPartialTicks)));
	}

	public float smoothTrans(double current, double last) {
		return (float) (current * mc.timer.renderPartialTicks + (last * (1.0f - mc.timer.renderPartialTicks)));
	}

	public void renderArraylist() {
		ScaledResolution sr = new ScaledResolution(this.mc);
		// int count = 0;
		float yStart = 1;
		float xStart = 0;
		int colorFade = 0;
		
		CopyOnWriteArrayList<Module> modules = new CopyOnWriteArrayList<Module>(SpicyClient.modules);
		
		Collections.sort(SpicyClient.modules, new ModuleComparator());
		for (Module module : SpicyClient.modules) {
			if (!module.isToggled())
				continue;
			xStart = (float) (sr.getScaledWidth() - FontUtil.jelloFont.getStringWidth(module.name) - 5);
			int color = Color.green.getRGB() + 00;
			GL11.glPushMatrix();
			GL11.glColor4f(1, 1, 1, 1);
			// this.handleAnimations(module);
			// this.drawPrefrences(xStart, yStart, color);
			GL11.glPopMatrix();
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.color(1, 1, 1, 1);
			// if(module.animation >= 0){
			this.mc.getTextureManager().bindTexture(new ResourceLocation("Jello/arraylistshadow.png"));
			// GlStateManager.disableBlend();
			// Gui.drawFloatRect(xStart -
			// (float)FontUtil.jelloFont.getStringWidth(module.getDisplayName())/2.5f + 5,
			// 0, sr.getScaledWidth(), 100, -1);
			// if(yStart < 10)
			GlStateManager.color(1, 1, 1, 0.7f);
			this.drawModalRectWithCustomSizedTexture(xStart - 8 - 2 - 1, yStart + 2 - 2.5f - 1.5f - 1.5f - 1.5f - 6 - 1,
					0, 0, FontUtil.jelloFont.getStringWidth(module.name) * 1 + 20 + 10, 18.5 + 6 + 12 + 2,
					FontUtil.jelloFont.getStringWidth(module.name) * 1 + 20 + 10, 18.5 + 6 + 12 + 2);
			// FontUtil.jelloFont.drawString(module.getDisplayName(), xStart, yStart + 7.5f,
			// 0xffffffff);
			// }
			yStart += 7.5f + 5.25f;
			if ((module.animation != -5)) {
				colorFade++;
				if (colorFade > 50) {
					colorFade = 0;
				}
			}
		}
		yStart = 1;
		xStart = 0;
		colorFade = 0;
		for (Module module : SpicyClient.modules) {
			if (!module.isToggled())
				continue;

			xStart = (float) (sr.getScaledWidth() - FontUtil.jelloFont.getStringWidth(module.name) - 5);
			int color = Color.green.getRGB() + 00;
			GL11.glPushMatrix();
			GL11.glColor4f(1, 1, 1, 1);
			// this.handleAnimations(module);
			// this.drawPrefrences(xStart, yStart, color);
			GL11.glPopMatrix();
			GlStateManager.enableBlend();
			GlStateManager.disableAlpha();
			GlStateManager.color(1, 1, 1, 1);
			if (module.animation >= 0) {
				// this.mc.getTextureManager().bindTexture(new
				// ResourceLocation("Jello/arraylistshadow.png"));
				// GlStateManager.disableBlend();
				// Gui.drawFloatRect(xStart -
				// (float)FontUtil.jelloFont.getStringWidth(module.getDisplayName())/2.5f + 5,
				// 0, sr.getScaledWidth(), 100, -1);
				// this.drawModalRectWithCustomSizedTexture(xStart - 8, yStart + 2 - 2.5f - 1.5f
				// - 1.5f - 0.5f, 0, 0,
				// FontUtil.jelloFont.getStringWidth(module.getDisplayName())*1.8f + 5, 18.5 +
				// 10, FontUtil.jelloFont.getStringWidth(module.getDisplayName())*1.8f + 5, 18.5
				// + 10);
				FontUtil.jelloFont.drawString(module.name, xStart, yStart + 7.5f, 0xffffffff);
			}
			yStart += 7.5f + 5.25f;// module.animHeight;
			if ((module.animation != -5)) {
				colorFade++;
				if (colorFade > 50) {
					colorFade = 0;
				}
			}
		}
		SpicyClient.modules = modules;
	}

	public static class ModuleComparator implements Comparator<Module> {
		@Override
		public int compare(Module o1, Module o2) {

			if (FontUtil.jelloFont.getStringWidth(o1.name) < FontUtil.jelloFont
					.getStringWidth(o2.name)) {
				return 1;
			}
			if (FontUtil.jelloFont.getStringWidth(o1.name) > FontUtil.jelloFont
					.getStringWidth(o2.name)) {
				return -1;
			}

			return 0;
		}
	}

	public class Timer {

		private long lastCheck = getSystemTime();

		public boolean hasReach(float mil) {
			return getTimePassed() >= (mil);
		}

		public boolean hasReach(double mil) {
			return getTimePassed() >= (mil);
		}

		public void reset() {
			lastCheck = getSystemTime();
		}

		private long getTimePassed() {
			return getSystemTime() - lastCheck;
		}

		private long getSystemTime() {
			return System.nanoTime() / (long) (1E6);
		}

	}

	protected void clearScreen(int displayWidth, int displayHeight, float zDepth) {
		GL11.glClearDepth(999.0D);
		GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
		GL11.glClear(16640);

		GL11.glDisable(3553);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(518, 0.0F);
		GL11.glBlendFunc(1, 0);
		GL11.glShadeModel(7424);
		GL11.glEnable(2929);
		GL11.glDepthMask(true);
		GL11.glDepthFunc(519);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer wr = tessellator.getWorldRenderer();
		//wr.startDrawingQuads();
		//wr.func_178960_a(0.0F, 0.0F, 0.0F, 1.0F);
		//wr.addVertex(displayWidth, 0.0D, -zDepth);
		//wr.addVertex(0.0D, 0.0D, -zDepth);
		//wr.addVertex(0.0D, displayHeight, -zDepth);
		//wr.addVertex(displayWidth, displayHeight, -zDepth);
		//wr.draw();
		GL11.glDepthFunc(515);
		GL11.glEnable(3008);
		GL11.glAlphaFunc(516, 0.1F);
		GL11.glEnable(3553);
		GL11.glDepthMask(true);
	}
	
	public float getNormalYaw(int offset) {
		return normalise(this.mc.thePlayer.rotationYaw, -90 + offset, 90 + offset) * 2.0F;
	}

	public float normalise(double value, double start, double end) {
		double width = end - start;
		double offsetValue = value - start;

		return (float) (offsetValue - Math.floor(offsetValue / width) * width + start);
	}

	private int getColorForCompass(String offset) {
		float normalYaw = getNormalYaw(Integer.valueOf(offset).intValue());

		int color = (int) Math.min(255.0F, Math.max(1.0F,
				Math.abs(-255.0F + (!String.valueOf(offset).contains("-")
						? (normalYaw + Float.valueOf(offset).floatValue() * 2.0F) / 2.0F / 90.0F * 255.0F
						: -((normalYaw + Float.valueOf(offset).floatValue() * 2.0F) / 2.0F / 90.0F * 255.0F))) + 4.0F));
		if (color == 32) {
			color = 31;
		}
		if (color == 66) {
			color = 64;
		}
		if (color == 67) {
			color = 65;
		}
		if (color == 35) {
			color = 34;
		}
		return new Color(255, 255, 255, color).getRGB();
	}

	public int getColorForCompass(float offset, float otherOffset) {
		float normalYaw = getNormalYaw((int) offset + (int) otherOffset);

		int color = (int) Math.min(255.0F,
				Math.max(1.0F,
						Math.abs(-255.0F + (!String.valueOf(offset).contains("-")
								? (normalYaw + offset * 2.0F) / 2.0F / 90.0F * 255.0F
								: -((normalYaw + offset * 2.0F) / 2.0F / 90.0F * 255.0F))) + 4.0F));
		if (color == 32) {
			color = 31;
		}
		if (color == 66) {
			color = 65;
		}
		if (color == 67) {
			color = 66;
		}
		if (color == 35) {
			color = 34;
		}
		return new Color(255, 255, 255, color).getRGB();
	}

	private void drawPrefrences(float xStart, float yStart, int color) {
		ScaledResolution sr = new ScaledResolution(this.mc);
		/** Drawing the real background **/
		// Gui.drawFloatRect(((xStart - 3)), ((yStart + 12)), (sr.getScaledWidth()),
		// (int) ((yStart - 2)), 0xff000000);
		Gui.drawRect(((xStart - 0)), ((yStart + 9)), (sr.getScaledWidth()), ((yStart - 1)),
				new Color(15, 15, 15, 255).getRGB() + 00);
		// if (this.boxes.getBooleanValue())
		// Gui.drawRect((int) ((sr.getScaledWidth() - 2)), (int) (((yStart - 16))),
		// (int) ((sr.getScaledWidth())), (int) ((yStart - 2)), color);
		// if (this.connect.getBooleanValue()) {
		Gui.drawHLine(((xStart - 0)), ((sr.getScaledWidth())), ((yStart + 9)), 0xffffffff);
		Gui.drawVLine(((xStart - 0)), ((yStart - 2)), ((yStart + 9)), 0xffffffff);
		// }

	}

	public void onWorldLoad() {
		timer2.reset();
		ScaledResolution sr = new ScaledResolution(this.mc);
		this.arrayTrans = 120;
		this.leftHudTrans = 170;
		this.timerStarted = true;
		this.transOver = false;
	}
}
