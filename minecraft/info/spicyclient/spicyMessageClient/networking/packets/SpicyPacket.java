package info.spicyclient.spicyMessageClient.networking.packets;

public class SpicyPacket extends BasicPacket {
	
	public SpicyPacket(type packetType, Object payload1, Object payload2, Object payload3, Object payload4) {
		super();
		this.packetType = packetType;
		this.payload1 = payload1;
		this.payload2 = payload2;
		this.payload3 = payload3;
		this.payload4 = payload4;
	}
	
	public type packetType;
	public Object payload1, payload2, payload3, payload4;
	
	public static enum type{
		
		MESSAGE,
		NAME,
		BROADCAST,
		LIST;
		
	}
	
}
