package info.spicyclient.modules.combat;

import org.lwjgl.input.Keyboard;

import info.spicyclient.modules.Module;
import net.minecraft.entity.EntityLivingBase;

public class Teams extends Module {

	public Teams() {
		super("Teams", Keyboard.KEY_NONE, Category.COMBAT);
	}

	public static boolean isOnSameTeam(EntityLivingBase entity) {
		if (entity.getTeam() != null && mc.thePlayer.getTeam() != null) {
			char c1 = entity.getDisplayName().getFormattedText().charAt(1);
			char c2 = mc.thePlayer.getDisplayName().getFormattedText().charAt(1);
			return c1 == c2;
		}
		return false;
	}

}
