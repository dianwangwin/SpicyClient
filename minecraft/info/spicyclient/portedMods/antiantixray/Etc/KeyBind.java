package info.spicyclient.portedMods.antiantixray.Etc;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;

public class KeyBind {
    int kc;
    boolean flag3 = false;

    public KeyBind(int kc) {
        this.kc = kc;
    }
    
	public int getKeyCode() {
		return kc;
	}
    
    public void setKeyCode(int kc) {
        this.kc = kc;
    }

    public boolean checkPressed() {
        if (Minecraft.getMinecraft().currentScreen != null) return false;
        
        if (Keyboard.isKeyDown(kc)) {
        	return true;
        }else {
        	return false;
        }
        
    }
    
}
