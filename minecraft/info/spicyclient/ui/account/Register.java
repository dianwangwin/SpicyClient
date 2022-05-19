package info.spicyclient.ui.account;

import java.io.IOException;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;

import info.spicyclient.SpicyClient;
import info.spicyclient.files.FileManager;
import info.spicyclient.networking.NetworkManager;
import info.spicyclient.networking.NetworkUtils;
import info.spicyclient.ui.customOpenGLWidgets.Button;
import info.spicyclient.ui.customOpenGLWidgets.TextBox;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class Register extends GuiScreen {
	
	public Register(GuiScreen parent) {
		this.parent = parent;
	}
	
	public final GuiScreen parent;
	
	public Button register = new Button((this.width / 2) - 180, (this.height / 3) + 48, (this.width / 2) + 180, (this.height / 3) + 8, 0xff40444b, 0xff40444b, -1, 4, this);
	public TextBox username = new TextBox((this.width / 2) - 180, this.height / 3, (this.width / 2) + 180,
			this.height / 3 - 40, 0xff40444b, 0xff40444b, -1, 0xff687275, 4, false, this),
			password = new TextBox((this.width / 2) - 180, (this.height / 3) + 48, (this.width / 2) + 180,
					(this.height / 3) + 8, 0xff40444b, 0xff40444b, -1, 0xff687275, 4, false, this),
			email = new TextBox((this.width / 2) - 80, (this.height / 3) - 80, (this.width / 2) + 120,
					this.height / 3 - 80, 0xff40444b, 0xff40444b, -1, 0xff687275, 4, false, this);
	
	public boolean error = false, displayErrorText = false;
	public String errorText;
	
	@Override
	public void initGui() {
		
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		drawRect(0, 0, this.width, this.height, 0xff36393f);
		
		email.left = (this.width / 2) - 180;
		email.bottom = this.height / 3;
		email.right = (this.width / 2) + 180;
		email.up = this.height / 3 - 40;
		email.setGhostText("Email");
		email.setTextScale(1.8f);
		
		username.left = (this.width / 2) - 180;
		username.bottom = (this.height / 3) + 48;
		username.right = (this.width / 2) + 180;
		username.up = (this.height / 3) + 8;
		username.setGhostText("Username");
		username.setTextScale(1.8f);
		
		password.left = (this.width / 2) - 180;
		password.bottom = (this.height / 3) + 96;
		password.right = (this.width / 2) + 180;
		password.up = (this.height / 3) + 56;
		password.setGhostText("Password");
		password.setTextScale(1.8f);
		
		register = new Button((this.width / 2) - 180, (this.height / 3) + 144, (this.width / 2) + 180, (this.height / 3) + 104, 0xff202225, 0xff7289da, -1, 2, this);
		register.setText("Register");
		register.setTextScale(1.65f);
		
		// For the register button
		if (mouseX >= register.left && mouseX <= register.right && mouseY > register.up && mouseY < register.bottom) {
			register.insideColor = 0xff4d5c91;
		}
		
		if (errorText != null) {
			
			double scaling = 1.4;
			
			GlStateManager.pushMatrix();
			GlStateManager.scale(scaling, scaling, 1);
			drawCenteredString(mc.fontRendererObj, (error ? "§c" : "§a") + errorText, ((float)((this.width / 2) / scaling)), ((float)(((this.height / 3) - 75) / scaling)), -1);
			GlStateManager.popMatrix();
			
		}
		
		email.draw();
		username.draw();
		password.draw();
		register.draw();
		
		// To prevent the text from blinking
		// The max fps is 30
		long fps = 18;
		try {
			Thread.sleep(1000 / fps);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		
		if (mouseX >= email.left && mouseX <= email.right && mouseY > email.up && mouseY < email.bottom) {
			email.selected = true;
			username.selected = false;
			password.selected = false;
		}
		
		if (mouseX >= username.left && mouseX <= username.right && mouseY > username.up && mouseY < username.bottom) {
			email.selected = false;
			username.selected = true;
			password.selected = false;
		}
		
		if (mouseX >= password.left && mouseX <= password.right && mouseY > password.up && mouseY < password.bottom) {
			email.selected = false;
			username.selected = false;
			password.selected = true;
		}
		
		// For the register button
		if (mouseX >= register.left && mouseX <= register.right && mouseY > register.up && mouseY < register.bottom) {
			
			try {
				
				JSONObject response = new JSONObject(NetworkManager.getNetworkManager().sendPost(new HttpPost("https://SpicyClient.info/api/V2/Register.php"), new BasicNameValuePair("username", username.getText()), new BasicNameValuePair("password", password.getText()), new BasicNameValuePair("email", email.getText())));
				
				if (response.getBoolean("error")) {
					
					error = true;
					errorText = response.getString("errorText");
					
				}else {
					
					error = false;
					errorText = response.getString("errorText");
					
					SpicyClient.account.username = username.getText();
					SpicyClient.account.session = response.getString("session");
					SpicyClient.account.loggedIn = true;
					FileManager.saveAccount(SpicyClient.account);
					
					mc.displayGuiScreen(parent);
					
				}
				
			} catch (HttpHostConnectException e) {
				error = true;
				errorText = "Unable to connect to the spicyclient servers (blocked, timed out, or servers are down)";
				e.printStackTrace();
			} catch (Exception e) {
				error = true;
				errorText = "An unknown error has occurred";
				e.printStackTrace();
			}
			
		}
		
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		if (keyCode == Keyboard.KEY_ESCAPE) {
			mc.displayGuiScreen(parent);
		}
		
		if (email.isSelected()) {
			
			if (keyCode == Keyboard.KEY_V && isCtrlKeyDown()) {
				email.addChar(getClipboardString());
			}else {
				email.typeKey(typedChar, keyCode);
			}
			
		}
		else if (username.isSelected()) {
			
			if (keyCode == Keyboard.KEY_V && isCtrlKeyDown()) {
				username.addChar(getClipboardString());
			}else {
				username.typeKey(typedChar, keyCode);
			}
			
		}
		else if (password.isSelected()) {
			
			if (keyCode == Keyboard.KEY_V && isCtrlKeyDown()) {
				password.addChar(getClipboardString());
			}else {
				password.typeKey(typedChar, keyCode);
			}
			
		}
		
	}
	
}
