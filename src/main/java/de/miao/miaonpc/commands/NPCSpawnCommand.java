package de.miao.miaonpc.commands;

import de.miao.miaonpc.MiaoMain;
import de.miao.miaonpc.npc.NPCType;
import de.miao.miaonpc.util.NPCUtil;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.npc.Villager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class NPCSpawnCommand implements CommandExecutor {

  private final Plugin plugin;
  public NPCSpawnCommand(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

    if (!(sender instanceof Player player)) {
      sender.sendMessage(MiaoMain.getPrefix() + "§cDazu musst Du ein Spieler sein!");
      return true;
    }

    if (!sender.hasPermission("npc.spawn")) {
      sender.sendMessage(MiaoMain.getPrefix() + "§cDazu hast Du keine Rechte!");
      return true;
    }

    for (var type : NPCType.values()) {
      if (args[0].equalsIgnoreCase(type.toString())) {
        if (args.length >= 2)
          try {
            int i = Integer.parseInt(args[1]);
            for (int i2 = 0; i2 < i; i2++)
              spawnNPC(type, player);

          } catch (NumberFormatException exception) {
            player.sendMessage("§cFalsches Format (Zahl)!");
            break;
          }
        else
          spawnNPC(type, player);
        sender.sendMessage(MiaoMain.getPrefix() + "§aNPC erstellt!");


      }
    }

    return false;
  }


  private void spawnNPC(NPCType type, Player player) {
    var npc = NPCUtil.getNewNPC(type, ((CraftPlayer) player).getHandle().getLevel(), plugin);
    npc.spawn(player.getLocation());
    NPCUtil.writeNPCType(Bukkit.getEntity(npc.getUUID()), npc.getNPCType());
  }

}
