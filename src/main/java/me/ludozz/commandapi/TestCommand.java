package me.ludozz.commandapi;

import me.ludozz.commandapi.brigadier.IntegerArgument;
import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class TestCommand extends SpigotCommand implements Executable {

    public TestCommand() {
        super("test");
        IntegerArgument integerArgument = new IntegerArgument("meeps", 1, 500);
        addArguments(integerArgument);
    }

    @Override
    public boolean executeCommand(@NotNull CommandSender sender, @NotNull String alias, @NotNull Object[] args) {
        if (args.length == 0) {
            sender.sendMessage("kip");
            return true;
        }
        sender.sendMessage("meep " + args[0]);
        return true;
    }
}
