package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockHutMiner extends BlockHut {
	
	private EntityMiner em;
	
	public BlockHutMiner(int blockID, int _textureID) {
		super(blockID);
		textureID = _textureID;
		hutWidth = 5;
		hutHeight = 4;
		clearingRange = 4;
		halfWidth = hutWidth/2;
		workingRange = 20;
		em=null;
		// Sets the recipe be two planks horizontal to each other
		// CraftingManager.getInstance().addRecipe(new ItemStack(blockID, 1,0),
				// new Object[] { "##", Character.valueOf('#'), Block.dirt,});
	}
	
	public boolean canPlaceBlockAt(World world, int i, int j, int k)
	{
		// check if there are other chests nearby
		Vec3D chestPos = scanForBlockNearPoint(world, mod_MineColony.hutMiner.blockID, i,j,k, workingRange, 20, workingRange);
		if (chestPos != null)
			return false;

		return super.canPlaceBlockAt(world, i, j, k);
	}
	
	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer)
	{	
		// build house when clicked with stick
		ItemStack is = entityplayer.getCurrentEquippedItem();
		if(is !=null && is.getItem()!=null && (is.getItem().shiftedIndex == mod_MineColony.scepterGold.shiftedIndex))
		{
			// Build Miner's hut

			// clean area around house
			for (int x = i - clearingRange; x <= i + clearingRange; x++)
				for (int z = k - clearingRange; z <= k + clearingRange; z++)
					for (int y = j; y < j + hutHeight; y++) {
						if(x!=i || y!=j || z!=k)
							world.setBlockWithNotify(x, y, z, 0);
					}

			// create empty box with stone
			for (int x = i - halfWidth; x <= i + halfWidth; x++)
				for (int z = k - halfWidth; z <= k + halfWidth; z++)
					for (int y = j - 1; y < j + hutHeight; y++) {

						if (x == i - halfWidth || x == i + halfWidth
								|| z == k - halfWidth || z == k + halfWidth) {
							// stone on sides
							world.setBlockWithNotify(x, y, z, Block.cobblestone.blockID);
						} else if(x!=i || y!=j || z!=k){
							// air inside
							world.setBlockWithNotify(x, y, z, 0);
						}
					}

			// floor
			for (int x = i - halfWidth-1; x <= i + halfWidth+1; x++)
				for (int z = k - halfWidth-1; z <= k + halfWidth+1; z++) {
					world.setBlockWithNotify(x, j - 1, z, Block.cobblestone.blockID);
				}
			
			// roof
			for (int x = i - halfWidth; x <= i + halfWidth; x++)
				for (int z = k - halfWidth; z <= k + halfWidth; z++) {
					world.setBlockWithNotify(x, j + hutHeight-1, z, Block.cobblestone.blockID);
				}

			// Pillars
			for (int y = j; y < j + hutHeight - 2; y++) {
				world.setBlockWithNotify(i - halfWidth, y, k - halfWidth,
						Block.wood.blockID);
				world.setBlockWithNotify(i + halfWidth, y, k - halfWidth,
						Block.wood.blockID);

				world.setBlockWithNotify(i - halfWidth, y, k + halfWidth,
						Block.wood.blockID);
				world.setBlockWithNotify(i + halfWidth, y, k + halfWidth,
						Block.wood.blockID);
			}

			// Door
			world.setBlockWithNotify(i+1, j, k - halfWidth, 0);
			world.setBlockWithNotify(i+1, j + 1, k - halfWidth,0);
			world.setBlockWithNotify(i+1, j + 2, k - halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i, j, k - halfWidth, 0);
			world.setBlockWithNotify(i, j + 1, k - halfWidth,0);
			world.setBlockWithNotify(i, j + 2, k - halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i-1, j, k - halfWidth, 0);
			world.setBlockWithNotify(i-1, j + 1, k - halfWidth,0);
			world.setBlockWithNotify(i-1, j + 2, k - halfWidth,Block.planks.blockID);

			world.setBlockWithNotify(i-2, j + 2, k - halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i+2, j + 2, k - halfWidth,Block.planks.blockID);
			
			// backyard
			
			world.setBlockWithNotify(i+1, j, k + halfWidth, 0);
			world.setBlockWithNotify(i+1, j + 1, k + halfWidth,0);
			world.setBlockWithNotify(i+1, j + 2, k + halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i, j, k + halfWidth, 0);
			world.setBlockWithNotify(i, j + 1, k + halfWidth,0);
			world.setBlockWithNotify(i, j + 2, k + halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i-1, j, k + halfWidth, 0);
			world.setBlockWithNotify(i-1, j + 1, k + halfWidth,0);
			world.setBlockWithNotify(i-1, j + 2, k + halfWidth,Block.planks.blockID);

			world.setBlockWithNotify(i-2, j + 2, k + halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i+2, j + 2, k + halfWidth,Block.planks.blockID);

			// Windows
			world.setBlockWithNotify(i - halfWidth, j + 1, k, 0);
			world.setBlockWithNotify(i - halfWidth, j, k, 0);
			world.setBlockWithNotify(i + halfWidth, j + 1, k, 0);
			world.setBlockWithNotify(i + halfWidth, j, k, 0);
			//world.setBlockWithNotify(i, j + 1, k+halfWidth, Block.glass.blockID);
		}
		else
			return super.blockActivated(world, i, j, k, entityplayer);
		
		return true;
		
	}
	
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);		
		//world.setWorldTime(0);

		// Chest for stuff with 5 stone axes
		world.setBlockWithNotify(i, j, k, mod_MineColony.hutMiner.blockID);
		TileEntityChest tileentitychest = (TileEntityChest) world
				.getBlockTileEntity(i, j, k);

		tileentitychest.setInventorySlotContents(0,  new ItemStack(Item.pickaxeWood, 1));
		tileentitychest.setInventorySlotContents(1,  new ItemStack(Item.shovelWood, 1));
		tileentitychest.setInventorySlotContents(2,  new ItemStack(Block.torchWood, 32));

//		tileentitychest.setInventorySlotContents(3,  new ItemStack(Item.pickaxeSteel, 1));
//		tileentitychest.setInventorySlotContents(4,  new ItemStack(Item.shovelSteel, 1));
//		tileentitychest.setInventorySlotContents(5,  new ItemStack(Item.pickaxeSteel, 1));
//		tileentitychest.setInventorySlotContents(6,  new ItemStack(Item.shovelSteel, 1));
//		tileentitychest.setInventorySlotContents(7,  new ItemStack(Item.pickaxeSteel, 1));
//		tileentitychest.setInventorySlotContents(8,  new ItemStack(Item.shovelSteel, 1));
//		tileentitychest.setInventorySlotContents(0,  new ItemStack(Item.pickaxeDiamond, 1));
//		tileentitychest.setInventorySlotContents(0,  new ItemStack(Item.pickaxeDiamond, 1));

		//		tileentitychest.setInventorySlotContents(0,  new ItemStack(Item.pickaxeDiamond, 1));
//		tileentitychest.setInventorySlotContents(1,  new ItemStack(Item.shovelDiamond, 1));
//		tileentitychest.setInventorySlotContents(8,  new ItemStack(mod_MineColony.scepterGold, 1));
//		tileentitychest.setInventorySlotContents(9,  new ItemStack(mod_MineColony.scepterSteel, 1));
//		tileentitychest.setInventorySlotContents(13,  new ItemStack(Item.shovelDiamond, 1));
//		tileentitychest.setInventorySlotContents(14,  new ItemStack(Item.pickaxeDiamond, 1));
//		tileentitychest.setInventorySlotContents(15,  new ItemStack(Item.shovelDiamond, 1));
//		tileentitychest.setInventorySlotContents(16,  new ItemStack(Item.pickaxeDiamond, 1));
//		tileentitychest.setInventorySlotContents(17,  new ItemStack(Item.shovelDiamond, 1));
//		tileentitychest.setInventorySlotContents(18,  new ItemStack(Item.pickaxeDiamond, 1));
//		tileentitychest.setInventorySlotContents(19,  new ItemStack(Item.shovelDiamond, 1));

		


		//tileentitychest.setInventorySlotContents(20, new ItemStack(Block.planks, 64));
//		tileentitychest.setInventorySlotContents(21,  new ItemStack(Item.axeSteel, 1));
//		tileentitychest.setInventorySlotContents(22,  new ItemStack(Item.axeSteel, 1));
//		tileentitychest.setInventorySlotContents(23,  new ItemStack(Item.axeGold, 1));
		//tileentitychest.setInventorySlotContents(24,  new ItemStack(Item.ingotIron, 12));
	//	tileentitychest.setInventorySlotContents(9,  new ItemStack(Item.ingotGold, 1));

		spawnWorker(world, i, j, k);
	}
	
	public void updateTick(World world, int i, int j, int k, Random random)
    {
		super.updateTick(world, i, j, k, random);
		if(getMinerAround(world, i,j,k)==null)
		{
			if(em!=null)
				em.isDead = true;
			spawnWorker(world, i, j, k);
		}
		// TileEntityChest tileentitychest = (TileEntityChest) world
		// .getBlockTileEntity(i, j, k);
		// tileentitychest.setInventorySlotContents(10,  new ItemStack(Item.pickaxeDiamond, 1));
		// tileentitychest.setInventorySlotContents(11,  new ItemStack(Item.shovelDiamond, 1));

    }
	
	public void spawnWorker(World world, int i, int j, int k)
	{
		// spawn miner
		em = (EntityMiner) EntityList.createEntityInWorld("Miner", world); 
		
		// scan for first free block near chest
		Vec3D spawnPoint = scanForBlockNearPoint(world, 0, i, j, k, 1, 0, 1);
		if(spawnPoint==null)
			spawnPoint = scanForBlockNearPoint(world, Block.snow.blockID, i, j, k, 1, 0, 1);
		
		if(spawnPoint!=null)
		{
			em.setPosition(spawnPoint.xCoord, spawnPoint.yCoord, spawnPoint.zCoord);
			em.setHomePosition(i, j, k);
			world.entityJoinedWorld(em);
		}

	}
	
	public void onBlockRemoval(World world, int i, int j, int k)
	{
		//EntityMiner miner = getMinerAround(world, i,j,k);
		em = getMinerAround(world, i,j,k);
		if(em!=null)
		{
			em.isDead=true;
		}
	}
	
	public EntityMiner getMinerAround(World world, int i, int j, int k)
	{
		List list = world.getEntitiesWithinAABB(EntityMiner.class, this.getCollisionBoundingBoxFromPool(world, i, j, k).expand(workingRange, 100, workingRange));
		if (list != null) {
			for (int ii = 0; ii < list.size(); ii++) {
				if (list.get(ii) instanceof EntityMiner) {
					return (EntityMiner)list.get(ii);
				}
			}
		}
		
		return null;
	}
	
	public int getBlockTextureFromSide(int side)
    {
       if(side==1)
       {
          // Workshop top
          return textureID;
       }
        return blockIndexInTexture;
    }
	
	public int getBlockTexture(IBlockAccess iblockaccess, int i, int j, int k, int l)
    {
		if(l == 1)
        {
            return textureID;
        }
		else
			return super.getBlockTexture(iblockaccess, i, j, k, l);
    }
    
}
