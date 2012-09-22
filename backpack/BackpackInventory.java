package backpack;

import com.google.common.collect.ContiguousSet;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;

public class BackpackInventory implements IInventory {
	// number of slots 3 lines a 9 slots
	private static final int slotsCount = 27;
	// the content of the inventory
	private ItemStack inventoryContents[];

	// if the backpack is an ender backpack or not
	private boolean isEnder;
	// the default title of the backpack
	private String inventoryTitle = "Backpack";

	// a instance of the player to get the used item and for magic backpack
	private EntityPlayer playerEntity;

	/**
	 * Takes a player and an ItemStack.
	 * 
	 * @param player
	 *            The player which has the backpack.
	 * @param is
	 *            The ItemStack which holds the backpack.
	 */
	public BackpackInventory(EntityPlayer player, ItemStack is) {
		playerEntity = player;
		setEnderbackpack(is.getItemDamage());
		inventoryContents = new ItemStack[slotsCount];
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
	 * Returns the NBTTagCompound of the user it the backpack is magic or the
	 * NBTTagCompound of the currently used item.
	 * 
	 * @return Returns the NBTTagCompound.
	 */
	public NBTTagCompound getNBT() {
		NBTTagCompound nbt = null;
		if(isEnder) {
			nbt = playerEntity.getEntityData();
		} else {
			if(!playerEntity.getCurrentEquippedItem().hasTagCompound()) {
				playerEntity.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());
			}
			nbt = playerEntity.getCurrentEquippedItem().getTagCompound();
		}

		return nbt;
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
		if(!hasInventory()) {
			createInventory(null);
		}

		readFromTag(getNBT().getCompoundTag("Inventory"));
	}

	/**
	 * Saves the actual content of the inventory to the NBT.
	 */
	public void saveInventory() {
		writeToTag(getNBT().getCompoundTag("Inventory"));
	}

	/**
	 * Creates the Inventory Tag in the NBT with an empty inventory.
	 * 
	 * @param name
	 *            The name of the inventory or null for default.
	 */
	public void createInventory(String name) {
		if(name == null) {
			setInvName("Backpack");
		} else {
			setInvName(name);
		}
		getNBT().setCompoundTag("Inventory", writeToTag(new NBTTagCompound()));
	}

	/**
	 * Returns if an Inventory is saved in the NBT.
	 * 
	 * @return True when the NBT has key "Inventory" otherwise false.
	 */
	public boolean hasInventory() {
		return getNBT().hasKey("Inventory");
	}

	/**
	 * Sets magic to true or false based on the item damage to identify a magic
	 * backpack.
	 * 
	 * @param itemDamage
	 *            The damage of the item.
	 */
	public void setEnderbackpack(int itemDamage) {
		isEnder = (itemDamage == BackpackItem.ENDERBACKPACK);
	}

	/**
	 * Drops Backpacks on the ground which are in this backpack
	 */
	public void dropContainedBackpacks() {
		for(int i = 0; i < getSizeInventory(); i++) {
			if(getStackInSlot(i) != null
					&& getStackInSlot(i).getItemDamage() == BackpackItem.ENDERBACKPACK) {
				playerEntity.dropPlayerItem(getStackInSlot(i).copy());
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
	public void readFromTag(NBTTagCompound outerTag) {
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
