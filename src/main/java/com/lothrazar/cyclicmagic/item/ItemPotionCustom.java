package com.lothrazar.cyclicmagic.item;

import java.util.ArrayList;
import java.util.List;

import com.lothrazar.cyclicmagic.ModMain;
import com.lothrazar.cyclicmagic.registry.PotionRegistry;
import com.lothrazar.cyclicmagic.util.Const; 
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemPotionCustom extends ItemFood  {
	private boolean							hasEffect	= false;
	private ArrayList<Potion>	potions = new ArrayList<Potion>();
	private ArrayList<Integer>	potionDurations = new ArrayList<Integer>();
	private ArrayList<Integer>	potionAmplifiers = new ArrayList<Integer>();

	public ItemPotionCustom(boolean has_effect) { 
		super(2, false);// is not edible by wolf
		hasEffect = has_effect;// true gives it enchantment shine
		this.setAlwaysEdible(); // can eat even if full hunger
		this.setCreativeTab(ModMain.TAB);
		this.setMaxStackSize(1);
	}

	public ItemPotionCustom(boolean has_effect
			,Potion potionId, int potionDuration, int potionAmplifier) {
		//super(2, false);// is not edible by wolf
		this(has_effect);

		this.addEffect(potionId, potionDuration, potionAmplifier);
	}
	
	public ItemPotionCustom addEffect(Potion potionId, int potionDuration, int potionAmplifier) {

		//currently, items pretty much just have one potion. but keeping the arrays in case that changes later
		potions.add(potionId);
		potionDurations.add(potionDuration * Const.TICKS_PER_SEC);
		potionAmplifiers.add(potionAmplifier);

		return this;// to chain together
	}

	@Override
	protected void onFoodEaten(ItemStack par1ItemStack, World world, EntityPlayer player) {
		addAllEffects(world, player);
	}

	@Override
	public EnumAction getItemUseAction(ItemStack stack)
    {
        return EnumAction.DRINK;
    }

	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
    {
        playerIn.setActiveHand(hand);
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
    }
	@Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving){
       

        if (entityLiving instanceof EntityPlayer){
            EntityPlayer entityplayer = (EntityPlayer)entityLiving;
            entityplayer.getFoodStats().addStats(this, stack);
            worldIn.playSound((EntityPlayer)null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_GENERIC_DRINK, 
            		SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            this.onFoodEaten(stack, worldIn, entityplayer);
            entityplayer.addStat(StatList.getObjectUseStats(this));
            

            if (entityplayer == null || !entityplayer.capabilities.isCreativeMode)
            {
            	stack.stackSize--;
                if (stack.stackSize <= 0)
                {
                    return new ItemStack(Items.GLASS_BOTTLE);
                }

                if (entityplayer != null)
                {
                    entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
                }
            }
            
        }

        return stack;
    }

	@Override
	public boolean hasEffect(ItemStack par1ItemStack) {
		return hasEffect; 
	}

	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack par1ItemStack) {
		if (hasEffect)
			return EnumRarity.EPIC; // dynamic text to match the two apple colours
		else
			return EnumRarity.RARE;
	}
 
	@Override
	public void addInformation(ItemStack held, EntityPlayer player, List<String> list, boolean par4) {
		for (int i = 0; i < potions.size(); i++) {
			list.add(I18n.format(potions.get(i).getName()));
		}
	}

	public void addAllEffects(World world, EntityLivingBase player) {
		for (int i = 0; i < potions.size(); i++) {
			PotionRegistry.addOrMergePotionEffect(player, new PotionEffect(potions.get(i), potionDurations.get(i), potionAmplifiers.get(i)));
		}
	} 
}
