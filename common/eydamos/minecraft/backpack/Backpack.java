package eydamos.minecraft.backpack;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid="Backpack", name="Backpack", version="1.0.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class Backpack {
	private static final int backpackItemId = 18330;
	public static final Item backpackItem = new BackpackItem(backpackItemId);
	
	@Instance("Backpack")
	public static Backpack instance;
	
	@SidedProxy(clientSide="eydamos.minecraft.backpack.ClientProxy", serverSide="eydamos.minecraft.backpack.CommonProxy")
	public static CommonProxy proxy;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		// Stub Method
	}
	
	@Init
	public void load(FMLInitializationEvent event) {
		ItemStack backpackStack = new ItemStack(backpackItem, 1, 0);
		ItemStack leatherStack = new ItemStack(Item.leather, 1, 0);
		ItemStack colorStack = new ItemStack(Item.dyePowder, 1, 0);
		
		// Normal Backpack without dye
		GameRegistry.addRecipe(backpackStack, "LLL", "L L", "LLL",
				'L', leatherStack);
		LanguageRegistry.addName(backpackStack, BackpackItem.backpackNames[0]);
		
		// Backpacks from red to white
		for(int i = 1; i < 16; i++) {
			backpackStack = new ItemStack(backpackItem, 1, i);
			colorStack = new ItemStack(Item.dyePowder, 1, i);
			GameRegistry.addRecipe(backpackStack, "LLL", "LDL", "LLL",
					'L', leatherStack, 'D', colorStack);
			LanguageRegistry.addName(backpackStack, BackpackItem.backpackNames[i]);
		}
		
		// Magic Backpack
		backpackStack = new ItemStack(backpackItem, 1, BackpackItem.MAGICBACKPACK);
		colorStack = new ItemStack(Item.diamond);
		GameRegistry.addRecipe(backpackStack, "LLL", "LDL", "LLL",
				'L', leatherStack, 'D', colorStack);
		LanguageRegistry.addName(backpackStack, BackpackItem.backpackNames[16]);
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		// Stub Method
	}
}
