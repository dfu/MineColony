package net.minecraft.src;

import net.minecraft.client.Minecraft;
import java.util.Properties;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

public class mod_MineColony extends BaseMod {

	public static int blockLumberjackID = 93;
	public static int blockMinerID = 94;
	public static int blockWarehouseID = 95;
	public static int blockFarmerID = 96;
	private static final Properties minecolProps = new Properties();

	public static Block hutLumberjack;
	public static Block hutMiner;
	public static Block hutWarehouse;
	public static Block hutFarmer;
	public static Item scepterGold;
	public static Item scepterSteel;



	public void AddRecipes(CraftingManager recipes) {
		//Defaults
		recipes.addRecipe(new ItemStack(mod_MineColony.scepterGold, 1),
				new Object[] { " X", "# ", Character.valueOf('#'), Item.stick,
						Character.valueOf('X'), Item.ingotGold });
		recipes.addRecipe(new ItemStack(mod_MineColony.scepterSteel, 1),
				new Object[] { " X", "# ", Character.valueOf('#'), Item.stick,
						Character.valueOf('X'), Item.ingotIron });						
		//Individual Huts

		//Farmer
		recipes.addRecipe(new ItemStack(blockFarmerID, 6,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.hoeDiamond });
		recipes.addRecipe(new ItemStack(blockFarmerID, 4,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.hoeGold });

		recipes.addRecipe(new ItemStack(blockFarmerID, 3,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.hoeSteel });

		recipes.addRecipe(new ItemStack(blockFarmerID, 2,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.hoeStone });

		recipes.addRecipe(new ItemStack(blockFarmerID, 1,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.hoeWood });

		//LumberJack
		recipes.addRecipe(new ItemStack(blockLumberjackID, 2,0),
		new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.axeStone });
		
		recipes.addRecipe(new ItemStack(blockLumberjackID, 6,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.axeDiamond });

		recipes.addRecipe(new ItemStack(blockLumberjackID, 4,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.axeGold });

		recipes.addRecipe(new ItemStack(blockLumberjackID, 3,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.axeSteel });

		recipes.addRecipe(new ItemStack(blockLumberjackID, 1,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.axeWood });

		//Miner
		recipes.addRecipe(new ItemStack(blockMinerID, 6,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.pickaxeDiamond });

		recipes.addRecipe(new ItemStack(blockMinerID, 4,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.pickaxeGold });

		recipes.addRecipe(new ItemStack(blockMinerID, 3,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.pickaxeSteel });

		recipes.addRecipe(new ItemStack(blockMinerID, 2,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.pickaxeStone });

		recipes.addRecipe(new ItemStack(blockMinerID, 1,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Item.pickaxeWood });

		//Warehouse
		recipes.addRecipe(new ItemStack(blockWarehouseID, 1,0),
				new Object[] { "###", "#X#", "###", Character.valueOf('#'), Block.planks, Character.valueOf('X'), Block.crate });
	}


	public mod_MineColony() {
		try {
			String mc_path = (new StringBuilder()).append(Minecraft.getMinecraftDir().getCanonicalPath()).append("/MineColony.properties").toString();
			FileInputStream f = new FileInputStream(mc_path);
			minecolProps.load(f);
			blockLumberjackID = Integer.parseInt(minecolProps.getProperty("LumberjackBlockID"));
			blockMinerID = Integer.parseInt(minecolProps.getProperty("MinerBlockID"));
			blockFarmerID = Integer.parseInt(minecolProps.getProperty("FarmerBlockID"));
			blockWarehouseID = Integer.parseInt(minecolProps.getProperty("WarehouseBlockID"));
			f.close();
		}
		catch (IOException e) {
			ModLoader.getLogger().warning("[MineColony] could not open conf file.");
			ModLoader.getLogger().warning("[MineColony] using default Block IDs\n");
		}

		int overrideID;

		overrideID = ModLoader.addOverride("/terrain.png", "/Block_hutLumberjack.png");
		hutLumberjack = (new BlockHutLumberjack(blockLumberjackID,overrideID)).setHardness(2.5F).setStepSound(Block.soundWoodFootstep).setBlockName("hutLumberjack");
		overrideID = ModLoader.addOverride("/terrain.png", "/Block_hutMiner.png");
		hutMiner = (new BlockHutMiner(blockMinerID,overrideID)).setHardness(2.5F).setStepSound(Block.soundWoodFootstep).setBlockName("hutMiner");
		overrideID = ModLoader.addOverride("/terrain.png", "/Block_hutWarehouse.png");
		hutWarehouse = (new BlockHutWarehouse(blockWarehouseID,overrideID)).setHardness(2.5F).setStepSound(Block.soundWoodFootstep).setBlockName("hutWarehouse");
		overrideID = ModLoader.addOverride("/terrain.png", "/Block_hutFarmer.png");
		hutFarmer = (new BlockHutFarmer(blockFarmerID,overrideID)).setHardness(2.5F).setStepSound(Block.soundWoodFootstep).setBlockName("hutFarmer");
		overrideID = ModLoader.addOverride("/gui/items.png", "/gui/Item_scepterGold.png");
		scepterGold = (new ItemScepter(ModLoader.getUniqueEntityId())).setIconIndex(overrideID).setFull3D().setItemName("scepterGold");
		overrideID = ModLoader.addOverride("/gui/items.png", "/gui/Item_scepterSteel.png");
		scepterSteel = (new ItemScepter(ModLoader.getUniqueEntityId())).setIconIndex(overrideID).setFull3D().setItemName("scepterSteel");

		// These return int overrides for something


		ModLoader.RegisterBlock(hutLumberjack);
		ModLoader.RegisterBlock(hutMiner);
		ModLoader.RegisterBlock(hutWarehouse);
		ModLoader.RegisterBlock(hutFarmer);
		ModLoader.RegisterEntityID(EntityLumberjack.class, "Lumberjack", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityMiner.class, "Miner", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityDeliveryMan.class, "DeliveryMan", ModLoader.getUniqueEntityId());
		ModLoader.RegisterEntityID(EntityFarmer.class, "Farmer", ModLoader.getUniqueEntityId());
		ModLoader.AddName(mod_MineColony.scepterGold, "Golden scepter");
		ModLoader.AddName(mod_MineColony.scepterSteel, "Iron scepter");
		ModLoader.AddName(mod_MineColony.hutLumberjack, "Lumberjack's chest");
		ModLoader.AddName(mod_MineColony.hutMiner, "Miner's chest");
		ModLoader.AddName(mod_MineColony.hutWarehouse, "Delivery man's chest");
		ModLoader.AddName(mod_MineColony.hutFarmer, "Farmer's chest");



	}

	public void AddRenderer(Map map) {
		map.put(EntityLumberjack.class, new RenderBiped(new ModelBiped(), 0.5F));
		map.put(EntityMiner.class, new RenderBiped(new ModelBiped(), 0.5F));
		map.put(EntityDeliveryMan.class, new RenderBiped(new ModelBiped(), 0.5F));
		map.put(EntityFarmer.class, new RenderBiped(new ModelBiped(), 0.5F));
	}

	/*
	public void RegisterTextureOverrides()
	{
		ModLoader.addOverride("/gui/items.png", "/gui/Item_scepterGold.png", scepterGoldID);
		ModLoader.addOverride("/gui/items.png", "/gui/Item_scepterSteel.png", scepterSteelID);
		ModLoader.addOverride("/terrain.png", "/Block_hutLumberjack.png", hutLumberjackID);
		ModLoader.addOverride("/terrain.png", "/Block_hutMiner.png", hutMinerID);
		ModLoader.addOverride("/terrain.png", "/Block_hutWarehouse.png", hutWarehouseID);
		ModLoader.addOverride("/terrain.png", "/Block_hutFarmer.png", hutFarmerID);
	}
	*/

	public String Version()
	{
		return "MineColony 0.41b";
	}

}
