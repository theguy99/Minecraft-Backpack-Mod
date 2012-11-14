package backpack;

import net.minecraft.src.IRecipe;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class RecipeEnhanceBackpack implements IRecipe {

	@Override
	public boolean matches(InventoryCrafting craftingGridInventory, World world) {
		ItemStack slot;
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				slot = craftingGridInventory.getStackInRowAndColumn(i, j);
				if(slot == null) {
					return false;
				}
				if(i == 1 && j == 1) {
					if(!(slot.getItem() instanceof ItemBackpack)) {
						return false;
					}
					if(slot.getItemDamage() >= 17) {
						return false;
					}
				} else {
					if(!(slot.getItem() instanceof ItemLeather)) {
						return false;
					}
					if(slot.itemID != Backpack.tannedLeather.shiftedIndex) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftingGridInventory) {
		ItemStack backpack = craftingGridInventory.getStackInRowAndColumn(1, 1).copy();
		backpack.setItemDamage(backpack.getItemDamage() + 32);
		return backpack;
	}

	@Override
	public int getRecipeSize() {
		return 9;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

}
