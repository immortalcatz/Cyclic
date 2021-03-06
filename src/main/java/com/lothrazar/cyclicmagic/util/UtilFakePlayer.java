package com.lothrazar.cyclicmagic.util;
import java.lang.ref.WeakReference;
import java.util.UUID;
import com.google.common.base.Charsets;
import com.lothrazar.cyclicmagic.ModMain;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class UtilFakePlayer {
  public static final GameProfile breakerProfile = new GameProfile(UUID.nameUUIDFromBytes("CyclicFakePlayer".getBytes(Charsets.UTF_8)), "CyclicFakePlayer");
  public static WeakReference<FakePlayer> initFakePlayer(WorldServer ws) {
    WeakReference<FakePlayer> fakePlayer;
    try {
      fakePlayer = new WeakReference<FakePlayer>(FakePlayerFactory.get(ws, breakerProfile));
    }
    catch (Exception e) {
      ModMain.logger.error("Exception thrown trying to create fake player : " + e.getMessage());
      fakePlayer = null;
    }
    if (fakePlayer == null || fakePlayer.get() == null) {
      fakePlayer = null;
      return null; // trying to get around https://github.com/PrinceOfAmber/Cyclic/issues/113
    }
    fakePlayer.get().onGround = true;
    fakePlayer.get().connection = new NetHandlerPlayServer(FMLCommonHandler.instance().getMinecraftServerInstance(), new NetworkManager(EnumPacketDirection.SERVERBOUND), fakePlayer.get()) {
      @SuppressWarnings("rawtypes")
      @Override
      public void sendPacket(Packet packetIn) {
      }
    };
    return fakePlayer;
  }
}
