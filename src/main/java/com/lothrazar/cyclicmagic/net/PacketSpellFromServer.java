package com.lothrazar.cyclicmagic.net;
import com.lothrazar.cyclicmagic.ModMain;
import com.lothrazar.cyclicmagic.registry.SpellRegistry;
import com.lothrazar.cyclicmagic.spell.ISpell;
import com.lothrazar.cyclicmagic.spell.ISpellFromServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSpellFromServer implements IMessage, IMessageHandler<PacketSpellFromServer, IMessage> {
  private BlockPos pos;
  private BlockPos posOffset;
  private int spellID;
  public PacketSpellFromServer() {
  }
  public PacketSpellFromServer(BlockPos mouseover, BlockPos offset, int spellid) {
    pos = mouseover;
    posOffset = offset;
    spellID = spellid;
  }
  @Override
  public void fromBytes(ByteBuf buf) {
    NBTTagCompound tags = ByteBufUtils.readTag(buf);
    int x = tags.getInteger("x");
    int y = tags.getInteger("y");
    int z = tags.getInteger("z");
    pos = new BlockPos(x, y, z);
    x = tags.getInteger("ox");
    y = tags.getInteger("oy");
    z = tags.getInteger("oz");
    posOffset = new BlockPos(x, y, z);
    spellID = tags.getInteger("spell");
  }
  @Override
  public void toBytes(ByteBuf buf) {
    NBTTagCompound tags = new NBTTagCompound();
    tags.setInteger("x", pos.getX());
    tags.setInteger("y", pos.getY());
    tags.setInteger("z", pos.getZ());
    tags.setInteger("ox", posOffset.getX());
    tags.setInteger("oy", posOffset.getY());
    tags.setInteger("oz", posOffset.getZ());
    tags.setInteger("spell", spellID);
    ByteBufUtils.writeTag(buf, tags);
  }
  @Override
  public IMessage onMessage(PacketSpellFromServer message, MessageContext ctx) {
    if (ctx.side.isServer() && message != null && message.pos != null) {
      EntityPlayer p = ctx.getServerHandler().playerEntity;
      // if(
      // p.worldObj.getBlockState(message.pos).getBlock().isReplaceable(p.worldObj,
      // message.pos)){
      ISpell spell = SpellRegistry.getSpellFromID(message.spellID);
      if (spell != null && spell instanceof ISpellFromServer) {
        ((ISpellFromServer) spell).castFromServer(message.pos, message.posOffset, p);
      }
      else {
        ModMain.logger.warn("WARNING: Message from server: spell not found" + message.spellID);
      }
    }
    return null;
  }
}
