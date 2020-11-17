package spicy.chatCommands.commands;

import com.thealtening.AltService.EnumAltService;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import spicy.SpicyClient;
import spicy.chatCommands.Command;

public class LoginWIthSpicyToken extends Command {

	public LoginWIthSpicyToken() {
		super("LoginWithSpicyToken", "LoginWithSpicyToken <username:uuid:token>", 1);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void commandAction(String message) {
		
		String[] messageParts = message.split(":");
		
		Command.sendPrivateChatMessage(messageParts[0].split(" ")[1] + " ----- " + messageParts[1] + " ----- " +  messageParts[2]);
		
		// Username, UUID, Token
		Minecraft.getMinecraft().session = new Session(messageParts[0].split(" ")[1], messageParts[1], messageParts[2], "mojang");
		try {
			SpicyClient.TheAltening.switchService(EnumAltService.MOJANG);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Command.sendPrivateChatMessage("Session changed...");
		
	}
	
}
