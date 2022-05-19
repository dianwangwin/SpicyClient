package info.spicyclient.modules.render;

import org.lwjgl.input.Keyboard;

import info.spicyclient.modules.Module;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class SpicyAntiAntiXray extends Module {

	public SpicyAntiAntiXray() {
		super("SpicyAntiAntiXray", Keyboard.KEY_NONE, Category.RENDER);
	}
	
	@Override
	public void onEnable() {
		for (short x = -12; x < 12; x += 3) {
			for (short y = -12; y < 12; y += 3) {
				for (short z = -12; z < 12; z += 3) {
					
					//mc.thePlayer.swingItem();
					mc.playerController.curBlockDamageMP = 1.0f;
					mc.playerController.onPlayerDamageBlock(new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z), EnumFacing.UP);
					
				}
			}
		}
		toggle();
	}
	
}
