package backpack;

import java.util.List;

import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.network.NetworkRegistry;

public class BackpackItem extends Item {
	public static final int colors[] = {
			0xf09954, // Backpack
			0xdc4c4c, // Red Backpack
			0x7fac3b, // Green Backpack
			0xb4805c, // Brown Backpack
			0x587fdf, // Blue Backpack
			0xc58de2, // Purple Backpack
			0x4b9fc1, // Cyan Backpack
			0xD9D9D9, // Light Gray Backpack
			0xBBBBBB, // Gray Backpack
			0xF7B4D6, // Pink Backpack
			0x90e227, // Lime Green Backpack
			0xE7E72A, // Yellow Backpack
			0xaaccFF, // Light Blue Backpack
			0xe08edb, // Magenta Backpack
			0xFFBB00, // Orange Backpack
			0xffffff, // White Backpack
			0x349988// Ender Backpack
	};

	static final String[] backpackNames = {
			"Backpack", "Red Backpack", "Green Backpack", "Brown Backpack", "Blue Backpack",
			"Purple Backpack", "Cyan Backpack", "Light Gray Backpack", "Gray Backpack",
			"Pink Backpack", "Lime Green Backpack", "Yellow Backpack", "Light Blue Backpack",
			"Magenta Backpack", "Orange Backpack", "White Backpack", "Ender Backpack"
	};

	public static final int MAGICBACKPACK = 31999;

	protected BackpackItem(int id) {
		super(id);
		setIconIndex(0);
		setMaxStackSize(1);
		setHasSubtypes(true);
		setItemName("backpack");
		setTabToDisplayOn(CreativeTabs.tabMisc);
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
		if(itemstack.getTagCompound() != null) {
			if(itemstack.getTagCompound().hasKey("Inventory")) {
				return itemstack.getTagCompound().getCompoundTag("Inventory").getString("title");
			}
		}
		if(itemstack.getItemDamage() >= 0 && itemstack.getItemDamage() < 16) {
			// return
			// StringTranslate.getInstance().translateNamedKey(backpackNames[itemstack.getItemDamage()]);
			return backpackNames[itemstack.getItemDamage()];
		}
		if(itemstack.getItemDamage() == MAGICBACKPACK) {
			return backpackNames[16];
		}
		return backpackNames[0];
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player) {
		// if world.isRemote than we are on the client side
		if(world.isRemote) {
			// display rename gui if player is sneaking
			if(player.isSneaking()){
				
				FMLCommonHandler.instance().showGuiScreen(new BackpackGui(player));
				System.out.println("gui vorbei");
				return player.getCurrentEquippedItem();
			}
		}
		if(!player.isSneaking()) {
			if(!is.hasTagCompound()) {
				is.setTagCompound(new NBTTagCompound());
			}
			BackpackInventory inv  = new BackpackInventory(player, is);
			if(!inv.hasInventory()) {
				inv.createInventory(getItemNameIS(is));
			}
	
			inv.loadInventory();
			Side side = FMLCommonHandler.instance().getEffectiveSide();
			if(side == Side.SERVER) {
				System.out.println("Server displays guichest " + inv.getInvName() + player.getCurrentEquippedItem().getTagCompound().getCompoundTag("Inventory").getTagList("Items").tagCount());
			} else if(side == Side.CLIENT) {
				System.out.println("Client displays guichest " + inv.getInvName() + player.getCurrentEquippedItem().getTagCompound().getCompoundTag("Inventory").getTagList("Items").tagCount());
			}
			player.displayGUIChest(inv);
		}
		return is;
	}

	@Override
	public String getItemDisplayName(ItemStack itemstack) {
		String name = backpackNames[0];
		if(itemstack.hasTagCompound()) {
			if(itemstack.getTagCompound().hasKey("Inventory")) {
				name = itemstack.getTagCompound().getCompoundTag("Inventory").getString("title");
			}
		}
		if(itemstack.getItemDamage() >= 0 && itemstack.getItemDamage() < 16) {
			name = backpackNames[itemstack.getItemDamage()];
		}
		if(itemstack.getItemDamage() == MAGICBACKPACK) {
			name = backpackNames[16];
		}
		
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if(side == Side.SERVER) {
			System.out.println("Server says name is " + name);
		} else if(side == Side.CLIENT) {
			System.out.println("Client says name is " + name);
		}
		
		return name;
	}

}
