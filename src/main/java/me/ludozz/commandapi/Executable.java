package me.ludozz.commandapi;

import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public interface Executable {

    boolean executeCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Object[] args) throws CommandSyntaxException;


    /**Always use this method to execute a command!!! VVV**/
    default boolean execute(CommandSender sender, String alias,  Object[] args) {
        try {
            if (!hasPermission(sender)) return true;
            executeCommand(sender, alias, args);
        } catch (CommandSyntaxException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
        }
        return true;
    }

    boolean hasPermission(@NotNull CommandSender sender);

    boolean hasPermissionSilent(@NotNull CommandSender sender);

}
