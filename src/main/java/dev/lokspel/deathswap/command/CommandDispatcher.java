package dev.lokspel.deathswap.command;

import dev.lokspel.deathswap.DeathSwap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CommandDispatcher implements CommandExecutor {

    private final DeathSwap plugin;
    private final Map<String, SubCommand> subcommands = new LinkedHashMap<>();

    public CommandDispatcher(DeathSwap plugin, List<RegisteredCommand> commands) {
        this.plugin = plugin;
        for (RegisteredCommand cmd : commands) {
            subcommands.put(cmd.name(), cmd.executor());
        }
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getMainConfig().messages().prefixed("usage"));
            return true;
        }

        SubCommand sub = subcommands.get(args[0].toLowerCase());
        if (sub == null) {
            sender.sendMessage(plugin.getMainConfig().messages().prefixed("usage"));
            return true;
        }

        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        return sub.execute(sender, subArgs);
    }
}
