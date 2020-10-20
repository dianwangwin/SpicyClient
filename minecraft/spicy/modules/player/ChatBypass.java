package spicy.modules.player;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import net.minecraft.network.play.client.C03PacketPlayer;
import spicy.events.Event;
import spicy.events.listeners.EventChatmessage;
import spicy.events.listeners.EventUpdate;
import spicy.modules.Module;
import spicy.settings.ModeSetting;

public class ChatBypass extends Module {
	
	private ModeSetting mode = new ModeSetting("Mode", "Russian", "Roblox", "Test1", "Test2", "Test3");
	
	public ChatBypass() {
		super("Chat Bypass", Keyboard.KEY_NONE, Category.PLAYER);
		resetSettings();
	}
	
	@Override
	public void resetSettings() {
		this.settings.clear();
		this.addSettings(mode);
	}
	
	public void onEnable() {
		
	}
	
	public void onDisable() {
		
	}
	
	
	public void onEvent(Event e) {
		
		if (e instanceof EventUpdate) {
			
			if (e.isPre()) {
				
				this.additionalInformation = mode.getMode();
				
			}
			
		}
		
		if (e instanceof EventChatmessage) {
			
			if (e.isPre()) {
				
				EventChatmessage chat = (EventChatmessage) e;
				
				if (!chat.getMessage().startsWith("/")) {
					
					if (mode.getMode() == "Russian") {
						chat.setMessage(StringUtils.replaceChars(chat.getMessage(), "ABESZIKMHOPCTXWVYaekmotb3hpcyx", "АВЕЅZІКМНОРСТХШѴУаекмотвзнрсух"));
					}
					if (mode.getMode() == "Roblox") {
						// https://instafonts.io/font/roblox-swear-bypass
						chat.setMessage(StringUtils.replaceChars(chat.getMessage(), "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOPASDFGHJKLZXCVBNM", "Ɋ山乇尺ㄒㄚㄩ丨ㄖ卩卂丂ᗪ千Ꮆ卄ﾌҜㄥ乙乂匚ᐯ乃几爪1234567890Ɋ山乇尺ㄒㄚㄩ丨ㄖ卩卂丂ᗪ千Ꮆ卄ﾌҜㄥ乙乂匚ᐯ乃几爪"));
					}
					if (mode.getMode() == "Test2") {
						chat.setMessage(StringUtils.replaceChars(chat.getMessage(), "", ""));
					}
					if (mode.getMode() == "Test3") {
						chat.setMessage(StringUtils.replaceChars(chat.getMessage(), "", ""));
					}
					
				}
				
			}
			
		}
		
	}
	
}
