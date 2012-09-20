package eydamos.minecraft.backpack;

import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;

public class BackpackItem extends Item {
	public static final int colors[] = {
			0xf09954, 0xFF3333, 0x44BB33, 0x51301a, 0x6666FF, 0xFF66FF, 0x66FFFF, 0x999999,
			0xBBBBBB, 0xFFaaaa, 0xAAFF66, 0xFFFF66, 0xaaccFF, 0xcc55d0, 0xFFBB00, 0xf0f0f0,
			0x00ffff
	};

	static final String[] backpackNames = {
			"Backpack", "Red Backpack", "Green Backpack", "Brown Backpack", "Blue Backpack",
			"Purple Backpack", "Cyan Backpack", "Light Gray Backpack", "Gray Backpack",
			"Pink Backpack", "Lime Green Backpack", "Yellow Backpack", "Light Blue Backpack",
			"Magenta Backpack", "Orange Backpack", "White Backpack", "Magic Backpack"
	};

	public static final int MAGICBACKPACK = 31999;

	private BackpackInventory backpackInventory = null;

	protected BackpackItem(int id) {
		super(id);
		setMaxStackSize(1);
		setTabToDisplayOn(CreativeTabs.tabMisc);
		setIconIndex(0);
		setItemName("backpackItem");
		setHasSubtypes(true);
	}

	@Override
	public String getTextureFile() {
		return CommonProxy.ITEMS_PNG;
	}

	@Override
	public int getColorFromDamage(int damage, int par2) {
		if(damage >= 0 && damage < 16) {
			return colors[damage];
		}
		if(damage == MAGICBACKPACK) {
			return colors[16];
		}
		return 0;
	}

	@Override
	public void getSubItems(int unknown, CreativeTabs tab, List subItems) {
		for(int i = 0; i < 16; i++) {
			subItems.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public String getItemNameIS(ItemStack itemstack) {
		if(itemstack.getItemDamage() >= 0 && itemstack.getItemDamage() < 16) {
			return backpackNames[itemstack.getItemDamage()];
		}
		if(itemstack.getItemDamage() == MAGICBACKPACK) {
			return backpackNames[16];
		}
		return backpackNames[0];
	}

	@Override
	public int getMetadata(int damageValue) {
		return damageValue;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
		if(world.isRemote) {
			return is;
		}

		if(is.getTagCompound() == null) {
			System.out.println("Creating new NBT for ItemStack");
			is.setTagCompound(new NBTTagCompound());
		}
		BackpackInventory inv = new BackpackInventory(is.getTagCompound());

		if(!inv.hasInventory()) {
			System.out.println("Create inventory");
			inv.createInventory(getItemNameIS(is));
		}

		inv.loadInventory();

		player.displayGUIChest(inv);
		
		return is;
	}

	public void onCreated(ItemStack is, World world, EntityPlayer entityplayer) {
		if(world.isRemote) {
			return;
		}
		
		if(is.getTagCompound() == null) {
			System.out.println("Creating new NBT for ItemStack in onCreated");
			is.setTagCompound(new NBTTagCompound());
		}
		BackpackInventory inv = new BackpackInventory(is.getTagCompound());

		if(!inv.hasInventory()) {
			inv.createInventory(getItemNameIS(is));
		}
	}

}
