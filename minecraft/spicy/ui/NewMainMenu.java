package spicy.ui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import spicy.SpicyClient;

public class NewMainMenu extends GuiScreen {
	
	private GuiScreen previousScreen;
	
	public NewMainMenu(GuiScreen previousScreen) {
		this.previousScreen = previousScreen;
	}
	
	@Override
	public void initGui() {
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		drawRect(0, 0, this.width, this.height, 0xff36393f);
		
		GlStateManager.pushMatrix();
		GlStateManager.scale(3.5, 3.5, 1);
		drawCenteredString(mc.fontRendererObj, SpicyClient.config.clientName + SpicyClient.config.clientVersion, (this.width / 2) / 3.5f, (this.height / 10) / 3.5f, 0xff7289da);
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		GlStateManager.scale(1.5, 1.5, 1);
		drawCenteredString(mc.fontRendererObj, "The open source minecraft client created by Lavaflowglow and Floofy Fox", (this.width / 2) / 1.5f, ((this.height / 10) + (mc.fontRendererObj.FONT_HEIGHT * 3.5f) + 15) / 1.5f, 0xff7289da);
		GlStateManager.popMatrix();
		
		String s1 = "Source code and downloads available at https://SpicyClient.info";
        this.drawString(this.fontRendererObj, s1, this.width - this.fontRendererObj.getStringWidth(s1) - 2, this.height - 10, 0xff7289da);
        
        drawRect(this.width / 2 + 100, this.height / 2 - 40, this.width / 2 + 220, this.height / 2 + 80, 0xff202225);
        drawRect(this.width / 2 - 60, this.height / 2 - 40, this.width / 2 + 60, this.height / 2 + 80, 0xff202225);
        drawRect(this.width / 2 - 220, this.height / 2 - 40, this.width / 2 - 100, this.height / 2 + 80, 0xff202225);
        
		// To prevent the text from blinking
		// The max fps is 30
		long fps = 15;
		try {
			Thread.sleep(1000 / fps);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		if (Keyboard.KEY_ESCAPE == keyCode) {
			mc.displayGuiScreen(previousScreen);
		}
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
		
	}
	
}
