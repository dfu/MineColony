package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.minecraft.client.Minecraft;

public class EntityDeliveryMan extends EntityWorker {
	private static final int actionFindNextCheckpoint = 30;
	private static final int actionGoToNextCheckpoint = 31;
	private static final int actionDeliverResources = 32;
	
	private int currentCheckPoint;
	private boolean goBack;
	
	private List<ItemStack> inventory;

	public EntityDeliveryMan(World world) {
		super(world);
		texture = "/mob/deliveryman.png";
		//texture = "/mob/char.png";
		setSize(0.9F, 1.3F);
		defaultHoldItem = null;
		currentAction = actionFindNextCheckpoint;
		currentCheckPoint = 0;
		destPoint = null;
		inventory = new ArrayList<ItemStack>();
		workingRange = 40;
		roamingStuckLimit = 12;
		goBack = false;
	}
	
	public void onUpdate() {
		super.onUpdate();
		fallDistance = 0.0F;
	    if(motionY < -0.3999999999999999D)
	    {
	        motionY = -0.3999999999999999D;
	    }
	}

	protected void workerUpdate() {
		blockJumping = false;

		destroySign();
		if(signText1 != "" || signText2 != ""|| signText3 != "")
		{
			placeSign(mod_MineColony.hutWarehouse.blockID, signText1, signText2, signText3);
			moveForward = 0;
			moveStrafing = 0;
			isJumping = false;
		}
		else
			destroySignsInRange(3,3,3);
			
		signText1 = "";
		signText2 = "";
		signText3 = "";
		
		speed = (float)1.0;
		if(destPoint==null)
			currentAction = actionFindNextCheckpoint;
		switch (currentAction) {
			case actionFindNextCheckpoint:
				if(currentCheckPoint<0)
				{
					currentCheckPoint = 0;
					goBack = false;
				}
				
				destPoint = null;
				isSwinging = false;
				
				if(!worldObj.isDaytime() && currentCheckPoint==1 && goBack == false)
				{
					signText3 = "Sleeping";
					break;
				}
				
				// search for next checkpoint sign near
				Vec3D nextCP = scanForNextCheckpoint(workingRange, 30, workingRange, currentCheckPoint);
				if(nextCP!=null)
				{
					
					//pathToEntity = findPathToXYZ(nextCP.xCoord, nextCP.yCoord, nextCP.zCoord);
					//if(pathToEntity!=null && pathToEntity.pathLength>2)
					//{
					if(goBack)
						currentCheckPoint--;
					else
						currentCheckPoint++;
						destPoint = Vec3D.createVectorHelper(nextCP.xCoord, nextCP.yCoord, nextCP.zCoord);
						currentAction = actionGoToNextCheckpoint;
					//}
					//else
						//walkToTargetStraight(nextCP);
				}
				else
				{
					currentCheckPoint-=2;
					goBack = true;
//					nextCP = scanForNextCheckpoint(workingRange, 30, workingRange, 0);
//					if(nextCP!=null)
//					{
//						
//						//pathToEntity = findPathToXYZ(nextCP.xCoord, nextCP.yCoord, nextCP.zCoord);
//						//if(pathToEntity!=null && pathToEntity.pathLength>1)
//						//{
//							currentCheckPoint = 0;
//							destPoint = Vec3D.createVectorHelper(nextCP.xCoord, nextCP.yCoord, nextCP.zCoord);
//							if(destPoint!=null)
//								currentAction = actionGoToNextCheckpoint;
//						//}
//						//else
//							//walkToTargetStraight(nextCP);
//					}
				}

				break;
			case actionGoToNextCheckpoint:
				
				Vec3D entVec = Vec3D.createVector(posX, destPoint.yCoord, posZ);
				if(destPoint!=null && destPoint.distanceTo(entVec)<=2)
					speed = (float) 0.5;
				else
					speed = 1;
				
				if(destPoint!=null && destPoint.distanceTo(entVec)<=2)
				{
					currentAction = actionDeliverResources;
				}
				break;
			case actionDeliverResources:
				stuckCount = 0;
				moveForward = 0;
				moveStrafing = 0;
				freeRoamCount = 100;
				blockJumping = true;
				// seek for a different kind of chests

				Vec3D chestPos = null;
				
				chestPos = scanForBlockNearEntity(mod_MineColony.hutLumberjack.blockID, 4, 3, 4);
				if(chestPos==null)
					chestPos = scanForBlockNearEntity(mod_MineColony.hutMiner.blockID, 4, 3, 4);
				if(chestPos==null)
					chestPos = scanForBlockNearEntity(mod_MineColony.hutWarehouse.blockID, 4, 3, 4);
				if(chestPos==null)
					chestPos = scanForBlockNearEntity(mod_MineColony.hutFarmer.blockID, 4, 3, 4);

				if (chestPos != null) {
					TileEntityChest tileentitychest = (TileEntityChest) worldObj
							.getBlockTileEntity(
									MathHelper.floor_double(chestPos.xCoord),
									MathHelper.floor_double(chestPos.yCoord),
									MathHelper.floor_double(chestPos.zCoord));
					if (tileentitychest != null)
					{				
						
						int ret = MakeDeliveryActions(tileentitychest);
						
						if(defaultHoldItem == null || ret ==2 )
						{
							defaultHoldItem = null;
							currentAction = actionFindNextCheckpoint;
						}
						else
							isSwinging = true;
					}
				}
				else
				{
					
					currentAction = actionFindNextCheckpoint;
				}
				break;
				
		}		
	}

	// if return 1 then delivere more
	// if return 2 then stop delivery
	private int MakeDeliveryActions(TileEntityChest chest) {
		defaultHoldItem = null;
		int chestType = worldObj.getBlockId(chest.xCoord, chest.yCoord, chest.zCoord);

		if(chestType == mod_MineColony.hutLumberjack.blockID)
		{
			// get goods
			if(getGoods(chest, Block.wood.blockID)) return 1;
			
			// deliver tools
			for(int i=0;i<inventory.size();i++)
			{
				ItemStack slot = inventory.get(i);
				if(slot!=null && slot.getItem().getClass() == ItemAxe.class || slot.itemID == Block.sapling.blockID)
				{
					while(slot.stackSize>0)
					{
						int quantity = 1;
						if(slot.itemID == Block.sapling.blockID)
						{
							if(slot.stackSize>5)
								quantity = 5;
							else
								quantity = slot.stackSize;
						}
						if(putItemIntoChest(chest, slot.getItem().shiftedIndex, quantity))
						{
							slot.stackSize -=quantity;
							defaultHoldItem = new ItemStack(slot.getItem().shiftedIndex,quantity,0);
							if(slot.stackSize<=0)
							{
								inventory.remove(i);
								i--;
							}
							return 2;
						}
					}
				}
			}
		}
		else if(chestType == mod_MineColony.hutMiner.blockID)
		{
			// get goods
			if(getGoods(chest, Block.dirt.blockID)) return 1;
			if(getGoods(chest, Block.cobblestone.blockID)) return 1;
			if(getGoods(chest, Item.coal.shiftedIndex)) return 1;
			if(getGoods(chest, Block.oreIron.blockID)) return 1;
			if(getGoods(chest, Block.sand.blockID)) return 1;
			if(getGoods(chest, Block.gravel.blockID)) return 1;
			if(getGoods(chest, Item.flint.shiftedIndex)) return 1;
			if(getGoods(chest, Block.oreGold.blockID)) return 1;
			if(getGoods(chest, Item.diamond.shiftedIndex)) return 1;
			if(getGoods(chest, Item.redstone.shiftedIndex)) return 1;
			
			// deliver tools
			for(int i=0;i<inventory.size();i++)
			{
				ItemStack slot = inventory.get(i);
				if(slot!=null && (slot.getItem().getClass() == ItemPickaxe.class ||
				   slot.getItem().getClass() == ItemSpade.class || slot.itemID == Block.torchWood.blockID))
				{
					while(slot.stackSize>0)
					{
						int quantity = 1;
						if(slot.itemID == Block.torchWood.blockID)
						{
							if(slot.stackSize>5)
								quantity = 5;
							else
								quantity = slot.stackSize;
						}
						if(putItemIntoChest(chest, slot.getItem().shiftedIndex, quantity))
						{
							slot.stackSize -=quantity;
							defaultHoldItem = new ItemStack(slot.getItem().shiftedIndex,quantity,0);
							if(slot.stackSize<=0)
							{
								inventory.remove(i);
								i--;
							}
							return 2;
						}
					}
					
				}
			}
		}
		else if(chestType == mod_MineColony.hutFarmer.blockID)
		{
			// get goods
			if(getGoods(chest, Item.wheat.shiftedIndex)) return 1;
			
			// deliver tools
			for(int i=0;i<inventory.size();i++)
			{
				ItemStack slot = inventory.get(i);
				if(slot!=null && (slot.getItem().getClass() == ItemHoe.class ||
				   slot.getItem().getClass() == ItemSpade.class || slot.getItem().shiftedIndex == Item.seeds.shiftedIndex))
				{
					while(slot.stackSize>0)
					{
						int quantity = 1;
						if(slot.getItem().shiftedIndex == Item.seeds.shiftedIndex)
						{
							if(slot.stackSize>5)
								quantity = 5;
							else
								quantity = slot.stackSize;
						}
						if(putItemIntoChest(chest, slot.getItem().shiftedIndex, quantity))
						{
							slot.stackSize -=quantity;
							defaultHoldItem = new ItemStack(slot.getItem().shiftedIndex,quantity,0);
							if(slot.stackSize<=0)
							{
								inventory.remove(i);
								i--;
							}
							return 2;
						}
					}
				}
			}
		}
		else if(chestType == mod_MineColony.hutWarehouse.blockID)
		{
			// get other goods
			if(getGoods(chest, Block.sapling.blockID)) return 1;
			if(getGoods(chest, Block.torchWood.blockID)) return 1;
			if(getGoods(chest, Item.seeds.shiftedIndex)) return 1;
			
			// deliver goods
			for(int i=0;i<inventory.size();i++)
			{
				ItemStack slot = inventory.get(i);
				if(slot!=null && (slot.getItem().shiftedIndex == Block.wood.blockID ||
						slot.getItem().shiftedIndex == Block.dirt.blockID ||
						slot.getItem().shiftedIndex == Block.cobblestone.blockID ||
						slot.getItem().shiftedIndex == Item.coal.shiftedIndex ||
						slot.getItem().shiftedIndex == Block.oreIron.blockID ||
						slot.getItem().shiftedIndex == Block.sand.blockID ||
						slot.getItem().shiftedIndex == Block.gravel.blockID ||
						slot.getItem().shiftedIndex == Block.oreGold.blockID ||
						slot.getItem().shiftedIndex == Item.diamond.shiftedIndex ||
						slot.getItem().shiftedIndex == Item.redstone.shiftedIndex ||
						slot.getItem().shiftedIndex == Item.wheat.shiftedIndex))
				{
					while(slot.stackSize>0)
					{
						if(putItemIntoChest(chest, slot.getItem().shiftedIndex, 1))
						{
							slot.stackSize -=1;
						}
					}
					inventory.remove(i);
					i--;
					defaultHoldItem = new ItemStack(slot.getItem().shiftedIndex,1,0);
					return 1;
				}
			}
			
			// get tools
			ItemStack items = null;
			
			if((items = getItemFromChest(chest, ItemAxe.class, 99999))!=null)
			{
				if(items!=null)
				{
					inventory.add(items);
					defaultHoldItem = new ItemStack(items.getItem().shiftedIndex, 1,0);
					return 1;
				}
			}
			items = null;


			if((items = getItemFromChest(chest, ItemPickaxe.class, 99999))!=null)
			{
				if(items!=null)
				{
					inventory.add(items);
					defaultHoldItem = new ItemStack(items.getItem().shiftedIndex, 1,0);
					return 1;
				}
			}
			items = null;

			if((items = getItemFromChest(chest, ItemSpade.class, 99999))!=null)
			{
				if(items!=null)
				{
					inventory.add(items);
					defaultHoldItem = new ItemStack(items.getItem().shiftedIndex, 1,0);
					return 1;
				}
			}
			items = null;
			
			if((items = getItemFromChest(chest, ItemHoe.class, 99999))!=null)
			{
				if(items!=null)
				{
					inventory.add(items);
					defaultHoldItem = new ItemStack(items.getItem().shiftedIndex, 1,0);
					return 1;
				}
			}
			items = null;

		}
		return 2;
			
	}

	

	private boolean getGoods(TileEntityChest chest, int blockID) {
		ItemStack items = null;
		while((items = getItemFromChest(chest, blockID, 99999))!=null)
		{
			if(items!=null)
			{
				inventory.add(items);
				defaultHoldItem = new ItemStack(blockID, 1,0);
				return true;
			}
		}
		return false;
		//items = null;
	}

	protected int getDropItemId() {
		return Block.crate.blockID;
	}
	
	public void onDeath(Entity entity) {
	   if(scoreValue > 0 && entity != null)
        {
            entity.addToPlayerScore(this, scoreValue);
        }
        field_9327_S = true;
        if(!worldObj.multiplayerWorld)
        {
        }
        worldObj.func_9425_a(this, (byte)3);
	}
	
	
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		nbttagcompound.setInteger("currentCheckPoint", currentCheckPoint);
		nbttagcompound.setBoolean("goBack", goBack);
	
		NBTTagList inventoryList = new NBTTagList();
		
		for(int i=0;i<inventory.size();i++)
		{
			NBTTagCompound tagCompound = new NBTTagCompound();
			inventory.get(i).writeToNBT(tagCompound);
	        inventoryList.setTag(tagCompound);
		}
		
		nbttagcompound.setTag("InventoryDelivery", inventoryList);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		
		currentCheckPoint = nbttagcompound.getInteger("currentCheckPoint");
		goBack = nbttagcompound.getBoolean("goBack");
		
		NBTTagList nbttaglist = nbttagcompound.getTagList("InventoryDelivery");
		for(int i = 0; i < nbttaglist.tagCount(); i++)
        {
            NBTTagCompound tagCompound = (NBTTagCompound)nbttaglist.tagAt(i);
           
            ItemStack itemstack = new ItemStack(tagCompound);
            
            if(itemstack.getItem() == null)
            {
                continue;
            }
            
            inventory.add(itemstack);
        }
	}
		
}
