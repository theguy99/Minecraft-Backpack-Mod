package backpack;

import java.util.ArrayList;

import net.minecraft.src.IRecipe;
import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.Item;
import net.minecraft.src.ItemDye;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class RecipeRecolorBackpack implements IRecipe {
	ArrayList<Integer> allowedDyes = new ArrayList<Integer>();

	public RecipeRecolorBackpack() {
		allowedDyes.add(Item.dyePowder.shiftedIndex);
		allowedDyes.add(Item.leather.shiftedIndex);
		allowedDyes.add(Backpack.tannedLeather.shiftedIndex);
	}

	@Override
	public boolean matches(InventoryCrafting craftingGridInventory, World world) {
		ItemStack backpack = null;
		ItemStack dye = null;

		for(int i = 0; i < craftingGridInventory.getSizeInventory(); i++) {
			ItemStack slot = craftingGridInventory.getStackInSlot(i);

			if(slot != null) {
				if(slot.getItem() instanceof ItemBackpack) {
					if(slot.getItemDamage() == ItemBackpack.ENDERBACKPACK || backpack != null) {
						return false;
					}
					backpack = slot;
				} else if(allowedDyes.contains(slot.itemID)) {
					if(dye != null) {
						return false;
					}
					dye = slot;
				}
			}
		}

		if(backpack != null && dye != null) {
			if(backpack.getItemDamage() > 17 && dye.itemID == Item.leather.shiftedIndex) {
				return false;
			} else if(backpack.getItemDamage() < 17	&& dye.itemID == Backpack.tannedLeather.shiftedIndex) {
				return false;
			}
		}

		return backpack != null && dye != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting craftingGridInventory) {
		ItemStack backpack = null;
		ItemStack dye = null;

		for(int i = 0; i < craftingGridInventory.getSizeInventory(); i++) {
			ItemStack slot = craftingGridInventory.getStackInSlot(i);

			if(slot != null) {
				if(slot.getItem() instanceof ItemBackpack) {
					backpack = slot;
				} else if(allowedDyes.contains(slot.itemID)) {
					dye = slot;
				}
			}
		}

		int damage = (dye.getItem() instanceof ItemDye) ? dye.getItemDamage() : 16;
		if(backpack.getItemDamage() > 17) {
			damage += 32;
		}

		ItemStack result = backpack.copy();
		result.setItemDamage(damage);

		return result;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

}
