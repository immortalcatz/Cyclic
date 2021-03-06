package com.lothrazar.cyclicmagic.block.tileentity;
import java.lang.ref.WeakReference;
import java.util.UUID;
import com.lothrazar.cyclicmagic.ModMain;
import com.lothrazar.cyclicmagic.util.UtilFakePlayer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

public class TileMachineUser extends TileEntityBaseMachineInvo {
  //vazkii wanted simple block breaker and block placer. already have the BlockBuilder for placing :D
  //of course this isnt standalone and hes probably found some other mod by now but doing it anyway https://twitter.com/Vazkii/status/767569090483552256
  // fake player idea ??? https://gitlab.prok.pw/Mirrors/minecraftforge/commit/f6ca556a380440ededce567f719d7a3301676ed0
  public static int maxHeight = 10;
  public static int TIMER_FULL = 80;
  private ItemStack[] inv;
  private int[] hopperInput = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };// all slots for all faces
  private static final String NBT_INV = "Inventory";
  private static final String NBT_SLOT = "Slot";
  private static final String NBT_TIMER = "Timer";
  private int speed = 1;
  private WeakReference<FakePlayer> fakePlayer;
  private UUID uuid;
  private int timer;
  public static enum Fields {
    TIMER, SPEED
  }
  public TileMachineUser() {
    inv = new ItemStack[9];
    timer = TIMER_FULL;
    speed = 1;
  }
  @Override
  public void update() {
    this.shiftAllUp();
    int toolSlot = 0;
    if (this.isPowered()) {
      this.spawnParticlesAbove();
    }
    if (worldObj instanceof WorldServer) {
      if (fakePlayer == null) {
        fakePlayer = UtilFakePlayer.initFakePlayer((WorldServer) worldObj);
        if (fakePlayer == null) {
          ModMain.logger.warn("Warning: Fake player failed to init ");
          return;
        }
      }
      if (uuid == null) {
        uuid = UUID.randomUUID();
        IBlockState state = worldObj.getBlockState(this.pos);
        worldObj.notifyBlockUpdate(pos, state, state, 3);
      }
      ItemStack maybeTool = inv[toolSlot];
      if (maybeTool == null) {
        fakePlayer.get().setHeldItem(EnumHand.MAIN_HAND, null);
      }
      else {
        if (maybeTool.stackSize == 0) {
          inv[toolSlot] = null;
        }
        if (!maybeTool.equals(fakePlayer.get().getHeldItem(EnumHand.MAIN_HAND))) {
          fakePlayer.get().setHeldItem(EnumHand.MAIN_HAND, maybeTool);
        }
        //else already equipped
      }
      BlockPos targetPos = pos.offset(this.getCurrentFacing()); //not sure if this is needed
      if (worldObj.isAirBlock(targetPos)) {
        targetPos = targetPos.down();
      }
      ItemStack stack = getStackInSlot(0);
      if (stack == null) {
        timer = TIMER_FULL;// reset just like you would in a
        // furnace
        return;
      }
      if (this.isPowered()) {
        timer -= this.getSpeed();
        if (timer <= 0) {
          timer = 0;
        }
        if (timer == 0) {
          //          System.out.println("GOOO" + fakePlayer.get().getHeldItemMainhand());
          fakePlayer.get().interactionManager.processRightClickBlock(fakePlayer.get(), worldObj, fakePlayer.get().getHeldItemMainhand(), EnumHand.MAIN_HAND, targetPos, EnumFacing.UP, .5F, .5F, .5F);
          timer = TIMER_FULL;
        }
      }
      else {
        timer = 0;
      }
    }
  }
  final int RADIUS = 4;//center plus 4 in each direction = 9x9
  private static final String NBTPLAYERID = "uuid";
  //  private static final String NBTTARGET = "target";
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    compound.setInteger(NBT_TIMER, timer);
    if (uuid != null) {
      compound.setString(NBTPLAYERID, uuid.toString());
    }
    //    if (targetPos != null) {
    //      compound.setIntArray(NBTTARGET, new int[] { targetPos.getX(), targetPos.getY(), targetPos.getZ() });
    //    }
    compound.setInteger("h", speed);
    //invo stuff
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
    compound.setTag(NBT_INV, itemList);
    return super.writeToNBT(compound);
  }
  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
    timer = compound.getInteger(NBT_TIMER);
    if (compound.hasKey(NBTPLAYERID)) {
      uuid = UUID.fromString(compound.getString(NBTPLAYERID));
    }
    //    if (compound.hasKey(NBTTARGET)) {
    //      int[] coords = compound.getIntArray(NBTTARGET);
    //      if (coords.length >= 3) {
    //        targetPos = new BlockPos(coords[0], coords[1], coords[2]);
    //      }
    //    }
    speed = compound.getInteger("h");
    //invo stuff
    NBTTagList tagList = compound.getTagList(NBT_INV, 10);
    for (int i = 0; i < tagList.tagCount(); i++) {
      NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
      byte slot = tag.getByte(NBT_SLOT);
      if (slot >= 0 && slot < inv.length) {
        inv[slot] = ItemStack.loadItemStackFromNBT(tag);
      }
    }
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
    return hopperInput;
  }
  @Override
  public int getField(int id) {
    switch (Fields.values()[id]) {
    case SPEED:
      return getSpeed();
    case TIMER:
      return getTimer();
    default:
      break;
    }
    return 0;
  }
  @Override
  public void setField(int id, int value) {
    switch (Fields.values()[id]) {
    case SPEED:
      if (value > maxHeight) {
        value = maxHeight;
      }
      setSpeed(value);
      break;
    case TIMER:
      timer = value;
      if (timer > TIMER_FULL) {
        timer = TIMER_FULL;
      }
      if (timer < 0) {
        timer = 0;
      }
      break;
    default:
      break;
    }
  }
  public int getSpeed() {
    return this.speed;//this.getField(Fields.HEIGHT.ordinal());
  }
  public void setSpeed(int val) {
    this.speed = val;
  }
  @Override
  public boolean receiveClientEvent(int id, int value) {
    if (id >= 0 && id < this.getFieldCount()) {
      this.setField(id, value);
      return true;
    }
    else
      return super.receiveClientEvent(id, value);
  }
  @Override
  public int getFieldCount() {
    return Fields.values().length;
  }
  @Override
  public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
    // Extracts data from a packet (S35PacketUpdateTileEntity) that was sent
    // from the server. Called on client only.
    this.readFromNBT(pkt.getNbtCompound());
    super.onDataPacket(net, pkt);
  }
  public int getTimer() {
    return timer;
  }
}
