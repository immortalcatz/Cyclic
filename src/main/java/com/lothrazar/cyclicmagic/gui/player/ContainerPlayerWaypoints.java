package com.lothrazar.cyclicmagic.gui.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

public class ContainerPlayerWaypoints extends Container {
	private World worldObj;

	public ContainerPlayerWaypoints(InventoryPlayer par1InventoryPlayer, World par2World) {
		super();
		worldObj = par2World;
	}

	@Override
	public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
		return true;
	}

}