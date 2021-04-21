package info.spicyclient.hudModules.impl;

import info.spicyclient.SpicyClient;
import info.spicyclient.hudModules.HudModule;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;

public class Keystrokes1 extends HudModule {
	
	@Override
	protected void onRender(boolean fakeRender) {
		
		if (!hasSetSize) {
			setSize(5, 60 + 207, 130 + 5, 30 + 130);
		}
		
		Gui.drawRect(5, 115 + 130, 45, 75 + 130, mc.gameSettings.keyBindLeft.isKeyDown() ? 0x90ffffff : 0x90000000);
		
		Gui.drawRect(50, 115 + 130, 90, 75 + 130, mc.gameSettings.keyBindBack.isKeyDown() ? 0x90ffffff : 0x90000000);
		
		Gui.drawRect(95, 115 + 130, 135, 75 + 130, mc.gameSettings.keyBindRight.isKeyDown() ? 0x90ffffff : 0x90000000);
		
		Gui.drawRect(50, 70 + 130, 90, 30 + 130, mc.gameSettings.keyBindForward.isKeyDown() ? 0x90ffffff : 0x90000000);
		
		Gui.drawRect(5, 60 + 207, 135, 40 + 210, mc.gameSettings.keyBindJump.isKeyDown() ? 0x90ffffff : 0x90000000);
		
	}
	
}
