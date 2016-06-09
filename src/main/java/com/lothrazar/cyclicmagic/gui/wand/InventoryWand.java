package com.lothrazar.cyclicmagic.gui.wand;

import java.util.ArrayList;
import java.util.Random;

import com.lothrazar.cyclicmagic.item.ItemCyclicWand;
import com.lothrazar.cyclicmagic.util.UtilSpellCaster;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.Constants;

public class InventoryWand implements IInventory {

	public static final int	INV_SIZE	= 18;		
	private ItemStack[]			inventory	= new ItemStack[INV_SIZE];
	private final ItemStack	internalWand;

	private EntityPlayer		thePlayer;

	public EntityPlayer getPlayer() {
		return thePlayer;
	}

	public InventoryWand(EntityPlayer player, ItemStack wand) {

		internalWand = wand;

		inventory = readFromNBT(wand);

		thePlayer = player;
	}

	@Override
	public String getName() {
		return "Wand Inventory";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return INV_SIZE;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		return inventory[index];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);

		if (stack != null) {
			if (stack.stackSize > amount) {
				stack = stack.splitStack(amount);
				// Don't forget this line or your inventory will not be saved!

				markDirty();
			}
			else {
				// this method also calls onInventoryChanged, so we don't need
				// to call it again
				setInventorySlotContents(slot, null);
			}
		}

		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {

		// used to be 'getStackInSlotOnClosing'
		ItemStack stack = getStackInSlot(index);
		setInventorySlotContents(index, null);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}

		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {

		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				inventory[i] = null;
			}
		}

		// set any empty item stacks (red zeroes) to empty
		for (int i = 0; i < thePlayer.inventory.getSizeInventory(); i++) {

			if (thePlayer.inventory.getStackInSlot(i) != null && thePlayer.inventory.getStackInSlot(i).stackSize == 0) {

				thePlayer.inventory.setInventorySlotContents(i, null);
			}
		}

		writeToNBT(internalWand, inventory);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return UtilSpellCaster.getPlayerWandIfHeld(player) != null;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {

		// only placeable blocks, not any old item
		return !(stack.getItem() instanceof ItemCyclicWand) && Block.getBlockFromItem(stack.getItem()) != null;
	}

	/************** public static ******************/

	public static ItemStack[] readFromNBT(ItemStack stack) {

		ItemStack[] inv = new ItemStack[INV_SIZE];

		if (stack == null || (stack.getItem() instanceof ItemCyclicWand) == false) { return inv; }

		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagList items = stack.getTagCompound().getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < items.tagCount(); ++i) {
			// 1.7.2+ change to items.getCompoundTagAt(i)

			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if (slot >= 0 && slot < INV_SIZE) {
				inv[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}

		return inv;
	}

	public static void writeToNBT(ItemStack wandStack, ItemStack[] theInventory) {

		NBTTagCompound tagcompound = wandStack.getTagCompound();
		// Create a new NBT Tag List to store itemstacks as NBT Tags
		NBTTagList items = new NBTTagList();
		ItemStack stack;
		for (int i = 0; i < theInventory.length; ++i) {
			stack = theInventory[i];
			if (stack != null && stack.stackSize == 0) {
				stack = null;
			}

			if (stack != null) {
				// Make a new NBT Tag Compound to write the itemstack and slot
				// index to
				NBTTagCompound itemTags = new NBTTagCompound();
				itemTags.setInteger("Slot", i);
				// Writes the itemstack in slot(i) to the Tag Compound we just
				// made
				stack.writeToNBT(itemTags);

				// add the tag compound to our tag list
				items.appendTag(itemTags);
			}
		}
		// Add the TagList to the ItemStack's Tag Compound with the name
		// "ItemInventory"
		tagcompound.setTag("ItemInventory", items);
	}

	public static void decrementSlot(ItemStack wand, int itemSlot) {

		ItemStack[] invv = InventoryWand.readFromNBT(wand);
		invv[itemSlot].stackSize--;

		if (invv[itemSlot].stackSize == 0) {
			invv[itemSlot] = null;
		}
		InventoryWand.writeToNBT(wand, invv);
	}

	public static ItemStack getFromSlot(ItemStack wand, int i) {

		if (i < 0 || i >= InventoryWand.INV_SIZE) { return null; }
		return InventoryWand.readFromNBT(wand)[i];
	}

	public static IBlockState getToPlaceFromSlot(ItemStack wand, int i) {

		ItemStack toPlace = getFromSlot(wand, i);

		if (toPlace != null && toPlace.getItem() != null && Block.getBlockFromItem(toPlace.getItem()) != null) {

			return Block.getBlockFromItem(toPlace.getItem()).getStateFromMeta(toPlace.getMetadata()); 
		}
		return null;
	}

	public static int calculateSlotCurrent(ItemStack wand, IBlockState matchState) {

		int itemSlot = -1;

		int buildType = ItemCyclicWand.BuildType.get(wand);
		ItemStack[] inv = InventoryWand.readFromNBT(wand);
		ArrayList<Integer> slotNonEmpty = new ArrayList<Integer>();

		for (int i = 0; i < inv.length; i++) {

			if (inv[i] != null && inv[i].getItem() != null && Block.getBlockFromItem(inv[i].getItem()) != null) {
				slotNonEmpty.add(i);
			}
		}

		// brute forcing it. there is surely a more elegant way in each branch
		if (buildType == ItemCyclicWand.BuildType.FIRST.ordinal()) {

			for (int i : slotNonEmpty) {
				if (inv[i] != null) {

					itemSlot = i;
					break;
				}
			}
		}
		else if (buildType == ItemCyclicWand.BuildType.ROTATE.ordinal()) {

			int rot = ItemCyclicWand.InventoryRotation.get(wand);

			int test = InventoryWand.INV_SIZE + 2;// like aninfloop but with a max
			// in case we have gaps, maybe its [0,1,4] have items, so cycle through
			for (int i = 0; i < test; i++) {

				rot++;// first, move one up from last position
				if (rot < 0 || rot >= inv.length) {// JIT validation
					rot = 0;
				}
				if (inv[rot] != null) {
					itemSlot = rot;

					if (rot >= inv.length) {
						rot = 0;
					}

					ItemCyclicWand.InventoryRotation.set(wand, rot);

					break;
				}
				// otherwise skip over empty slot one and keep looking
			}
		}
		else if (buildType == ItemCyclicWand.BuildType.RANDOM.ordinal()) {

			Random rand = new Random();
			itemSlot = slotNonEmpty.get(rand.nextInt(slotNonEmpty.size()));
		}
		else if (buildType == ItemCyclicWand.BuildType.MATCH.ordinal() && matchState != null) {

			// damage dropped meaning what it really is , not item version
			int meta = matchState.getBlock().damageDropped(matchState);

			ItemStack compareStack = new ItemStack(matchState.getBlock(), 1, meta);
			ItemStack curr;

			// for (int i = 0; i < player.inventory.getSizeInventory(); ++i) {
			for (int i : slotNonEmpty) {
				curr = inv[i];// player.inventory.getStackInSlot(i);

				if (curr != null && curr.isItemEqual(compareStack)) {

					itemSlot = i;
					break;
				}
			}

			// could be null in this one
		}

		return itemSlot;
	}

	/******** required unmodified ****/

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public int getField(int id) {

		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {

		return 0;
	}

	@Override
	public void clear() {

	}
}
