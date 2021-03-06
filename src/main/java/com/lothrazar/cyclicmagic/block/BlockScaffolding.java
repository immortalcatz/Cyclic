package com.lothrazar.cyclicmagic.block;
import java.util.List;
import java.util.Random;
import com.lothrazar.cyclicmagic.IHasRecipe;
import com.lothrazar.cyclicmagic.registry.SoundRegistry;
import com.lothrazar.cyclicmagic.util.UtilChat;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;// EnumWorldBlockLayer;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * imported from https://github.com/PrinceOfAmber/SamsPowerups/blob/
 * 5083ec601e34bbe045d9a3d0ca091e3d44af562f/src/main/java/com/lothrazar/
 * samscontent/BlockRegistry.j a v a
 * 
 * @author Lothrazar
 *
 */
public class BlockScaffolding extends Block implements IHasRecipe {
  public static final String name = "block_fragile";
  public BlockScaffolding() {
    super(Material.WOOD);
    this.setTickRandomly(true);
    this.setHardness(0F);
    this.setResistance(0F);
    SoundEvent crackle = SoundRegistry.crackle;
    this.setSoundType(new SoundType(1.0F, 1.0F, crackle, crackle, crackle, crackle, crackle));
  }
  @Override
  public boolean isOpaqueCube(IBlockState state) {
    return false; // http://greyminecraftcoder.blogspot.ca/2014/12/transparent-blocks-18.html
  }
  @SideOnly(Side.CLIENT)
  public BlockRenderLayer getBlockLayer() {
    return BlockRenderLayer.TRANSLUCENT;
  }
  @Override
  public void updateTick(World worldObj, BlockPos pos, IBlockState state, Random rand) {
    worldObj.destroyBlock(pos, true);
  }
  public int tickRate(World worldIn) {
    return 200;
  }
  @Override
  public void addRecipe() {
    GameRegistry.addRecipe(new ItemStack(this, 12), "s s", " s ", "s s", 's', new ItemStack(Items.STICK));
  }
  @SideOnly(Side.CLIENT)
  public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    String s = UtilChat.lang("tile.block_fragile.tooltip");
    tooltip.add(s);
  }
}