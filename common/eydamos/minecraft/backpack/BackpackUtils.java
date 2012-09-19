package eydamos.minecraft.backpack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.src.CompressedStreamTools;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.MinecraftException;
import net.minecraft.src.ModLoader;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.SaveHandlerMP;
import net.minecraft.src.World;

public class BackpackUtils {
	public static final int NUM_START = 16;
	public static boolean firstRun = true;

	public static NBTTagCompound storageTag = null;

	public BackpackUtils() {}
	
	public static void reloadIfNeeded() {
		if(firstRun) {
			if(firstRun) {
				firstRun = false;
			}

			if(storageTag == null) {
				storageTag = new NBTTagCompound();
			}
		}
	}

	public static NBTTagCompound getBackpackTag(int number) {
		reloadIfNeeded();
		return storageTag.getCompoundTag(getBackpackIdentifier(number));
	}

	public static NBTTagCompound getMagicBackpackTag(EntityPlayer player) {
		reloadIfNeeded();
		return storageTag.getCompoundTag(getMagicBackpackIdentifier(player));
	}

	public static int getFirstFreeSlot() {
		reloadIfNeeded();
		for(int cnt = NUM_START; cnt < 32000; cnt++) {
			if(!backpackExists(cnt))
				return cnt;
		}
		return 31998;
	}

	public static int createNewBackpack(String name, int color) {
		reloadIfNeeded();
		int num = getFirstFreeSlot();
		storageTag.setCompoundTag(getBackpackIdentifier(num), (new BackpackInventory(num, color))
				.setInvName(name).writeToTag(new NBTTagCompound()));
		return num;
	}

	public static void createNewMagicBackpack(EntityPlayer player) {
		reloadIfNeeded();
		if(player == null)
			return;
		if(!magicBackpackExists(player)) {
			storageTag.setCompoundTag(getMagicBackpackIdentifier(player), (new BackpackInventory(
					31999, 0)).setInvName("Magic Backpack").writeToTag(new NBTTagCompound()));
		}
		return;
	}

	public static int createNewBackpack(int color) {
		reloadIfNeeded();
		int num = getFirstFreeSlot();
		storageTag.setCompoundTag(getBackpackIdentifier(num),
				(new BackpackInventory(num, color)).writeToTag(new NBTTagCompound()));
		System.out.println("Creating new backpack with id " + num);
		return num;
	}

	public static String getBackpackIdentifier(int number) {
		return "Backpack" + ((String) Integer.toString(number)) + "inv";
	}

	public static String getMagicBackpackIdentifier(EntityPlayer player) {
		return "MagicBackpack_inv";/* +player.username+"inv"; */
	}

	public static boolean backpackExists(int num) {
		reloadIfNeeded();
		return storageTag.hasKey(getBackpackIdentifier(num));
	}

	public static boolean magicBackpackExists(EntityPlayer pl) {
		return storageTag.hasKey(getMagicBackpackIdentifier(pl));
	}

	public static BackpackInventory getBackpackInventory(int num) {
		reloadIfNeeded();

		if(!backpackExists(num)) {
			System.out.println("backpack does not exist!");
			createNewBackpack(0);
		}

		return (new BackpackInventory()).readFromTag(getBackpackTag(num));
	}

	public static BackpackInventory getMagicBackpackInventory(EntityPlayer player) {
		reloadIfNeeded();

		if(!magicBackpackExists(player)) {
			System.out.println("magic backpack does not exist!");
			createNewMagicBackpack(player);
		}

		return (new BackpackInventory()).readFromTag(getMagicBackpackTag(player));
	}

	public static int getBackpackColor(int num) {
		reloadIfNeeded();
		if(!backpackExists(num)) {
			return 0;
		}
		return (new BackpackInventory()).getColorFromTag(getBackpackTag(num));
	}

	public static void loadBackpackNames() {
		return;
		// if(true) return;
		// if(ModLoader.getMinecraftInstance().theWorld.isRemote) return;
		//
		// reloadIfNeeded();
		// for(int q = NUM_START; q<getFirstFreeSlot(); q++){
		// if(backpackExists(q)){
		// setTooltip(q, getBackpackTag(q).getString("title"));
		//
		// //System.out.println("loadBackpackNames-"+q+"- "+getBackpackTag(q).getString("title"));
		// }
		// }
	}

	public static void setTooltip(int num, String name) {
		ModLoader.addLocalization("item.backpack." + Integer.toString(num) + ".name", name);
	}

	public static void setBackpackName(int num, String name) {
		reloadIfNeeded();
		setTooltip(num, name);

		// System.out.println("setBackpackName");
		getBackpackInventory(num).setInvName(name).writeToTag(getBackpackTag(num));
	}

	public static void setMagicBackpackName(EntityPlayer player, String name) {
		reloadIfNeeded();
		setTooltip(31999, name);
		getMagicBackpackInventory(player).setInvName(name).writeToTag(getMagicBackpackTag(player));
	}

	public static String getBackpackName(int num) {
		reloadIfNeeded();
		return (new BackpackInventory()).getNameFromTag(getBackpackTag(num));
	}

	public static String getMagicBackpackName(EntityPlayer player) {
		reloadIfNeeded();
		return (new BackpackInventory()).getNameFromTag(getMagicBackpackTag(player));
	}

}
