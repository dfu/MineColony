package net.minecraft.src;

// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) braces deadcode 

import java.util.Map;

public class mod_MineColony extends BaseMod {

	// Needs Updated 
	/*
	public static int hutLumberjackID = ModLoader.getUniqueSpriteIndex("/terrain.png");
	public static int hutMinerID = ModLoader.getUniqueSpriteIndex("/terrain.png");
	public static int hutWarehouseID = ModLoader.getUniqueSpriteIndex("/terrain.png");
	public static int hutFarmerID = ModLoader.getUniqueSpriteIndex("/terrain.png");
	public static int scepterGoldID = ModLoader.getUniqueSpriteIndex("/gui/items.png");
	public static int scepterSteelID = ModLoader.getUniqueSpriteIndex("/gui/items.png");
	*/
	public static int hutLumberjackID = 1;
	public static int hutMinerID = 2;
	public static int hutWarehouseID = 3;
	public static int hutFarmerID = 4;
	public static int scepterGoldID = 5;
	public static int scepterSteelID = 6;

	public static int blockLumberjackID = 93;
	public static int blockMinerID = 94;
	public static int blockWarehouseID = 95;
	public static int blockFarmerID = 96;


	public static final Block hutLumberjack = (new BlockHutLumberjack(93,hutLumberjackID)).setHardness(2.5F).setStepSound(Block.soundWoodFootstep).setBlockName("hutLumberjack");
	public static final Block hutMiner = (new BlockHutMiner(94,hutMinerID)).setHardness(2.5F).setStepSound(Block.soundWoodFootstep).setBlockName("hutMiner");
	public static final Block hutWarehouse = (new BlockHutWarehouse(95,hutWarehouseID)).setHardness(2.5F).setStepSound(Block.soundWoodFootstep).setBlockName("hutWarehouse");
	public static final Block hutFarmer = (new BlockHutFarmer(96,hutFarmerID)).setHardness(2.5F).setStepSound(Block.soundWoodFootstep).setBlockName("hutFarmer");
	public static Item scepterGold = (new ItemScepter(ModLoader.getUniqueEntityId())).setIconIndex(scepterGoldID).setFull3D().setItemName("scepterGold");
	public static Item scepterSteel = (new ItemScepter(ModLoader.getUniqueEntityId())).setIconIndex(scepterSteelID).setFull3D().setItemName("scepterSteel");


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
		//Removed until testing finishes

		ModLoader.addOverride("/gui/items.png", "/gui/Item_scepterGold.png");
		ModLoader.addOverride("/gui/items.png", "/gui/Item_scepterSteel.png");
		ModLoader.addOverride("/terrain.png", "/Block_hutLumberjack.png");
		ModLoader.addOverride("/terrain.png", "/Block_hutMiner.png");
		ModLoader.addOverride("/terrain.png", "/Block_hutWarehouse.png");
		ModLoader.addOverride("/terrain.png", "/Block_hutFarmer.png");


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
