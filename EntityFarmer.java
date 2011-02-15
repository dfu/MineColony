package net.minecraft.src;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import net.minecraft.client.Minecraft;


public class EntityFarmer extends EntityWorker {
	private static final int actionFindPlaceToPlant = 40;
	private static final int actionPlantSeeds = 41;
	private static final int actionPlaceSeed = 42;
	private static final int actionIrrigate = 43;
	
	private int seeds = 0;
	private int wheats = 0;
	private int idleTime = 0;
	
	// block on which the farmer were standing before
	private int previousBlockStanding = -1;
	private int previousBlockStandingMetadata = 0;
	private int previousBlockX = 0;
	private int previousBlockY = 0;
	private int previousBlockZ = 0;
	
	Vec3D workingTile;
	private Vec3D irrTile;

	public EntityFarmer(World world) {
		super(world);
		texture = "/mob/farmer.png";
		setSize(0.9F, 1.3F);
		defaultHoldItem = null;
		currentAction = actionGetEquipment;
		destPoint = null;
		workingRange = 14;
		roamingStuckLimit = 12;
		workingTile = null;
	}
	
	protected void onSwing()
	{
		if(currentAction==actionPlantSeeds && isSwinging)
		{
			StepSound stepsound = Block.tilledField.stepSound;
        	worldObj.playSoundAtEntity(this, stepsound.func_1145_d(), stepsound.func_1147_b() * 0.15F, stepsound.func_1144_c());
		}
		// digging animation
		if(isSwinging)
			ModLoader.getMinecraftInstance().effectRenderer.func_1186_a((int)starringPointX,(int)starringPointY,(int)starringPointZ);
	
	}
	
	public void onUpdate() {
		super.onUpdate();
		
		// check if the farmer is walking on tilled block, and if it gets destroyed, revert it back
		if(worldObj.getBlockId(previousBlockX, previousBlockY, previousBlockZ) == Block.dirt.blockID &&
				previousBlockStanding == Block.tilledField.blockID)
		{
			worldObj.setBlockAndMetadataWithNotify(previousBlockX, previousBlockY, previousBlockZ, previousBlockStanding, previousBlockStandingMetadata);
		}
		
		
		if(worldObj.getBlockId(iPosX, iPosY-1, iPosZ) == Block.tilledField.blockID)
		{
			previousBlockStanding = worldObj.getBlockId(iPosX, iPosY-1, iPosZ);
			previousBlockStandingMetadata = worldObj.getBlockMetadata(iPosX, iPosY-1, iPosZ);
			previousBlockX = iPosX;
			previousBlockY = iPosY-1;
			previousBlockZ = iPosZ;
		}
	}
	
	protected void workerUpdate() {
		blockJumping = false;
		
		// gather the nearby item
		EntityItem nearbyItem  = gatherItemNearby(ItemHoe.class);
		if(nearbyItem != null)
		{
			onGetItem();
			equipItem(nearbyItem.item, 0);
			nearbyItem.setEntityDead();
		}
		
		nearbyItem  = gatherItemNearby(ItemSpade.class);
		if(nearbyItem != null)
		{
			onGetItem();
			equipItem(nearbyItem.item, 1);
			nearbyItem.setEntityDead();
		}
		
		nearbyItem = gatherItemNearby(Item.seeds.shiftedIndex);
		if(nearbyItem!=null)
		{
			onGetItem();
			nearbyItem.setEntityDead();
			seeds++;
		}
		nearbyItem = gatherItemNearby(Item.wheat.shiftedIndex);
		if(nearbyItem!=null)
		{
			onGetItem();
			nearbyItem.setEntityDead();
			wheats++;
			
			if (wheats >= 8) {
				defaultHoldItem = new ItemStack(Item.wheat, 1);
				actualTicksToDelivery = 0;
				currentAction = actionGoBack;
				actionFreq=0;
				isSwinging = false;
				starringPointSet = false;
				rotationPitch = defaultPitch;
			}
		}
		
		destroySign();
		if(signText1 != "" || signText2 != ""|| signText3 != "")
		{
			placeSign(mod_MineColony.hutFarmer.blockID, signText1, signText2, signText3);
			moveForward = 0;
			moveStrafing = 0;
			isJumping = false;
		}
		else
			destroySignsInRange(3,3,3);
		
		signText1 = "";
		signText2 = "";
		signText3 = "";
		
		speed = (float)0.4;
		switch (currentAction) {
		case actionIrrigate:
			
			speed = (float)0.4;
			moveStrafing = 0;
			moveForward = 0;
			blockJumping = true;
			isJumping = false;
			freeRoamCount = 100;
			stuckCount = 0;
			moveForward = (float) 0;
			
			if(actionFreq==0)
			{
				// check which blocks around can be used as irrigation
				int bx = MathHelper.floor_double(workingTile.xCoord);
				int by = MathHelper.floor_double(workingTile.yCoord);
				int bz = MathHelper.floor_double(workingTile.zCoord);
				irrTile = scanForIrrigatingBlockNearPoint(bx, by, bz);
				if(irrTile!=null)
				{
					starringPointSet = true;
	        		starringPointX = irrTile.xCoord;
	        		starringPointY = irrTile.yCoord;
	        		starringPointZ = irrTile.zCoord;
	        		
	        		if(!equipItem(1))
					{
						currentAction = actionGoBack;
						starringPointSet = false;
						currentToolEfficiency = 12;
					}
				}
				else
				{
					starringPointSet = false;
					currentAction = actionFindPlaceToPlant;
					isSwinging = false;
		    		defaultHoldItem = toolsList[0].copy();
		    		actionFreq = 0;
				}
			}
		
			if (actionFreq == 1)
			{
				isSwinging = true;
			}

			if(actionFreq==3)
			{
				worldObj.playSoundEffect((float)posX + 0.5F, (float)posY + 0.5F, (float)posZ + 0.5F, Block.dirt.stepSound.func_1145_d(), (Block.dirt.stepSound.func_1147_b() + 1.0F) / 2.0F, Block.dirt.stepSound.func_1144_c() * 0.8F);
				placeBlockAt(MathHelper.floor_double(irrTile.xCoord),
											MathHelper.floor_double(irrTile.yCoord),
											MathHelper.floor_double(irrTile.zCoord), Block.waterStill.blockID);
				
				actionFreq = 0;
				break;
			}
    		
			actionFreq++;
			
			break;
		case actionFindPlaceToPlant:
			if(!worldObj.isDaytime())
			{
				signText3 = "Sleeping";
				currentAction = actionGoBack;
				starringPointSet = false;
				break;
			}
			
			rotationPitch = defaultPitch;
			speed = (float)0.4;
			if(!equipItem(0))
			{
				currentAction = actionGoBack;
				starringPointSet = false;
				currentToolEfficiency = 12;
				break;
			}
			
			if(destPoint==null)
			{
			
			
			// seek for grass or dirt
			// first check if there is soil alreade tilled and scan near it
			int radius = 1;
			Vec3D plantPos;
			while((plantPos=scanForPlantPlaceNearPoint(iPosX, iPosY, iPosZ, radius, 3, radius))==null && radius<=workingRange*2)
			{
				radius++;
			}
			
			if(plantPos!=null)
			{
				workingTile = Vec3D.createVectorHelper(plantPos.xCoord-1,plantPos.yCoord, plantPos.zCoord);
				destPoint = Vec3D.createVectorHelper(plantPos.xCoord-1,plantPos.yCoord, plantPos.zCoord);
			}
			else
			{
				currentAction = actionGoBack;
				starringPointSet = false;
				break;
			}
		}
			else
			{
				// check if there is grass/dirt or crop nearby
				Vec3D plantPos = scanForPlantPlaceNearPoint(iPosX, iPosY, iPosZ, 3, 3, 3);
				
				if (plantPos != null) {
					workingTile = Vec3D.createVectorHelper(plantPos.xCoord, plantPos.yCoord, plantPos.zCoord);
					currentAction = actionPlantSeeds;
					moveForward =0;
					moveStrafing = 0;
					checkFreq = 9;
					break;
				}
				else
				{
					destPoint = null;
				}
				
			}

			break;
		case actionPlantSeeds:
			moveStrafing = 0;
			moveForward = 0;
			blockJumping = true;
			stuckCount = 0;
			freeRoamCount = 100;
			// choose block to stare at
			
			if (!starringPointSet) {
				if (workingTile != null) {
					starringPointX = workingTile.xCoord;
					starringPointY = MathHelper.floor_double(workingTile.yCoord);
					starringPointZ = workingTile.zCoord;
					starringPointSet = true;
				}
			}

			isSwinging = true;
			isJumping = false;
			actionFreq++;
			int bx = MathHelper.floor_double(workingTile.xCoord);
			int by = MathHelper.floor_double(workingTile.yCoord);
			int bz = MathHelper.floor_double(workingTile.zCoord);
			int blockId = worldObj.getBlockId(bx,by,bz);
			
			if(blockId == Block.dirt.blockID || blockId == Block.grass.blockID)
			{
				if (actionFreq >= currentToolEfficiency) {
					// tool weariness
					if(toolWeariness(toolsList[0]))
					{
						currentAction = actionGetEquipment;
						break;
					}
	
					actionFreq = 0;
	
					if (workingTile != null) {
						starringPointSet = false;
						
						placeBlockAt(bx,by,bz, Block.tilledField.blockID);
						if(rand.nextInt(8) == 0 && blockId == Block.grass.blockID)
						{
							spawnSeed(bx,by,bz);
				        }
						if(seeds>0)
						{
							currentAction = actionPlaceSeed;
							defaultHoldItem = new ItemStack(Item.seeds, 1);
							isSwinging = false;
							actionFreq = 0;
						}
						else if(rand.nextInt(100)<5)
						{
							// check for saplings
							currentAction = actionGoBack;
							starringPointSet = false;
						}
						else
						{
							if(needIrrigate(worldObj, bx,by,bz))
							{
								currentAction = actionIrrigate;
							}
							else
							{
								currentAction = actionFindPlaceToPlant;
								isSwinging = false;
								workingTile =null;
								starringPointSet = false;
							}
						}
						break;
					}
				}
			}
				else if(blockId == Block.tilledField.blockID && seeds>0)
				{
					currentAction = actionPlaceSeed;
					defaultHoldItem = new ItemStack(Item.seeds, 1);
					isSwinging = false;
					actionFreq = 0;
				}
				else if(blockId == Block.crops.blockID && worldObj.getBlockMetadata(bx,by,bz) == 7)
				{
					if(!equipItem(0))
					{
						currentAction = actionGoBack;
						actualTicksToDelivery = 0;
						starringPointSet = false;
						currentToolEfficiency = 12;
					}
					
					if (actionFreq >= currentToolEfficiency) {
						// tool weariness
						if(toolWeariness(toolsList[0]))
						{
							currentAction = actionGetEquipment;
							break;
						}
		
						actionFreq = 0;
		
						if (workingTile != null) {
							starringPointSet = false;
							
						placeBlockAt(bx,by,bz, 0);
						placeBlockAt(bx,by-1,bz, Block.tilledField.blockID);
						spawnSeed(bx,by,bz);
						spawnWheat(bx,by,bz);
						currentAction = actionFindPlaceToPlant;
						workingTile =null;

						}
					}
				}
			
			break;
		case actionPlaceSeed:
			moveStrafing = 0;
			moveForward = 0;
			blockJumping = true;
			isJumping = false;
			freeRoamCount = 100;
			stuckCount = 0;
			moveForward = (float) 0;
			
			if(actionFreq==0)
			{
				starringPointSet = true;
        		starringPointX = workingTile.xCoord;
        		starringPointY = workingTile.yCoord;
        		starringPointZ = workingTile.zCoord;
			}
			
			if (actionFreq == 2)
			{
				isSwinging = true;
			}

			if(actionFreq==3)
			{
				worldObj.playSoundEffect((float)posX + 0.5F, (float)posY + 0.5F, (float)posZ + 0.5F, Block.tilledField.stepSound.func_1145_d(), (Block.tilledField.stepSound.func_1147_b() + 1.0F) / 2.0F, Block.tilledField.stepSound.func_1144_c() * 0.8F);
				seeds--;
				placeBlockAt(MathHelper.floor_double(workingTile.xCoord),
											MathHelper.floor_double(workingTile.yCoord)+1,
											MathHelper.floor_double(workingTile.zCoord), Block.crops.blockID);
	    		currentAction = actionFindPlaceToPlant;
				workingTile =null;

	    		isSwinging = false;
				starringPointSet = false;
	    		defaultHoldItem = toolsList[0].copy();
	    		actionFreq = 0;
	    		defaultHoldItem=null;
			}
    		
			actionFreq++;
			break;			
		case actionGetOutOfBuilding:
			starringPointSet = false;
				
			// get out through front or through back of the house, depending on location
			int  offsetZ = Math.abs(initialZOffset);
			int destZ = 0;
			if(iPosZ>=homePosZ)
				destZ = homePosZ + offsetZ;
			else
				destZ = homePosZ - offsetZ;

			int ySolid = findTopGround(homePosX+initialXOffset, destZ) ;
				
			destPoint = Vec3D.createVectorHelper(homePosX+initialXOffset, ySolid, destZ);
			Vec3D entVec = Vec3D.createVector(iPosX, iPosY, iPosZ);
			
			if(entVec.distanceTo(destPoint)<2)
			{
				currentAction = actionFindPlaceToPlant;
				workingTile =null;

				isSwinging = false;
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

			if(entVec.distanceTo(destPoint)<2)
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
			
			
			// check if player is near chest with tools
			// if yes then check if there are some tools available
			// if yes then take item and change action to seek for tree
			// if no then
			Vec3D chestPos = scanForBlockNearEntity(
					mod_MineColony.hutFarmer.blockID, 1, 3, 1);

			if (chestPos != null) {
				speed = 0;
				TileEntityChest tileentitychest = (TileEntityChest) worldObj
						.getBlockTileEntity(
								MathHelper.floor_double(chestPos.xCoord),
								MathHelper.floor_double(chestPos.yCoord),
								MathHelper.floor_double(chestPos.zCoord));
				if (tileentitychest != null)
				{				
					if(toolsList[0]==null) equipItemFromChest(tileentitychest, Item.hoeDiamond.shiftedIndex, 1, 0);
					if(toolsList[0]==null) equipItemFromChest(tileentitychest, Item.hoeSteel.shiftedIndex, 1, 0);
					if(toolsList[0]==null) equipItemFromChest(tileentitychest, Item.hoeGold.shiftedIndex, 1, 0);
					if(toolsList[0]==null) equipItemFromChest(tileentitychest, Item.hoeStone.shiftedIndex, 1, 0);
					if(toolsList[0]==null) equipItemFromChest(tileentitychest, Item.hoeWood.shiftedIndex, 1, 0);
					
					if(toolsList[1]==null) equipItemFromChest(tileentitychest, Item.shovelDiamond.shiftedIndex, 1, 1);
					if(toolsList[1]==null) equipItemFromChest(tileentitychest, Item.shovelSteel.shiftedIndex, 1, 1);
					if(toolsList[1]==null) equipItemFromChest(tileentitychest, Item.shovelGold.shiftedIndex, 1, 1);
					if(toolsList[1]==null) equipItemFromChest(tileentitychest, Item.shovelStone.shiftedIndex, 1, 1);
					if(toolsList[1]==null) equipItemFromChest(tileentitychest, Item.shovelWood.shiftedIndex, 1, 1);
					
					ItemStack seedsStack = getItemFromChest(tileentitychest, Item.seeds.shiftedIndex, 64);
					if(seedsStack!=null)
						seeds += seedsStack.stackSize;
				}
			} 
			else 
			{
				destPoint = Vec3D.createVectorHelper(homePosX, homePosY, homePosZ);
				if(Vec3D.createVector(iPosX, homePosY, iPosZ).distanceTo(destPoint)<=3)
					speed = (float) 0.5;
			}
			
			// get the item in the pocket
			if(toolsList[0]!=null && toolsList[1]!=null)
			{
				defaultHoldItem = toolsList[0].copy();
				currentAction = actionGetOutOfBuilding;
				break;
			}
			else
			{
				// place sign when no tools
				if(toolsList[0]==null)
					signText1 = "Out of hoes";
				
				if(toolsList[1]==null)
					signText2 = "Out of shovels";
			}
			break;
			
		case actionDeliverGoods:
			rotationPitch = defaultPitch;
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
			Vec3D chest = scanForBlockNearEntity(mod_MineColony.hutFarmer.blockID,2, 2, 2);
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
						
						if(wheats>0 && putItemIntoChest(tileentitychest, Item.wheat.shiftedIndex, wheats))
						{
							wheats = 0;
							defaultHoldItem = new ItemStack(Item.wheat.shiftedIndex, 1,0);
						}
						
						currentAction = actionGetEquipment;

						ItemStack seedsStack = getItemFromChest(tileentitychest, Item.seeds.shiftedIndex, 64);
						if(seedsStack!=null)
							seeds += seedsStack.stackSize;
						
						// go to sleep
						if(!worldObj.isDaytime())
						{
							currentAction = actionIdle;
						}
					}
				}
			} else {
					// go back and depending on current location go to different side of the chest
					destPoint = Vec3D.createVectorHelper(homePosX, homePosY, homePosZ);				
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
					mod_MineColony.hutFarmer.blockID, 1, 1, 1);

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
		}		
	}
	
	private void spawnSeed(int bx, int by, int bz) {
		 int j1 = 1;
         for(int k1 = 0; k1 < j1; k1++)
         {
             float f = 0.7F;
             float f1 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
             float f2 = 1.2F;
             float f3 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
             EntityItem entityitem = new EntityItem(worldObj, (float)bx + f1, (float)by + f2, (float)bz + f3, new ItemStack(Item.seeds));
             entityitem.delayBeforeCanPickup = 10;
             worldObj.entityJoinedWorld(entityitem);
         }
	}
	
	private void spawnWheat(int bx, int by, int bz) {
		 int j1 = 1;
        for(int k1 = 0; k1 < j1; k1++)
        {
            float f = 0.7F;
            float f1 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
            float f2 = 1.2F;
            float f3 = rand.nextFloat() * f + (1.0F - f) * 0.5F;
            EntityItem entityitem = new EntityItem(worldObj, (float)bx + f1, (float)by + f2, (float)bz + f3, new ItemStack(Item.wheat));
            entityitem.delayBeforeCanPickup = 10;
            worldObj.entityJoinedWorld(entityitem);
        }
	}
	

	protected boolean needIrrigate(World worldObj, int x, int y, int z) {
		for (int i = x - 1; i <= x + 1; i++)
				for (int k = z - 1; k <= z + 1; k++)
					if(i%6 ==0 || k%6==0)
						return true;
		return false;
	}

	protected void equipItemSpecific(ItemStack item) {
		if(item.itemID == Item.hoeDiamond.shiftedIndex)
			currentToolEfficiency = 2;
		else if(item.itemID == Item.hoeSteel.shiftedIndex)
			currentToolEfficiency = 4;
		else if(item.itemID == Item.hoeGold.shiftedIndex)
			currentToolEfficiency = 6;
		else if(item.itemID == Item.hoeStone.shiftedIndex)
			currentToolEfficiency = 6;
		else if(item.itemID == Item.hoeWood.shiftedIndex)
			currentToolEfficiency = 8;
	}

	protected int getDropItemId() {
		return Item.hoeWood.shiftedIndex;
	}
	
	public void onDeath(Entity entity) {
	   if(scoreValue > 0 && entity != null)
        {
            entity.addToPlayerScore(this, scoreValue);
        }
        field_9327_S = true;
        if(!worldObj.multiplayerWorld)
        {
        	 if(toolsList[0]!=null)
             	dropItem(toolsList[0].itemID, 1);
             
             if(seeds>0)
             	dropItem(Item.seeds.shiftedIndex, seeds);
             	
             if(wheats>0)
             	dropItem(Item.wheat.shiftedIndex, wheats);
        }
        worldObj.func_9425_a(this, (byte)3);
	}
	
	protected Vec3D scanForPlantPlaceNearPoint(int x, int y, int z,	int rx, int ry, int rz) {
		Vec3D entityVec = Vec3D.createVector(x, y, z);

		// priority for growed crops
		for (int i = x - workingRange; i <= x + workingRange; i++)
			for (int j = y - 10; j <= y + 10; j++)
				for (int k = z - workingRange; k <= z + workingRange; k++) {
					if(Vec3D.createVector(i, j, k).distanceTo(Vec3D.createVector(homePosX, j, homePosZ)) <= workingRange &&
						worldObj.getBlockId(i, j, k) == Block.crops.blockID && worldObj.getBlockMetadata(i,j,k) == 7)
					{
						return Vec3D.createVectorHelper(i, j, k);
					}
				}
		
		Vec3D closestVec = null;
		double minDistance = 999999999;

		for (int i = x - rx; i <= x + rx; i++)
			for (int j = y - ry; j <= y + ry; j++)
				for (int k = z - rz; k <= z + rz; k++) {
					if(Vec3D.createVector(i, j, k).distanceTo(Vec3D.createVector(homePosX, j, homePosZ)) <= workingRange)
					{
						if (i % 6 != 0 && k % 6 != 0 &&
								(worldObj.getBlockId(i, j, k) == Block.grass.blockID ||
								(worldObj.getBlockId(i, j, k) == Block.tilledField.blockID && worldObj.getBlockId(i, j+1, k) != Block.crops.blockID && seeds>0) ||
								(worldObj.getBlockId(i, j, k) == Block.dirt.blockID)) && worldObj.getBlockId(i, j+1, k) == 0)
								 {
	
							Vec3D tempVec = Vec3D.createVectorHelper(i, j, k);
							
							if (closestVec == null
									|| tempVec.distanceTo(entityVec) < minDistance) {
								closestVec = tempVec;
								minDistance = closestVec.distanceTo(entityVec);
							}
						}
					}
				}

		if(minDistance<999999999)
			return closestVec;
		else
			return null;
	}
	
	protected Vec3D scanForIrrigatingBlockNearPoint(int x, int y, int z) {
		Vec3D entityVec = Vec3D.createVector(x, y, z);

		Vec3D closestVec = null;
		double minDistance = 999999999;
		int j = y;
		for (int i = x - 1; i <= x + 2; i++)
				for (int k = z - 1; k <= z + 2; k++) {
					int blockId = worldObj.getBlockId(i, j, k);
					int blockUpperId = worldObj.getBlockId(i, j+1, k);
					int blockLowerId = worldObj.getBlockId(i, j-1, k);
					
					if ((i % 6 ==0 || k % 6 == 0) && 
							blockUpperId!=Block.waterStill.blockID && 
							blockLowerId!=Block.waterStill.blockID && 
							blockUpperId!=Block.waterMoving.blockID && 
							blockLowerId!=Block.waterMoving.blockID && 
							(blockId == Block.dirt.blockID ||
							blockId == Block.gravel.blockID ||
							blockId == Block.sand.blockID ||
							blockId == Block.grass.blockID ||
							blockId == 0)) {

						// this block must be surrounded by opaque blocks
						if(canWaterLeaveBlock(i,j,k))
							continue;
						
						Vec3D tempVec = Vec3D.createVectorHelper(i, j, k);
						
						if (closestVec == null
								|| tempVec.distanceTo(entityVec) < minDistance) {
							closestVec = tempVec;
							minDistance = closestVec.distanceTo(entityVec);
						}
					}
				}

		if(minDistance<999999999)
			return closestVec;
		else
			return null;
	}
	
	private boolean canWaterLeaveBlock(int i, int j, int k) {
		if(canWaterFlow(i+1, j, k))
			return true;
		if(canWaterFlow(i-1, j, k))
			return true;
		if(canWaterFlow(i, j, k+1))
			return true;
		if(canWaterFlow(i, j, k-1))
			return true;
		if(canWaterFlow(i, j-1, k))
			return true;
		
		return false;
	}
	
	private boolean canWaterFlow(int i, int j, int k)
	{
		int blockId = worldObj.getBlockId(i, j, k);
		if(blockId == 0 || blockId==Block.snow.blockID)
			return true;
		
		return false;
	}
	
	private float getGrowthRate(World world, int i, int j, int k)
    {
		int blockID = Block.crops.blockID;
		
        float f = 1.0F;
        int l = world.getBlockId(i, j, k - 1);
        int i1 = world.getBlockId(i, j, k + 1);
        int j1 = world.getBlockId(i - 1, j, k);
        int k1 = world.getBlockId(i + 1, j, k);
        int l1 = world.getBlockId(i - 1, j, k - 1);
        int i2 = world.getBlockId(i + 1, j, k - 1);
        int j2 = world.getBlockId(i + 1, j, k + 1);
        int k2 = world.getBlockId(i - 1, j, k + 1);
        boolean flag = j1 == blockID || k1 == blockID;
        boolean flag1 = l == blockID || i1 == blockID;
        boolean flag2 = l1 == blockID || i2 == blockID || j2 == blockID || k2 == blockID;
        for(int l2 = i - 1; l2 <= i + 1; l2++)
        {
            for(int i3 = k - 1; i3 <= k + 1; i3++)
            {
                int j3 = world.getBlockId(l2, j - 1, i3);
                float f1 = 0.0F;
                if(j3 == Block.tilledField.blockID)
                {
                    f1 = 1.0F;
                    if(world.getBlockMetadata(l2, j - 1, i3) > 0)
                    {
                        f1 = 3F;
                    }
                }
                if(l2 != i || i3 != k)
                {
                    f1 /= 4F;
                }
                f += f1;
            }

        }

        if(flag2 || flag && flag1)
        {
            f /= 2.0F;
        }
        return f;
    }
	
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);

		nbttagcompound.setInteger("seeds", seeds);
		nbttagcompound.setInteger("crops", wheats);
		
		if(workingTile!=null)
		{
			nbttagcompound.setInteger("workingTileX", MathHelper.floor_double(workingTile.xCoord));
			nbttagcompound.setInteger("workingTileY", MathHelper.floor_double(workingTile.yCoord));
			nbttagcompound.setInteger("workingTileZ", MathHelper.floor_double(workingTile.zCoord));
		}

		
		
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		
		seeds = nbttagcompound.getInteger("seeds");
		wheats = nbttagcompound.getInteger("crops");
		
		workingTile = Vec3D.createVectorHelper(nbttagcompound.getInteger("workingTileX"),
												nbttagcompound.getInteger("workingTileY"),
												nbttagcompound.getInteger("workingTileZ"));
	}
	
	
}
