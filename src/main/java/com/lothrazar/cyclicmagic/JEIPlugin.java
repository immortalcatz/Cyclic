package com.lothrazar.cyclicmagic;
import com.lothrazar.cyclicmagic.gui.playerworkbench.ContainerPlayerExtWorkbench;
import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;

@mezz.jei.api.JEIPlugin
public class JEIPlugin extends BlankModPlugin {
  @Override
  public void register(IModRegistry registry) {
    // thanks to http://www.minecraftforum.net/forums/mapping-and-modding/mapping-and-modding-tutorials/1571434-tutorial-modding-with-apis
    //and of course readme on https://github.com/mezz/JustEnoughItems
    // setup [+] feature
    registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerPlayerExtWorkbench.class, VanillaRecipeCategoryUid.CRAFTING,
        6, // @param recipeSlotStart    the first slot for recipe inputs // skip over the 1 output and the 5 armor slots
        9, // @param recipeSlotCount    the number of slots for recipe inputs //3x3
        15, //@param inventorySlotStart the first slot of the available inventory (usually player inventory) =9+6
        36);//@param inventorySlotCount the number of slots of the available inventory //top right including hotbar =4*9
  }
}
