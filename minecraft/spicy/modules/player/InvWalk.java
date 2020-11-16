package spicy.modules.player;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import spicy.events.Event;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;

public class InvWalk extends Module {

	public InvWalk() {
		super("Inventory Walk", Keyboard.KEY_NONE, Category.PLAYER);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			if (mc.currentScreen instanceof GuiChat) {
				return;
			}
			
			if (mc.currentScreen != null) {
				
				KeyBinding[] moveKeys = new KeyBinding[]{
    				mc.gameSettings.keyBindForward,
    				mc.gameSettings.keyBindBack,
    				mc.gameSettings.keyBindLeft,
    				mc.gameSettings.keyBindRight,
    				mc.gameSettings.keyBindJump					
    			};
    			for (KeyBinding bind : moveKeys){
    				KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode()));
    			}
    			
			}
			
		}
		
	}
	
}
