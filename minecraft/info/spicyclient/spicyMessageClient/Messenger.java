package info.spicyclient.spicyMessageClient;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import com.google.gson.Gson;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;
import info.spicyclient.spicyMessageClient.networking.NetworkConnection;
import info.spicyclient.spicyMessageClient.networking.NetworkSubscription;
import info.spicyclient.spicyMessageClient.networking.Security;
import info.spicyclient.spicyMessageClient.networking.packets.SpicyPacket;
import info.spicyclient.spicyMessageClient.networking.packets.SpicyPacket.type;
import net.minecraft.util.EnumChatFormatting;

public class Messenger extends NetworkConnection {
	
	private String username;
	private boolean hasSetUsername = false;
	
	@Override
	public void openConnection(String address, int port) throws Exception {
		super.openConnection(address, port);
		subscribeToInput(new NetworkSubscription() {
			@Override
			public void onEvent(String input) {
				SpicyPacket sPacket = new Gson().fromJson(input, SpicyPacket.class);
				
				if (sPacket.packetType == type.NAME) {
					username = (String) sPacket.payload1;
					Command.sendPrivateChatMessage("irc", false, "Your username has been set to " + username);
					hasSetUsername = true;
				}
				else if (sPacket.packetType == type.BROADCAST) {
					Command.sendPrivateChatMessage("irc", false, (String) sPacket.payload1);
				}
				else if (sPacket.packetType == type.LIST) {
					StringBuilder names = new StringBuilder();
					names.append("Online players: ");
					((ArrayList<String>) sPacket.payload2).forEach(name -> names.append(name + ", "));
					Command.sendPrivateChatMessage("irc", false, names.toString().substring(0, names.toString().length() - 2));
				}
			}
		});
		
		while (!handshakeCompleted) {
			System.out.println("Waiting for handshake...");
		}
		
		Thread.sleep(500);
		
		if (SpicyClient.account.loggedIn) {
			setUsername(SpicyClient.account.username);
		}else {
			setUsername((SpicyClient.originalAccountOnline ? "p_"  : "c_") + SpicyClient.originalUsername);
		}
		
		//setUsername(Security.getRandomString(10 + new Random().nextInt(10)));
		
	}
	
	public void setUsername(String name) {
		
		sendPacket(new SpicyPacket(type.NAME, name, null, null, null));
		if (hasSetUsername) {
			Command.sendPrivateChatMessage("irc", false, "Requested a username change");
		}else {
			Command.sendPrivateChatMessage("irc", false, "Logging in...");
		}
		
		hasSetUsername = true;
		
	}
	
	public String getUsername() {
		return username;
	}
	
	@Override
	public void closeConnection() throws Exception {
		super.closeConnection();
	}
	
}
