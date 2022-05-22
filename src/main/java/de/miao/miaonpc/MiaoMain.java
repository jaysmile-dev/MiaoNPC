package de.miao.miaonpc;

import de.miao.miaonpc.commands.NPCSpawnCommand;
import de.miao.miaonpc.commands.SetNPCSkinCommand;
import de.miao.miaonpc.listeners.JoinLeaveEvent;
import de.miao.miaonpc.listeners.MobEventListener;
import de.miao.miaonpc.reader.PacketReader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public final class MiaoMain extends JavaPlugin {
  private static String prefix = "§9Miao §7-> ";

  @Override
  public void onEnable() {
    saveConfig();
    registerEvents();
    registerCommands();

    //reload more safe -> no npc spawning while reload -> restart instead
    getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
      getServer().getOnlinePlayers().forEach(player -> {
        new PacketReader(player).inject(this);
      });
    }, 10);


  }

  @Override
  public void onDisable() {
    if (PacketReader.class != null)
      getServer().getOnlinePlayers().forEach(PacketReader::uninject);
  }

  private void registerEvents() {
    var manager = Bukkit.getPluginManager();

    manager.registerEvents(new JoinLeaveEvent(this), this);
    manager.registerEvents(new MobEventListener(this), this);
  }

  private void registerCommands() {
    var spawnCmd = new NPCSpawnCommand(this);
    var skinCmd = new SetNPCSkinCommand(this);
    getCommand("npc").setExecutor(spawnCmd);
    getCommand("npc").setTabCompleter(spawnCmd);
    getCommand("setnpcskin").setExecutor(skinCmd);
    getCommand("setnpcskin").setTabCompleter(skinCmd);
  }

  public static String getPrefix() {
    return prefix;
  }

}
