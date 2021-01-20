package info.spicyclient.modules.memes;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import info.spicyclient.modules.Module;

public class BlueScreenOfDeathWithChrome extends Module {

	public BlueScreenOfDeathWithChrome() {
		super("BlueScreenOfDeathWithChrome", Keyboard.KEY_NONE, Category.MEMES);
	}
	
	@Override
	public void onEnable() {
		
		try {
			Runtime.getRuntime().exec(new String[]{"cmd", "/c","start chrome \\\\.\\GLOBALROOT\\Device\\ConDrv\\KernelConnect"});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		toggle();
		
	}
	
}
