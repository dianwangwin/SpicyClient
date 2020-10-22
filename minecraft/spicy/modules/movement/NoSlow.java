package spicy.modules.movement;

import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import spicy.chatCommands.Command;
import spicy.events.Event;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;

public class NoSlow extends Module {

	public NoSlow() {
		super("NoSlow", Keyboard.KEY_NONE, Category.MOVEMENT);
	}

	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	public void onEvent(Event e) {
		
	}
	
}
