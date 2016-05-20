package com.lothrazar.cyclicmagic.event;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import com.google.common.io.Files;
import com.lothrazar.cyclicmagic.IHasConfig;
import com.lothrazar.cyclicmagic.ModMain;
import com.lothrazar.cyclicmagic.gui.player.ButtonTabToggleCrafting;
import com.lothrazar.cyclicmagic.gui.player.ButtonTabToggleInventory;
import com.lothrazar.cyclicmagic.gui.player.ButtonTabToggleWaypoints;
import com.lothrazar.cyclicmagic.gui.player.GuiPlayerExtended;
import com.lothrazar.cyclicmagic.gui.player.InventoryPlayerExtended;
import com.lothrazar.cyclicmagic.util.Const;
import com.lothrazar.cyclicmagic.util.UtilPlayerInventoryFilestorage;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EventExtendedInventory implements IHasConfig{


	public boolean dropOnDeath;
	
	static HashSet<Integer> playerEntityIds = new HashSet<Integer>();

	@SubscribeEvent
	public void playerLoggedInEvent(PlayerLoggedInEvent event) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		if (side == Side.SERVER) {
			EventExtendedInventory.playerEntityIds.add(event.player.getEntityId());
		}
	}

	public static void syncItems(EntityPlayer player) {
		int size = InventoryPlayerExtended.ICOL*InventoryPlayerExtended.IROW;
		for (int a = 0; a < size; a++) {
			UtilPlayerInventoryFilestorage.getPlayerInventory(player).syncSlotToClients(a);
		}
	}

	@SubscribeEvent
	public void playerTick(PlayerEvent.LivingUpdateEvent event) {

		// player events
		if (event.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.getEntity();

			if (!playerEntityIds.isEmpty() && playerEntityIds.contains(player.getEntityId())) {
				syncItems(player);
				playerEntityIds.remove(player.getEntityId());
			}
		}
	}
	@SubscribeEvent
	public void playerDeath(PlayerDropsEvent event) {
		if(dropOnDeath == false){
			return;
		}
		//else drop on death is true, so do it
		Entity entity = event.getEntity();
		World world = entity.getEntityWorld();
		
		if (entity instanceof EntityPlayer && !world.isRemote && !world.getGameRules().getBoolean("keepInventory")) {
			UtilPlayerInventoryFilestorage.getPlayerInventory(event.getEntityPlayer()).dropItemsAt(event.getDrops(), event.getEntityPlayer());
		}
	}

	@SubscribeEvent
	public void playerLoad(PlayerEvent.LoadFromFile event) {
		UtilPlayerInventoryFilestorage.clearPlayerInventory(event.getEntityPlayer());

		File playerFile = getPlayerFile(ext, event.getPlayerDirectory(), event.getEntityPlayer().getDisplayNameString());
		if (!playerFile.exists()) {
			File fileNew = event.getPlayerFile(ext);
			if (fileNew.exists()) {
				try {
					Files.copy(fileNew, playerFile);
					
					
					ModMain.logger.info("Using and converting UUID Baubles savefile for " + event.getEntityPlayer().getDisplayNameString());
					
					fileNew.delete();
					File fb = event.getPlayerFile(extback);
					if (fb.exists())
						fb.delete();
				} catch (IOException e) {}
			}
		}

		UtilPlayerInventoryFilestorage.loadPlayerInventory(event.getEntityPlayer(), playerFile, getPlayerFile(extback, event.getPlayerDirectory(), event.getEntityPlayer().getDisplayNameString()));
		EventExtendedInventory.playerEntityIds.add(event.getEntityPlayer().getEntityId());
	}
	
	final String ext = "invo";
	final String extback = "backup";

	private boolean extendedInventory;

	private boolean extendedCrafting;

	public File getPlayerFile(String suffix, File playerDirectory, String playername) {
	//	if ("dat".equals(suffix))
			//throw new IllegalArgumentException("The suffix 'dat' is reserved");
		return new File(playerDirectory, "_" + playername + "." + suffix);
	}

	@SubscribeEvent
	public void playerSave(PlayerEvent.SaveToFile event) {
		UtilPlayerInventoryFilestorage.savePlayerBaubles(event.getEntityPlayer(), getPlayerFile(ext, event.getPlayerDirectory(), event.getEntityPlayer().getDisplayNameString()), getPlayerFile(extback, event.getPlayerDirectory(), event.getEntityPlayer().getDisplayNameString()));
	}
	

	@SideOnly(value = Side.CLIENT)
	@SubscribeEvent
	public void guiPostInit(GuiScreenEvent.InitGuiEvent.Post event) {
		GuiScreen gui = event.getGui();
		
		if (gui instanceof GuiInventory || gui instanceof GuiPlayerExtended
				|| gui instanceof GuiCrafting 
				|| gui instanceof GuiScreenHorseInventory) {
		

			// TODO: reflection helper?
			// gui left and top are private, so are the sizes
			// int guiLeft = ;//gui.guiLeft
			// int guiTop = ;//gui.guiTop

			int xSize = 176;
			int ySize = 166;
			int guiLeft = (gui.width - xSize) / 2;
			int guiTop = (gui.height - ySize) / 2;
			int x = 30 + guiLeft;
			int y = guiTop + 2;
			
			if(extendedInventory){
				event.getButtonList().add(new ButtonTabToggleInventory(gui, x, y));
			}
			if(extendedCrafting){
				x -= 12;
				event.getButtonList().add(new ButtonTabToggleCrafting(gui, x, y));
			}

			x -= 12;
			event.getButtonList().add(new ButtonTabToggleWaypoints( Minecraft.getMinecraft().thePlayer, x, y));
		}
	}
	
	

	@Override
	public void syncConfig(Configuration config) {


		String category = Const.ConfigCategory.inventory;  
		
		extendedCrafting = config.getBoolean("CraftingTab", category, true, "A tab for 3x3 crafting in the survival inventory");

		extendedInventory = config.getBoolean("ExtendedStorageTab", category, true, "A tab for extended item storage in the survival inventory");
		
		dropOnDeath = config.getBoolean("DropExtendedInventoryOnDeath", category, true,
				"When false, this never drops your extra inventories items on death (for the extended inventory).  If true, this will obey the keepInventory rule");
		
		
	}
}
