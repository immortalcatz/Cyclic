package com.lothrazar.cyclicmagic.net;

import com.lothrazar.cyclicmagic.ModMain;
import com.lothrazar.cyclicmagic.gui.ModGuiHandler;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenWaypoints implements IMessage, IMessageHandler<PacketOpenWaypoints, IMessage> {

	public PacketOpenWaypoints() {}

	public PacketOpenWaypoints(EntityPlayer player) {}

	@Override
	public void toBytes(ByteBuf buffer) {}

	@Override
	public void fromBytes(ByteBuf buffer) {}

	@Override
	public IMessage onMessage(PacketOpenWaypoints message, MessageContext ctx) {
		ctx.getServerHandler().playerEntity.openGui(ModMain.instance, ModGuiHandler.GUI_INDEX_WAYPOINTS, ctx.getServerHandler().playerEntity.worldObj, (int) ctx.getServerHandler().playerEntity.posX, (int) ctx.getServerHandler().playerEntity.posY, (int) ctx.getServerHandler().playerEntity.posZ);
		return null;
	}

}
