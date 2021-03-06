package com.lothrazar.cyclicmagic.block.tileentity;
import java.util.ArrayList;
import java.util.Random;
import com.lothrazar.cyclicmagic.util.UtilEntity;
import com.lothrazar.cyclicmagic.util.UtilInventory;
import com.lothrazar.cyclicmagic.util.UtilParticle;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.LootTableManager;

public class TileEntityFishing extends TileEntityBaseMachineInvo implements ITickable {
  private static final String NBT_INV = "Inventory";
  private static final String NBT_SLOT = "Slot";
  final static float SPEED = 0.007F;//// bigger == faster
  public static final int RODSLOT = 1;
  public static final int FISHSLOTS = 15;
  private int toolSlot = 0;
  public ArrayList<Block> waterBoth = new ArrayList<Block>();
  private ItemStack[] inv;
  public TileEntityFishing() {
    inv = new ItemStack[RODSLOT + FISHSLOTS];
    waterBoth.add(Blocks.FLOWING_WATER);
    waterBoth.add(Blocks.WATER);
  }
  public boolean isValidPosition() {
    return waterBoth.contains(worldObj.getBlockState(pos.down()).getBlock()) &&
        waterBoth.contains(worldObj.getBlockState(pos.down(2)).getBlock()) &&
        waterBoth.contains(worldObj.getBlockState(pos.north()).getBlock()) &&
        waterBoth.contains(worldObj.getBlockState(pos.east()).getBlock()) &&
        waterBoth.contains(worldObj.getBlockState(pos.west()).getBlock()) &&
        waterBoth.contains(worldObj.getBlockState(pos.south()).getBlock());
  }
  public boolean isEquipmentValid() {
    return inv[toolSlot] != null;
  }
  @Override
  public void update() {
    Random rand = worldObj.rand;
    //make sure surrounded by water
    if (rand.nextDouble() < SPEED &&
        isValidPosition() && isEquipmentValid() &&
        this.worldObj instanceof WorldServer && this.worldObj != null) {
      LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) this.worldObj);
      float luck = (float) EnchantmentHelper.getEnchantmentLevel(Enchantments.LUCK_OF_THE_SEA, this.inv[0]);
      lootcontext$builder.withLuck(luck);
      //      java.lang.NullPointerException: Ticking block entity    at com.lothrazar.cyclicmagic.block.tileentity.TileEntityFishing.func_73660_a(TileEntityFishing.java:58)
      LootTableManager loot = this.worldObj.getLootTableManager();
      if (loot == null) { return; }
      LootTable table = loot.getLootTableFromLocation(LootTableList.GAMEPLAY_FISHING);
      if (table == null) { return; }
      LootContext context = lootcontext$builder.build();
      if (context == null) { return; }
      for (ItemStack itemstack : table.generateLootForPools(this.worldObj.rand, context)) {
        UtilParticle.spawnParticle(worldObj, EnumParticleTypes.WATER_WAKE, pos.up());
        inv[toolSlot].attemptDamageItem(1, worldObj.rand);
        if (inv[toolSlot].getItemDamage() >= inv[toolSlot].getMaxDamage()) {
          inv[toolSlot] = null;
        }
        for (int i = RODSLOT; i <= FISHSLOTS; i++) {
          if (itemstack != null && itemstack.stackSize != 0) {
            itemstack = tryMergeStackIntoSlot(itemstack, i);
          }
        }
        if (itemstack != null && itemstack.stackSize != 0) { //FULL
          UtilEntity.dropItemStackInWorld(worldObj, this.pos.down(), itemstack);
        }
      }
    }
  }
  private ItemStack tryMergeStackIntoSlot(ItemStack held, int furnaceSlot) {
    ItemStack current = this.getStackInSlot(furnaceSlot);
    boolean success = false;
    if (current == null) {
      this.setInventorySlotContents(furnaceSlot, held);
      held = null;
      success = true;
    }
    else if (held.isItemEqual(current)) {
      success = true;
      UtilInventory.mergeItemsBetweenStacks(held, current);
    }
    if (success) {
      if (held != null && held.stackSize == 0) {// so now we just fix if something is size zero
        held = null;
      }
      this.markDirty();
    }
    return held;
  }
  @Override
  public int getSizeInventory() {
    return inv.length;
  }
  @Override
  public ItemStack getStackInSlot(int index) {
    return inv[index];
  }
  @Override
  public ItemStack decrStackSize(int index, int count) {
    ItemStack stack = getStackInSlot(index);
    if (stack != null) {
      if (stack.stackSize <= count) {
        setInventorySlotContents(index, null);
      }
      else {
        stack = stack.splitStack(count);
        if (stack.stackSize == 0) {
          setInventorySlotContents(index, null);
        }
      }
    }
    return stack;
  }
  @Override
  public ItemStack removeStackFromSlot(int index) {
    ItemStack stack = getStackInSlot(index);
    if (stack != null) {
      setInventorySlotContents(index, null);
    }
    return stack;
  }
  @Override
  public void setInventorySlotContents(int index, ItemStack stack) {
    inv[index] = stack;
    if (stack != null && stack.stackSize > getInventoryStackLimit()) {
      stack.stackSize = getInventoryStackLimit();
    }
  }
  @Override
  public int[] getSlotsForFace(EnumFacing side) {
    if (side == EnumFacing.UP) { return new int[] { 0 }; }
    return new int[0];
  }
  @Override
  public void readFromNBT(NBTTagCompound tagCompound) {
    super.readFromNBT(tagCompound);
    NBTTagList tagList = tagCompound.getTagList(NBT_INV, 10);
    for (int i = 0; i < tagList.tagCount(); i++) {
      NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
      byte slot = tag.getByte(NBT_SLOT);
      if (slot >= 0 && slot < inv.length) {
        inv[slot] = ItemStack.loadItemStackFromNBT(tag);
      }
    }
  }
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {
    NBTTagList itemList = new NBTTagList();
    for (int i = 0; i < inv.length; i++) {
      ItemStack stack = inv[i];
      if (stack != null) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte(NBT_SLOT, (byte) i);
        stack.writeToNBT(tag);
        itemList.appendTag(tag);
      }
    }
    tagCompound.setTag(NBT_INV, itemList);
    return super.writeToNBT(tagCompound);
  }
}
