package com.lothrazar.cyclicmagic.block.tileentity;

import java.util.ArrayList;
import java.util.List;

import com.lothrazar.cyclicmagic.block.BlockBuilder;
import com.lothrazar.cyclicmagic.util.UtilNBT;
import com.lothrazar.cyclicmagic.util.UtilParticle;
import com.lothrazar.cyclicmagic.util.UtilPlaceBlocks;
import com.lothrazar.cyclicmagic.util.UtilSearchWorld;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;// net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

public class TileEntityBuilder extends TileEntity implements IInventory, ITickable, ISidedInventory {

	private ItemStack[] inv = new ItemStack[9];
	private int	timer;
	private int	shapeIndex = 0;
//	private BuildType currentType;
	private int currentType;
	private BlockPos nextPos;
	private List<BlockPos> shape = new ArrayList<BlockPos>();
	public static final int	TIMER_FULL = 100;
	public static final int	MAXRANGE = 16;
 
	private static final String	NBT_INV					= "Inventory";
	private static final String	NBT_SLOT				= "Slot";
	private static final String	NBT_TIMER				= "Timer";
	private static final String	NBT_NEXTPOS				= "Pos";
	private static final String	NBT_BUILDTYPE			= "build";
	private static final String NBT_SHAPE = "shape";
	private static final String NBT_SHAPEINDEX = "shapeindex";

	public TileEntityBuilder() {
	}
	
	public void setBuildType(BuildType buildType) {
		this.currentType = buildType.ordinal();
		
		switch(buildType){
		case CIRCLE:
			this.shape = UtilPlaceBlocks.circle(this.pos, 10);
			this.shapeIndex = 0;
			this.nextPos = this.shape.get(shapeIndex);
			break;
		case FACING:
			this.shape = new ArrayList<BlockPos>();
			this.incrementPosition();
			break;
		case SQUARE:
			this.shape = UtilPlaceBlocks.squareHorizontal(this.pos, 5);
			this.shapeIndex = 0;
			this.nextPos = this.shape.get(shapeIndex);
			break;
		case UP:
			this.shape = new ArrayList<BlockPos>();
			this.incrementPosition();
			break;
		default:
			break;
		}
		
		
//		this.markDirty();
	}
	public int getBuildType(){
		return this.currentType;
	}
	public BuildType getBuildTypeEnum(){
		return BuildType.values()[this.currentType];
	}
	@Override
	public boolean hasCustomName() {

		return false;
	}
	
	@Override
    public void markDirty(){
		super.markDirty();
		
		if(this.worldObj != null && this.pos != null)
			this.worldObj.markBlockRangeForRenderUpdate(this.pos, this.pos.up());
	}
	

	//
	@Override
	public ITextComponent getDisplayName() {

		return null;
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
	public void setInventorySlotContents(int index, ItemStack stack) {

		inv[index] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return true; 
	}

	@Override
	public void openInventory(EntityPlayer player) {

	}

	@Override
	public void closeInventory(EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return Block.getBlockFromItem(stack.getItem()) != null;
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

	@Override
	public void readFromNBT(NBTTagCompound tagCompound) {

		super.readFromNBT(tagCompound);

		timer = tagCompound.getInteger(NBT_TIMER);
		shapeIndex = tagCompound.getInteger(NBT_SHAPEINDEX);
		
		nextPos = UtilNBT.stringCSVToBlockPos(tagCompound.getString(NBT_NEXTPOS));// = tagCompound.getInteger(NBT_TIMER);
		if(nextPos == null || (nextPos.getX() == 0 && nextPos.getY()==0 && nextPos.getZ()==0)){
			nextPos = this.pos;//fallback if it fails
		}
		
		this.shape = new ArrayList<BlockPos>();
		NBTTagList sh = tagCompound.getTagList(NBT_SHAPE, 10);
		for (int i = 0; i < sh.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) sh.getCompoundTagAt(i);
			BlockPos pos = UtilNBT.stringCSVToBlockPos(tag.getString("shapepos"));
			this.shape.add(pos);
		}
//		for(BlockPos p : this.shape){
//
//			NBTTagCompound tag = new NBTTagCompound();
//			tag.setString("shapepos", UtilNBT.posToStringCSV(p));
//			sh.appendTag(tag);
//		}
//		tagCompound.setTag(NBT_SHAPE, sh);
		
		
 
		NBTTagList tagList = tagCompound.getTagList(NBT_INV, 10);
		for (int i = 0; i < tagList.tagCount(); i++) {
			NBTTagCompound tag = (NBTTagCompound) tagList.getCompoundTagAt(i);
			byte slot = tag.getByte(NBT_SLOT);
			if (slot >= 0 && slot < inv.length) {
				inv[slot] = ItemStack.loadItemStackFromNBT(tag);
			}
		}
		

		this.currentType = tagCompound.getInteger(NBT_BUILDTYPE);
		this.setBuildType(BuildType.values()[this.currentType]);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tagCompound) {

		tagCompound.setInteger(NBT_TIMER, timer);
		tagCompound.setInteger(NBT_SHAPEINDEX, this.shapeIndex);

		if(nextPos == null || (nextPos.getX() == 0 && nextPos.getY()==0 && nextPos.getZ()==0)){
			nextPos = this.pos;//fallback if it fails
		}
		tagCompound.setString(NBT_NEXTPOS, UtilNBT.posToStringCSV(this.nextPos));

		NBTTagList sh = new NBTTagList();
		if(this.shape == null){
			this.shape = new ArrayList<BlockPos>();
		}
		for(BlockPos p : this.shape){

			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("shapepos", UtilNBT.posToStringCSV(p));
			sh.appendTag(tag);
		}
		tagCompound.setTag(NBT_SHAPE, sh);
		
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
		
		tagCompound.setInteger(NBT_BUILDTYPE, this.getBuildType());
		
		return super.writeToNBT(tagCompound);
	}
 
	@Override
	public SPacketUpdateTileEntity getUpdatePacket(){//getDescriptionPacket() {

		// Gathers data into a packet (S35PacketUpdateTileEntity) that is to be
		// sent to the client. Called on server only.
		NBTTagCompound syncData = new NBTTagCompound();
		this.writeToNBT(syncData);

		return new SPacketUpdateTileEntity(this.pos, 1, syncData);
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
	public BlockPos getNextPos() {
		return this.nextPos;
	}
 
 
	private void shiftAllUp() {

		for(int i = 0; i < this.getSizeInventory() - 1; i++){
			shiftPairUp(i, i+1);
		}
	}

	private void shiftPairUp(int low, int high){
		ItemStack main = getStackInSlot(low);
		ItemStack second = getStackInSlot(high);

		if (main == null && second != null) { // if the one below this is not
			// empty, move it up
			this.setInventorySlotContents(high, null);
			this.setInventorySlotContents(low, second);
		}
	}
	
	public boolean isBurning() {
		return this.timer > 0 && this.timer < TIMER_FULL;
	}

	@Override
	public void update() {
	
		this.shiftAllUp();

		boolean trigger = false;
		if(nextPos == null || (nextPos.getX() == 0 && nextPos.getY()==0 && nextPos.getZ()==0)){
			nextPos = this.pos;//fallback if it fails
		}
 
		if(this.worldObj.getStrongPower(this.getPos()) == 0){
			//it works ONLY if its powered
			return;
		}

		//??render
		if(!this.worldObj.isRemote && this.nextPos != null && this.worldObj.rand.nextDouble() < 0.1 && 
				this.inv[0] != null){
			UtilParticle.spawnParticlePacket(EnumParticleTypes.DRAGON_BREATH, nextPos, 5);
		}
		
		
		//center of the block
		double x = this.getPos().getX() + 0.5;
		double y = this.getPos().getY() + 0.5;
		double z = this.getPos().getZ() + 0.5;

		ItemStack stack = getStackInSlot(0);
		if (stack == null) {
			timer = TIMER_FULL;// reset just like you would in a
			// furnace
			return;
		}

		timer--;
		if (timer <= 0) {
			timer = TIMER_FULL;
			trigger = true;
		}

		if (trigger) {

			int h = (int)UtilSearchWorld.distanceBetweenHorizontal(this.pos, this.nextPos);
			int v = (int)UtilSearchWorld.distanceBetweenVertical(this.pos, this.nextPos);
			if( h >= MAXRANGE || 
				v >= MAXRANGE){

				System.out.println("TODO:Y only for line: maxrange past");
				this.nextPos = this.pos;
				return;
			}
			
			Block stuff = Block.getBlockFromItem(stack.getItem());
			
			if(stuff != null){

				if(this.worldObj.isRemote == false){
				
					System.out.println("try place "+this.nextPos +" type "+this.getBuildTypeEnum().name());
					
					if(UtilPlaceBlocks.placeStateSafe(this.worldObj, null, this.nextPos, stuff.getStateFromMeta(stack.getMetadata()))){
						this.decrStackSize(0, 1);
					}
				}
				///even if it didnt place. move up maybe something was in the way

				this.incrementPosition();
			}
			
//			this.worldObj.markBlockRangeForRenderUpdate(this.getPos(), this.getPos().up());
			
		}
		else{
			//dont trigger an uncraft event, its still processing

			if(this.worldObj.isRemote && this.worldObj.rand.nextDouble() < 0.1){
		
				UtilParticle.spawnParticle(worldObj, EnumParticleTypes.SMOKE_NORMAL, x, y, z); 
			}
		}
		this.markDirty();
	}

	private void incrementPosition() {
		if(this.nextPos == null){
			this.nextPos = this.pos;
		}
		if(this.worldObj == null){
			return;
		}
//		if(!this.worldObj.isRemote)
//			System.out.println("increment:"+this.getBuildType());

		switch(this.getBuildTypeEnum()){
		case FACING:
			// detect what direction my block faces)
			EnumFacing facing = null;
			// not sure why this happens or if it ever will again, just being
			// super safe to avoid null ptr -> ticking entity exception
			BlockBuilder b = ((BlockBuilder) this.blockType);
			if (b == null || this.worldObj.getBlockState(this.pos) == null || b.getFacingFromState(this.worldObj.getBlockState(this.pos)) == null)
				facing = EnumFacing.UP;
			else
				facing = b.getFacingFromState(this.worldObj.getBlockState(this.pos));

			this.nextPos = this.nextPos.offset(facing);
			
			break;
		case UP:
			
			this.nextPos = this.nextPos.up();
			break;
		case CIRCLE:
		case SQUARE:
			
			int c = shapeIndex+1;
			
			if(c < 0 || c >= this.shape.size()) {c = 0;}
			this.nextPos = this.shape.get(c);
			
			shapeIndex = c;
			
			break;
		default:
			break;
		}
	}

	private int[] hopperInput = { 0, 1, 2,3,4,5,6,7,8 };// all slots for all faces

	@Override
	public int[] getSlotsForFace(EnumFacing side) {

		return hopperInput;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {

		return this.isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {

		//do not let hoppers pull out of here for any reason
		return false;// direction == EnumFacing.DOWN;
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
	public String getName() {

		return null;
	}

	public enum BuildType{
		FACING,UP,SQUARE,CIRCLE;
		
		public static BuildType getNextType(BuildType btype){
			int type = btype.ordinal();
			type++;
			if (type > CIRCLE.ordinal()) {
				type = FACING.ordinal();
			}
			
			return BuildType.values()[type];
		}
	}
	
//	@Override
//    public boolean receiveClientEvent(int id, int type)
//    {
//		System.out.println("receiveClientEvent "+id +")"+ type);
//		this.currentType = BuildType.values()[type];
//		
//		
//		
//        return super.receiveClientEvent(id, type);
//    }
}