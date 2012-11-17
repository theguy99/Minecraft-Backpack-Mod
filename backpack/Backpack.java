package backpack;

import net.minecraft.src.Block;
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

@Mod(modid = "Backpack", name = "Backpack", version = "1.5.4")
@NetworkMod(clientSideRequired = true, serverSideRequired = false, channels = {"BackpackRename"}, packetHandler = BackpackPacketHandler.class)
public class Backpack {
	// the id of the backpack items
	protected static Property backpackId;
	protected static Property boundLeatherId;
	protected static Property tannedLeatherId;
	// an instance of the actual item
	public static Item backpack;
	public static Item boundLeather;
	public static Item tannedLeather;
	
	protected Property enderRecipe;
	protected static Integer sizeM;
	protected static Integer sizeL;

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
		backpackId = config.getItem("backpackId", 18330);
		boundLeatherId = config.getItem("boundLeatherId", 18331);
		tannedLeatherId = config.getItem("tannedLeatherId", 18332);
		
		config.addCustomCategoryComment(Configuration.CATEGORY_GENERAL, getCommentText());
		enderRecipe = config.get(Configuration.CATEGORY_GENERAL, "enderRecipe", 0);
		sizeM = config.get(Configuration.CATEGORY_GENERAL, "backpackSizeM", 3).getInt();
		sizeL = config.get(Configuration.CATEGORY_GENERAL, "backpackSizeL", 6).getInt();

		// save the file so it will be generated if it doesn't exists
		config.save();
	}

	@Init
	public void load(FMLInitializationEvent event) {
		// create an instance of the backpack item with the id loaded from the
		// configuration file
		backpack = new ItemBackpack(backpackId.getInt());
		boundLeather = new ItemLeather(boundLeatherId.getInt());
		tannedLeather = new ItemLeather(tannedLeatherId.getInt());

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
		ItemStack backpackStack = new ItemStack(backpack, 1, 16);
		ItemStack boundLeatherStack = new ItemStack(boundLeather);
		ItemStack colorStack = new ItemStack(Item.dyePowder, 1, 0);

		// normal backpack without dye
		GameRegistry.addRecipe(backpackStack, "LLL", "L L", "LLL", 'L', Item.leather);
		
		// normal big backpack without dye
		backpackStack = new ItemStack(backpack, 1, 48);
		GameRegistry.addRecipe(backpackStack, "LLL", "L L", "LLL", 'L', tannedLeather);

		// backpacks and big backpacks from black(0) to white(15)
		for(int i = 0; i < 16; i++) {
			// the dye
			colorStack = new ItemStack(Item.dyePowder, 1, i);
			
			// backpacks
			backpackStack = new ItemStack(backpack, 1, i);
			GameRegistry.addRecipe(backpackStack, "LLL", "LDL", "LLL", 
					'L', Item.leather, 
					'D', colorStack);
			LanguageRegistry.addName(backpackStack, ItemBackpack.backpackNames[i]);
			
			// big backpacks
			backpackStack = new ItemStack(backpack, 1, i + 32);
			GameRegistry.addRecipe(backpackStack, "LLL", "LDL", "LLL", 
					'L', tannedLeather, 
					'D', colorStack);
			LanguageRegistry.addName(backpackStack, "Big " + ItemBackpack.backpackNames[i]);
		}

		// ender Backpack
		if(enderRecipe.getInt() == 0) {
			backpackStack = new ItemStack(backpack, 1, ItemBackpack.ENDERBACKPACK);
			GameRegistry.addRecipe(backpackStack, "LLL", "LEL", "LLL",
					'L', Item.leather,
					'E', Block.enderChest);
		} else {
			backpackStack = new ItemStack(backpack, 1, ItemBackpack.ENDERBACKPACK);
			GameRegistry.addRecipe(backpackStack, "LLL", "LDL", "LLL",
					'L', Item.leather,
					'D', Item.eyeOfEnder);
		}
		LanguageRegistry.addName(backpackStack, ItemBackpack.backpackNames[16]);
		
		// bound leather
		GameRegistry.addRecipe(boundLeatherStack, "SSS", "LSL", "SSS",
				'S', Item.silk,
				'L', Item.leather);
		LanguageRegistry.addName(boundLeatherStack, "Bound Leather");
		
		// tanned leather
		ItemStack tannedLeatherStack = new ItemStack(tannedLeather);
		GameRegistry.addSmelting(boundLeather.shiftedIndex, tannedLeatherStack, 0.1f);
		LanguageRegistry.addName(tannedLeatherStack, "Tanned Leather");
		
		// enhance backpack to big backpack
		GameRegistry.addRecipe(new RecipeEnhanceBackpack());
		
		// recolor backpack
		GameRegistry.addRecipe(new RecipeRecolorBackpack());
	}
	
	private String getCommentText() {
		return "enderRecipe:\n" +
				"0 enderChest\n" +
				"1 eye of the ender\n" +
				"\n" +
				"backpackSizeM / backpackSizeL:\n" +
				"number of rows (9 slots)\n" +
				"valid: integers 1-6";
	}
}
