package info.spicyclient.modules.render;

import org.lwjgl.input.Keyboard;

import info.spicyclient.modules.Module;

public class FirstPerson extends Module {

	public FirstPerson() {
		super("FirstPerson", Keyboard.KEY_NONE, Category.RENDER);
	}

}
