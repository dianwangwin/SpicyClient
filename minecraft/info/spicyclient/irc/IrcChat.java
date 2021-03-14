package info.spicyclient.irc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import info.spicyclient.SpicyClient;
import info.spicyclient.chatCommands.Command;

public class IrcChat extends Thread {
	
	public static Socket socket = null;
	public static OutputStream outputStream = null;
	public static InputStream inputStream = null;
	public static Scanner ircScanner = null;
	public static PrintWriter ircWriter = null;
	
	public boolean connected = false;
	
	@Override
	public void run() {
		
		this.setName("IRC Thread");
		
		// Remove later
		if (true) {
			connected = false;
			if (SpicyClient.config.ircChat.isEnabled()) {
				SpicyClient.config.ircChat.toggle();
			}
			Command.sendPrivateChatMessage("IRC", false, "IRC is not done yet");
			return;
		}
		
		if (!SpicyClient.account.loggedIn) {
			Command.sendPrivateChatMessage("IRC", false, "You must create a spicyclient account to use the irc chat");
			connected = false;
			if (SpicyClient.config.ircChat.isEnabled()) {
				SpicyClient.config.ircChat.toggle();
			}
			return;
		}else {
			
			try {
				socket = new Socket("51.79.69.82", 3000);
			} catch (Exception e) {
				e.printStackTrace();
				Command.sendPrivateChatMessage("IRC", false, "Something went wrong while connecting to the irc server");
				connected = false;
				if (SpicyClient.config.ircChat.isEnabled()) {
					SpicyClient.config.ircChat.toggle();
				}
				return;
			}
			
			try {
				outputStream = socket.getOutputStream();
				inputStream = socket.getInputStream();
			} catch (Exception e) {
				e.printStackTrace();
				Command.sendPrivateChatMessage("IRC", false, "Something went wrong while connecting to the irc server");
				connected = false;
				if (SpicyClient.config.ircChat.isEnabled()) {
					SpicyClient.config.ircChat.toggle();
				}
				return;
			}
			
			try {
				ircScanner = new Scanner(inputStream);
				ircWriter = new PrintWriter(outputStream);
				sendQuery("NICK " + SpicyClient.account.username);
				sendQuery("USER " + SpicyClient.account.username + " 0 * :" + SpicyClient.account.username);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			long wait = System.currentTimeMillis() + 5000;
			connected = false;
			 
			new Thread() {
				@Override
				public void run() {
					while (true) {
						if (System.currentTimeMillis() >= wait) {
							sendQuery("JOIN #spicy");
							Command.sendPrivateChatMessage("IRC", false, "You have been connected to the irc chat");
							break;
						}
					}
				}
			}.start();
			
			try {
				while (ircScanner.hasNext()) {
					try {
						String message = ircScanner.nextLine();
						System.out.println("IRC>>>" + message);
						if (message.startsWith("PING")) {
							String pong = message.split(" ", 2)[1];
							sendQuery("PONG " + pong);
						}
						else if (message.split(":")[message.split(":").length - 1].equalsIgnoreCase("End of /NAMES list.")) {
							connected = true;
						}
						else if (connected) {
							Command.sendPrivateChatMessage("IRC", false, message.split("!")[0].substring(1) + ": " + message.split(":")[message.split(":").length - 1]);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Command.sendPrivateChatMessage("IRC", false, "You were disconnected from the server");
			connected = false;
			if (SpicyClient.config.ircChat.isEnabled()) {
				SpicyClient.config.ircChat.toggle();
			}
			
			try {
				inputStream.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ircScanner.close();
			ircWriter.close();
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void sendQuery(Object query) {
		
		String stringQuery = query.toString();
		if (connected && !stringQuery.startsWith("PONG")) {
			Command.sendPrivateChatMessage("IRC", false, stringQuery);
		}
		System.out.println("IRC>>>" + stringQuery);
		ircWriter.println(stringQuery);
		ircWriter.flush();
		
	}
	
	public static void quit() {
		try {
			inputStream.close();
			outputStream.close();
			ircScanner.close();
			ircWriter.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
