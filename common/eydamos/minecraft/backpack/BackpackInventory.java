package eydamos.minecraft.backpack;

import com.google.common.collect.ContiguousSet;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;
import net.minecraft.src.TileEntity;

public class BackpackInventory implements IInventory {
	private static final int slotsCount = 27;
	private ItemStack inventoryContents[];
	private String inventoryTitle = "Backpack";
	private NBTTagCompound invTag;

	private int number;
	private int color;
	private boolean magic;
	EntityPlayer currentUser;

	public BackpackInventory(NBTTagCompound itemStackTag) {
		invTag = itemStackTag;
		inventoryContents = new ItemStack[slotsCount];
	}

	public BackpackInventory(int num, int col) {
		number = num;
		color = col;
		if(num == 31999) {
			setMagic();
		}
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
				onInventoryChanged();
				return itemstack;
			}
			ItemStack itemstack1 = inventoryContents[position].splitStack(decrease);
			if(inventoryContents[position].stackSize == 0) {
				inventoryContents[position] = null;
			}
			onInventoryChanged();
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
		onInventoryChanged();
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
		saveInventory();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openChest() {
		if(backpackExists(number)) {
			System.out.println("Backpack exists");
		} else {
			System.out.println("Backpack doesn't exists fuuuuuuuuuuuuuuuuuuuuu");
		}
		readFromTag(getTag());
	}

	@Override
	public void closeChest() {
		dropContainedBackpacks();
		saveInventory();
	}

	// ***************custom methods which are not in
	// IInventory*****************
	/**
	 * loads the content of the inventory from the NBT
	 */
	public void loadInventoryContent(int uid) {
		if(!backpackExists(uid)){
		    System.out.println("backpack does not exist!");
		    createUniqueInventory(0);
		}

		readFromTag(getBackpackTag(uid));
	}

	/**
	 * saves the actual content of the inventory to the NBT
	 */
	public void saveInventory() {
		System.out.println("saveInventory for uid " + number);
		if(backpackExists(number)) {
			System.out.println("Backpack exists");
		} else {
			System.out.println("Backpack doesn't exists fuuuuuuuuuuuuuuuuuuuuu");
		}
		writeToTag(getBackpackTag(number));
	}
	
	public void createUniqueInventory(int color) {
		int uid = getFirstFreeSlot();
		invTag.setCompoundTag(getBackpackIdentifier(uid),
				(new BackpackInventory(uid, color)).writeToTag(new NBTTagCompound()));
		System.out.println("Creating new backpack with id " + uid);
	}

	/**
	 * Creates an unique inventory for a backpack with a specific color.
	 * 
	 * @param name
	 * @param color
	 * @return
	 */
	public int createUniqueInventory(String name, int color) {
		int uid = getFirstFreeSlot();
		System.out.println("Creating new backpack with id " + uid);
		invTag.setCompoundTag(getBackpackIdentifier(uid),
				(new BackpackInventory(uid, color)).setInvName(name).writeToTag(new NBTTagCompound()));
		return uid;
	}

	/**
	 * Returns the first free UID between 16 and 32000.
	 * 
	 * @return
	 */
	public int getFirstFreeSlot() {
		for(int cnt = 16; cnt < 32000; cnt++) {
			if(!backpackExists(cnt))
				return cnt;
		}
		return 31998;
	}

	/**
	 * Checks if a Backpack with a specific uid exists in the NBT.
	 * 
	 * @param uid
	 * @return
	 */
	public boolean backpackExists(int uid) {
		return invTag.hasKey(getBackpackIdentifier(uid));
	}

	/**
	 * Creates an unique string based on the uid.
	 * 
	 * @param uid
	 * @return
	 */
	public static String getBackpackIdentifier(int uid) {
		System.out.println("get Backpack Identifier: " + "Backpack" + uid + "inv");
		return "Backpack" + uid + "inv";
	}
	
	/**
	 * Reads the InventoryData from the NBT for the specific uid.
	 * @param uid
	 * @return
	 */
	public NBTTagCompound getBackpackTag(int uid) {
		return invTag.getCompoundTag(getBackpackIdentifier(uid));
	}

	public BackpackInventory setMagic() {
		magic = true;
		return this;
	}

	public NBTTagCompound getTag() {
		if(magic) {
			return BackpackUtils.getMagicBackpackTag(currentUser);
		}
		return getBackpackTag(number);
	}

	/**
	 * Drops Backpacks on the ground which are in this backpack
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
	 * Sets the title
	 * @param name
	 * @return
	 */
	public BackpackInventory setInvName(String name) {
		inventoryTitle = name;
		System.out.println("set inventoryTitle to " + name);
		return this;
	}

	/**
	 * Writes a NBT Node with number, color, title and inventory.
	 * 
	 * @param outerTag
	 *            A clean NBT Node.
	 * @return The written NBT Node.
	 */
	public NBTTagCompound writeToTag(NBTTagCompound outerTag) {
		if(outerTag == null) {
			return null;
		}
		
		System.out.println("writeToTag");

		outerTag.setInteger("number", number);
		outerTag.setInteger("color", color);
		outerTag.setBoolean("magic", magic);
		outerTag.setString("title", getInvName());
		
		System.out.println("Number: " + number + " Color: " + color + " Magic: " + magic + " Name: " + getInvName());

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

		outerTag.setTag("Items", itemList);
		return outerTag;
	}

	/**
	 * Reads the number, color, title and content from a NBT.
	 * @param outerTag
	 * @return
	 */
	public BackpackInventory readFromTag(NBTTagCompound outerTag) {
		if(outerTag == null) {
			return this;
		}
		
		System.out.println("readFromTag");
		
		number = outerTag.getInteger("number");
		color = outerTag.getInteger("color");
		magic = outerTag.getBoolean("magic");
		setInvName(outerTag.getString("title"));

		System.out.println("Number: " + number + " Color: " + color + " Magic: " + magic);

		NBTTagList itemList = outerTag.getTagList("Items");
		inventoryContents = new ItemStack[getSizeInventory()];
		for(int i = 0; i < itemList.tagCount(); i++) {
			NBTTagCompound slotEntry = (NBTTagCompound) itemList.tagAt(i);
			int j = slotEntry.getByte("Slot") & 0xff;

			if(j >= 0 && j < getSizeInventory()) {
				inventoryContents[j] = ItemStack.loadItemStackFromNBT(slotEntry);
			}
			System.out.println(inventoryContents[i].itemID + " now at slot " + i);
		}
		return this;
	}

	/*public int getColorFromTag(NBTTagCompound outerTag) {
		if(outerTag == null) {
			return 0;
		}
		return outerTag.getInteger("color");
	}

	public String getNameFromTag(NBTTagCompound outerTag) {
		if(outerTag == null) {
			return "";
		}
		return outerTag.getString("title");
	}

	public BackpackInventory setPlayer(EntityPlayer player) {
		currentUser = player;
		return this;
	}*/

}
