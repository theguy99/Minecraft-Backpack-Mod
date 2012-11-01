package backpack;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "Backpack", name = "Backpack", version = "1.2.4")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {"BackpackRename"}, packetHandler = BackpackPacketHandler.class)
public class Backpack {
	// the id of the backpack items
	private static Property backpackItemId;
	// an instance of the actual item
	public static Item backpackItem;

	@Instance("Backpack")
	public static Backpack instance;

	@SidedProxy(clientSide = "backpack.ClientProxy", serverSide = "backpack.CommonProxy")
	public static CommonProxy proxy;

	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		// get the configuration file and let forge guess it's name
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		// load the content of the configuration file
		config.load();

		// gets the item id from the configuration or creates it if it doesn't exists
		backpackItemId = config.getItem("backpackItemId", 18330);

		// save the file so it will be generated it it doesn't exists
		config.save();
	}

	@Init
	public void load(FMLInitializationEvent event) {
		// create an instance of the backpack item with the id loaded from the
		// configuration file
		backpackItem = new BackpackItem(backpackItemId.getInt());

		// register recipes
		registerRecipes();

		// register GuiHandler for backpack name change
		NetworkRegistry.instance().registerGuiHandler(this, new BackpackGuiHandler());
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		// Stub Method
	}

	/**
	 * adds all recipes to the game registry
	 */
	private void registerRecipes() {
		ItemStack backpackStack = new ItemStack(backpackItem, 1, 0);
		ItemStack colorStack = new ItemStack(Item.dyePowder, 1, 0);

		// Normal Backpack without dye
		GameRegistry.addRecipe(backpackStack, "LLL", "L L", "LLL", 'L', Item.leather);
		LanguageRegistry.addName(backpackStack, BackpackItem.backpackNames[0]);

		// Backpacks from red to white
		for(int i = 1; i < 16; i++) {
			backpackStack = new ItemStack(backpackItem, 1, i);
			colorStack = new ItemStack(Item.dyePowder, 1, i);
			GameRegistry.addRecipe(backpackStack, "LLL", "LDL", "LLL", 
					'L', Item.leather, 
					'D', colorStack);
			LanguageRegistry.addName(backpackStack, BackpackItem.backpackNames[i]);
		}

		// Ender Backpack
		backpackStack = new ItemStack(backpackItem, 1, BackpackItem.ENDERBACKPACK);
		GameRegistry.addRecipe(backpackStack, "LLL", "LDL", "LLL",
				'L', Item.leather,
				'D', Item.eyeOfEnder);
		LanguageRegistry.addName(backpackStack, BackpackItem.backpackNames[16]);
	}
}
