package de.miao.miaonpc.listeners;

import de.miao.miaonpc.reader.PacketReader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public record JoinLeaveEvent(Plugin plugin) implements Listener {

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    var reader = new PacketReader(event.getPlayer());
    reader.inject(plugin);
  }

}

