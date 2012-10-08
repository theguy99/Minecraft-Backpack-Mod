package backpack;

import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet250CustomPayload;

import org.lwjgl.input.Keyboard;

public class BackpackGui extends GuiScreen {
	private String TITLE = "Rename your backpack";
	EntityPlayer entityPlayer;

	private GuiTextField txt_backpackName;
	private GuiButton btn_ok, btn_cancel;

	/**
	 * Basic constructor. Takes instance of EntityPlayer to send a package to
	 * the server.
	 * 
	 * @param player
	 *            The player who sends the package with the new name to the
	 *            server.
	 */
	public BackpackGui(EntityPlayer player) {
		entityPlayer = player;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen() {
		txt_backpackName.updateCursorCounter();
	}

	/**
	 * Initializes the GUI elements.
	 */
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(false);
		// clear control list
		controlList.clear();

		// create button for ok and disable it at the beginning
		int posX = width / 2 + 100 - 80;
		int posY = height / 2 + 50 - 24;
		btn_ok = new GuiButton(0, posX, posY, 60, 20, "OK");
		btn_ok.enabled = false;

		// create button for cancel
		posX = width / 2 - 100 + 20;
		posY = height / 2 + 50 - 24;
		btn_cancel = new GuiButton(1, posX, posY, 60, 20, "Cancel");

		// add buttons to control list
		controlList.add(btn_ok);
		controlList.add(btn_cancel);

		// create text field
		posX = width / 2 - 100;
		posY = height / 2 - 50 + 47;
		txt_backpackName = new GuiTextField(fontRenderer, posX, posY, 200, 20);
		txt_backpackName.setFocused(true);
		txt_backpackName.setMaxStringLength(32);
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton guibutton) {
		// if button is disabled ignore click
		if(!guibutton.enabled) {
			return;
		}

		// id 0 = ok; id 1 = cancel
		switch(guibutton.id) {
			case 0:
				String name = txt_backpackName.getText().trim();

				// save the name
				sendNewNameToServer(name);
			case 1:
				// remove the GUI
				mc.displayGuiScreen(null);
				mc.setIngameFocus();
				break;
			default:
		}
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	@Override
	protected void keyTyped(char c, int i) {
		// add char to GuiTextField
		txt_backpackName.textboxKeyTyped(c, i);
		// enable ok button when GuiTextField content is greater than 0 chars
		((GuiButton) controlList.get(0)).enabled = txt_backpackName.getText().trim().length() > 0;
		// perform click event on ok button when Enter is pressed
		if(c == '\n') {
			actionPerformed((GuiButton) controlList.get(0));
		}
		// perform click event on cancel button when Esc is pressed
		if(Integer.valueOf(c) == 27) {
			actionPerformed((GuiButton) controlList.get(1));
		}
	}

	/**
	 * Called when the mouse is clicked.
	 */
	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		// move cursor to clicked position in GuiTextField
		txt_backpackName.mouseClicked(i, j, k);
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int i, int j, float f) {
		// draw transparent background
		drawDefaultBackground();

		// draw GUI background
		drawGuiBackground();

		// draw "Rename your Backpack" at the top in the middle
		int posX = width / 2 - (fontRenderer.getStringWidth(TITLE) / 2);
		int posY = (height / 2 - 50) + 20;
		fontRenderer.drawString(TITLE, posX, posY, 0x000000);

		// draw "New name:" at the left site above the GuiTextField
		posX = width / 2 - 100;
		posY = (height / 2 - 50) + 35;
		fontRenderer.drawString("New name:", posX, posY, 0x404040);

		// draw the GuiTextField
		txt_backpackName.drawTextBox();

		// draw the things in the controlList (buttons)
		super.drawScreen(i, j, f);
	}

	/**
	 * Gets the image for the background and renders it in the middle of the
	 * screen.
	 */
	protected void drawGuiBackground() {
		// get id of the texture
		int i = mc.renderEngine.getTexture("/gfx/backpack/guibackpack.png");
		// bind texture to render engine by id
		mc.renderEngine.bindTexture(i);
		// calculate position and draw texture
		int j = (width - 100) / 2;
		int k = (height - 50) / 2;
		drawTexturedModalRect(j - 100 + 30, k - 50 + 30 + 5, 0, 0, 240, 100);
	}

	/**
	 * Send the given name to the server.
	 * 
	 * @param name
	 *            The new name for the backpack.
	 */
	protected void sendNewNameToServer(String name) {
		// create packet set channel to send to, data as byte array and length
		// of the data
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "BackpackRename";
		packet.data = name.getBytes();
		packet.length = name.getBytes().length;

		// send the packet via players send queue
		((EntityClientPlayerMP) entityPlayer).sendQueue.addToSendQueue(packet);
	}

}
