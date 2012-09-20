package eydamos.minecraft.backpack;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class BackpackGuiHandler implements IGuiHandler {

	//returns an instance of the Container 
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof BackpackItem){
			//return player.getCurrentEquippedItem();
		}
		return null;
	}

	//returns an instance of the Gui
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof BackpackItem){
			return new BackpackGui(player);
		}
		return null;
	}

}
