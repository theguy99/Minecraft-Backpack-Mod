package backpack;

import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;

public class ItemBackpack extends Item {
	// the names of all backpacks
	static final String[] backpackNames = {
			"Black Backpack", "Red Backpack", "Green Backpack", "Brown Backpack", "Blue Backpack",
			"Purple Backpack", "Cyan Backpack", "Light Gray Backpack", "Gray Backpack",
			"Pink Backpack", "Lime Green Backpack", "Yellow Backpack", "Light Blue Backpack",
			"Magenta Backpack", "Orange Backpack", "White Backpack", "Backpack", "Ender Backpack"
	};

	// the damage of an magic backpack
	public static final int ENDERBACKPACK = 31999;

	/**
	 * Creates an instance of the backpack item and sets some default values.
	 * 
	 * @param id
	 *            The item id.
	 */
	protected ItemBackpack(int id) {
		super(id);
		setIconIndex(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setItemName("backpack");
		setCreativeTab(CreativeTabs.tabMisc);
	}

	/**
	 * Returns the image with the items.
	 * 
	 * @return The path to the item file.
	 */
	@Override
	public String getTextureFile() {
		return CommonProxy.ITEMS_PNG;
	}

	/**
	 * Returns the icon index based on the item damage.
	 * 
	 * @param damage
	 *            The damage to check for.
	 * @return The icon index.
	 */
	@Override
	public int getIconFromDamage(int damage) {
		if(damage >= 0 && damage < 17) {
			return damage;
		}
		if(damage >= 32 && damage < 49) {
			return damage;
		}
		if(damage == ENDERBACKPACK) {
			return 17;
		}
		return 0;
    }

	/**
	 * Returns the sub items.
	 * 
	 * @param itemId
	 *            the id of the item
	 * @param tab
	 *            A creative tab.
	 * @param A
	 *            List which stores the sub items.
	 */
	@Override
	public void getSubItems(int itemId, CreativeTabs tab, List subItems) {
		for(int i = 0; i < 17; i++) {
			subItems.add(new ItemStack(itemId, 1, i));
		}
		for(int i = 32; i < 49; i++) {
			subItems.add(new ItemStack(itemId, 1, i));
		}
		if(itemId == Backpack.backpack.shiftedIndex) {
			subItems.add(new ItemStack(itemId, 1, ENDERBACKPACK));
		}
	}

	/**
	 * Gets item name based on the ItemStack.
	 * 
	 * @param itemstack
	 *            The ItemStack to use for check.
	 * @return The name of the backpack.
	 */
	@Override
	public String getItemNameIS(ItemStack itemstack) {
		if(itemstack.getTagCompound() != null) {
			if(itemstack.getTagCompound().hasKey("display")) {
				return itemstack.getTagCompound().getCompoundTag("display").getString("Name");
			}
		}
		int dmg = itemstack.getItemDamage();
		if(dmg >= 0 && dmg < 17) {
			return backpackNames[itemstack.getItemDamage()];
		}
		if(dmg >= 32 && dmg < 49) {
			return "Big " + backpackNames[itemstack.getItemDamage() - 32];
		}
		if(itemstack.getItemDamage() == ENDERBACKPACK) {
			return backpackNames[17];
		}
		return backpackNames[16];
	}

	/**
	 * Handles what should be done on right clicking the item.
	 * 
	 * @param is
	 *            The ItemStack which is right clicked.
	 * @param world
	 *            The world in which the player is.
	 * @param player
	 *            The player who right clicked the item.
	 * @param Returns
	 *            the ItemStack after the process.
	 */
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
		// if world.isRemote than we are on the client side
		if(world.isRemote) {
			// display rename GUI if player is sneaking
			if(player.isSneaking() && is.getItemDamage() != ItemBackpack.ENDERBACKPACK) {
				FMLCommonHandler.instance().showGuiScreen(new BackpackGui(player));
			}
			return is;
		}

		// when the player is not sneaking
		if(!player.isSneaking()) {
			// get the inventory
			IInventory inv;
			if(is.getItemDamage() == ItemBackpack.ENDERBACKPACK) {
				inv = player.getInventoryEnderChest();
			} else {
				inv = new BackpackInventory(player, is);
			}

			// open the GUI for a chest based on the loaded inventory
			player.displayGUIChest(inv);
		}
		return is;
	}

	/**
	 * Returns the item name to display in the tooltip.
	 * @param itemstack The ItemStack to use for check.
	 * @return The name of the backpack for the tooltip.
	 */
	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		// it ItemStack has a NBTTagCompound load name from inventory title.
		if(itemstack.hasTagCompound()) {
			if(itemstack.getTagCompound().hasKey("Inventory")) {
				return itemstack.getTagCompound().getCompoundTag("Inventory").getString("title");
			}
		}
		// else if damage is between 0 and 15 return name from backpackNames array
		int dmg = itemstack.getItemDamage();
		if(dmg >= 0 && dmg < 17) {
			return backpackNames[itemstack.getItemDamage()];
		}
		if(dmg >= 32 && dmg < 49) {
			return "Big " + backpackNames[itemstack.getItemDamage() - 32];
		}
		// else if damage is equal to ENDERBACKPACK then return backpackNames index 16
		if(itemstack.getItemDamage() == ENDERBACKPACK) {
			return backpackNames[17];
		}

		// return index 0 of backpackNames array as fallback
		return backpackNames[16];
	}
}
