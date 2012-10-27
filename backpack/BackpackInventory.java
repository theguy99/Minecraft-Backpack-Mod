package backpack;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

public class BackpackInventory implements IInventory {
	// number of slots 3 lines a 9 slots
	private static final int slotsCount = 27;
	// the content of the inventory
	private ItemStack inventoryContents[];

	// the default title of the backpack
	private String inventoryTitle;

	// an instance of the player to get the used item and for magic backpack
	private EntityPlayer playerEntity;
	// the original ItemStack to compare with the player inventory
	private ItemStack originalIS;
	// the NBT data of the inventory
	private NBTTagCompound data;

	/**
	 * Takes a player and an ItemStack.
	 * 
	 * @param player
	 *            The player which has the backpack.
	 * @param is
	 *            The ItemStack which holds the backpack.
	 */
	public BackpackInventory(EntityPlayer player, ItemStack is) {
		inventoryContents = new ItemStack[slotsCount];
		playerEntity = player;
		originalIS = is;

		// check if inventory exists if not create one
		if(!hasInventory(is.getTagCompound())) {
			createInventory();
		}

		// get NBTTagCompound from player or ItemStack
		getNBT();

		loadInventory();
	}

	/**
	 * Returns how many slots the backpack has.
	 * 
	 * @return The number of slots in the backpack.
	 */
	@Override
	public int getSizeInventory() {
		return slotsCount;
	}

	/**
	 * Returns the ItemStack in the given slot or null if the slot doesn't
	 * exists.
	 * 
	 * @param position
	 *            The position of the slot.
	 * @return Returns an ItemStack or null if slot at position is empty or
	 *         position is greater slotsCount.
	 */
	@Override
	public ItemStack getStackInSlot(int position) {
		if(position < slotsCount) {
			return inventoryContents[position];
		}
		return null;
	}

	/**
	 * Decreases the ItemStack at the given position.
	 * 
	 * @param position
	 *            The position of the ItemStack.
	 * @param decrease
	 *            The number the ItemStack is reduced by.
	 * @return The decreased ItemStack.
	 */
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

	/**
	 * Sets an ItemStack at the given position.
	 * 
	 * @param position
	 *            The position where the ItemStack should be put in.
	 * @param itemstack
	 *            The ItemStack which should be put in the inventory.
	 */
	@Override
	public void setInventorySlotContents(int position, ItemStack itemstack) {
		inventoryContents[position] = itemstack;
		if(itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	/**
	 * Returns the title of the inventory.
	 * 
	 * @return The title.
	 */
	@Override
	public String getInvName() {
		return inventoryTitle;
	}

	/**
	 * Returns the maximum stack size an ItemStack could have.
	 * 
	 * @return The maximum stack size.
	 */
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	/**
	 * Is called whenever something is changed in the inventory.
	 */
	@Override
	public void onInventoryChanged() {
		saveInventory();
	}

	/**
	 * Returns if this inventory is usable by a player.
	 * 
	 * @return True if user can use it false otherwise.
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	/**
	 * This method is called when the chest opens the inventory. It loads the
	 * content of the inventory and its title.
	 */
	@Override
	public void openChest() {
		loadInventory();
	}

	/**
	 * This method is called when the chest closes the inventory. It then throws
	 * out every backpack which is inside the backpack and saves the inventory.
	 */
	@Override
	public void closeChest() {
		dropContainedBackpacks();
		saveInventory();
	}

	// ***** custom methods which are not in IInventory *****
	/**
	 * Saves the NBTTagCompound of the user if the backpack is an ender backpack
	 * or the NBTTagCompound of the currently used item in the data attribute.
	 */
	private void getNBT() {
		data = (NBTTagCompound) originalIS.getTagCompound();
	}

	/**
	 * Searches the backpack in players inventory and saves NBT data in it.
	 */
	private void setNBT() {
		// get players inventory
		ItemStack[] inventory = playerEntity.inventory.mainInventory;
		ItemStack itemStack;
		// iterate over all items in player inventory
		for(int i = 0; i < inventory.length; i++) {
			// get ItemStack at slot i
			itemStack = inventory[i];
			// check if slot is not null and ItemStack is equal to original
			if(itemStack != null && isItemStackEqual(itemStack)) {
				// save new data in ItemStack
				itemStack.setTagCompound(data);
				break;
			}
		}
	}

	/**
	 * Checks if ItemStack is equal to the original ItemStack.
	 * 
	 * @param itemStack
	 *            The ItemStack to check.
	 * @return true if equal otherwise false.
	 */
	private boolean isItemStackEqual(ItemStack itemStack) {
		// check if ItemStack is a BackpackItem and normal properties are equal
		if(itemStack.getItem() instanceof BackpackItem && itemStack.isItemEqual(originalIS)) {
			// never opened backpacks have no NBT so make sure it is there
			if(itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("Inventory")) {
				// check if NBT data is equal too
				return isTagCompoundEqual(itemStack);
			}
		}
		return false;
	}

	/**
	 * Checks if ItemStacks NBT data is equal to original ItemStacks NBT data.
	 * 
	 * @param itemStack
	 *            The ItemStack to check.
	 * @return true if equal otherwise false.
	 */
	private boolean isTagCompoundEqual(ItemStack itemStack) {
		NBTTagCompound itemStackTag = itemStack.getTagCompound().getCompoundTag("Inventory");
		NBTTagCompound origItemStackTag = originalIS.getTagCompound().getCompoundTag("Inventory");

		// check if title is unequal
		if(itemStackTag.getString("title") != origItemStackTag.getString("title")) {
			return false;
		}
		
		// everything is equal
		return true;
	}

	/**
	 * Sets the name of the inventory.
	 * 
	 * @param name
	 *            The new name.
	 */
	public void setInvName(String name) {
		inventoryTitle = name;
	}

	/**
	 * If there is no inventory create one. Then load the content and title of
	 * the inventory from the NBT
	 */
	public void loadInventory() {
		readFromTag(data.getCompoundTag("Inventory"));
	}

	/**
	 * Saves the actual content of the inventory to the NBT.
	 */
	public void saveInventory() {
		writeToTag(data.getCompoundTag("Inventory"));
		setNBT();
	}

	/**
	 * Creates the Inventory Tag in the NBT with an empty inventory.
	 */
	private void createInventory() {
		// new String so that a new String object is created
		// so that title == title is false
		// needed for two new created backpacks
		setInvName(new String(originalIS.getItemName()));
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setCompoundTag("Inventory", writeToTag(new NBTTagCompound()));
		originalIS.setTagCompound(nbt);
	}

	/**
	 * Returns if an Inventory is saved in the NBT.
	 * 
	 * @param nbt
	 *            The NBTTagCompound to check for an inventory.
	 * @return True when the NBT is not null and the NBT has key "Inventory"
	 *         otherwise false.
	 */
	private boolean hasInventory(NBTTagCompound nbt) {
		return (nbt != null && (nbt.hasKey("Inventory") || nbt.hasKey(playerEntity.getEntityName())));
	}

	/**
	 * Drops Backpacks on the ground which are in this backpack
	 */
	private void dropContainedBackpacks() {
		for(int i = 0; i < getSizeInventory(); i++) {
			ItemStack item = getStackInSlot(i);
			if(item != null && item.getItem() instanceof BackpackItem) {
				playerEntity.dropPlayerItem(getStackInSlot(i));
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
	private NBTTagCompound writeToTag(NBTTagCompound outerTag) {
		if(outerTag == null) {
			return null;
		}

		outerTag.setString("title", getInvName());

		NBTTagList itemList = new NBTTagList();
		for(int i = 0; i < inventoryContents.length; i++) {
			if(inventoryContents[i] != null) {
				NBTTagCompound slotEntry = new NBTTagCompound();
				slotEntry.setByte("Slot", (byte) i);
				inventoryContents[i].writeToNBT(slotEntry);
				itemList.appendTag(slotEntry);
			}
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
	private void readFromTag(NBTTagCompound outerTag) {
		if(outerTag == null) {
			return;
		}

		setInvName(outerTag.getString("title"));

		NBTTagList itemList = outerTag.getTagList("Items");
		inventoryContents = new ItemStack[getSizeInventory()];
		for(int i = 0; i < itemList.tagCount(); i++) {
			NBTTagCompound slotEntry = (NBTTagCompound) itemList.tagAt(i);
			int j = slotEntry.getByte("Slot") & 0xff;

			if(j >= 0 && j < getSizeInventory()) {
				inventoryContents[j] = ItemStack.loadItemStackFromNBT(slotEntry);
			}
		}
	}
}
