package info.spicyclient.modules.render;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import info.spicyclient.events.Event;
import info.spicyclient.events.listeners.EventRenderLivingLabel;
import info.spicyclient.events.listeners.EventUpdate;
import info.spicyclient.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class NameTags extends Module {

	public NameTags() {
		super("NameTags", Keyboard.KEY_NONE, Category.RENDER);
	}
	
	@Override
	public void onEvent(Event e) {
		
		if (e instanceof EventRenderLivingLabel) {
			
			EventRenderLivingLabel nametag = (EventRenderLivingLabel)e;
			
			if (e.isPre()) {
				GlStateManager.pushMatrix();
                GL11.glEnable(32823);
                GL11.glPolygonOffset(1.0f, -1100000.0f);
                nametag.maxDistance = 100;
			}
			else if (e.isPost()) {
                GL11.glDisable(32823);
                GL11.glPolygonOffset(1.0f, 1100000.0f);
                GlStateManager.popMatrix();
			}
			
		}
		
		if (e instanceof EventUpdate && e.isPre()) {
			
			try {
				
				for (Object maybeEntity : mc.theWorld.loadedEntityList) {
					
					if (maybeEntity instanceof Entity) {
						Entity entity = ((Entity)maybeEntity);
						entity.setAlwaysRenderNameTag(true);
						entity.setCustomNameTag(entity.getName());
					}
					
				}
				
			} catch (Exception e2) {
				
			}
			
		}
		
	}
	
}
