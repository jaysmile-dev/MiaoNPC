package de.miao.miaonpc.event;

import de.miao.miaonpc.npc.NPC;
import de.miao.miaonpc.util.NPCUtil;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class NPCLoadEvent extends Event {

  private static final HandlerList handlers = new HandlerList();
  private final UUID uuid;
  private final Plugin plugin;
  private final int entityId;

  public NPCLoadEvent(UUID uuid, Plugin plugin, int entityId) {
    this.uuid = uuid;
    this.plugin = plugin;
    this.entityId = entityId;
  }

  public UUID getUUID() {
    return this.uuid;
  }

  public NPC getNPC() {
    return NPCUtil.getNPC(uuid, entityId, plugin);
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return handlers;
  }

  public static HandlerList getHandlerList() {
    return handlers;
  }
}
