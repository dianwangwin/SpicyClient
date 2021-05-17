package info.spicyclient.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class WorldUtils {
	
	public static BlockPos getForwardBlock(double length) {
		
		Minecraft mc = Minecraft.getMinecraft();
        final double yaw = Math.toRadians(mc.thePlayer.rotationYaw);
        BlockPos fPos = new BlockPos(mc.thePlayer.posX + (-Math.sin(yaw) * length), mc.thePlayer.posY, mc.thePlayer.posZ + (Math.cos(yaw) * length));
        return fPos;
		
	}
	
    public static double getDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double d0 = x1 - x2;
        double d1 = y1 - y2;
        double d2 = z1 - z2;
        return (double)MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }
}
