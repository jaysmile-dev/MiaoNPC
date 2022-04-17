package de.miao.miaonpc.listeners;

import de.miao.miaonpc.event.NPCLoadEvent;
import de.miao.miaonpc.util.NPCUtil;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class MobEventListener implements Listener {

  private final Plugin plugin;

  public MobEventListener(Plugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void on(PlayerInteractAtEntityEvent event) {
    if (NPCUtil.isNPC(event.getRightClicked()))
      NPCUtil.getNPC(event.getRightClicked().getUniqueId(), event.getRightClicked().getEntityId(), plugin).onInteract(event);

  }



  @EventHandler
  public void on(EntityDamageByEntityEvent event) {
    if (!(event.getEntity() instanceof LivingEntity entity)) return;
    var damager = event.getDamager();
    if (event.getDamage() >= entity.getHealth())
      if (entity instanceof Player)
        if (NPCUtil.isNPC(damager))
          NPCUtil.getNPC(damager.getUniqueId(), damager.getEntityId(), plugin).addGoals();

    if (NPCUtil.isNPC(entity))
      NPCUtil.getNPC(entity.getUniqueId(), entity.getEntityId(), plugin).onDamage(damager);
    if (NPCUtil.isNPC(event.getDamager())) {
      for (var online : Bukkit.getOnlinePlayers())
        ((CraftPlayer) online).getHandle().connection.send(new ClientboundAnimatePacket(((CraftEntity) event.getDamager()).getHandle(), 0));
      NPCUtil.getNPC(damager.getUniqueId(), damager.getEntityId(), plugin).onAttack(entity);
    }
  }

  @EventHandler
  public void on(PlayerJoinEvent event) {
    for (var world : Bukkit.getWorlds())
      for (var entity : world.getEntities())
        if (NPCUtil.isNPC(entity))
          NPCUtil.getNPC(entity.getUniqueId(), entity.getEntityId(), plugin).onJoin();

  }

  @EventHandler
  public void on(PlayerQuitEvent event) {
    for (var world : Bukkit.getWorlds())
      for (var entity : world.getEntities()) {
        if (NPCUtil.isNPC(entity))
          NPCUtil.getNPC(entity.getUniqueId(), entity.getEntityId(), plugin).onLeave();
      }
  }

  @EventHandler
  public void on(VillagerCareerChangeEvent event) {
    if (NPCUtil.isNPC(event.getEntity()))
      event.setCancelled(true);
  }

  @EventHandler
  public void on(EntitiesLoadEvent event) {
    for (var entity : event.getEntities()) {
      NPCUtil.respawnNPC(entity, plugin);

    }
  }

  @EventHandler
  public void on(NPCLoadEvent event) {
    event.getNPC().onLoad();
  }

  @EventHandler
  public void on(VillagerAcquireTradeEvent event) {
    if (NPCUtil.isNPC(event.getEntity())) {
      event.setCancelled(true);
      Bukkit.broadcast(Component.text("VillagerAcquireTradeEvent detected"));
    }
  }


  @EventHandler
  public void on(VillagerReplenishTradeEvent event) {
    if (NPCUtil.isNPC(event.getEntity())) {
      event.setCancelled(true);
      Bukkit.broadcast(Component.text("VillagerReplenishTradeEvent detected"));
    }
  }

}
