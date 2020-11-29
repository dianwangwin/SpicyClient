package info.spicyclient.blockCoding;

import info.spicyclient.ClickGUI.Tab;

public class Block extends Tab {
	
	public Block(Color color) {
		this.color = color;
	}
	
	public Block connected;
	public Color color;

	public Block getConnected() {
		return connected;
	}

	public void setConnected(Block connected) {
		this.connected = connected;
	}
	
}
