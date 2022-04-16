package de.miao.miaonpc.reader;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import de.miao.miaonpc.util.BufferUtil;
import de.miao.miaonpc.util.NPCUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundAddMobPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

public class PacketReader {

  private final Player player;
  private Channel channel;
  private static String name = "MiaoPacketInjector";

  public PacketReader(Player player) {
    this.player = player;
  }


  public void inject(Plugin plugin) {
    channel = ((CraftPlayer) player).getHandle().connection.getConnection().channel;
    addPacketsToChannel(plugin);
  }


  public static void uninject(Player player) {
    var channel = ((CraftPlayer) player).getHandle().connection.getConnection().channel;
    if (channel.pipeline().get(name + "MobSpawn") != null)
      channel.pipeline().remove(name + "MobSpawn");
    if (channel.pipeline().get(name + "Interact") != null)
      channel.pipeline().remove(name + "Interact");
  }


  private void addPacketsToChannel(Plugin plugin) {
    channel.pipeline().addAfter("encoder", name + "MobSpawn", new ChannelOutboundHandlerAdapter() {
      @Override
      public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (!(msg instanceof ClientboundAddMobPacket)) {
          super.write(ctx, msg, promise);
          return;
        }
        var packet = (ClientboundAddMobPacket) msg;
        Bukkit.getScheduler().runTask(plugin, task -> {
          var entity = Bukkit.getEntity(packet.getUUID());

          if (!NPCUtil.isNPC(entity)) {
            try {
              super.write(ctx, packet, promise);
              var equipmentList = new ArrayList<Pair<EquipmentSlot, ItemStack>>();

              if (!(entity instanceof LivingEntity)) return;

              var serverEntity = ((CraftLivingEntity) entity).getHandle();
              equipmentList.add(new Pair<>(EquipmentSlot.MAINHAND, serverEntity.getItemInHand(InteractionHand.MAIN_HAND)));

              var packet2 = new ClientboundSetEquipmentPacket(packet.getId(), equipmentList);

              super.write(ctx, packet2, promise);
              return;
            } catch (Exception e) {
              e.printStackTrace();
              return;
            }
          }
          var randomString = "§8" + RandomStringUtils.random(13, true, true);
          var name = "§8" + NPCUtil.getNPCType(entity).toString();
          if(name.length() > 15)
            name = randomString;
          var profile = new GameProfile(entity.getUniqueId(), name);
          var skin = NPCUtil.getNPCSkin(NPCUtil.getNPCType(Bukkit.getEntity(packet.getUUID())), plugin.getConfig());
          if (skin != null) {
            profile.getProperties().removeAll("textures");

            profile.getProperties().put("textures", new Property("textures",
              skin.getValue(),
              skin.getSignature()));
          }
          var buf = new FriendlyByteBuf(Unpooled.buffer());
          var playerUpdate = new ClientboundPlayerInfoPacket.PlayerUpdate(profile, 0, GameType.DEFAULT_MODE, new TextComponent(name));
          List<ClientboundPlayerInfoPacket.PlayerUpdate> list = new ArrayList<>();
          list.add(playerUpdate);
          buf.writeVarInt(packet.getId());
          buf.writeUUID(packet.getUUID());
          buf.writeDouble(packet.getX());
          buf.writeDouble(packet.getY());
          buf.writeDouble(packet.getZ());
          buf.writeByte(packet.getyRot());
          buf.writeByte(packet.getxRot());

          var buf2 = new FriendlyByteBuf(Unpooled.buffer());
          buf2.writeEnum(ClientboundPlayerInfoPacket.Action.ADD_PLAYER);
          buf2.writeCollection(list, BufferUtil::writeOnAdd);

          var buf3 = new FriendlyByteBuf(Unpooled.buffer());
          buf3.writeEnum(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER);
          buf3.writeCollection(list, BufferUtil::writeOnRemove);
          NPCUtil.spawnNPC(buf, buf2, buf3, plugin, packet.getUUID(), packet.getId(), profile, player);

          task.cancel();

        });

      }
    });

  }


}
