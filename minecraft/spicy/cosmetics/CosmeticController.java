package spicy.cosmetics;

import net.minecraft.client.entity.AbstractClientPlayer;
import spicy.SpicyClient;

public class CosmeticController {
	
	public static boolean shouldRenderTophat(AbstractClientPlayer player) {
		
		return SpicyClient.config.tophat.toggled;
		
	}
	
	public static float[] getTophatColor(AbstractClientPlayer player) {
		
		// R G B values should be between 0 and 1
		return new float[] {1, 0, 0};
		
	}
	
}
