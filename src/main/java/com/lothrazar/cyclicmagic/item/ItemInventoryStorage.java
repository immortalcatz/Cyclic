package com.lothrazar.cyclicmagic.item;

import java.util.List;

import com.lothrazar.cyclicmagic.IHasConfig;
import com.lothrazar.cyclicmagic.IHasRecipe;
import com.lothrazar.cyclicmagic.ModMain;
import com.lothrazar.cyclicmagic.gui.ModGuiHandler;
import com.lothrazar.cyclicmagic.util.Const;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemInventoryStorage extends BaseItem implements IHasRecipe, IHasConfig{

	public static boolean enabled;

	public ItemInventoryStorage() {
		this.setMaxStackSize(1);
	}
	
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return false;
	}
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {

		return 1; // Without this method, your inventory will NOT work!!!
	}
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {

		super.addInformation(stack, playerIn, tooltip, advanced);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World world, EntityPlayer player, EnumHand hand) {

		if(!world.isRemote){
			BlockPos pos = player.getPosition();
			int x = pos.getX(), y = pos.getY(), z = pos.getZ();
			player.openGui(ModMain.instance, ModGuiHandler.GUI_INDEX_STORAGE, world, x, y, z);
		}
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@Override
	public void addRecipe() {
		GameRegistry.addRecipe(new ItemStack(this),"lsl","ldl","lrl", 
				'l',Items.leather,
				's',Items.string,
				'r',Items.redstone,
				'd',Blocks.diamond_block
				);
		
	}

	@Override
	public void syncConfig(Configuration config) {

		Property prop = config.get(Const.ConfigCategory.items, "StorageBag", true, "A simple storage backpack");
		prop.setRequiresMcRestart(true);
		enabled = prop.getBoolean();
	}
}
