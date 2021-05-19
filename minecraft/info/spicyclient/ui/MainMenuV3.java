package info.spicyclient.ui;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import info.spicyclient.SpicyClient;
import info.spicyclient.util.Data5d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class MainMenuV3 extends GuiScreen {
	
	public MainMenuV3() {
		for (Data5d data : buttons) {
			data.data = 0;
		}
	}
	
	public Data5d singlePlayerButton = new Data5d(), multiPlayerButton = new Data5d(), altManagerButton = new Data5d(), settingsButton = new Data5d(),
			languageButton = new Data5d();
	
	public ArrayList<Data5d> buttons = new ArrayList<Data5d>(Arrays.asList(singlePlayerButton, multiPlayerButton, altManagerButton, settingsButton, languageButton));
	
	public ResourceLocation background = new ResourceLocation("spicy/MainMenuV3/background.jpg");
	
	public ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
	
	@Override
	public void initGui() {
		
		SpicyClient.discord.update("In the main menu");
		SpicyClient.discord.refresh();
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		mc.getTextureManager().bindTexture(new ResourceLocation("spicy/mainMenuV3/background.jpg"));
		int imageWidth = 3840, imageHeight = 2160;
		Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);
		
		// Everything breaks if this is more than 20
		try {
			Thread.sleep(1000 / 18);
		} catch (InterruptedException e) {
			
		}
		
	}
	
}
