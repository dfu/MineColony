package net.minecraft.src;

import java.util.List;
import java.util.Random;

public class BlockHutFarmer extends BlockHut {
	
	private EntityFarmer em;
	
	public BlockHutFarmer(int blockID, int _textureID) {
		super(blockID);
		textureID = _textureID;
		hutWidth = 5;
		hutHeight = 3;
		clearingRange = 4;
		halfWidth = hutWidth/2;
		workingRange = 16;
		em=null;
		// Sets the recipe be two planks horizontal to each other
		// CraftingManager.getInstance().addRecipe(new ItemStack(blockID, 1,0),
				// new Object[] { "##", Character.valueOf('#'), Block.dirt,});
	}


	public boolean canPlaceBlockAt(World world, int i, int j, int k)
	{
		// check if there are other chests nearby
		Vec3D chestPos = scanForBlockNearPoint(world, mod_MineColony.hutFarmer.blockID, i,j,k, workingRange, 20, workingRange);
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
			// clean area around house
			for (int x = i - clearingRange; x <= i + clearingRange; x++)
				for (int z = k - clearingRange; z <= k + clearingRange; z++)
					for (int y = j; y < j + hutHeight; y++) {
						if(x!=i || y!=j || z!=k)
							world.setBlockWithNotify(x, y, z, 0);
					}

			// floor
			for (int x = i - halfWidth-1; x <= i + halfWidth+1; x++)
				for (int z = k - halfWidth-2; z <= k + halfWidth+2; z++) {
					world.setBlockWithNotify(x, j - 1, z, Block.cobblestone.blockID);
				}
			
			// roof
			for (int x = i - halfWidth-1; x <= i + halfWidth+1; x++)
				for (int z = k - halfWidth-1; z <= k + halfWidth+1; z++) {
					world.setBlockWithNotify(x, j + hutHeight-1, z, Block.planks.blockID);
				}
			
			for (int dy = 0 ; dy <= 5; dy++)
				for (int x = i - halfWidth -1 +dy; x <= i + halfWidth-dy+1; x++)
					for (int z = k - halfWidth-1; z <= k + halfWidth+1; z++) {
						if(dy>=0 && x!=i-halfWidth-1+dy && x!=i+halfWidth-dy+1)
							world.setBlockWithNotify(x, j +hutHeight + dy, z, Block.cloth.blockID);
						else
						{
							if(x==i)
								world.setBlockWithNotify(x, j +hutHeight + dy, z, Block.planks.blockID);
							else
							{
								world.setBlockWithNotify(x, j +hutHeight + dy, z, Block.stairCompactPlanks.blockID);
								if(x>i)
									world.setBlockMetadataWithNotify(x, j +hutHeight + dy, z, 1);
							}
						}
					}

			// wall
			for (int z = k - halfWidth-1; z <= k + halfWidth+1; z++)
			for (int y = j; y<= j+1; y++)
			{
				if(z!=k)
				{
					if(y==j)
						world.setBlockWithNotify(i - halfWidth-1, y, z,Block.wood.blockID);

					if(y==j+1)
						world.setBlockWithNotify(i - halfWidth-1, y, z,Block.planks.blockID);

					world.setBlockWithNotify(i + halfWidth+1, y, z,Block.fence.blockID);
				}
			}
			
			// windows
			// wooden
			world.setBlockWithNotify(i - halfWidth-1, j+1, k-2, Block.stairCompactPlanks.blockID);
			world.setBlockMetadataWithNotify(i - halfWidth-1, j+1, k-2, 3);
			world.setBlockWithNotify(i - halfWidth-1, j+1, k-1, Block.stairCompactPlanks.blockID);
			world.setBlockMetadataWithNotify(i - halfWidth-1, j+1, k-1, 2);
			
			world.setBlockWithNotify(i - halfWidth-1, j+1, k+1, Block.stairCompactPlanks.blockID);
			world.setBlockMetadataWithNotify(i - halfWidth-1, j+1, k+1, 3);
			world.setBlockWithNotify(i - halfWidth-1, j+1, k+2, Block.stairCompactPlanks.blockID);
			world.setBlockMetadataWithNotify(i - halfWidth-1, j+1, k+2, 2);
			
			// fences
			world.setBlockWithNotify(i + halfWidth+1, j+1, k-2, 0);
			world.setBlockWithNotify(i + halfWidth+1, j+1, k+2, 0);
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
		world.setBlockWithNotify(i, j, k, mod_MineColony.hutFarmer.blockID);
		TileEntityChest tileentitychest = (TileEntityChest) world
				.getBlockTileEntity(i, j, k);

		tileentitychest.setInventorySlotContents(0,  new ItemStack(Item.hoeWood, 1));
		tileentitychest.setInventorySlotContents(1,  new ItemStack(Item.shovelWood, 1));
		tileentitychest.setInventorySlotContents(2,  new ItemStack(Item.seeds, 10));


		//tileentitychest.setInventorySlotContents(8,  new ItemStack(mod_MineColony.scepterGold, 1));

		spawnWorker(world, i, j, k);
	}
	
	public void updateTick(World world, int i, int j, int k, Random random)
    {
		super.updateTick(world, i, j, k, random);
		
		if(getFarmerAround(world, i,j,k)==null)
		{
			if(em!=null)
				em.isDead = true;
			spawnWorker(world, i, j, k);
		}
    }
	
	public void spawnWorker(World world, int i, int j, int k)
	{
		em = (EntityFarmer)EntityList.createEntityInWorld("Farmer", world); 
		
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
		em = getFarmerAround(world, i,j,k);
		if(em!=null)
		{
			em.isDead=true;
		}
	}
	
	public EntityFarmer getFarmerAround(World world, int i, int j, int k)
	{
		List list = world.getEntitiesWithinAABB(EntityFarmer.class, this.getCollisionBoundingBoxFromPool(world, i, j, k).expand(workingRange, 20, workingRange));
		if (list != null) {
			for (int ii = 0; ii < list.size(); ii++) {
				if (list.get(ii) instanceof EntityFarmer) {
					return (EntityFarmer)list.get(ii);
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
