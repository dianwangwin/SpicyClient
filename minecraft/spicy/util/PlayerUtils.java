package spicy.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerUtils {
	
	public static List<EntityPlayer> getTabPlayerList() {
        final NetHandlerPlayClient var4 = Minecraft.getMinecraft().thePlayer.sendQueue;
        final List<EntityPlayer> list = new ArrayList<>();
        final List players = GuiPlayerTabOverlay.field_175252_a.sortedCopy(var4.getPlayerInfoMap());
        for (final Object o : players) {
            final NetworkPlayerInfo info = (NetworkPlayerInfo) o;
            if (info == null) {
                continue;
            }
            list.add(Minecraft.getMinecraft().theWorld.getPlayerEntityByName(info.getGameProfile().getName()));
        }
        return list;
    }
	
}
