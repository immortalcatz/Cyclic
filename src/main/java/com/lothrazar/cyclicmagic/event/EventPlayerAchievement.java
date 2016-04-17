package com.lothrazar.cyclicmagic.event;

import com.lothrazar.cyclicmagic.util.UtilExperience;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.AchievementEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventPlayerAchievement {

	@SubscribeEvent
	public void onAchievementEvent(AchievementEvent event){
		
		EntityPlayer player = event.getEntityPlayer();
		World world = player.worldObj;
		BlockPos pos = player.getPosition();
		//event.getAchievement()
		
		
		EntityXPOrb orb = new EntityXPOrb(world , pos.getX(),pos.getY(),pos.getZ(), 50);
		
		//UtilExperience.drainExp(event.getEntityPlayer(), 5.0F);
		
	}
}
