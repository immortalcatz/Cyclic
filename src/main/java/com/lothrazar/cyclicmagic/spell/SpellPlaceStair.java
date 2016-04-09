package com.lothrazar.cyclicmagic.spell;

import com.lothrazar.cyclicmagic.util.UtilEntity;
import com.lothrazar.cyclicmagic.util.UtilPlaceBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpellPlaceStair extends BaseSpellPlace{

	public SpellPlaceStair(int id, String name){

		super.init(id, name);
		this.cost = 25;
		this.cooldown = 10;
	}
	@Override
	public boolean cast(World world, EntityPlayer player, BlockPos pos, EnumFacing side) {
		// TODO Auto-generated method stub
		
		
		BlockPos startPos = pos;
		if(startPos == null){
			startPos = player.getPosition();
		}
		else{
			//we have a valid start position that was clicked on.  was the face of a block clicked on too?
			startPos = startPos.offset(side);
		}
		IBlockState placing = Blocks.stone.getDefaultState();
		int distOrRadius = 10;
	 
		System.out.println("place line of stone"+startPos.toString()); 
		EnumFacing efacing = (player.isSneaking()) ? EnumFacing.DOWN : UtilEntity.getPlayerFacing(player);
	      //  boolean isLookingUp = (player.getLookVec().yCoord >= 0);//TODO: use this somehow? to place up/down? 
        
		
		if(world.isRemote == false)
			UtilPlaceBlocks.stairway(world, player, startPos,placing, distOrRadius );//,vertOffset
		
		
		return false;
	}
}