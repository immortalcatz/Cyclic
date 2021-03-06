package com.lothrazar.cyclicmagic.module;
import com.lothrazar.cyclicmagic.item.ItemEnderBook;
import com.lothrazar.cyclicmagic.registry.ItemRegistry;
import com.lothrazar.cyclicmagic.registry.LootTableRegistry;
import com.lothrazar.cyclicmagic.registry.LootTableRegistry.ChestType;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraftforge.common.config.Configuration;

public class EnderBookModule extends BaseModule {
  private boolean enableEnderBook;
  @Override
  public void onInit() {
    if (enableEnderBook) {
      ItemEnderBook book_ender = new ItemEnderBook();
      ItemRegistry.addItem(book_ender, "book_ender");
      LootTableRegistry.registerLoot(book_ender, ChestType.ENDCITY, 10);
      LootTableRegistry.registerLoot(book_ender, ChestType.GENERIC, 1);
    }
  }
  @Override
  public void syncConfig(Configuration config) {
    enableEnderBook = config.getBoolean("EnderBook", Const.ConfigCategory.content, true, Const.ConfigCategory.contentDefaultText);
  }
}
