package info.spicyclient.modules.render;

import org.lwjgl.input.Keyboard;

import info.spicyclient.modules.Module;

public class Cape extends Module {

	public Cape() {
		super("Cape", Keyboard.KEY_NONE, Category.RENDER);
		this.toggled = true;
	}

}
