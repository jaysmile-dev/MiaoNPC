package de.miao.miaonpc.commands;

import de.miao.miaonpc.MiaoMain;
import de.miao.miaonpc.npc.NPCType;
import de.miao.miaonpc.util.NPCUtil;
import de.miao.miaonpc.util.SkinUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class SetNPCSkinCommand implements CommandExecutor {

  private final Plugin plugin;

  public SetNPCSkinCommand(Plugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!sender.hasPermission("npc.setskin")) {
      sender.sendMessage(MiaoMain.getPrefix() + "§cDazu hast Du keine Rechte!");
      return true;
    }

    if (args.length == 2) {
      try {
        var type = Enum.valueOf(NPCType.class, args[0].toUpperCase());
        SkinUtil.getSkin(Bukkit.getPlayerUniqueId(args[1])).thenAccept(skin -> {
          Bukkit.getScheduler().runTask(plugin, task -> {
            NPCUtil.setNPCSkin(skin, type, plugin);
            task.cancel();
          });
          sender.sendMessage(MiaoMain.getPrefix() + "§aSkin für §6" + type + " §anpcs gesetzt!");
        });
      } catch (NullPointerException e) {
        sender.sendMessage(MiaoMain.getPrefix() + "§§cEs ist kein Skin für diesen npc type gesetzt!");
      } catch (CommandException e) {
        sender.sendMessage(MiaoMain.getPrefix() + "§cDies ist kein gültiger npc type!");
      }

    } else
      sender.sendMessage(MiaoMain.getPrefix() + "§cFormat: /setnpcskin <npc type> <player name for skin>!");

    return false;
  }
}
