package net.minecraft.client.gui;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;

import com.thealtening.AltService.EnumAltService;

import info.spicyclient.SessionChanger;
import info.spicyclient.SpicyClient;
import info.spicyclient.TheAlteningAPI;
import info.spicyclient.files.FileManager;
import info.spicyclient.files.AltInfo.Alt;
import info.spicyclient.ui.NewAltManager;
import info.spicyclient.util.ServerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

public class GuiDisconnected extends GuiScreen
{
    private String reason;
    private IChatComponent message;
    private List<String> multilineMessage;
    private final GuiScreen parentScreen;
    private int field_175353_i;
    
    public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp)
    {
        this.parentScreen = screen;
        this.reason = I18n.format(reasonLocalizationKey, new Object[0]);
        this.message = chatComp;
        
        new Thread("Ban time updater") {
        	public void run() {
        		
                try {
                	long dateUnbanned = System.currentTimeMillis();
                	String time = message.getSiblings().get(1).getFormattedText().substring(2, message.getSiblings().get(1).getFormattedText().length() - 2);
                	if (time.equalsIgnoreCase("reason: ")) {
                		for (int e = SpicyClient.altInfo.alts.size(); e > 0; e--) {
                    		try {
                    			Alt a = SpicyClient.altInfo.alts.get(e);
                    			if (a.username.equals(Minecraft.getMinecraft().getSession().getUsername())) {
                        			a.unbannedAt = Long.MAX_VALUE;
                        			FileManager.saveAltInfo(SpicyClient.altInfo);
                        			return;
                        		}
        					} catch (Exception e2) {
        						
        					}
                    	}
                		return;
                	}
                	String[] times = time.split(" ");
                	for (String date : times) {
                		try {
                			String numbers = date.substring(0, date.length() - 1);
                    		if (date.endsWith("d")) {
                    			dateUnbanned += TimeUnit.DAYS.toMillis(Integer.valueOf(numbers));
                    		}
                    		else if (date.endsWith("h")) {
                    			dateUnbanned += TimeUnit.HOURS.toMillis(Integer.valueOf(numbers));
                    		}
                    		else if (date.endsWith("m")) {
                    			dateUnbanned += TimeUnit.MINUTES.toMillis(Integer.valueOf(numbers));
                    		}
                    		else if (date.endsWith("s")) {
                    			dateUnbanned += TimeUnit.SECONDS.toMillis(Integer.valueOf(numbers));
                    		}
        				} catch (Exception e) {
        					e.printStackTrace();
        				}
                	}
                	
                	for (int e = SpicyClient.altInfo.alts.size(); e > 0; e--) {
                		try {
                			Alt a = SpicyClient.altInfo.alts.get(e);
                			if (a.username.equals(Minecraft.getMinecraft().getSession().getUsername())) {
                    			a.unbannedAt = dateUnbanned;
                    			FileManager.saveAltInfo(SpicyClient.altInfo);
                    		}
        				} catch (Exception e2) {
        					
        				}
                	}
        		} catch (Exception e) {
        			
        		}
        		
        	}
        }.start();
        
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
        this.field_175353_i = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
        
        GuiButton altManager = new GuiButton(1, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, "Alt manager");
        GuiButton genAlt = new GuiButton(2, (this.width / 2 - 100), this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, "Generate and reconnect");
        GuiButton reconnectAlt = new GuiButton(3, (this.width / 2 - 100), this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT, "Reconnect");
        
        //altManager.width = altManager.width / 2;
        altManager.yPosition += (altManager.height * 2) + 10;
        reconnectAlt.yPosition += reconnectAlt.height + 5;
        //genAlt.width = genAlt.width / 2;
        //genAlt.xPosition = genAlt.xPosition + genAlt.width;
        
        this.buttonList.add(altManager);
        this.buttonList.add(genAlt);
        this.buttonList.add(reconnectAlt);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.field_175353_i / 2 + this.fontRendererObj.FONT_HEIGHT + 75, I18n.format("§aBack to the server list", new Object[0])));
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == 0)
        {
            this.mc.displayGuiScreen(this.parentScreen);
        }
        else if (button.id == 1) {
        	mc.displayGuiScreen(new NewAltManager(parentScreen));
        }
        else if (button.id == 2) {
        	
			boolean loginSuccess = false;
    		Alt Alt = null;
        	int altAmount = 0;
        	String oldname = mc.session.getUsername();
        	Alt a = null;
        	
    		try {
				JSONObject account = TheAlteningAPI.call_me();
				//a = new alt(account.getString("token"), account.getString("password"), true);
				a = new Alt(account.getString("token"), "fur", true);
				a.username = account.getString("username");
				SpicyClient.altInfo.addCreatedAlt(a);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		if (a == null) {
    			loginSuccess = false;
    		}
    		else if (a.premium) {
    			
    			if (a.email.toLowerCase().contains("@alt.com".toLowerCase())) {
					
					// Used for thealtening api
					try {
						SpicyClient.TheAltening.switchService(EnumAltService.THEALTENING);
					} catch (NoSuchFieldException | IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}else {
					
					// Used for thealtening api
					try {
						SpicyClient.TheAltening.switchService(EnumAltService.MOJANG);
					} catch (NoSuchFieldException | IllegalAccessException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
				
				SessionChanger.getInstance().setUser(a.email, a.password);
				if (a.username != mc.session.getUsername() && mc.session.getUsername() != oldname) {
					a.username = mc.session.getUsername();
				}
				
    		}
				
				
    		
    		if (a == null) {
    			loginSuccess = false;
    		}
    		else if (mc.session.getUsername() != oldname) {
    			a.status = 2;
    			Alt = a;
    			loginSuccess = true;
			}else {
				loginSuccess = false;
			}
    		
    		if (a == null) {
    			loginSuccess = false;
    		}
    		else if (loginSuccess) {
    			
    			for (int e = SpicyClient.altInfo.alts.size(); e > 0; e--) {
    				a = SpicyClient.altInfo.alts.get(e - 1);
    				
        			if (a != Alt) {
        				a.status = 0;
        			}
        			altAmount++;
        		}
        		FileManager.saveAltInfo(SpicyClient.altInfo);
    		}
    		
    		if (loginSuccess) {
    			
	        	if (parentScreen instanceof GuiMultiplayer) {
	        		
	        		((GuiMultiplayer)parentScreen).connectToSelected();
	        		
	        	}
    			
    		}
    		
        }
        
        else if (button.id == 3) {
        	
        	if (parentScreen instanceof GuiMultiplayer) {
        		
        		((GuiMultiplayer)parentScreen).connectToSelected();
        		
        	}
        	
        }
        
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.field_175353_i / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810);
        int i = this.height / 2 - this.field_175353_i / 2;

        if (this.multilineMessage != null)
        {
            for (String s : this.multilineMessage)
            {
                this.drawCenteredString(this.fontRendererObj, s, this.width / 2, i, 16777215);
                i += this.fontRendererObj.FONT_HEIGHT;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
