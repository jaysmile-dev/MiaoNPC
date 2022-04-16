package de.miao.miaonpc.commands.tabcompleter;

import de.miao.miaonpc.npc.NPCType;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SetNPCSkinTabCompleter implements TabCompleter {

  List<String> arguments = new ArrayList<>();

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
    if (!sender.hasPermission("npc.setskin")) return null;
    if (arguments.isEmpty())
      for (var type : NPCType.values())
        arguments.add(type.toString());
    if (args.length == 1) {
      var result = new ArrayList<String>();
      for (String argument : arguments) {
        if (argument.toLowerCase().startsWith(args[0].toLowerCase())) result.add(argument);
      }
      return result;
    }
    return null;
  }

}
