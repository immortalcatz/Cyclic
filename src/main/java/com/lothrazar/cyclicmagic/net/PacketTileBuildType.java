package com.lothrazar.cyclicmagic.net;

import com.lothrazar.cyclicmagic.block.tileentity.TileEntityBuilder;
import com.lothrazar.cyclicmagic.block.tileentity.TileEntityBuilder.BuildType;
import com.lothrazar.cyclicmagic.gui.builder.ContainerBuilder;
import com.lothrazar.cyclicmagic.util.UtilChat;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTileBuildType implements IMessage, IMessageHandler<PacketTileBuildType, IMessage> {

	public static final int ID = 55;
	private BlockPos			pos;

	public PacketTileBuildType() {

	}

	public PacketTileBuildType(BlockPos p) {
		pos = p;
	}

	@Override
	public void fromBytes(ByteBuf buf) {

		NBTTagCompound tags = ByteBufUtils.readTag(buf);

		int x = tags.getInteger("x");
		int y = tags.getInteger("y");
		int z = tags.getInteger("z");
		pos = new BlockPos(x, y, z);
	}

	@Override
	public void toBytes(ByteBuf buf) {

		NBTTagCompound tags = new NBTTagCompound();
		tags.setInteger("x", pos.getX());
		tags.setInteger("y", pos.getY());
		tags.setInteger("z", pos.getZ());

		ByteBufUtils.writeTag(buf, tags);
	}


	@Override
	public IMessage onMessage(PacketTileBuildType message, MessageContext ctx) {

		EntityPlayer player = ctx.getServerHandler().playerEntity;

		TileEntityBuilder container = (TileEntityBuilder)player.getEntityWorld().getTileEntity(message.pos);
		
		if(container != null){

			int curType = container.getField(TileEntityBuilder.FIELD_BUILDTYPE);
			TileEntityBuilder.BuildType next = TileEntityBuilder.BuildType.getNextType(curType);
			container.setField(TileEntityBuilder.FIELD_BUILDTYPE, next.ordinal());
			container.setShape(next);

//			System.out.println("set SERVER "+next.name());
			
			UtilChat.addChatMessage(player, next.name());
			
			if(player.openContainer != null && player.openContainer instanceof ContainerBuilder){
				
//				((ContainerBuilder)player.openContainer ).buildType = next;
//
//				System.out.println("yep found container, so send update to GUI");
//				System.out.println("CONTAINER="+next.name());
				/*
				 * http://www.minecraftforge.net/forum/index.php?topic=38013.0
				 * Those types of GUIs use the Container as an intermediary between 
				 * the server and the client.

		If you take a look at the ContainerFurnace class, you will see how it calls the 
		IInventory methods as well as #detectAndSendChanges and another method like 
		#addCraftingToCrafters - 
		the former is the one responsible for updating the GUI values from the 
		TE values, and the latter is one that typically adds players that need to be informed of changes.

		Ideally, your TE-based Container/GUI combo wouldn't ever need to explicitly send messages 
		or block updates, but would all be handled via the Container*/
				
				player.openContainer.detectAndSendChanges();
			}
			
			//sending updates to clientside block FROM server
			//http://www.minecraftforge.net/forum/index.php?topic=30903.0
//			player.getEntityWorld().addBlockEvent(message.pos, container.getBlockType(), 1, next.ordinal());
//
//			player.getEntityWorld().markBlockRangeForRenderUpdate(message.pos, message.pos.up());
		}

		return null;
	}
}
