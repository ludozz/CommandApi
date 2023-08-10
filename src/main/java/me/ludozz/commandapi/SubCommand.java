package me.ludozz.commandapi;

import me.ludozz.commandapi.brigadier.Argument;
import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import me.ludozz.commandapi.exceptions.IncorrectArgumentException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public abstract class SubCommand extends Argument<String> implements Executable {

    private Command command;
    private final String name;
    private List<String> aliases = new ArrayList<>();
    protected String description;
    private String permissionMessage;

    public SubCommand(String name) {
        super("sub-command");
        name = name.toLowerCase(Locale.ENGLISH).trim();
        this.name = name;
        this.description = "";
    }

    @Override
    public final @NotNull String checkValue(@NotNull String arg) throws CommandSyntaxException {
        if (arg.equalsIgnoreCase(name)) {
            return name;
        }
        for (String alias : getAliases()) {
            if (arg.equalsIgnoreCase(alias)) return alias;
        }
        throw new IncorrectArgumentException(arg);
    }

    public void setExecutable() {
        super.registerExecutable(this);
    }


    /*@SuppressWarnings("UnusedReturnValue")
    public final boolean execute(CommandSender sender, String alias,  Object[] args) {
        if (!hasPermission(sender)) return true;
        try {
            executeCommand(sender, alias, args);
        } catch (CommandSyntaxException ex) {
            sender.sendMessage(ChatColor.RED + ex.getMessage());
        }
        return true;
    }*/

    public final @NotNull String getName() {
        return this.name;
    }

    public final Command getCommand() {
        return command;
    }

    public void register(@NotNull SpigotCommand command, @Nullable Argument<?> parentArgument) {
        if (isRegistered()) throw new IllegalStateException("subCommand is already registered");
        this.command = command;
        final List<String> examples = getAliases();
        examples.add(name);
        setExamples(examples);
        super.register(command, parentArgument);
    }

    @Override
    public void unregister() {
        command = null;
        super.unregister();
    }

    public final String getDescription() {
        return this.description;
    }

    public final void setAliases(@NotNull List<String> aliases) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        this.aliases = new ArrayList<>(aliases);
    }

    public final void setAliases(@NotNull String... aliases) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        this.aliases = Arrays.asList(aliases);
    }

    @NotNull
    public List<String> getAliases() {
        return new ArrayList<>(aliases);
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    /*public final void setArgument(Argument argument) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        this.argument = argument;
    }

    public final Argument getArgument() {
        return argument;
    }*/

    /*public boolean testPermission(@NotNull CommandSender sender) {
        if (testPermissionSilent(sender)) {
            return true;
        }
        if (getPermissionMessage() == null) {
            sender.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is a mistake.");
            return false;
        }
        if (getPermissionMessage().length() != 0) {
            sender.sendMessage(getPermissionMessage().replace("<permission>", getPermission()));
        }
        return false;
    }

    public boolean testPermissionSilent(@NotNull CommandSender target) {
        if (target instanceof ConsoleCommandSender) return true;
        if (permissions.isEmpty()) return true;
        for (Permission permission : permissions) {
            if (target.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }*/

    public String color(@Nullable String text) {
        if (text == null || text.isEmpty()) return text;
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public void sendMessage(@NotNull CommandSender sender, @Nullable String message) {
        if (message == null || message.isEmpty()) return;
        sender.sendMessage(color(message));
    }

    public void sendRawMessage(@NotNull CommandSender sender, @Nullable String message) {
        if (message == null || message.isEmpty()) return;
        sender.sendMessage(message);
    }

}
