package net.minecraft.src;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.minecraft.client.Minecraft;


public class EntityLumberjack extends EntityWorker {

	private static final int actionSeekForTree = 10;
	private static final int actionChop = 11;
	private static final int actionPlantTree = 12;

	private int woodLogs = 0;
	private int saplings = 2;
	private int idleTime = 0;
	
	private Vec3D currentTrunkPos; 
	
	public EntityLumberjack(World world) {
		super(world);
		texture = "/mob/lumberjack.png";
		setSize(0.9F, 1.3F);
		currentAction = actionGetEquipment;
		defaultHoldItem = null;
		pathToEntity = null;
		destPoint = null;
		currentTrunkPos = null;
	}
	
	protected void onSwing()
	{
		if(currentAction==actionChop)
		{
			worldObj.playSoundAtEntity(this, "random.wood click", 1.0F,
					1.0F / (rand.nextFloat() * 0.4F + 0.8F));
			ModLoader.getMinecraftInstance().effectRenderer.func_1186_a((int)starringPointX,(int)starringPointY,(int)starringPointZ);
		}
	}

	protected void workerUpdate() {
		speed = (float) 1.0;
		blockJumping = false;
		// gather nearby saplings
		EntityItem nearbyItem = gatherItemNearby(Block.sapling.blockID);
		if(nearbyItem!=null)
		{
			onGetItem();
			nearbyItem.setEntityDead();
			saplings++;
		}
		
		// gather nearby wood logs
		nearbyItem = gatherItemNearby(Block.wood.blockID);
		if(nearbyItem!=null)
		{
			onGetItem();
			nearbyItem.setEntityDead();
			woodLogs++;
		}
		
		nearbyItem  = gatherItemNearby(ItemAxe.class);
		if(nearbyItem != null)
		{
			onGetItem();
			equipItem(nearbyItem.item, 0);
			nearbyItem.setEntityDead();
		}
		
		destroySign();
		if(signText1 != "" || signText2 != ""|| signText3 != "")
		{
			placeSign(mod_MineColony.hutLumberjack.blockID, signText1, signText2, signText3);
			moveForward = 0;
			moveStrafing = 0;
			isJumping = false;
		}
		else
			destroySignsInRange(3,3,3);

		
		signText1 = "";
		signText2 = "";
		//signText3 = "";
		
		switch (currentAction) {
		case actionGetOutOfBuilding:
			starringPointSet = false;
				
			// get out through front or through back of the house, depending on location
			int  offsetZ = Math.abs(initialZOffset);
			int destZ = 0;
			if(iPosZ>=homePosZ)
				destZ = homePosZ + offsetZ;
			else
				destZ = homePosZ - offsetZ;

			int ySolid = findTopGround(homePosX+initialXOffset, destZ);
			destPoint = Vec3D.createVectorHelper(homePosX+initialXOffset, ySolid, destZ);
			Vec3D entVec = Vec3D.createVector(iPosX, iPosY, iPosZ);
			// Vec3D entVec;
			// if(iPosY<homePosY)
				// entVec = Vec3D.createVector(iPosX, iPosY, iPosZ);
			// else
				// entVec = Vec3D.createVector(iPosX, homePosY, iPosZ);
			
			if(entVec.distanceTo(destPoint)<2)
			{
				currentAction = actionSeekForTree;
			}
			break;
		case actionGoBack:
			starringPointSet = false;
			
			// go back through front or through back of the house, depending on location
			offsetZ = Math.abs(initialZOffset);
			destZ = 0;
			if(iPosZ>=homePosZ)
				destZ = homePosZ + offsetZ;
			else
				destZ = homePosZ - offsetZ;
				
			ySolid = findTopGround(homePosX+initialXOffset, destZ);
			entVec = Vec3D.createVector(iPosX, iPosY, iPosZ);
			destPoint = Vec3D.createVectorHelper(homePosX+initialXOffset, ySolid, destZ);
			
			
			// if(iPosY<homePosY)
				// entVec = Vec3D.createVector(iPosX, iPosY, iPosZ);
			// else
				// entVec = Vec3D.createVector(iPosX, homePosY, iPosZ);
			
			if(entVec.distanceTo(destPoint)<3)
			{
				actualTicksToDelivery = 0;
				currentAction = actionDeliverGoods;
			}
			break;
		case actionGetEquipment:
			if(!worldObj.isDaytime())
			{
				signText3 = "Sleeping";
				destPoint = null;
				break;
			}
			
			// get the item in the pocket
			if(toolsList[0]!=null)
			{
				// get out through front or through back of the house, depending on location
//				offsetZ = Math.abs(initialZOffset);
//				destZ = 0;
//				if(iPosZ>=homePosZ)
//					destZ = homePosZ + offsetZ;
//				else
//					destZ = homePosZ - offsetZ;
//				
//				destPoint = Vec3D.createVectorHelper(homePosX+initialXOffset, homePosY+initialYOffset, destZ);
				defaultHoldItem = toolsList[0].copy();
				currentAction = actionGetOutOfBuilding;
				break;
			}		
			else
				signText1 = "Out of axes";
			
			// check if player is near chest with tools
			// if yes then check if there are some tools available
			// if yes then take item and change action to seek for tree
			// if no then
			Vec3D chestPos = scanForBlockNearEntity(
					mod_MineColony.hutLumberjack.blockID, 1, 3, 1);

			if (chestPos != null) {
				speed = 0;
				TileEntityChest tileentitychest = (TileEntityChest) worldObj
						.getBlockTileEntity(
								MathHelper.floor_double(chestPos.xCoord),
								MathHelper.floor_double(chestPos.yCoord),
								MathHelper.floor_double(chestPos.zCoord));
				if (tileentitychest != null)
				{				
					equipItemFromChest(tileentitychest, Item.axeDiamond.shiftedIndex, 1, 0);
					if(toolsList[0]!=null) break;
					equipItemFromChest(tileentitychest, Item.axeSteel.shiftedIndex, 1, 0);
					if(toolsList[0]!=null) break;
					equipItemFromChest(tileentitychest, Item.axeGold.shiftedIndex, 1, 0);
					if(toolsList[0]!=null) break;
					equipItemFromChest(tileentitychest, Item.axeStone.shiftedIndex, 1, 0);
					if(toolsList[0]!=null) break;
					equipItemFromChest(tileentitychest, Item.axeWood.shiftedIndex, 1, 0);
					if(toolsList[0]!=null) break;
					
					
				}
			} 
			else 
			{
				destPoint = Vec3D.createVectorHelper(homePosX, homePosY, homePosZ);
				if(Vec3D.createVector(iPosX, homePosY, iPosZ).distanceTo(destPoint)<=3)
					speed = (float) 0.5;
			}
			break;
		case actionSeekForTree:
			// check if he has something to cut
			if(defaultHoldItem == null)
			{
				rotationPitch = defaultPitch;
				currentAction = actionGetEquipment;
				starringPointSet = false;
				break;
			}
			else
				destPoint = null;
			
			// check if is close to tree
			Vec3D closestTrunk = scanForWoodNearPoint(
					MathHelper.floor_double(posX),
					MathHelper.floor_double(posY),
					MathHelper.floor_double(posZ), 1, 30, 1);
			if (closestTrunk != null) {
				currentTrunkPos = Vec3D.createVectorHelper(closestTrunk.xCoord, closestTrunk.yCoord, closestTrunk.zCoord);
				currentAction = actionChop;
				moveForward =0;
				moveStrafing = 0;
				checkFreq = 9;
				break;
			}

			if(destPoint==null) {
				// check if there are some trees nearby
				Vec3D woodPos = scanForWoodNearPoint(homePosX, homePosY,
						homePosZ, workingRange, 30, workingRange);

				if (woodPos != null) {
					checkFreq = 0;
					destPoint = Vec3D.createVectorHelper(woodPos.xCoord,woodPos.yCoord, woodPos.zCoord);
					signText3 = "";
				} else {
					// if no trees in range then go to home
					currentAction = actionIdle;
					signText3 = "No trees in range";
				}

			}
			break;
		case actionIdle:
			if (idleTime > 100 && worldObj.isDaytime()) {
				idleTime = 0;
				currentAction = actionGetEquipment;
				break;
			}
			else
			{
				if(!worldObj.isDaytime())
					signText3 = "Sleeping";
				
				destPoint = null;
			}
			moveForward = 0;
			Vec3D chestPos2 = scanForBlockNearEntity(
					mod_MineColony.hutLumberjack.blockID, 1, 1, 1);

			if (chestPos2 != null) {
				speed = 0;
				TileEntityChest tileentitychest = (TileEntityChest) worldObj
				.getBlockTileEntity(
						MathHelper.floor_double(chestPos2.xCoord),
						MathHelper.floor_double(chestPos2.yCoord),
						MathHelper.floor_double(chestPos2.zCoord));
				
				if(defaultHoldItem!=null)
				{
					putItemIntoChest(tileentitychest, defaultHoldItem.itemID, 1);
					defaultHoldItem = null;
					toolsList[0] = null;
				}



			} else {
				if (destPoint == null) {
					destPoint = Vec3D.createVectorHelper(homePosX, homePosY,homePosZ);
				}
			}
			idleTime++;
			break;
		case actionChop:
			moveStrafing = 0;
			moveForward = 0;
			blockJumping = true;
			// choose block to stare at
			Vec3D closestWood = scanForWoodNearPoint(
					MathHelper.floor_double(posX),
					MathHelper.floor_double(posY),
					MathHelper.floor_double(posZ), 1, 30, 1);
			
			if (!starringPointSet) {
				if (closestWood != null) {
					starringPointX = closestWood.xCoord;
					starringPointY = MathHelper.floor_double(closestWood.yCoord);
					starringPointZ = closestWood.zCoord;
					starringPointSet = true;
				}
			}

			
			isSwinging = true;
			isJumping = false;
			actionFreq++;

			if (actionFreq >= currentToolEfficiency) {
				
				// tool weariness
				if(toolWeariness(toolsList[0]))
				{
					currentAction = actionGetEquipment;
					break;
				}

				actionFreq = 0;

				if (closestWood != null) {
					starringPointSet = false;
					placeBlockAt(
							MathHelper.floor_double(closestWood.xCoord),
							MathHelper.floor_double(closestWood.yCoord),
							MathHelper.floor_double(closestWood.zCoord), 0);
					Block.wood.harvestBlock(worldObj, MathHelper.floor_double(closestWood.xCoord),
							MathHelper.floor_double(closestWood.yCoord),
							MathHelper.floor_double(closestWood.zCoord), 0);

					// update inventory
					//woodLogs++;
				} else if (currentTrunkPos!=null && saplings > 0 && scanForBlockNearEntity(Block.sapling.blockID,3, 3, 3)==null) {
					defaultHoldItem = new ItemStack(Block.sapling, 1);
					currentAction = actionPlantTree;
					isSwinging = false;
					actionFreq = 0;
					break;
				} else if (woodLogs > 0) {
					defaultHoldItem = new ItemStack(Block.wood, 1);
					currentAction = actionGoBack;
					
					// go back through front or through back of the house, depending on location
//					offsetZ = Math.abs(initialZOffset);
//					destZ = 0;
//					if(iPosZ>=homePosZ)
//						destZ = homePosZ + offsetZ;
//					else
//						destZ = homePosZ - offsetZ;
//					
//					destPoint = Vec3D.createVectorHelper(homePosX+initialXOffset, homePosY+initialYOffset, destZ);
//					actionFreq=0;
					isSwinging = false;
					starringPointSet = false;
					rotationPitch = defaultPitch;
					break;
				} else {
					currentAction = actionSeekForTree;
					isSwinging = false;
					starringPointSet = false;
					rotationPitch = defaultPitch;
				}
			}
			break;
		case actionPlantTree:
			isJumping = false;
			moveForward = 0;
			if (actionFreq == 1)
				isSwinging = true;

			if (actionFreq == 2) {
				actionFreq = 0;

				// plant sapling here
				// will rather plant new seed where the tree was cut
				if (currentTrunkPos != null) {
					int x = MathHelper.floor_double(currentTrunkPos.xCoord);
					int z = MathHelper.floor_double(currentTrunkPos.zCoord);
					int y = MathHelper.floor_double(currentTrunkPos.yCoord);
					
					if(worldObj.isBlockOpaqueCube(x, y-1, z) && !worldObj.isBlockOpaqueCube(x, y, z))
					{
						placeBlockAt(x, y, z, Block.sapling.blockID);
	
						// update inventory
						saplings--;
					}
					currentTrunkPos = null;
				}
				if (woodLogs > 0) {
					defaultHoldItem = new ItemStack(Block.wood, 1);
					actualTicksToDelivery = 0;
					currentAction = actionDeliverGoods;
					actionFreq=0;
					isSwinging = false;
					starringPointSet = false;
					rotationPitch = defaultPitch;
					break;
				} else {
					defaultHoldItem = toolsList[0].copy();
					currentAction = actionSeekForTree;
					isSwinging = false;
					starringPointSet = false;
					rotationPitch = defaultPitch;
				}		
			}
			actionFreq++;
			break;
		case actionDeliverGoods:
			// respawn worker near chest if too long delivery
			if(actualTicksToDelivery > ticksToDelivery)
			{
				setPosition(homePosX, homePosY+1, homePosZ);
				actualTicksToDelivery = 0;
			}
			actualTicksToDelivery++;
			
			isJumping = false;
			moveForward = 0;
			isSwinging = false;
			Vec3D chest = scanForBlockNearEntity(mod_MineColony.hutLumberjack.blockID,
					1, 1, 1);
			if (chest != null) {

				if (actionFreq == 0) {
					{
					isSwinging = true;
					actionFreq++;
					}
				} else {
					TileEntityChest tileentitychest = (TileEntityChest) worldObj
							.getBlockTileEntity(
									MathHelper.floor_double(chest.xCoord),
									MathHelper.floor_double(chest.yCoord),
									MathHelper.floor_double(chest.zCoord));
					if (tileentitychest != null) {
						
						if(putItemIntoChest(tileentitychest, Block.wood.blockID, woodLogs))
						{
							woodLogs = 0;
							defaultHoldItem = toolsList[0].copy();
							currentAction = actionGetEquipment;
						}
						
						ItemStack saplingsStack = getItemFromChest(tileentitychest, Block.sapling.blockID, 64);
						if(saplingsStack!=null)
							saplings += saplingsStack.stackSize;
						
						// go to sleep
						if(!worldObj.isDaytime())
						{
							currentAction = actionIdle;
						}
					}
				}
			} else {
					// go back and depending on current location go to different side of the chest
					//currentAction = actionGoBack;
					
//					destZ = 0;
//					if(iPosZ>=homePosZ)
//						destZ = homePosZ + 1;
//					else
//						destZ = homePosZ - 1;
					destPoint = Vec3D.createVectorHelper(homePosX, homePosY, homePosZ);				
			}
			break;
		}

	}

	protected void equipItemSpecific(ItemStack item) {
		if(item.itemID == Item.axeDiamond.shiftedIndex)
			currentToolEfficiency = 5;
		else if(item.itemID == Item.axeSteel.shiftedIndex)
			currentToolEfficiency = 10;
		else if(item.itemID == Item.axeGold.shiftedIndex)
			currentToolEfficiency = 15;
		else if(item.itemID == Item.axeStone.shiftedIndex)
			currentToolEfficiency = 15;
		else if(item.itemID == Item.axeWood.shiftedIndex)
			currentToolEfficiency = 20;
	}
	


	private Vec3D scanForWoodNearPoint(int x, int y, int z, int rx, int ry,
			int rz) {

		Vec3D entityVec = Vec3D.createVector(x, y, z);

		Vec3D closestVec = null;
		double minDistance = 999999999;

		for (int i = x - rx; i <= x + rx; i++)
			for (int j = y - ry; j <= y + ry * 2; j++)
				for (int k = z - rz; k <= z + rz; k++) {
					if (worldObj.getBlockId(i, j, k) == Block.wood.blockID) {
						Vec3D tempVec = Vec3D.createVector(i, j, k);

						// check if the wood is a part of tree, so check the
						// block or blocks nearby
						// have some leaves around it

						int dy = 0;
						Vec3D areLeavesAround = null;

						while (dy < 10
								&& areLeavesAround == null
								&& worldObj
										.getBlockId(
												MathHelper
														.floor_double(tempVec.xCoord),
												MathHelper
														.floor_double(tempVec.yCoord)
														+ dy,
												MathHelper
														.floor_double(tempVec.zCoord)) == Block.wood.blockID) {
							areLeavesAround = scanForBlockNearPoint(
									Block.leaves.blockID,
									MathHelper.floor_double(tempVec.xCoord),
									MathHelper.floor_double(tempVec.yCoord)
											+ dy,
									MathHelper.floor_double(tempVec.zCoord), 1,
									1, 1);

							dy++;
						}

						if (areLeavesAround == null)
							continue;

						Vec3D tempVecDist = Vec3D.createVector(tempVec.xCoord, posY, tempVec.zCoord);
						double distance = tempVecDist.distanceTo(entityVec);

						if (closestVec == null
								||  distance< minDistance) {
							closestVec = tempVec;
							minDistance = distance;
							
						}
					}
				}

		// if wood found then seek for the root
		if (closestVec != null) {
			int fy = 0;
			int dy = -1;
			while (dy > -10) {
				
				if(worldObj.getBlockId(
							MathHelper.floor_double(closestVec.xCoord),
							MathHelper.floor_double(closestVec.yCoord) + dy,
							MathHelper.floor_double(closestVec.zCoord)) == Block.wood.blockID)
				{
					fy = dy;
				}
				
				dy--;
			}

			closestVec = Vec3D.createVector(
					MathHelper.floor_double(closestVec.xCoord),
					MathHelper.floor_double(closestVec.yCoord) + fy,
					MathHelper.floor_double(closestVec.zCoord));
		}

		return closestVec;

	}

	protected int getDropItemId() {
		return Item.axeWood.shiftedIndex;
	}
	
	public void onDeath(Entity entity) {
		destroySign();
	   if(scoreValue > 0 && entity != null)
        {
            entity.addToPlayerScore(this, scoreValue);
        }
        field_9327_S = true;
        if(!worldObj.multiplayerWorld)
        {
            if(toolsList[0]!=null)
            	dropItem(toolsList[0].itemID, 1);
            
            if(saplings>0)
            	dropItem(Block.sapling.blockID, saplings);
            	
            if(woodLogs>0)
            	dropItem(Block.wood.blockID, woodLogs);
            
        }
        worldObj.func_9425_a(this, (byte)3);
	}
	
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);

		nbttagcompound.setInteger("woodLogs", woodLogs);
		nbttagcompound.setInteger("saplings", saplings);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		
		woodLogs = nbttagcompound.getInteger("woodLogs");
		saplings = nbttagcompound.getInteger("saplings");
	}
	
	
}
