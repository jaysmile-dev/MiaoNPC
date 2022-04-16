package de.miao.miaonpc.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.datafixers.util.Pair;
import de.miao.miaonpc.event.NPCLoadEvent;
import de.miao.miaonpc.npc.NPC;
import de.miao.miaonpc.npc.NPCType;
import de.miao.miaonpc.npc.custom.Knight;
import de.miao.miaonpc.npc.custom.Sauron;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftMob;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NPCUtil {
  public static void writeNPCType(Entity entity, NPCType type) {
    entity.getPersistentDataContainer()
      .set(NamespacedKey.fromString("npc"),
        PersistentDataType.STRING,
        type.toString());
  }

  public static NPCType getNPCType(Entity entity) {
    var container = entity.getPersistentDataContainer();
    var key = NamespacedKey.fromString("npc");

    if (isNPC(entity)) {
      return Enum.valueOf(NPCType.class, container.get(key, PersistentDataType.STRING).toUpperCase());
    }
    return null;
  }

  public static boolean isNPC(Entity entity) {
    return entity.getPersistentDataContainer().has(NamespacedKey.fromString("npc"));
  }

  public static NPC getNewNPC(NPCType type, Level level, Plugin plugin) {
    switch (type) {
      case KNIGHT -> {
        return new Knight(level, plugin);
      }
      case SAURON -> {
        return new Sauron(level);
      }
    }
    return null;
  }

  public static NPC getNPC(UUID uuid, int entityId, Plugin plugin) {
    var entity = Bukkit.getEntity(uuid);
    if (!isNPC(entity)) return null;

    var level = ((CraftWorld) entity.getWorld()).getHandle();

    switch (getNPCType(entity)) {
      case KNIGHT -> {
        return new Knight(level, entity.getUniqueId(), plugin, entityId);
      }
      case SAURON -> {
        return new Sauron(level, entity.getUniqueId(), entityId);
      }
    }
    return null;

  }

  public static Property getNPCSkin(NPCType type, FileConfiguration config) {

    if (config.contains("npc." + type.toString().toLowerCase())) {

      return new Property("textures",
        config.getString("npc." + type.toString().toLowerCase() + ".value"),
        config.getString("npc." + type.toString().toLowerCase() + ".signature"));
    }
    return null;
  }

  public static void setNPCSkin(Property skin, NPCType type, Plugin plugin) {

    plugin.getConfig().set("npc." + type.toString().toLowerCase() + ".value", skin.getValue());
    plugin.getConfig().set("npc." + type.toString().toLowerCase() + ".signature", skin.getSignature());

    plugin.saveConfig();
    for (var world : Bukkit.getWorlds()) {
      if (world == null) return;
      for (var entity : world.getEntities()) {
        if (NPCUtil.isNPC(entity) && NPCUtil.getNPCType(entity) == type) {


          var randomString = RandomStringUtils.random(15, true, true);
          var name = "§8" + type;
          if(name.length() > 15)
            name = randomString;
          var profile = new GameProfile(entity.getUniqueId(), name);

          profile.getProperties().removeAll("textures");
          profile.getProperties().put("textures", new Property("textures",
            skin.getValue(),
            skin.getSignature()));
          var buf = new FriendlyByteBuf(Unpooled.buffer());
          var playerUpdate = new ClientboundPlayerInfoPacket.PlayerUpdate(profile, 0, GameType.DEFAULT_MODE, new TextComponent(name));
          List<ClientboundPlayerInfoPacket.PlayerUpdate> list = new ArrayList<>();
          list.add(playerUpdate);
          buf.writeVarInt(entity.getEntityId());
          buf.writeUUID(entity.getUniqueId());
          buf.writeDouble(entity.getLocation().getX());
          buf.writeDouble(entity.getLocation().getY());
          buf.writeDouble(entity.getLocation().getZ());
          buf.writeByte((int) ((CraftEntity) entity).getHandle().getYRot());
          buf.writeByte((int) ((CraftEntity) entity).getHandle().getXRot());
          var buf2 = new FriendlyByteBuf(Unpooled.buffer());
          buf2.writeEnum(ClientboundPlayerInfoPacket.Action.ADD_PLAYER);
          buf2.writeCollection(list, BufferUtil::writeOnAdd);

          var buf3 = new FriendlyByteBuf(Unpooled.buffer());
          buf3.writeEnum(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER);
          buf3.writeCollection(list, BufferUtil::writeOnRemove);
          for (var online : Bukkit.getOnlinePlayers())
            spawnNPC(buf, buf2, buf3, plugin, entity.getUniqueId(), entity.getEntityId(), profile, online);
          getNPC(entity.getUniqueId(), entity.getEntityId(), plugin).addGoals();
          System.out.println(4);
        }
      }
    }
  }

  public static void respawnNPC(Entity npcEntity, Plugin plugin) {
    if (!isNPC(npcEntity)) return;
    npcEntity.remove();
    var npc = NPCUtil.getNewNPC(NPCUtil.getNPCType(npcEntity), ((CraftEntity) npcEntity).getHandle().getLevel(), plugin);
    npc.setHealth((float) ((Mob) npcEntity).getHealth());
    npc.setItemInHand(InteractionHand.MAIN_HAND, ((CraftMob) npcEntity).getHandle().getItemInHand(InteractionHand.MAIN_HAND));
    npc.spawn(npcEntity.getLocation());

    Bukkit.getMobGoals().removeAllGoals((Mob) Bukkit.getEntity(npc.getUUID()));
    npc.onLoad();
    NPCUtil.writeNPCType(Bukkit.getEntity(npc.getUUID()), npc.getNPCType());
  }

  public static void addToTeam(String teamName, GameProfile profile) {
    Team team;
    if (Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName) == null) {
      team = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(teamName);
      team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
      team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
      team.prefix(net.kyori.adventure.text.Component.text("§8"));
    } else
      team = Bukkit.getScoreboardManager().getMainScoreboard().getTeam(teamName);

    team.addEntry(profile.getName());
  }

  public static void spawnNPC(FriendlyByteBuf buf, FriendlyByteBuf buf2, FriendlyByteBuf buf3, Plugin plugin, UUID uuid, int entityId, GameProfile profile, Player player) {

    var npc = new ServerPlayer(((CraftPlayer) player).getHandle().getServer(), ((CraftPlayer) player).getHandle().getLevel(), profile);
    Bukkit.getEntity(uuid).customName(net.kyori.adventure.text.Component.text(npc.getName().getString()));

    var dataWatcher = npc.getEntityData();
    dataWatcher.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);

    var equipmentList = new ArrayList<Pair<EquipmentSlot, ItemStack>>();


    equipmentList.add(new Pair<>(EquipmentSlot.MAINHAND, ((CraftLivingEntity) Bukkit.getEntity(uuid)).getHandle().getItemInHand(InteractionHand.MAIN_HAND)));

    for (var online : Bukkit.getOnlinePlayers()) {
      var connection = ((CraftPlayer) online).getHandle().connection;
      connection.send(new ClientboundPlayerInfoPacket(buf2));
      connection.send(new ClientboundAddPlayerPacket(buf));
      connection.send(new ClientboundSetEquipmentPacket(entityId, equipmentList));
      connection.send(new ClientboundSetEntityDataPacket(entityId, dataWatcher, true));
    }

    NPCUtil.addToTeam("invisibleTag", profile);
    Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, () -> {

      for (var online : Bukkit.getOnlinePlayers())
        ((CraftPlayer) online).getHandle().connection.send(new ClientboundPlayerInfoPacket(buf3));

    }, 2 * 20);
    Bukkit.getPluginManager().callEvent(new NPCLoadEvent(uuid, plugin, entityId));


  }

}
