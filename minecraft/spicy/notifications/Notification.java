package spicy.notifications;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import spicy.chatCommands.Command;

public class Notification {
	
	public Notification(String title, String text, boolean showTimer, long timeOnScreen, Type type, Color color, int targetX, int targetY, int startingX, int startingY, int speed) {
		
		this.title = title;
		this.text = text;
		this.showTimer = showTimer;
		this.timeOnScreen = System.currentTimeMillis() + timeOnScreen;
		this.originalTime = timeOnScreen;
		this.type = type;
		this.color = color;
		this.targetX = targetX;
		this.targetY = targetY;
		this.startingX = startingX;
		this.startingY = startingY;
		this.speed = speed;
		
	}
	
	public String title = "", text = "";
	public boolean showTimer;
	public long timeOnScreen;
	public long originalTime;
	public final Type type;
	public final Color color;
	public int targetX, targetY, startingX, startingY, speed;
	
	// Just so notifications don't get stuck
	public boolean leaving = false;
	public boolean left = false;
	
	public void onRender() {
		
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer fr = mc.fontRendererObj;
		ScaledResolution sr = new ScaledResolution(mc);
		
		if (System.currentTimeMillis() >= timeOnScreen) {
			leaving = true;
			targetY = sr.getScaledHeight() + 10;
		}
		
		if (startingX < targetX) {
			startingX += speed;
		}
		else if (startingX > targetX) {
			startingX -= speed;
		}
		
		if (startingY < targetY) {
			startingY += speed;
		}
		else if (startingY > targetY) {
			startingY -= speed;
		}
		
		Gui.drawRect(startingX, startingY, startingX + 170, startingY + 45, 0xff202225);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(4, 4, 0);
		GlStateManager.scale(1.1, 1.1, 1);
		GlStateManager.translate(-4, -4, 0);
		fr.drawString(title, (int) ((startingX + 45) / 1.1), (int) ((int) ((startingY + 15 - (fr.FONT_HEIGHT / 2))) / 1.1), color.color);
		GlStateManager.popMatrix();
		
		mc.getTextureManager().bindTexture(new ResourceLocation("spicy/" + type.filePrefix + color.fileSuffix + ".png"));
		int size = 30;
		Gui.drawModalRectWithCustomSizedTexture(startingX + 4, startingY + 5, 0, 0, size, size, size, size);
		
		if (showTimer && timeOnScreen - System.currentTimeMillis() > 0) {
			
			Gui.drawRect(startingX, startingY + 40, startingX + ((((double)170) / originalTime) * ((timeOnScreen - System.currentTimeMillis()))), startingY + 45, color.color);
			
		}
		
		
		if (startingY == targetY && leaving) {
			left = true;
			NotificationManager.getNotificationManager().notifications.remove(this);
		}
		
	}
	
}
