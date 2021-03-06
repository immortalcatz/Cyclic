package com.lothrazar.cyclicmagic.module;
import com.lothrazar.cyclicmagic.item.ItemChestSack;
import com.lothrazar.cyclicmagic.item.ItemChestSackEmpty;
import com.lothrazar.cyclicmagic.registry.ItemRegistry;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraftforge.common.config.Configuration;

public class ChestSackModule extends BaseModule {
  private boolean enableChestSack;
  @Override
  public void onInit() {
    if (enableChestSack) {
      ItemRegistry.chest_sack_empty = new ItemChestSackEmpty();
      ItemRegistry.chest_sack = new ItemChestSack();
      ItemRegistry.chest_sack.setHidden();
      ItemRegistry.addItem(ItemRegistry.chest_sack, "chest_sack");
      ItemRegistry.addItem(ItemRegistry.chest_sack_empty, "chest_sack_empty");
    }
  }
  @Override
  public void syncConfig(Configuration config) {
    enableChestSack = config.getBoolean("ChestSack", Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
  }
}
