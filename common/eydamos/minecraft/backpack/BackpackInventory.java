package eydamos.minecraft.backpack;

import com.google.common.collect.ContiguousSet;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;

public class BackpackInventory implements IInventory {
	private static final int slotsCount = 27;
	private ItemStack inventoryContents[];
	private String inventoryTitle = "Backpack";
	private NBTTagCompound itemStackTag;

	private boolean magic;
	EntityPlayer currentUser;

	public BackpackInventory(NBTTagCompound itemStackTag) {
		this.itemStackTag = itemStackTag;
		inventoryContents = new ItemStack[slotsCount];
	}

	@Override
	public int getSizeInventory() {
		return slotsCount;
	}

	@Override
	public ItemStack getStackInSlot(int position) {
		if(position < slotsCount) {
			return inventoryContents[position];
		}
		return null;
	}

	@Override
	public ItemStack decrStackSize(int position, int decrease) {
		if(inventoryContents[position] != null) {
			if(inventoryContents[position].stackSize <= decrease) {
				ItemStack itemstack = inventoryContents[position];
				inventoryContents[position] = null;
				return itemstack;
			}
			ItemStack itemstack1 = inventoryContents[position].splitStack(decrease);
			if(inventoryContents[position].stackSize == 0) {
				inventoryContents[position] = null;
			}
			return itemstack1;
		}
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int position) {
		if(inventoryContents[position] != null) {
			ItemStack itemstack = inventoryContents[position];
			inventoryContents[position] = null;
			return itemstack;
		}
		return null;
	}

	@Override
	public void setInventorySlotContents(int position, ItemStack itemstack) {
		inventoryContents[position] = itemstack;
		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInvName() {
		return inventoryTitle;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void onInventoryChanged() {
		System.out.println("oninventory chanded");
		saveInventory();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openChest() {
		loadInventory();
	}

	@Override
	public void closeChest() {
		//dropContainedBackpacks();
		//saveInventory();
	}

	// ***** custom methods which are not in IInventory *****
	/**
	 * loads the content of the inventory from the NBT.
	 */
	public void setInvName(String name) {
		System.out.println("new name is " + name);
		inventoryTitle = name;
	}
	
	public void loadInventory() {
		if(!hasInventory()) {
			System.out.println("backpack does not exist!");
			createInventory(null);
		}

		readFromTag(itemStackTag.getCompoundTag("Inventory"));
	}

	/**
	 * Saves the actual content of the inventory to the NBT.
	 */
	public void saveInventory() {
		writeToTag(itemStackTag.getCompoundTag("Inventory"));
	}

	/**
	 * Creates the Inventory Tag in the NBT with an empty inventory.
	 */
	public void createInventory(String name) {
		if(name == null) {
			setInvName("Backpack");
		} else {
			setInvName(name);
		}
		itemStackTag.setCompoundTag("Inventory", writeToTag(new NBTTagCompound()));
	}

	/**
	 * Returns if an Inventory is saved in the NBT.
	 * 
	 * @return
	 */
	public boolean hasInventory() {
		return itemStackTag.hasKey("Inventory");
	}

	/**
	 * Sets magic to true to identify a magic backpack.
	 */
	public void setMagic() {
		magic = true;
	}

	/**
	 * Drops Backpacks on the ground which are in this backpack
	 * 
	 * @return
	 */
	public void dropContainedBackpacks() {
		for(int i = 0; i < getSizeInventory(); i++) {
			if(getStackInSlot(i) != null
					&& getStackInSlot(i).itemID == Backpack.backpackItem.shiftedIndex) {
				currentUser.dropPlayerItem(getStackInSlot(i).copy());
				setInventorySlotContents(i, null);
			}
		}
	}

	/**
	 * Writes a NBT Node with inventory.
	 * 
	 * @param outerTag
	 *            The NBT Node to write to.
	 * @return The written NBT Node.
	 */
	public NBTTagCompound writeToTag(NBTTagCompound outerTag) {
		if(outerTag == null) {
			return null;
		}
		
		boolean init = false;
		if(!outerTag.hasKey("title")) {
			init = true;
		}
		outerTag.setString("title", getInvName());
		System.out.println("title written is " + getInvName());

		NBTTagList itemList = new NBTTagList();
		for(int i = 0; i < inventoryContents.length; i++) {
			if(inventoryContents[i] != null) {
				NBTTagCompound slotEntry = new NBTTagCompound();
				slotEntry.setByte("Slot", (byte) i);
				inventoryContents[i].writeToNBT(slotEntry);
				itemList.appendTag(slotEntry);
				System.out.println(inventoryContents[i].itemID + " at slot " + i);
			}
		}
		if(init) {
			NBTTagCompound slotEntry = new NBTTagCompound();
			slotEntry.setByte("Slot", (byte) 1);
			(new ItemStack(Item.leather)).writeToNBT(slotEntry);
			itemList.appendTag(slotEntry);
		}

		outerTag.setTag("Items", itemList);
		return outerTag;
	}

	/**
	 * Reads the inventory from a NBT Node.
	 * 
	 * @param outerTag
	 *            The NBT Node to read from.
	 */
	public void readFromTag(NBTTagCompound outerTag) {
		if(outerTag == null) {
			return;
		}
		
		setInvName(outerTag.getString("title"));
		System.out.println("titel read is " + outerTag.getString("title"));

		NBTTagList itemList = outerTag.getTagList("Items");
		inventoryContents = new ItemStack[getSizeInventory()];
		for(int i = 0; i < itemList.tagCount(); i++) {
			NBTTagCompound slotEntry = (NBTTagCompound) itemList.tagAt(i);
			int j = slotEntry.getByte("Slot") & 0xff;

			if(j >= 0 && j < getSizeInventory()) {
				inventoryContents[j] = ItemStack.loadItemStackFromNBT(slotEntry);
			}
			System.out.println(inventoryContents[j].itemID + " now at slot " + j);
		}
	}
}
