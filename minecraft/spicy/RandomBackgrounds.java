package spicy;

import net.minecraft.util.*;

public enum RandomBackgrounds {
	
	SPICYCLIENT(new ResourceLocation("spicy/splash/SpicyClient.png")),
	GREENDEBUG(new ResourceLocation("spicy/splash/debugGreen.png"));
	
	public ResourceLocation image;
	
	RandomBackgrounds(ResourceLocation image) {
		
		this.image = image;
		
	}
	
}
