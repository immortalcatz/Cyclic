package com.lothrazar.cyclicmagic.module;
import com.lothrazar.cyclicmagic.util.Const;
import net.minecraftforge.common.config.Configuration;

public class KeyInventoryShiftModule extends BaseModule {
  public static boolean enableInvoKeys;//static because mod proxy looks at this
  public void syncConfig(Configuration c) {
    enableInvoKeys = c.getBoolean("KeybindInventoryShift", Const.ConfigCategory.inventory, true, "Set this to false (and restart your client) to remove the inventory shift keybindings");
  }
}
