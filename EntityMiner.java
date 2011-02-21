package net.minecraft.src;

import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;


public class EntityMiner extends EntityWorker {

	private static final int actionDig = 20;
	private static final int actionPlaceTorch = 21;
	
	private Vec3D currentBlock;
	private List<Vec3D> miningTunnel;
	private int miningTunnelIndex = 0;
	
	private int dirtBlocks = 0;
	private int stoneBlocks = 0;
	private int coalBlocks = 0;
	private int ironBlocks = 0;
	private int sandBlocks = 0;
	private int gravelBlocks = 0;
	private int goldBlocks = 0;
	private int diamondBlocks = 0;
	private int redstoneBlocks = 0;
	
	private int initialDiggingXOffset = 0;
	private int initialDiggingYOffset = -2;
	private int initialDiggingZOffset = 8;
	
	private int garbageBlockId = -1;
	private int garbageBlockX = 0;
	private int garbageBlockY = 0;
	private int garbageBlockZ = 0;
	
	private int torches = 0;

	private int allBlocks = 0;
	private int limitStone = 0;
	private int defaultLimitStone = 64;
	
	private boolean forceGetOut = false;
	
	private int idleTime = 0;

	public EntityMiner(World world) {
		super(world);
		texture = "/mob/miner.png";
		setSize(0.9F, 1.3F);
		currentAction = actionGetEquipment;
		defaultHoldItem = null;
		pathToEntity = null;
		currentBlock = null;
		miningTunnel = null;
		speed = (float)0.5;
		maxAnimFreq = 16;
	}

	public void onUpdate() {
		super.onUpdate();
		fallDistance = 0.0F;
	    if(motionY < -0.14999999999999999D)
	    {
	        motionY = -0.14999999999999999D;
	    }
	}
	
	protected void onSwing()
	{
		if(currentAction==actionDig && isSwinging)
		{
			StepSound stepsound = Block.stone.stepSound;
        	worldObj.playSoundAtEntity(this, stepsound.func_1145_d(), stepsound.func_1147_b() * 0.15F, stepsound.func_1144_c());
			
			// digging animation
			ModLoader.getMinecraftInstance().effectRenderer.func_1186_a((int)starringPointX,(int)starringPointY,(int)starringPointZ);
		}
	}
	
	protected void workerUpdate() {
		
		speed = (float) 1.0;
		// gather the nearby item
		EntityItem nearbyItem  = gatherItemNearby(ItemPickaxe.class);
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
		
		// get nearby torches
		nearbyItem = gatherItemNearby(Block.torchWood.blockID);
		if(nearbyItem!=null)
		{
			onGetItem();
			nearbyItem.setEntityDead();
		}
		
		// get nearby resources
		nearbyItem=gatherItemNearby(Block.dirt.blockID);
		if(nearbyItem!=null) {
			onGetItem();nearbyItem.setEntityDead();
//			dirtBlocks++;allBlocks++;
		}
		nearbyItem=gatherItemNearby(Block.cobblestone.blockID);
		if(nearbyItem!=null) {
			onGetItem();nearbyItem.setEntityDead();
			stoneBlocks++;allBlocks++;
		}
		nearbyItem=gatherItemNearby(Item.coal.shiftedIndex);
		if(nearbyItem!=null) {
			onGetItem();nearbyItem.setEntityDead();
			coalBlocks++;allBlocks++;
		}
		nearbyItem=gatherItemNearby(Block.oreIron.blockID);
		if(nearbyItem!=null) {
			onGetItem();nearbyItem.setEntityDead();
			ironBlocks++;allBlocks++;
		}
		nearbyItem=gatherItemNearby(Block.sand.blockID);
		if(nearbyItem!=null) {
			onGetItem();nearbyItem.setEntityDead();
			sandBlocks++;allBlocks++;
		}
		nearbyItem=gatherItemNearby(Item.flint.shiftedIndex);
		if(nearbyItem!=null) {
			onGetItem();nearbyItem.setEntityDead();
			gravelBlocks++;allBlocks++;
		}
		nearbyItem=gatherItemNearby(Block.gravel.blockID);
		if(nearbyItem!=null) {
			onGetItem();nearbyItem.setEntityDead();
			gravelBlocks++;allBlocks++;
		}
		nearbyItem=gatherItemNearby(Block.oreGold.blockID);
		if(nearbyItem!=null) {
			onGetItem();nearbyItem.setEntityDead();
			goldBlocks++;allBlocks++;
		}
		nearbyItem=gatherItemNearby(Item.diamond.shiftedIndex);
		if(nearbyItem!=null) {
			onGetItem();nearbyItem.setEntityDead();
			diamondBlocks++;allBlocks++;
		}
		nearbyItem=gatherItemNearby(Item.redstone.shiftedIndex);
		if(nearbyItem!=null) {
			onGetItem();nearbyItem.setEntityDead();
			redstoneBlocks++;allBlocks++;
		}
		
		destroySign();
		if(signText1 != "" || signText2 != ""|| signText3 != "")
		{
			placeSign(mod_MineColony.hutMiner.blockID, signText1, signText2, signText3);
			moveForward = 0;
			moveStrafing = 0;
			isJumping = false;
		}
		else
			destroySignsInRange(3,3,3);
		
		signText1 = "";
		signText2 = "";
		signText3 = "";
		
		switch (currentAction) {
		case actionGetOutOfBuilding:
			
			starringPointSet = false;
			
			// go back through front or through back of the house, depending on location
			//int offsetZ = Math.abs(initialDiggingZOffset);
			//double destZ = 0;
			//if(iPosZ>=homePosZ)
			//	destZ = homePosZ + offsetZ;
			//else
			//	destZ = homePosZ - offsetZ;
			
			int ySolid = findTopGround(homePosX+initialDiggingXOffset, homePosZ+initialDiggingZOffset);
			Vec3D entVec = Vec3D.createVector(iPosX, iPosY, iPosZ);
			destPoint = Vec3D.createVectorHelper(homePosX+initialDiggingXOffset+0.5, ySolid, homePosZ+initialDiggingZOffset+0.5);
			
			// if(iPosY<homePosY)
				// entVec = Vec3D.createVector(iPosX, iPosY, iPosZ);
			// else
				// entVec = Vec3D.createVector(iPosX, homePosY, iPosZ);
			
			if(entVec.distanceTo(destPoint)<2.5)
			{
				destPoint = Vec3D.createVectorHelper(homePosX+initialDiggingXOffset, ySolid, homePosZ+initialDiggingZOffset);
				createTunnelWayToDestPoint(homePosX+initialDiggingXOffset, ySolid, homePosZ+initialDiggingZOffset, 20);
				limitStone = defaultLimitStone;
				currentAction = actionDig;
			}
			
			break;
		case actionGetEquipment:
			// respawn worker near chest if too long delivery
			if(actualTicksToDelivery > ticksToDelivery)
			{
				actualTicksToDelivery = 0;
				setPosition(homePosX, homePosY+1, homePosZ);
			}
			actualTicksToDelivery++;
			
			isSwinging = false;
		
			// check if player is near chest with tools
			// if yes then check if there are some tools available
			Vec3D chestPos = scanForBlockNearEntity(
					mod_MineColony.hutMiner.blockID, 2, 3, 2);

			if (chestPos != null) {
				if(!worldObj.isDaytime())
				{
					signText3 = "Sleeping";
					break;
				}
				
				TileEntityChest tileentitychest = (TileEntityChest) worldObj.getBlockTileEntity(
								MathHelper.floor_double(chestPos.xCoord),
								MathHelper.floor_double(chestPos.yCoord),
								MathHelper.floor_double(chestPos.zCoord));
				if (tileentitychest != null)
				{				
					currentlyEquipedTool = 0;
					if(toolsList[0]==null) equipItemFromChest(tileentitychest, Item.pickaxeDiamond.shiftedIndex, 1, 0);
					if(toolsList[0]==null) equipItemFromChest(tileentitychest, Item.pickaxeSteel.shiftedIndex, 1, 0);
					if(toolsList[0]==null) equipItemFromChest(tileentitychest, Item.pickaxeGold.shiftedIndex, 1, 0);
					if(toolsList[0]==null) equipItemFromChest(tileentitychest, Item.pickaxeStone.shiftedIndex, 1, 0);
					if(toolsList[0]==null) equipItemFromChest(tileentitychest, Item.pickaxeWood.shiftedIndex, 1, 0);
					
					if(toolsList[1]==null) equipItemFromChest(tileentitychest, Item.shovelDiamond.shiftedIndex, 1, 1);
					if(toolsList[1]==null) equipItemFromChest(tileentitychest, Item.shovelSteel.shiftedIndex, 1, 1);
					if(toolsList[1]==null) equipItemFromChest(tileentitychest, Item.shovelGold.shiftedIndex, 1, 1);
					if(toolsList[1]==null) equipItemFromChest(tileentitychest, Item.shovelStone.shiftedIndex, 1, 1);
					if(toolsList[1]==null) equipItemFromChest(tileentitychest, Item.shovelWood.shiftedIndex, 1, 1);

					ItemStack torchesStack = getItemFromChest(tileentitychest, Block.torchWood.blockID, 64);
					if(torchesStack!=null)
						torches += torchesStack.stackSize;
					
				}
			} 
			else 
			{
				if (destPoint == null || stuckCount>5) {
					destPoint = Vec3D.createVectorHelper(homePosX, homePosY,homePosZ - 1);
					stuckCount = 0;
					currentAction = actionDig;
//					if(rand.nextInt(100)<75)
//					{
						forceGetOut = true;
					//}
//					else
//					{
//						limitStone = allBlocks + 10;
//						createTunnelWayToDestPoint(iPosX, iPosY, iPosZ, 20);
//					}
				}
			}
			
			// get the item in the pocket
			if(toolsList[0]!=null && toolsList[1]!=null)
			{
				//pathToEntity = findPathToXYZ(homePosX+initialDiggingXOffset, homePosY, homePosZ+initialDiggingZOffset);
				defaultHoldItem = toolsList[0].copy();
				currentAction = actionGetOutOfBuilding;
				break;
			}
			else
			{
				// place sign when no tools
				if(toolsList[0]==null)
					signText1 = "Out of pickaxes";
				
				if(toolsList[1]==null)
					signText2 = "Out of shovels";
			}
			
			break;
		case actionPlaceTorch:
			isJumping = false;
			moveForward = (float) 0;
			if (actionFreq == 1) {

				// scan for first free block around
				int rx = -1+rand.nextInt(3);
				int rz = -1+rand.nextInt(3);
				int ry = rand.nextInt(2);
				
				Vec3D place = scanForBlockNearPoint(0, iPosX+rx, iPosY+1+ry, iPosZ+rz, 1, 0, 1);
				if(place!=null && Block.torchWood.canPlaceBlockAt(worldObj, MathHelper.floor_double(place.xCoord),
						MathHelper.floor_double(place.yCoord), 
						MathHelper.floor_double(place.zCoord)))
				{
					starringPointSet = true;
            		starringPointX = place.xCoord;
            		starringPointY = place.yCoord;
            		starringPointZ = place.zCoord;
				}
				else
            	{
            		currentAction = actionDig;
    	    		actionFreq = 0;
    	    		defaultHoldItem=null;
    	    		break;
            	}
			}
			if (actionFreq == 2)
			{
				isSwinging = true;
			}

			if(actionFreq==3)
			{
				//worldObj.playSoundAtEntity(this, "random.wood click", 1.0F,	1.0F / (rand.nextFloat() * 0.4F + 0.8F));
				
				worldObj.playSoundEffect((float)posX + 0.5F, (float)posY + 0.5F, (float)posZ + 0.5F, Block.stone.stepSound.func_1145_d(), (Block.stone.stepSound.func_1147_b() + 1.0F) / 2.0F, Block.stone.stepSound.func_1144_c() * 0.8F);
				torches--;
				placeBlockAt(MathHelper.floor_double(starringPointX),
											MathHelper.floor_double(starringPointY),
											MathHelper.floor_double(starringPointZ), Block.torchWood.blockID);
	    		currentAction = actionDig;
	    		actionFreq = 0;
	    		defaultHoldItem=null;
			}
    		
			actionFreq++;
			break;
		case actionDig:
			if(stuckCount>12 || (!forceGetOut && allBlocks>limitStone) || (forceGetOut && iPosY>=homePosY+rand.nextInt(5) && worldObj.getBlockId(iPosX, iPosY+2, iPosZ)==0))
			{
				stuckCount = 0;
				forceGetOut = false;
				currentAction = actionGoBack;
				actualTicksToDelivery = 0;

				destPoint = null;

				// show block
				if(allBlocks>0)
					defaultHoldItem = new ItemStack(Block.stone.blockID,1,0);
				actionFreq=0;
				isSwinging = false;
				starringPointSet = false;
				rotationPitch = defaultPitch;	
				//pathToEntity = findPathToXYZ(homePosX, homePosY, homePosZ+1);
				//destPoint = Vec3D.createVectorHelper(homePosX, homePosY, homePosZ+1);
				break;
			}
			
			if(destPoint!=null && destPoint.squareDistanceTo(posX, posY, posZ) < 4)
				destPoint = null;
			
			if(destPoint == null)
			{
				int rx=0;
				int rz=0;
				int ry=0;
				
				if(forceGetOut)
				{
					rx = iPosX;
					ry = homePosY+1;
					rz = iPosZ;
				}
				else
				{
					while(true)
					{
						rx = -workingRange + rand.nextInt(workingRange*2)+homePosX;
						rz = -workingRange + rand.nextInt(workingRange*2)+homePosZ;
						if(Vec3D.createVector(posX, posY, posZ).distanceTo(Vec3D.createVector(rx, posY, rz))>6)
								break;
					}
					if(posY>=homePosY+initialDiggingYOffset)
						ry = homePosY+initialDiggingYOffset;
					else
						ry = iPosY-5;
						//ry = MathHelper.floor_double(boundingBox.minY)+(-4+rand.nextInt(3));
				}
				destPoint = Vec3D.createVectorHelper(rx, ry, rz);
				createTunnelWayToDestPoint(iPosX, iPosY, iPosZ, 20);
			}
			
			isSwinging = false;
			actionFreq++;
			
            // place torch if needed
			 if(!forceGetOut &&  torches>0 && this.getEntityBrightness(0)>0.1 && this.getEntityBrightness(0)<0.3 && rand.nextInt(100)<30)
			 {
				defaultHoldItem = new ItemStack(Block.torchWood, 1);
				currentAction = actionPlaceTorch;
				isSwinging = false;
				actionFreq = 0;
				break;
			 }
			 		 
			// select current block to dig
			 if(worldObj.getBlockId(iPosX, iPosY, iPosZ) != 0 && worldObj.getBlockId(iPosX, iPosY, iPosZ) != Block.waterMoving.blockID &&
						worldObj.getBlockId(iPosX, iPosY, iPosZ) != Block.waterStill.blockID)
			 {
				 currentBlock = Vec3D.createVectorHelper(iPosX, iPosY, iPosZ);
			 }
			 else if(worldObj.getBlockId(iPosX, iPosY+1, iPosZ) != 0 && worldObj.getBlockId(iPosX, iPosY+1, iPosZ) != Block.waterMoving.blockID &&
						worldObj.getBlockId(iPosX, iPosY+1, iPosZ) != Block.waterStill.blockID)
			 {
				 currentBlock = Vec3D.createVectorHelper(iPosX, iPosY+1, iPosZ);
			 }
			else if(forceGetOut)
            {
				destPoint = null;
            	currentBlock = Vec3D.createVectorHelper(iPosX+0.5, iPosY+2, iPosZ+0.5);
            }
            else if(miningTunnel!=null && miningTunnelIndex < miningTunnel.size())
			{
				if(Vec3D.createVector(posX, posY, posZ).distanceTo(miningTunnel.get(miningTunnelIndex))>10)
				{
					destPoint = null;
					break;
				}
					
				if(miningTunnelIndex >= miningTunnel.size())
				{
					destPoint = null;
					break;
				}

				Vec3D tempVec = miningTunnel.get(miningTunnelIndex);
				
				int x = MathHelper.floor_double(tempVec.xCoord);
				int y = MathHelper.floor_double(tempVec.yCoord);
				int z = MathHelper.floor_double(tempVec.zCoord);
				
				if(MathHelper.floor_double(destPoint.yCoord)>MathHelper.floor_double(boundingBox.minY))
				{
					y+=2;
				}
				else if(y==MathHelper.floor_double(boundingBox.minY))
					y++;
				
				// don't dig if is above ground
				Vec3D diggingPlace = Vec3D.createVector(homePosX+initialDiggingXOffset, iPosY, homePosZ+initialDiggingZOffset);
				entVec = Vec3D.createVector(iPosX, iPosY, iPosZ);
				double distance = diggingPlace.distanceTo(entVec);
				if(iPosY>homePosY+initialDiggingYOffset && distance>1.3)
				{
					destPoint = Vec3D.createVectorHelper(homePosX+initialDiggingXOffset, iPosY, homePosZ+initialDiggingZOffset); 
					starringPointSet = false;
					x = MathHelper.floor_double(destPoint.xCoord);
					y = MathHelper.floor_double(iPosY-1);
					z = MathHelper.floor_double(destPoint.zCoord);
				}

				currentBlock = scanForBlockToDig(Block.grass.blockID, x, y, z, 1, 2, 1);
				if(currentBlock == null)
					currentBlock = scanForBlockToDig(Block.snow.blockID, x, y, z, 1, 2, 1);
				
				// don't dig grass if it's above ground so it won't mess around home
//				if(currentBlock != null && currentBlock.yCoord>homePosY+initialDiggingYOffset)
//				{
//					distance = diggingPlace.distanceTo(Vec3D.createVector(currentBlock.xCoord, iPosY, currentBlock.zCoord));
//					if(distance>2)
//						currentBlock = null;
//				}
					
				
				if(toolsList[0]!=null && toolsList[0].getItem().canHarvestBlock(Block.oreDiamond))
				{
					if(currentBlock == null)
						currentBlock = scanForBlockToDig(Block.oreDiamond.blockID, x, y, z, 1, 1, 1);
					if(currentBlock == null)
						currentBlock = scanForBlockToDig(Block.oreGold.blockID, x, y, z, 1, 1, 1);
					if(currentBlock == null)
						currentBlock = scanForBlockToDig(Block.oreRedstone.blockID, x, y, z, 1, 1, 1);
				}				
				if(currentBlock == null)
					currentBlock = scanForBlockToDig(Block.oreIron.blockID, x, y, z, 1, 1, 1);
				if(currentBlock == null)
					currentBlock = scanForBlockToDig(Block.oreCoal.blockID, x, y, z, 1, 1, 1);
				if(currentBlock == null)
					currentBlock = scanForBlockToDig(Block.gravel.blockID, x, y, z, 1, 1, 1);
				if(currentBlock == null)
					currentBlock = scanForBlockToDig(Block.sand.blockID, x, y, z, 1, 1, 1);
				if(currentBlock == null)
					currentBlock = scanForBlockToDig(Block.dirt.blockID, x, y, z, 1, 1, 1);
				if(currentBlock == null)
					currentBlock = scanForBlockToDig(Block.stone.blockID, x, y, z, 1, 1, 1);

			}
		
			
			if(currentBlock==null)
			{			
				if(miningTunnel!=null && miningTunnelIndex < miningTunnel.size())
					walkToTargetStraight( miningTunnel.get(miningTunnelIndex));
				stuckCount++;
			}
			else
			{
				stuckCount=0;
				speed = (float) 0.1;
				currentBlock.xCoord += 0.5;
				currentBlock.zCoord += 0.5;
				walkToTargetStraight(currentBlock);
			}
			
			if(stuckCount>4)
			{
				destPoint=null;
				break;
			}
			
			if(forceGetOut)
			{
				if(currentBlock!=null && MathHelper.floor_double(currentBlock.yCoord)-MathHelper.floor_double(boundingBox.minY)>0 ||
						worldObj.getBlockId(iPosX, iPosY, iPosZ) == Block.waterMoving.blockID ||
						worldObj.getBlockId(iPosX, iPosY, iPosZ) == Block.waterStill.blockID)
				{
					if(worldObj.getBlockId(iPosX,iPosY+2,iPosZ) == 0)
						isJumping = true;
					else
						isJumping = false;

					if(isNaturalBlock(iPosX,iPosY-1,iPosZ) && iPosY < homePosY)
					{
						this.setVelocity((-0.5+rand.nextFloat())/10, 0.2, (-0.5+rand.nextFloat())/10);

						// clean previously placed dirt
						if(garbageBlockId != -1 && (garbageBlockX!=0 || garbageBlockY!=0 || garbageBlockZ!=0))
						{
							placeBlockAt(garbageBlockX,garbageBlockY,garbageBlockZ, garbageBlockId);
						}
						garbageBlockX = iPosX;
						garbageBlockY = iPosY-1;
						garbageBlockZ = iPosZ;
						garbageBlockId = worldObj.getBlockId(garbageBlockX,garbageBlockY,garbageBlockZ);
						
						placeBlockAt(iPosX,iPosY-1,iPosZ, Block.dirt.blockID);
						
						currentBlock = Vec3D.createVectorHelper(iPosX,iPosY+2,iPosZ);
						if(isNaturalBlock(iPosX,iPosY+2,iPosZ))
							placeBlockAt(iPosX,iPosY+2,iPosZ, 0);
					}
				}
			}
			else if(miningTunnel!=null && miningTunnelIndex<miningTunnel.size() && MathHelper.floor_double(miningTunnel.get(miningTunnelIndex).yCoord)-MathHelper.floor_double(boundingBox.minY)>0) {
				if(worldObj.getBlockId(iPosX,iPosY-1,iPosZ) == 0 ||
					worldObj.getBlockId(iPosX, iPosY, iPosZ) == Block.waterMoving.blockID ||
					worldObj.getBlockId(iPosX, iPosY, iPosZ) == Block.waterStill.blockID)
					isJumping = true;
			
				if(worldObj.getBlockId(iPosX,iPosY-1,iPosZ) == 0 && iPosY < homePosY)
				{
					// clean previously placed dirt
					if(garbageBlockId != -1 && (garbageBlockX!=0 || garbageBlockY!=0 || garbageBlockZ!=0))
					{
						placeBlockAt(garbageBlockX,garbageBlockY,garbageBlockZ, garbageBlockId);
					}
					garbageBlockX = iPosX;
					garbageBlockY = iPosY-1;
					garbageBlockZ = iPosZ;
					garbageBlockId = worldObj.getBlockId(garbageBlockX,garbageBlockY,garbageBlockZ);
					
					placeBlockAt(iPosX,iPosY-1,iPosZ, Block.dirt.blockID);
						
					currentBlock = Vec3D.createVectorHelper(iPosX,iPosY+2,iPosZ);
				}
			}
			
			if(miningTunnel!=null && miningTunnelIndex < miningTunnel.size())
			{
				Vec3D tempMinePoint = miningTunnel.get(miningTunnelIndex);
				int minX = (int)Math.round(tempMinePoint.xCoord);
				int minY = (int)Math.round(tempMinePoint.yCoord);
				int minZ = (int)Math.round(tempMinePoint.zCoord);
				
				if(miningTunnel!=null && miningTunnelIndex<miningTunnel.size() && 
					countBlocksAround(minX,minY+1,minZ,1,1,1)<5)
					miningTunnelIndex++;
			}
//			Vec3D.createVector(posX, posY, posZ).distanceTo(miningTunnel.get(miningTunnelIndex))<1.5)

			
			if(currentBlock==null)
				break;
			
			// set starring point
			starringPointX = MathHelper.floor_double(currentBlock.xCoord);
			starringPointY = MathHelper.floor_double(currentBlock.yCoord);
			starringPointZ = MathHelper.floor_double(currentBlock.zCoord);
			starringPointSet = true;

			isSwinging = true;
            
			// check block id
			int blockId = worldObj.getBlockId(MathHelper.floor_double(currentBlock.xCoord),
					MathHelper.floor_double(currentBlock.yCoord),
					MathHelper.floor_double(currentBlock.zCoord));
								
			// choose shovel or pickaxe		
			if(blockId == Block.snow.blockID ||
			   blockId == Block.gravel.blockID ||
			   blockId == Block.sand.blockID ||
			   blockId == Block.grass.blockID ||
			   blockId == Block.dirt.blockID)
			{
				if(!equipItem(1))
				{
					currentAction = actionGoBack;
					actualTicksToDelivery = 0;
					starringPointSet = false;
					currentToolEfficiency = 12;
				}
				else
					currentlyEquipedTool = 1;
			}
			else
			{
				if(!equipItem(0))
				{
					currentAction = actionGoBack;
					actualTicksToDelivery = 0;
					currentToolEfficiency = 12;
					starringPointSet = false;
				}
				else
					currentlyEquipedTool = 0;
			}
					
				

			if (blockId>0 && blockId != mod_MineColony.hutMiner.blockID && actionFreq >= currentToolEfficiency * Block.blocksList[blockId].blockHardness) {
				actionFreq=0;
				
				Block.blocksList[blockId].harvestBlock(worldObj,(int)starringPointX,(int)starringPointY,(int)starringPointZ, 0);
			
				// tool weariness
				if(toolWeariness(toolsList[currentlyEquipedTool]))
				{
					currentAction = actionGetEquipment;
					actualTicksToDelivery = 0;
					starringPointSet = false;
				}

				placeBlockAt(
						MathHelper.floor_double(currentBlock.xCoord),
						MathHelper.floor_double(currentBlock.yCoord),
						MathHelper.floor_double(currentBlock.zCoord), 0);
				
				currentBlock = null;			
			}
			break;
		case actionGoBack:
			// respawn worker near chest if too long delivery
			if(actualTicksToDelivery > ticksToDelivery)
			{
				actualTicksToDelivery = 0;
				setPosition(homePosX, homePosY+1, homePosZ);
			}
			actualTicksToDelivery++;
			
			starringPointSet = false;

			if (stuckCount>20) {
				int offsetZ = Math.abs(initialDiggingZOffset);
				int destZ = 0;
				if(iPosZ>=homePosZ)
				{
					destZ = homePosZ + offsetZ;
				}
				else
				{
					destZ = homePosZ - offsetZ;
				}
				
				destPoint = Vec3D.createVectorHelper(homePosX, homePosY+initialDiggingYOffset,destZ);
				stuckCount = 0;
				currentAction = actionDig;
				//if(rand.nextInt(100)<85)
				//{
					forceGetOut = true;
				//}
//				else
//				{
//					limitStone = allBlocks + 10;
//					createTunnelWayToDestPoint(iPosX, iPosY, iPosZ, 20);
//				}
			}
			else
			{
				// go back through front or through back of the house, depending on location		
				int offsetZ = Math.abs(initialDiggingZOffset);
				int destZ = 0;
				if(iPosZ>=homePosZ)
				{
					for(int oz=offsetZ; oz>=0; oz--)
					{
						if(worldObj.getBlockId(homePosX+initialDiggingXOffset, homePosY, homePosZ + oz)!=0)
						{
							destZ = homePosZ + oz;
							break;
						}
					}
				}
				else
				{
					for(int oz=offsetZ; oz>=0; oz--)
					{
						if(worldObj.getBlockId(homePosX+initialDiggingXOffset, homePosY, homePosZ - oz)!=0)
						{
							destZ = homePosZ - oz;
							break;
						}
					}
				}
				
				ySolid = findTopGround(homePosX+initialDiggingXOffset, destZ);
				entVec = Vec3D.createVector(iPosX, iPosY, iPosZ);
				destPoint = Vec3D.createVectorHelper(homePosX+initialDiggingXOffset, ySolid, destZ);
				// if(iPosY<homePosY)
					// entVec = Vec3D.createVector(iPosX, iPosY, iPosZ);
				// else
					// entVec = Vec3D.createVector(iPosX, homePosY, iPosZ);
				if(entVec.distanceTo(destPoint)<3)
				{
					currentAction = actionDeliverGoods;
				}
			}
			break;
		case actionIdle:
			if (idleTime > 100 && worldObj.isDaytime()) {
				idleTime = 0;
				currentAction = actionGetEquipment;
				break;
			}

			Vec3D chestPos2 = scanForBlockNearEntity(
					mod_MineColony.hutMiner.blockID, 1, 1, 1);

			if (chestPos2 != null) {
				TileEntityChest tileentitychest = (TileEntityChest) worldObj
				.getBlockTileEntity(
						MathHelper.floor_double(chestPos2.xCoord),
						MathHelper.floor_double(chestPos2.yCoord),
						MathHelper.floor_double(chestPos2.zCoord));
				
				
				if(toolsList[0]!=null)
					putItemIntoChest(tileentitychest, toolsList[0].itemID, 1);
				if(toolsList[1]!=null)
					putItemIntoChest(tileentitychest, toolsList[1].itemID, 1);

				defaultHoldItem = null;
				toolsList[0] = null;
				toolsList[1] = null;
				
				moveForward = 0;
		

			} else {
				if (destPoint == null) {
					//pathToEntity = findPathToXYZ(homePosX + 1, homePosY,homePosZ);
					destPoint = Vec3D.createVector(homePosX + 1, homePosY,homePosZ);
				}
			}
			idleTime++;
			break;
		case actionDeliverGoods:			
			isJumping = false;
			moveForward = 0;
			moveStrafing = 0;
			rotationPitch = defaultPitch;
			isSwinging = false;
			Vec3D chest = scanForBlockNearEntity(mod_MineColony.hutMiner.blockID,2, 2, 2);
			if (chest != null) {
				
				starringPointSet = true;
				starringPointX = homePosX;
				starringPointY = homePosY;
				starringPointZ = homePosZ;
				
				if (actionFreq == 0) {
					{
					isSwinging = true;
					actionFreq++;
					}
				} else {
					actionFreq=0;
					TileEntityChest tileentitychest = (TileEntityChest) worldObj
							.getBlockTileEntity(
									MathHelper.floor_double(chest.xCoord),
									MathHelper.floor_double(chest.yCoord),
									MathHelper.floor_double(chest.zCoord));
					if (tileentitychest != null) {
						if(allBlocks>0)
						{
							if(dirtBlocks>0 && putItemIntoChest(tileentitychest, Block.dirt.blockID, dirtBlocks))
							{
								allBlocks-=dirtBlocks;
								dirtBlocks=0;
								defaultHoldItem = new ItemStack(Block.dirt.blockID, 1,0);
								break;
							}
							if(stoneBlocks>0 && putItemIntoChest(tileentitychest, Block.cobblestone.blockID, stoneBlocks))
							{
								allBlocks-=stoneBlocks;
								stoneBlocks=0;
								defaultHoldItem = new ItemStack(Block.cobblestone.blockID, 1,0);
								break;
							}
							if(coalBlocks>0 && putItemIntoChest(tileentitychest, Item.coal.shiftedIndex, coalBlocks))
							{
								allBlocks-=coalBlocks;
								coalBlocks=0;
								defaultHoldItem = new ItemStack(Item.coal.shiftedIndex, 1,0);
								break;
							}
							if(ironBlocks>0 && putItemIntoChest(tileentitychest, Block.oreIron.blockID, ironBlocks))
							{
								allBlocks-=ironBlocks;
								ironBlocks=0;
								defaultHoldItem = new ItemStack(Block.oreIron.blockID, 1,0);
								break;
							}
							if(sandBlocks>0 && putItemIntoChest(tileentitychest, Block.sand.blockID, sandBlocks))
							{
								allBlocks-=sandBlocks;
								sandBlocks=0;
								defaultHoldItem = new ItemStack(Block.sand.blockID, 1,0);
								break;
							}
							if(gravelBlocks>0 && putItemIntoChest(tileentitychest, Item.flint.shiftedIndex, gravelBlocks))
							{
								allBlocks-=gravelBlocks;
								gravelBlocks=0;
								defaultHoldItem = new ItemStack(Item.flint.shiftedIndex, 1,0);
								break;
							}
							if(goldBlocks>0 && putItemIntoChest(tileentitychest, Block.oreGold.blockID, goldBlocks))
							{
								allBlocks-=goldBlocks;
								goldBlocks=0;
								defaultHoldItem = new ItemStack(Block.oreGold.blockID, 1,0);
								break;
							}
							if(diamondBlocks>0 && putItemIntoChest(tileentitychest, Item.diamond.shiftedIndex, diamondBlocks))
							{
								allBlocks-=diamondBlocks;
								diamondBlocks=0;
								defaultHoldItem = new ItemStack(Block.oreDiamond.blockID, 1,0);
								break;
							}
							if(redstoneBlocks>0 && putItemIntoChest(tileentitychest, Item.redstone.shiftedIndex, redstoneBlocks))
							{
								allBlocks-=redstoneBlocks;
								redstoneBlocks=0;
								defaultHoldItem = new ItemStack(Block.oreRedstone.blockID, 1,0);
								break;
							}
						}
						else
						{
							equipItem(0);
							//defaultHoldItem = toolsList[0].copy();
							currentAction = actionGetEquipment;
							destPoint = Vec3D.createVectorHelper(homePosX+initialDiggingXOffset, homePosY+initialDiggingYOffset, homePosZ+initialDiggingZOffset);
							createTunnelWayToDestPoint(homePosX+initialDiggingXOffset, homePosY+initialDiggingYOffset, homePosZ+initialDiggingZOffset, 20);
							limitStone = defaultLimitStone;
							//pathToEntity = findPathToXYZ(homePosX+initialDiggingXOffset, homePosY, homePosZ+initialDiggingZOffset);
							break;
						}
						// go to sleep
//						if(!worldObj.isDaytime())
//						{
//							signText3 = "Sleeping";
//							currentAction = actionIdle;
//						}
					}
				}
			} else {
				if (stuckCount>20) {
					destPoint = Vec3D.createVectorHelper(homePosX+initialDiggingXOffset, homePosY,homePosZ+initialDiggingZOffset);
					stuckCount = 0;
					currentAction = actionDig;
					//if(rand.nextInt(100)<50)
					//{
						forceGetOut = true;
					//}
//					else
//					{
//						limitStone = allBlocks + 10;
//						createTunnelWayToDestPoint(iPosX, iPosY, iPosZ, 20);
//					}
				}
				else
				{
					destPoint = Vec3D.createVectorHelper(homePosX, homePosY,homePosZ);
					starringPointSet = false;
				}
			}
			break;
		}
		
		
	}

	private void createTunnelWayToDestPoint(int startX, int startY, int startZ, int maxLenght) {
		//create a set of blocks to destroy
		miningTunnel = new ArrayList<Vec3D>();
		miningTunnelIndex = 0;
		int cx = startX;
		int cy = startY;
		int cz = startZ;
		
		int dx = MathHelper.floor_double(destPoint.xCoord);
		int dy = MathHelper.floor_double(destPoint.yCoord);
		int dz = MathHelper.floor_double(destPoint.zCoord);
		
		// temp dest x
		int tdx = dx;
		int tdy = dy;
		int tdz = dz;
		
		
		int lenght = 0;
		miningTunnel.add(Vec3D.createVectorHelper(cx+0.5,cy,cz+0.5));
		while(lenght<maxLenght && Vec3D.createVector(cx,cy,cz).distanceTo(Vec3D.createVector(dx,dy,dz))>1)
		{
			lenght++;
			Vec3D blockVec = selectBestBlockAround(cx,cy,cz,3,3,3);
			
			// if smth interesting around then dig it elsewhere dig towards dest
			if(blockVec!=null)
			{
				tdx = MathHelper.floor_double(blockVec.xCoord);
				tdy = MathHelper.floor_double(blockVec.yCoord);
				tdz = MathHelper.floor_double(blockVec.zCoord);
			}
			else
			{
				tdx = dx;
				tdy = dy;
				tdz = dz;
			}
			
			if(cx<tdx)
			{
				cx++;
				//miningTunnel.add(Vec3D.createVectorHelper(cx, cy, cz));
			}
			else if(cx>tdx)
			{
				cx--;
				//miningTunnel.add(Vec3D.createVectorHelper(cx, cy, cz));
			}
			
			if(cy<tdy)
			{
				cy++;
				//miningTunnel.add(Vec3D.createVectorHelper(cx, cy, cz));
			}
			else if(cy>tdy)
			{
				cy--;
				//miningTunnel.add(Vec3D.createVectorHelper(cx, cy, cz));
			}

			if(cz<tdz)
			{
				cz++;
				//miningTunnel.add(Vec3D.createVectorHelper(cx, cy, cz));
			}
			else if(cz>tdz)
			{
				cz--;
				//miningTunnel.add(Vec3D.createVectorHelper(cx, cy, cz));
			}
			
			if(countBlocksAround(cx,cy,cz,1,1,1)>=18)
			{
				miningTunnel.add(Vec3D.createVectorHelper(cx, cy, cz));
			}
			else
			{
				blockVec = selectUnderGroundBlock(cx,cy,cz,3,3,3);
				if(blockVec !=null)
				{
					blockVec.xCoord += 0.5;
					blockVec.zCoord += 0.5;
					miningTunnel.add(blockVec);
					cx = MathHelper.floor_double(blockVec.xCoord);
					cy = MathHelper.floor_double(blockVec.yCoord);
					cz = MathHelper.floor_double(blockVec.zCoord);
				}
			}
			
		}
		miningTunnel.add(Vec3D.createVectorHelper(dx, dy, dz));
	}
	
	private Vec3D selectUnderGroundBlock(int x, int y, int z, int rx, int ry, int rz) {
		for (int i = x - rx; i <= x + rx; i++)
			for (int j = y - ry; j <= y + ry; j++)
				for (int k = z - rz; k <= z + rz; k++) 
				{
					if(countBlocksAround(i,j,k,1,1,1)<18)
						continue;
					return Vec3D.createVectorHelper(i, j, k);
				}
		
		return null;
	}

	private Vec3D selectBestBlockAround(int x, int y, int z, int rx, int ry, int rz)
	{
		for (int i = x - rx; i <= x + rx; i++)
			for (int j = y - ry; j <= y + ry; j++)
				for (int k = z - rz; k <= z + rz; k++) 
				{
					if(countBlocksAround(i,j,k,1,1,1)<18)
						continue;
					
					int blockID = worldObj.getBlockId(i, j, k);
					
					if(toolsList[0]!=null && toolsList[0].getItem().canHarvestBlock(Block.oreDiamond))
					{
						if(blockID == Block.oreDiamond.blockID)
							return Vec3D.createVectorHelper(i, j, k);
						if(blockID == Block.oreGold.blockID)
							return Vec3D.createVectorHelper(i, j, k);
						if(blockID == Block.oreRedstoneGlowing.blockID)
							return Vec3D.createVectorHelper(i, j, k);
						if(blockID == Block.oreRedstone.blockID)
							return Vec3D.createVectorHelper(i, j, k);
					}
					
					if(blockID == Block.oreIron.blockID)
						return Vec3D.createVectorHelper(i, j, k);
					if(blockID == Block.oreCoal.blockID)
						return Vec3D.createVectorHelper(i, j, k);
					
					
				}

		return null;
	}
	
	

	private Vec3D scanForBlockToDig(int blockId, int x, int y, int z, int rx, int ry, int rz) {
		
		Vec3D entityVec = Vec3D.createVector(posX, posY+(double)getEyeHeight(), posZ);
		// first scan if there are some blocks around you, if no then scan for blocks on path next
		if(iPosY<=homePosY+initialDiggingYOffset-2)
		{
		for (int i = iPosX - 1; i <= iPosX + 1; i++)
			for (int j=iPosY+1; j >= iPosY; j--)
				for (int k = iPosZ - 1; k <= iPosZ + 1; k++)
				{
					// leave the floor
					//if(j<homePosY+initialDiggingYOffset && (j%5 == 0) && (i>homePosX+initialDiggingXOffset+3 || i<homePosX+initialDiggingXOffset-3 || k>homePosZ+initialDiggingZOffset+3 || k<homePosZ+initialDiggingZOffset-3))
					if(j>homePosY+initialDiggingYOffset && (i>homePosX+initialDiggingXOffset+2 || i<homePosX+initialDiggingXOffset-2 || k>homePosZ+initialDiggingZOffset+2 || k<homePosZ+initialDiggingZOffset-2))
						continue;
					
					if(miningTunnel!= null && Vec3D.createVector(i, j, k).distanceTo(miningTunnel.get(miningTunnelIndex))<2 &&
							worldObj.isBlockOpaqueCube(i, j, k) && 
							!doesBlockHaveTorch(i,j,k) &&
							worldObj.getBlockId(i, j, k) != Block.grass.blockID &&
							worldObj.getBlockId(i, j, k) != Block.snow.blockID &&
							scanForBlockNearPoint(Block.lavaMoving.blockID, i, j, k, 1, 1, 1)==null &&
							scanForBlockNearPoint(Block.lavaStill.blockID, i, j, k, 1, 1, 1)==null &&
							scanForBlockNearPoint(Block.waterStill.blockID, i, j, k, 1, 1, 1)==null &&
							scanForBlockNearPoint(Block.waterMoving.blockID, i, j, k, 1, 1, 1)==null)
						return Vec3D.createVectorHelper(i, j, k);
				}
		}
		


		Vec3D closestVec = null;
		double minDistance = 999999999;

		for (int i = x - rx; i <= x + rx; i++)
			for (int j=y+ry; j >= y - ry; j--)
				for (int k = z - rz; k <= z + rz; k++) {
					// leave the floor
					//if((j<homePosY+initialDiggingYOffset && (j%5 == 0) && (i>homePosX+initialDiggingXOffset+3 || i<homePosX+initialDiggingXOffset-3 || k>homePosZ+initialDiggingZOffset+3 || k<homePosZ+initialDiggingZOffset-3)) ||
					if(j>homePosY+initialDiggingYOffset && ((i>homePosX+initialDiggingXOffset+2 || i<homePosX+initialDiggingXOffset-2 || k>homePosZ+initialDiggingZOffset+2 || k<homePosZ+initialDiggingZOffset-2)) ||
					(worldObj.getBlockId(i, j+1, k)==Block.grass.blockID && blockId == Block.dirt.blockID))
						continue;

					if (worldObj.getBlockId(i, j, k) == blockId && 
							!doesBlockHaveTorch(i,j,k) &&
							(worldObj.getBlockMetadata(i, j, k)<1 || worldObj.getBlockMetadata(i, j, k)>5) &&
							scanForBlockNearPoint(Block.lavaMoving.blockID, i, j, k, 1, 1, 1)==null &&
							scanForBlockNearPoint(Block.lavaStill.blockID, i, j, k, 1, 1, 1)==null &&
							scanForBlockNearPoint(Block.waterStill.blockID, i, j, k, 1, 1, 1)==null &&
							scanForBlockNearPoint(Block.waterMoving.blockID, i, j, k, 1, 1, 1)==null
							) {
						
						Vec3D tempVec = Vec3D.createVectorHelper(i, j, k);
						
						double distance = tempVec.distanceTo(entityVec);
						if ((closestVec == null || distance < minDistance)) {
							closestVec = Vec3D.createVectorHelper(i, j, k);
							minDistance = distance;
						}
					}
				}

		if(minDistance<2.5)
			return closestVec;
		else
			return null;
	}
	
	private boolean doesBlockHaveTorch(int x, int y, int z) {
		
		for (int i = x - 1; i <= x + 1; i++)
			for (int j=y+1; j >= y-1; j--)
				for (int k = z - 1; k <= z + 1; k++)
				{
					if(worldObj.getBlockId(i, j, k)==Block.torchWood.blockID)
						return true;
				}
		return false;
	}

	private Vec3D scanForBlockNearPointAndEntity(int blockId, int x, int y,
			int z, int rx, int ry, int rz) {
		
		Vec3D entityVec = Vec3D.createVector(posX, posY+(double)getEyeHeight(), posZ);

		Vec3D closestVec = null;
		double minDistance = 999999999;

		for (int i = x - rx; i <= x + rx; i++)
			for (int j=y+ry; j >= y - ry; j--)
				for (int k = z - rz; k <= z + rz; k++) {
					if (worldObj.getBlockId(i, j, k) == blockId) {
						
//						if(mod_MineColony.hutMiner.blockID != blockId)
//						{
//							worldObj.setBlockWithNotify(i,j,k, Block.glass.blockID);
//						}
						Vec3D tempVec = Vec3D.createVectorHelper(i, j, k);
						
						double distance = tempVec.distanceTo(entityVec);
						if ((closestVec == null || distance < minDistance)) {
							closestVec = Vec3D.createVectorHelper(i, j, k);
							minDistance = distance;
						}
					}
				}

		if(minDistance<2.6)
			return closestVec;
		else
			return null;
	}

	public void walkToTargetStraight(Vec3D vec3d) {
		if(vec3d == null)
			return;
		
		isJumping = false;
		
		if(currentAction!=actionDig)
		{
			if(destPoint!=null && destPoint.distanceTo(Vec3D.createVector(posX, posY, posZ))<2)
			{
				speed = (float) 0.5;
			}
			
			if(countBlocksAround(iPosX, iPosY, iPosZ, 1,1,1)>12)
				speed = (float) 0.5;
		}
		
		// check if there are no gravel nor sand up a head
		// zeby nie runelo na glowe
		int vx = MathHelper.floor_double(vec3d.xCoord);
		int vz = MathHelper.floor_double(vec3d.zCoord);
		if(worldObj.getBlockId(vx, iPosY+2, vz) == Block.gravel.blockID ||
		   worldObj.getBlockId(vx, iPosY+2, vz) == Block.sand.blockID)
			speed = (float) -0.05;
		else
			speed = (float) 0.5;
		
		isJumping = false;
		if(rand.nextInt(100)<25 && worldObj.getBlockId(iPosX, iPosY+2, iPosZ)==0 && (MathHelper.floor_double(vec3d.yCoord)>iPosY || stuckCount>2 || currentAction != actionDig) && (scanForBlockNearPoint(Block.dirt.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.stone.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.cobblestone.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.sand.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.grass.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(mod_MineColony.hutMiner.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.gravel.blockID, iPosX, iPosY, iPosZ, 1, 0, 1) != null))
			isJumping = true;
		
		// strafe
		Vec3D normVec = vec3d.normalize();
		int nx = (int)Math.round(normVec.xCoord);
		int nz = (int)Math.round(normVec.zCoord);
		
		moveStrafing *= 0.9;
		if(	(scanForBlockNearPoint(Block.grass.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) != null && currentAction != actionDig) ||
				(scanForBlockNearPoint(Block.wood.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) !=null && currentAction != actionDig) ||			
				(scanForBlockNearPoint(Block.dirt.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) !=null && currentAction != actionDig) ||
				(scanForBlockNearPoint(Block.stone.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) !=null && currentAction != actionDig) ||
				(scanForBlockNearPoint(Block.sand.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) !=null && currentAction != actionDig) ||
				(scanForBlockNearPoint(Block.gravel.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) !=null && currentAction != actionDig) ||
				scanForBlockNearPoint(Block.cobblestone.blockID, iPosX, iPosY+1, iPosZ, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.waterMoving.blockID, iPosX+nx, iPosY-1, iPosZ+nz, 1, 0, 1) != null ||
				scanForBlockNearPoint(Block.waterStill.blockID, iPosX+nx, iPosY-1, iPosZ+nz, 1, 0, 1) != null)
		{
			if(strafingDirChange>rand.nextInt(20)+40)
			{
				strafingDir = -strafingDir;
				strafingDirChange=0;
			}
			strafingDirChange++;
			moveStrafing = strafingDir*1;
		}
		
		// if(!isInHouse())
		// {
			if(vec3d.yCoord>iPosY && currentAction!=actionDig && worldObj.getBlockId(iPosX,iPosY-1,iPosZ) == 0)
			{
				//stuckCount = 0;
				placeBlockAt(iPosX,iPosY-1,iPosZ, Block.dirt.blockID);
			}
		//}
		
		// check if entity is moving
		Vec3D prevPos = Vec3D.createVector(prevPosX, posY, prevPosZ);
		double distanceWalked = prevPos.squareDistanceTo(posX, posY, posZ);
		if (distanceWalked>=0 && distanceWalked < 0.0001 && currentAction!=actionDig)
			stuckCount++;
//		else 
//			stuckCount = 0;
		
		double d = width; 
		
		if(vec3d != null && vec3d.squareDistanceTo(posX, posY, posZ) < d * d * 2)
		{
			return;
		}
		
		if (vec3d != null) {
			double dx = vec3d.xCoord - posX;
			double dz = vec3d.zCoord - posZ;
			double dy = vec3d.yCoord - MathHelper.floor_double(boundingBox.minY);
			float f4 = (float) ((Math.atan2(dz, dx) * 180D) / 3.1415927410125732D) - 90F;
			float f5 = f4 - rotationYaw;
			moveForward = speed;
			for (; f5 < -180F; f5 += 360F) {
			}
			for (; f5 >= 180F; f5 -= 360F) {
			}
			if (f5 > 30F) {
				f5 = 30F;
			}
			if (f5 < -30F) {
				f5 = -30F;
			}

			rotationYaw += f5;

			boolean flag1 = handleWaterMovement();
			boolean flag2 = handleLavaMovement();
			if (rand.nextFloat() < 0.8F && (flag1 || flag2)) {
				isJumping = true;
			}

		
		}
		vec3d = null;
		
		if(currentAction!=actionDig && stuckCount>10 && worldObj.getBlockId(iPosX,iPosY-1,iPosZ) == 0)
		{
			stuckCount = 0;
			placeBlockAt(iPosX,iPosY-1,iPosZ, Block.dirt.blockID);
		}
	}

	protected void equipItemSpecific(ItemStack item) {
		if(item.itemID == Item.pickaxeDiamond.shiftedIndex)
			currentToolEfficiency = 1;
		else if(item.itemID == Item.pickaxeSteel.shiftedIndex)
			currentToolEfficiency = 2;
		else if(item.itemID == Item.pickaxeGold.shiftedIndex)
			currentToolEfficiency = 4;
		else if(item.itemID == Item.pickaxeStone.shiftedIndex)
			currentToolEfficiency = 4;
		else if(item.itemID == Item.pickaxeWood.shiftedIndex)
			currentToolEfficiency = 8;
		else if(item.itemID == Item.shovelDiamond.shiftedIndex)
			currentToolEfficiency = 2;
		else if(item.itemID == Item.shovelSteel.shiftedIndex)
			currentToolEfficiency = 4;
		else if(item.itemID == Item.shovelGold.shiftedIndex)
			currentToolEfficiency = 6;
		else if(item.itemID == Item.shovelStone.shiftedIndex)
			currentToolEfficiency = 6;
		else if(item.itemID == Item.shovelWood.shiftedIndex)
			currentToolEfficiency = 8;
	}
	
	protected int getDropItemId() {
		return Item.pickaxeWood.shiftedIndex;
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
            if(toolsList[1]!=null)
            	dropItem(toolsList[1].itemID, 1);
            if(torches>0)
            	dropItem(Block.torchWood.blockID, torches);
            if(dirtBlocks>0)
            	dropItem(Block.dirt.blockID, dirtBlocks);
            if(stoneBlocks>0)
            	dropItem(Block.cobblestone.blockID, stoneBlocks);
            if(coalBlocks>0)
            	dropItem(Item.coal.shiftedIndex, coalBlocks);
            if(ironBlocks>0)
            	dropItem(Block.oreIron.blockID, ironBlocks);
            if(gravelBlocks>0)
            	dropItem(Block.gravel.blockID, gravelBlocks);
            if(goldBlocks>0)
            	dropItem(Block.oreGold.blockID, goldBlocks);
            if(diamondBlocks>0)
            	dropItem(Block.oreDiamond.blockID, diamondBlocks);
            if(redstoneBlocks>0)
            	dropItem(Block.oreRedstone.blockID, redstoneBlocks);
        }
        worldObj.func_9425_a(this, (byte)3);
	}
	
	
	public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
		super.writeEntityToNBT(nbttagcompound);
		
		nbttagcompound.setInteger("dirtBlocks", dirtBlocks);
		nbttagcompound.setInteger("stoneBlocks", stoneBlocks);
		nbttagcompound.setInteger("coalBlocks", coalBlocks);
		nbttagcompound.setInteger("ironBlocks", ironBlocks);
		nbttagcompound.setInteger("stoneBlocks", stoneBlocks);
		nbttagcompound.setInteger("sandBlocks", sandBlocks);
		nbttagcompound.setInteger("gravelBlocks", gravelBlocks);
		nbttagcompound.setInteger("goldBlocks", goldBlocks);
		nbttagcompound.setInteger("diamondBlocks", diamondBlocks);
		nbttagcompound.setInteger("redstoneBlocks", redstoneBlocks);
		nbttagcompound.setInteger("torches", torches);
		nbttagcompound.setInteger("allBlocks", redstoneBlocks);
		nbttagcompound.setInteger("limitStone", limitStone);
		nbttagcompound.setBoolean("forceGetOut", forceGetOut);
	}

	public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
		super.readEntityFromNBT(nbttagcompound);
		dirtBlocks = nbttagcompound.getInteger("dirtBlocks");
		stoneBlocks = nbttagcompound.getInteger("stoneBlocks");
		coalBlocks =  nbttagcompound.getInteger("coalBlocks");
		ironBlocks = nbttagcompound.getInteger("ironBlocks");
		sandBlocks = nbttagcompound.getInteger("sandBlocks");
		gravelBlocks =  nbttagcompound.getInteger("gravelBlocks");
		goldBlocks = nbttagcompound.getInteger("goldBlocks");
		diamondBlocks = nbttagcompound.getInteger("diamondBlocks");
		redstoneBlocks = nbttagcompound.getInteger("redstoneBlocks");

		torches =  nbttagcompound.getInteger("torches");
		allBlocks =  nbttagcompound.getInteger("allBlocks");
		limitStone = nbttagcompound.getInteger("limitStone");

		forceGetOut = nbttagcompound.getBoolean("forceGetOut");
		
	}
}
