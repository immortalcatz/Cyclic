package com.lothrazar.cyclicmagic.module;
import com.lothrazar.cyclicmagic.block.BlockBucketStorage;
import com.lothrazar.cyclicmagic.block.tileentity.TileEntityBucketStorage;
import com.lothrazar.cyclicmagic.item.itemblock.ItemBlockBucket;
import com.lothrazar.cyclicmagic.registry.BlockRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BucketBlockModule extends BaseModule {
  private boolean enableBucketBlocks;
  public void onInit() {
    if (enableBucketBlocks) {
      BlockRegistry.block_storewater = new BlockBucketStorage(Items.WATER_BUCKET);
      BlockRegistry.registerBlock(BlockRegistry.block_storewater, new ItemBlockBucket(BlockRegistry.block_storewater), "block_storewater", true);
      BlockRegistry.block_storemilk = new BlockBucketStorage(Items.MILK_BUCKET);
      BlockRegistry.registerBlock(BlockRegistry.block_storemilk, new ItemBlockBucket(BlockRegistry.block_storemilk), "block_storemilk", true);
      BlockRegistry.block_storelava = new BlockBucketStorage(Items.LAVA_BUCKET);
      BlockRegistry.registerBlock(BlockRegistry.block_storelava, new ItemBlockBucket(BlockRegistry.block_storelava), "block_storelava", true);
      BlockRegistry.block_storeempty = new BlockBucketStorage(null);
      BlockRegistry.registerBlock(BlockRegistry.block_storeempty, new ItemBlockBucket(BlockRegistry.block_storeempty), "block_storeempty", false);
      BlockRegistry.block_storeempty.addRecipe();
      GameRegistry.registerTileEntity(TileEntityBucketStorage.class, "bucketstorage");
      MinecraftForge.EVENT_BUS.register(BlockRegistry.block_storeempty);
    }
  }
  @Override
  public void syncConfig(Configuration config) {
    enableBucketBlocks = config.getBoolean("BucketBlocks", Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
  }
}
