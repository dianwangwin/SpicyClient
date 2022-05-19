package info.spicyclient.spicyMessageClient.networking.packets;

import java.util.ArrayList;

public class BasicPacket {
	
	public BasicPacket(byte[] message) {
		this.message = message;
	}
	
	public BasicPacket() {
		
	}
	
	public byte[] message;
	
	public ArrayList<Byte> aesPadding = new ArrayList<Byte>();
	
}
