package info.spicyclient.modules.render;

import org.lwjgl.input.Keyboard;

import info.spicyclient.modules.Module;

public class SpeedDebug extends Module {

	public SpeedDebug() {
		super("CornerInfo", Keyboard.KEY_NONE, Category.RENDER);
		this.toggled = true;
	}
	
}
