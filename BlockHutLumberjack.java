package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockHutLumberjack extends BlockHut {
	
	private EntityLumberjack el;
	
	public BlockHutLumberjack(int blockID, int _textureID) {
		super(blockID);
		textureID = _textureID;
		hutWidth = 7;
		hutHeight = 5;
		clearingRange = 4;
		halfWidth = hutWidth/2;
		workingRange = 20;
		// Sets the recipe be two planks horizontal to each other
		// CraftingManager.getInstance().addRecipe(new ItemStack(blockID, 1,0),
				// new Object[] { "##", Character.valueOf('#'), Block.planks,});
	}

	public boolean canPlaceBlockAt(World world, int i, int j, int k)
	{
		// check if there are other chests nearby
		Vec3D chestPos = scanForBlockNearPoint(world, mod_MineColony.hutLumberjack.blockID, i,j,k, workingRange, 20, workingRange);
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
			
			// Build Lumberjack's hut

			// clean area around house
			for (int x = i - clearingRange; x <= i + clearingRange; x++)
				for (int z = k - clearingRange; z <= k + clearingRange; z++)
					for (int y = j; y < j + hutHeight; y++) {
						if(x!=i || y!=j || z!=k)
							world.setBlockWithNotify(x, y, z, 0);
					}

			// create empty box with planks
			for (int x = i - halfWidth; x <= i + halfWidth; x++)
				for (int z = k - halfWidth; z <= k + halfWidth; z++)
					for (int y = j - 1; y < j + hutHeight - 1; y++) {

						if (x == i - halfWidth || x == i + halfWidth
								|| z == k - halfWidth || z == k + halfWidth) {
							// planks on sides
							world.setBlockWithNotify(x, y, z, Block.sandStone.blockID);
						} else if(x!=i || y!=j || z!=k){
							// air inside
							world.setBlockWithNotify(x, y, z, 0);
						}
					}

			// floor
			for (int x = i - halfWidth; x <= i + halfWidth; x++)
				for (int z = k - halfWidth; z <= k + halfWidth; z++) {
					world.setBlockWithNotify(x, j - 1, z, Block.stone.blockID);
				}
			
			// roof
			for (int dy = 0 ; dy <= 5; dy++)
				for (int x = i - halfWidth -1 +dy; x <= i + halfWidth-dy + 1; x++)
					for (int z = k - halfWidth; z <= k + halfWidth; z++) {
						if(dy>0 && x!=i-halfWidth-1+dy && x!=i+halfWidth-dy+1)
							world.setBlockWithNotify(x, j +hutHeight + dy-2, z, Block.sandStone.blockID);
						else
							world.setBlockWithNotify(x, j +hutHeight + dy-2, z, Block.planks.blockID);
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
			///world.setBlockWithNotify(i+1, j + 2, k - halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i, j, k - halfWidth, 0);
			world.setBlockWithNotify(i, j + 1, k - halfWidth,0);
			///world.setBlockWithNotify(i, j + 2, k - halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i-1, j, k - halfWidth, 0);
			world.setBlockWithNotify(i-1, j + 1, k - halfWidth,0);
			//world.setBlockWithNotify(i-1, j + 2, k - halfWidth,Block.planks.blockID);
			
			// torches
//			world.setBlockWithNotify(i-2, j + 2, k - halfWidth-1,Block.torchWood.blockID);
//			world.setBlockWithNotify(i+2, j + 2, k - halfWidth-1,Block.torchWood.blockID);
//
//			world.setBlockWithNotify(i-2, j + 2, k + halfWidth+1,Block.torchWood.blockID);
//			world.setBlockWithNotify(i+2, j + 2, k + halfWidth+1,Block.torchWood.blockID);

			world.setBlockWithNotify(i+1, j, k + halfWidth, 0);
			world.setBlockWithNotify(i+1, j + 1, k + halfWidth,0);
			//world.setBlockWithNotify(i+1, j + 2, k + halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i, j, k +halfWidth, 0);
			world.setBlockWithNotify(i, j + 1, k + halfWidth,0);
			//world.setBlockWithNotify(i, j + 2, k + halfWidth,Block.planks.blockID);
			world.setBlockWithNotify(i-1, j, k + halfWidth, 0);
			world.setBlockWithNotify(i-1, j + 1, k + halfWidth,0);
			//world.setBlockWithNotify(i-1, j + 2, k + halfWidth,Block.planks.blockID);
			
			// Windows
			world.setBlockWithNotify(i - halfWidth, j + 1, k-1, Block.glass.blockID);
			//world.setBlockWithNotify(i - halfWidth, j + 2, k, Block.planks.blockID);
			world.setBlockWithNotify(i - halfWidth, j + 1, k, 0);
			world.setBlockWithNotify(i - halfWidth, j, k, 0);
			world.setBlockWithNotify(i - halfWidth, j + 1, k+1, Block.glass.blockID);
			
			world.setBlockWithNotify(i + halfWidth, j + 1, k-1, Block.glass.blockID);
			//world.setBlockWithNotify(i + halfWidth, j + 2, k, Block.planks.blockID);
			world.setBlockWithNotify(i + halfWidth, j + 1, k, 0);
			world.setBlockWithNotify(i + halfWidth, j, k, 0);
			world.setBlockWithNotify(i + halfWidth, j + 1, k+1, Block.glass.blockID);

			// Some piles of woods for decoration
			for (int z = k - halfWidth + 1; z <= k + halfWidth - 1; z++) {
				if(z!=k)
				{
					// left pile
					world.setBlockWithNotify(i - halfWidth + 1, j, z,
							Block.wood.blockID);
					// right pile
					world.setBlockWithNotify(i + halfWidth - 1, j, z,
							Block.wood.blockID);
				}
			}
			
			// sign
//			world.setBlockWithNotify(i, j + 2, k + halfWidth+1, Block.signWall.blockID);
//    		TileEntitySign sign = (TileEntitySign)world.getBlockTileEntity(i, j + 2, k + halfWidth+1);
//    		sign.signText = new String[]{"Lumberjack's", "hut"};
		}
		
		else if(is !=null && (is.getItem().shiftedIndex == mod_MineColony.scepterSteel.shiftedIndex))
		{
    		// fence
    		Vec3D chestPos = Vec3D.createVector(i, j, k);
    		for(int dx = i-workingRange; dx<=i+workingRange;dx++)
    			for(int dz=k-workingRange; dz<=k+workingRange;dz++)
    			{
    				int dy = findTopGround(world, dx, dz);
    				int groundBlockId = world.getBlockId(dx, dy, dz);
    				if(groundBlockId == Block.dirt.blockID || 
    						groundBlockId == Block.grass.blockID || 
    						groundBlockId == Block.gravel.blockID || 
    						groundBlockId == Block.sand.blockID || 
    						groundBlockId == Block.stone.blockID)
    				{
	    					if(dx == i-workingRange || dx == i+workingRange ||
    							dz == k-workingRange || dz == k+workingRange)
								world.setBlockWithNotify(dx,dy+1,dz, Block.fence.blockID);
    				}
    			}
		}
		else
			return super.blockActivated(world, i, j, k, entityplayer);
		
		return true;
		
	}
	
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);		
		//world.setWorldTime(0);

		// Chest for stuff with 5 stone axes
		world.setBlockWithNotify(i, j, k, mod_MineColony.hutLumberjack.blockID);
		TileEntityChest tileentitychest = (TileEntityChest) world
				.getBlockTileEntity(i, j, k);

		tileentitychest.setInventorySlotContents(0,  new ItemStack(Item.axeWood, 1));
		
//		tileentitychest.setInventorySlotContents(8,  new ItemStack(Item.axeStone, 1));
//		tileentitychest.setInventorySlotContents(9,  new ItemStack(Item.axeGold, 1));
//		tileentitychest.setInventorySlotContents(10,  new ItemStack(Item.axeSteel, 1));
//		tileentitychest.setInventorySlotContents(12,  new ItemStack(Item.axeDiamond, 1));
		// tileentitychest.setInventorySlotContents(1,  new ItemStack(Item.axeDiamond, 1));
		// tileentitychest.setInventorySlotContents(2,  new ItemStack(Item.ingotIron, 1));
		// tileentitychest.setInventorySlotContents(4,  new ItemStack(Item.ingotGold, 1));
//		
		//tileentitychest.setInventorySlotContents(0,  new ItemStack(Item.pickaxeSteel, 1));
//		tileentitychest.setInventorySlotContents(1,  new ItemStack(Item.shovelSteel, 1));
		// tileentitychest.setInventorySlotContents(8,  new ItemStack(mod_MineColony.scepterGold, 1));
		// tileentitychest.setInventorySlotContents(9,  new ItemStack(mod_MineColony.scepterSteel, 1));
//		tileentitychest.setInventorySlotContents(13,  new ItemStack(Item.shovelSteel, 1));
//		tileentitychest.setInventorySlotContents(14,  new ItemStack(Item.pickaxeSteel, 1));
//		tileentitychest.setInventorySlotContents(15,  new ItemStack(Item.shovelSteel, 1));
//		tileentitychest.setInventorySlotContents(16,  new ItemStack(Item.pickaxeSteel, 1));
//		tileentitychest.setInventorySlotContents(17,  new ItemStack(Item.shovelSteel, 1));
//		tileentitychest.setInventorySlotContents(18,  new ItemStack(Item.pickaxeSteel, 1));
//		tileentitychest.setInventorySlotContents(19,  new ItemStack(Item.shovelSteel, 1));


		// spawn lumberjack
		spawnWorker(world, i, j, k);
	}
	
	public void spawnWorker(World world, int i, int j, int k)
	{
		// spawn miner
		//el = new EntityLumberjack(world, workingRange);
		el =(EntityLumberjack) EntityList.createEntityInWorld("Lumberjack", world); 
		
		// scan for first free block near chest
		Vec3D spawnPoint = scanForBlockNearPoint(world, 0, i, j, k, 1, 0, 1);
		
		if(spawnPoint==null)
			spawnPoint = scanForBlockNearPoint(world, Block.snow.blockID, i, j, k, 1, 0, 1);
		
		if(spawnPoint!=null)
		{
			el.setPosition(spawnPoint.xCoord, spawnPoint.yCoord, spawnPoint.zCoord);
			el.setHomePosition(i, j, k);
			world.entityJoinedWorld(el);
		}

	}
	
	public void updateTick(World world, int i, int j, int k, Random random)
    {
		super.updateTick(world, i, j, k, random);
		
		if(getLumberJackAround(world, i,j,k)==null)
		{
			if(el!=null)
				el.isDead = true;
			spawnWorker(world, i, j, k);
		}
		
    }
	
	public void onBlockRemoval(World world, int i, int j, int k)
	{
		//EntityLumberjack lumber = getLumberJackAround(world, i,j,k);
		el = getLumberJackAround(world, i,j,k);
		if(el!=null)
		{
			el.isDead=true;
		}
	}
	
	public EntityLumberjack getLumberJackAround(World world, int i, int j, int k)
	{
		List list = world.getEntitiesWithinAABB(EntityLumberjack.class, this.getCollisionBoundingBoxFromPool(world, i, j, k).expand(workingRange, 20, workingRange));
		if (list != null) {
			for (int ii = 0; ii < list.size(); ii++) {
				if (list.get(ii) instanceof EntityLumberjack) {
					return (EntityLumberjack)list.get(ii);
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
