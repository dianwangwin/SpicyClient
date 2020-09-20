package spicy.ui;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import spicy.SpicyClient;
import spicy.files.AltInfo;
import spicy.files.FileManager;
import spicy.ui.customOpenGLWidgets.ButtonType1;

import org.lwjgl.input.Keyboard;

public class AddAlt extends GuiScreen
{
    private final GuiScreen field_146303_a;
    private GuiTextField field_146302_g;

    public AddAlt(GuiScreen p_i1031_1_)
    {
        this.field_146303_a = p_i1031_1_;
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        this.field_146302_g.updateCursorCounter();
    }

    /**
     * Adds the buttons (and other controls) to the screen in question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui()
    {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new ButtonType1(0, this.width / 3 - 75, this.height / 4 + 96 + 12, this.width / 2, mc.fontRendererObj.FONT_HEIGHT + 10, "Set API Key"));
        this.buttonList.add(new ButtonType1(1, this.width / 3 - 75, this.height / 4 + 120 + 12, this.width / 2, mc.fontRendererObj.FONT_HEIGHT + 10, "But i dont have an API key!"));
        this.buttonList.add(new ButtonType1(2, this.width / 3 - 75, this.height / 4 + 144 + 12, this.width / 2, mc.fontRendererObj.FONT_HEIGHT + 10, "Cancel"));
        this.field_146302_g = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 116, 200, 20);
        this.field_146302_g.setMaxStringLength(18);
        this.field_146302_g.setFocused(true);
        //((GuiButton)this.buttonList.get(0)).enabled = this.field_146302_g.getText().length() > 0 && this.field_146302_g.getText().split(":").length > 0;
    }

    /**
     * Called when the screen is unloaded. Used to disable keyboard repeat events
     */
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
        this.mc.gameSettings.lastServer = this.field_146302_g.getText();
        this.mc.gameSettings.saveOptions();
    }

    /**
     * Called by the controls from the buttonList when activated. (Mouse pressed for buttons)
     */
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.enabled)
        {
        	if (button.id == 1)
            {
            	SpicyClient.altInfo.API_Key = "none";
            	System.out.println("Set the API key to none");
            	FileManager.saveAltInfo(SpicyClient.altInfo);
            	mc.displayGuiScreen(new AltManager());
            }
        	else if (button.id == 2)
            {
            	this.mc.displayGuiScreen((GuiScreen)null);
            }
            else if (button.id == 0)
            {
            	if (!this.field_146302_g.getText().startsWith("api-")) {
            		SpicyClient.altInfo.API_Key = "none";
                	System.out.println("Set the API key to none");
            	}else {
            		SpicyClient.altInfo.API_Key = this.field_146302_g.getText();
                	System.out.println("Set the API key to " + this.field_146302_g.getText());
            	}
            	FileManager.saveAltInfo(SpicyClient.altInfo);
            	mc.displayGuiScreen(new AltManager());
            }
        }
    }

    /**
     * Fired when a key is typed (except F11 which toggles full screen). This is the equivalent of
     * KeyListener.keyTyped(KeyEvent e). Args : character (character on the key), keyCode (lwjgl Keyboard key code)
     */
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (this.field_146302_g.textboxKeyTyped(typedChar, keyCode))
        {
            ((GuiButton)this.buttonList.get(0)).enabled = this.field_146302_g.getText().length() > 0 && this.field_146302_g.getText().split(":").length > 0;
        }
        else if (keyCode == 28 || keyCode == 156)
        {
            this.actionPerformed((GuiButton)this.buttonList.get(0));
        }
    }

    /**
     * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.field_146302_g.mouseClicked(mouseX, mouseY, mouseButton);
    }

    /**
     * Draws the screen and all the components in it. Args : mouseX, mouseY, renderPartialTicks
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("Set API Key", new Object[0]), this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, I18n.format("Set TheAltening API Key", new Object[0]), this.width / 2 - 100, 100, 10526880);
        this.field_146302_g.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
