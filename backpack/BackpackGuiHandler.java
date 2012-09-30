package backpack;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class BackpackGuiHandler implements IGuiHandler {

	// returns an instance of the Container
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// as we have no container always return null
		return null;
	}

	// returns an instance of the GUI
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		// if user holds an item and this item is an backpack return GUI
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof BackpackItem) {
			return new BackpackGui(player);
		}
		return null;
	}

}
