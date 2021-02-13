package info.spicyclient.modules.render;

import org.lwjgl.input.Keyboard;

import info.spicyclient.dragon.RenderWings;
import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventPlayerRender;
import info.spicyclient.events.listeners.EventRender3D;
import info.spicyclient.modules.Module;

public class DragonWings extends Module {

	public DragonWings() {
		super("DragonWings", Keyboard.KEY_NONE, Category.RENDER);
	}
	
	public static float partialTicks = 0;
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventRender3D && e.isPre()) {
			partialTicks = ((EventRender3D)e).renderPartialTicks;
		}
		
		if (e instanceof EventPlayerRender & e.isPre()) {
			
			EventPlayerRender event = (EventPlayerRender) e;
			
			RenderWings.getWings().onRenderPlayer(event.entity, partialTicks);
			
		}
		
	}
	
}
