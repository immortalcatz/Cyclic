package com.lothrazar.cyclicmagic.util;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

public class UtilNBT {

	public static String posToStringCSV(BlockPos position) {

		return position.getX() + "," + position.getY() + "," + position.getZ();
	}

	public static void setItemStackNBT(ItemStack item, String prop, int value) {

		if (item.getTagCompound() == null) {
			item.setTagCompound(new NBTTagCompound());
		}
		item.getTagCompound().setInteger(prop, value);
	}

	public static BlockPos stringCSVToBlockPos(String csv) {

		String[] spl = csv.split(",");
		// on server i got java.lang.ClassCastException: java.lang.String cannot
		// be cast to java.lang.Integer
		// ?? is it from this?
		BlockPos p = null;
		try {
			p = new BlockPos(Integer.parseInt(spl[0]), Integer.parseInt(spl[1]), Integer.parseInt(spl[2]));
		} catch (java.lang.ClassCastException e) {
			// System.out.println(e.getMessage());
		}
		return p;
	}
	public static void setPlayerString(EntityPlayer player, String prop, String data) {
		player.getEntityData().setString(prop, data);
	}
	public static void setPlayerInteger(EntityPlayer player, String prop, int data) {
		player.getEntityData().setInteger(prop, data);
	}
	public static int getPlayerInteger(EntityPlayer player, String prop) {
		return player.getEntityData().getInteger(prop);
	}
	public static String getPlayerString(EntityPlayer player, String prop) {
		return player.getEntityData().getString(prop);
	}
	public static void incrementPlayerIntegerNBT(EntityPlayer player, String prop, int inc) {

		int prev = player.getEntityData().getInteger(prop);
		prev += inc;
		player.getEntityData().setInteger(prop, prev);
	}

	public static void writeTagsToInventory(IInventory invo, NBTTagCompound tags, String key) {

		NBTTagList items = tags.getTagList(key, tags.getId());
		ItemStack stack;
		int slot;
		for (int i = 0; i < items.tagCount(); ++i) {
			// tagAt(int) has changed to getCompoundTagAt(int)
			NBTTagCompound item = items.getCompoundTagAt(i);

			stack = ItemStack.loadItemStackFromNBT(item);

			slot = item.getInteger("slot");
			// list.add(ItemStack.loadItemStackFromNBT(item));

			invo.setInventorySlotContents(slot, stack);
		}
	}

	public static NBTTagCompound writeInventoryToTag(IInventory invo, NBTTagCompound returnTag, String key) {

		ItemStack chestItem;
		NBTTagCompound itemTag;

		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < invo.getSizeInventory(); i++) {
			// zeroes to avoid nulls, and signify nothing goes there

			chestItem = invo.getStackInSlot(i);

			if (chestItem == null || chestItem.stackSize == 0) {
				continue;
			}// not an error; empty chest slot

			itemTag = chestItem.writeToNBT(new NBTTagCompound());
			itemTag.setInteger("slot", i);

			nbttaglist.appendTag(itemTag);

			// its either in the bag, or dropped on the player
			invo.setInventorySlotContents(i, null);
		}

		returnTag.setTag(key, nbttaglist);

		return returnTag;
	}

	public static NBTTagCompound writeInventoryToNewTag(IInventory invo, String key) {
		return writeInventoryToTag(invo, new NBTTagCompound(), key);
	}

	public static int countItemsFromNBT(NBTTagCompound tags, String key) {
		NBTTagList items = tags.getTagList(key, tags.getId());

		return items.tagCount();
	}

	public static ArrayList<ItemStack> readItemsFromNBT(NBTTagCompound tags, String key) {
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();

		NBTTagList items = tags.getTagList(key, tags.getId());

		for (int i = 0; i < items.tagCount(); ++i) {
			// tagAt(int) has changed to getCompoundTagAt(int)
			NBTTagCompound item = items.getCompoundTagAt(i);

			list.add(ItemStack.loadItemStackFromNBT(item));
		}

		return list;
	}

	public static ItemStack buildEnchantedNametag(String customNameTag) {
		// build multi-level NBT tag so it matches a freshly enchanted one

		ItemStack nameTag = new ItemStack(Items.name_tag, 1);

		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagCompound display = new NBTTagCompound();
		display.setString("Name", customNameTag);// NOT "CustomName" implied by
		                                         // commandblocks/google
		nbt.setTag("display", display);
		nbt.setInteger("RepairCost", 1);

		nameTag.setTagCompound(nbt);// put the data into the item stack

		return nameTag;
	}

	public static ItemStack buildNamedPlayerSkull(EntityPlayer player) {
		return buildNamedPlayerSkull(player.getDisplayNameString());
	}

	public static ItemStack buildNamedPlayerSkull(String displayNameString) {
		ItemStack skull = new ItemStack(Items.skull, 1, Const.skull_player);

		if (skull.getTagCompound() == null) {
			skull.setTagCompound(new NBTTagCompound());
		}

		skull.getTagCompound().setString(Const.SkullOwner, displayNameString);

		return skull;
	}

	public static NBTTagCompound getTagCompoundNotNull(ItemStack held) {

		if (held.getTagCompound() == null) {
			held.setTagCompound(new NBTTagCompound());
		}

		return held.getTagCompound();
	}
}
