package de.miao.miaonpc;

import de.miao.miaonpc.commands.NPCSpawnCommand;
import de.miao.miaonpc.commands.SetNPCSkinCommand;
import de.miao.miaonpc.commands.tabcompleter.NPCSpawnTabCompleter;
import de.miao.miaonpc.commands.tabcompleter.SetNPCSkinTabCompleter;
import de.miao.miaonpc.listeners.JoinLeaveEvent;
import de.miao.miaonpc.listeners.MobEventListener;
import de.miao.miaonpc.reader.PacketReader;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class MiaoMain extends JavaPlugin {
  private static String prefix = "§9Miao §7-> ";

  @Override
  public void onEnable() {
    saveConfig();
    registerEvents();
    registerCommands();

    //removed lines 24 - 26 for a safe reload

    //Bukkit.getOnlinePlayers().forEach(player -> {
    //    new PacketReader(player).inject(this);
    //});


  }

  @Override
  public void onDisable() {
    //removed lines 34 - 35 for a safe reload
    //if (PacketReader.class != null)
    //  Bukkit.getOnlinePlayers().forEach(PacketReader::uninject);
  }

  private void registerEvents() {
    var manager = Bukkit.getPluginManager();

    manager.registerEvents(new JoinLeaveEvent(this), this);
    manager.registerEvents(new MobEventListener(this), this);
  }

  private void registerCommands() {
    getCommand("npc").setExecutor(new NPCSpawnCommand(this));
    getCommand("npc").setTabCompleter(new NPCSpawnTabCompleter());
    getCommand("setnpcskin").setExecutor(new SetNPCSkinCommand(this));
    getCommand("setnpcskin").setTabCompleter(new SetNPCSkinTabCompleter());
  }

  public static String getPrefix() {
    return prefix;
  }

}
