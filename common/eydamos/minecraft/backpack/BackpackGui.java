package eydamos.minecraft.backpack;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiButton;
import net.minecraft.src.GuiScreen;
import net.minecraft.src.GuiTextField;
import net.minecraft.src.ItemStack;
import net.minecraft.src.RenderHelper;
import net.minecraft.src.StringTranslate;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class BackpackGui extends GuiScreen {
	private String TITLE = "Rename your backpack";
	EntityPlayer entityPlayer;

	private GuiTextField txt_backpackName;
	private GuiButton btn_ok, btn_cancel;
	//private final String EditedString;

	public BackpackGui(EntityPlayer player) {
		entityPlayer = player;
	}

	public void updateScreen() {
		txt_backpackName.updateCursorCounter();
	}

	public void initGui() {
		//Keyboard.enableRepeatEvents(true);
		// clear control list
		controlList.clear();
		
		// create button for ok and cancel
		int x = width / 2 + 100 - 80;
		int y = height / 2 + 50 - 24;
		btn_ok = new GuiButton(0, x, y, 60, 20, "OK");
		x = width / 2 - 100 + 20;
		y = height / 2 + 50 - 24;
		btn_cancel = new GuiButton(1, x, y, 60, 20, "Cancel");
		// add buttons to control list
		controlList.add(btn_ok);
		controlList.add(btn_cancel);

		// create text field
		x = width / 2 - 100;
		y = height / 2 - 50 + 47;
		txt_backpackName = new GuiTextField(fontRenderer, x, y, 200, 20);
		txt_backpackName.setFocused(true);
		txt_backpackName.setMaxStringLength(32);
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	protected void actionPerformed(GuiButton guibutton) {
		if(!guibutton.enabled) {
			return;
		}
		// id 0 = ok; id 1 = cancel
		if(guibutton.id == 1) {
			// remove the gui
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		} else if(guibutton.id == 0) {
			String name = txt_backpackName.getText().trim();

			// save the name
			if(mc.theWorld.isRemote) {
				if(entityPlayer.getCurrentEquippedItem() != null) {
					BackpackInventory inv = new BackpackInventory(entityPlayer, entityPlayer.getCurrentEquippedItem());
					if(!inv.hasInventory()) {
						inv.createInventory(name);
					} else {
						inv.setInvName(name);
					}
					inv.saveInventory();
				}
			}
			
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	protected void keyTyped(char c, int i) {
		txt_backpackName.textboxKeyTyped(c, i);
		((GuiButton) controlList.get(0)).enabled = txt_backpackName.getText().trim().length() > 0;
		if(c == '\r') {
			actionPerformed((GuiButton) controlList.get(0));
		}
	}

	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		txt_backpackName.mouseClicked(i, j, k);
	}

	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();

		drawGuiRadioBackgroundLayer(f);

		GL11.glPushMatrix();
		GL11.glRotatef(120F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();

		GL11.glPushMatrix();

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glDisable(32826);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(2896);
		GL11.glDisable(2929);

		fontRenderer.drawString(TITLE, width / 2 - (fontRenderer.getStringWidth(TITLE) / 2),
				(height / 2 - 50) + 20, 0x000000);
		fontRenderer.drawString("New name:", width / 2 - 100, (height / 2 - 50) + 35, 0x404040);
		txt_backpackName.drawTextBox();

		GL11.glPopMatrix();
		super.drawScreen(i, j, f);
		GL11.glEnable(2896);
		GL11.glEnable(2929);
	}

	protected void drawGuiRadioBackgroundLayer(float f) {
		int i = mc.renderEngine.getTexture("/eydamos/minecraft/backpack/img/guibackpack.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.renderEngine.bindTexture(i);
		int j = (width - 100) / 2;
		int k = (height - 50) / 2;
		drawTexturedModalRect(j - 100 + 30, k - 50 + 30 + 5, 0, 0, 240, 100);
	}

}
