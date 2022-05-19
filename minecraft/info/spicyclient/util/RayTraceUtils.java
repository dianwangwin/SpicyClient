package info.spicyclient.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class RayTraceUtils {
	
    public static MovingObjectPosition rayTrace(double blockReachDistance, float yaw, float pitch){
        Vec3 vec3 = Minecraft.getMinecraft().thePlayer.getPositionEyes(0);
        Vec3 vec31 = RotationUtils.getVectorForRotation(pitch, yaw);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * blockReachDistance, vec31.yCoord * blockReachDistance, vec31.zCoord * blockReachDistance);
        return Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec3, vec32, false, false, true);
    }
	
}
