package me.ludozz.commandapi;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import me.ludozz.commandapi.brigadier.Argument;
import me.ludozz.commandapi.brigadier.ArgumentTypeImp;
import me.ludozz.commandapi.brigadier.CommandImp;
import me.ludozz.commandapi.brigadier.Suggestion;
import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import me.ludozz.commandapi.exceptions.IncorrectArgumentException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class SpigotCommand extends Command implements PluginIdentifiableCommand {

    private CommandNode<BukkitBrigadierCommandSource> commandNode;
    private final String name;
    private Executable executable;
    private boolean isPlayerOnly;
    private String playersOnlyMessage = "Only players can execute this command";
    private final List<String> aliases = new ArrayList<>();
    private final List<Permission> permissions = new ArrayList<>();
    private final List<Permission> extraPermissions = new ArrayList<>();
    private PermissionDefault permissionDefault = PermissionDefault.OP;
    private final Map<String, List<Permission>> usage = new HashMap<>();
    private Plugin plugin;//Preventing a server shutdown when registering fails
    private final List<Argument<?>> arguments = new ArrayList<>();

    public SpigotCommand(@NotNull String name) {
        super(name.toLowerCase(Locale.ENGLISH).trim());
        name = name.toLowerCase(Locale.ENGLISH).trim();
        this.name = name;
        super.setLabel(name);
    }

    public void setPlayersOnlyMessage(@NotNull String playersOnlyMessage) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        this.playersOnlyMessage = playersOnlyMessage;
    }

    @NotNull
    public String getPlayersOnlyMessage() {
        return playersOnlyMessage;
    }

    public void setPlayerOnly(boolean playerOnly) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        isPlayerOnly = playerOnly;
    }

    public boolean isPlayerOnly() {
        return isPlayerOnly;
    }


    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (alias.contains(":")) {
            sender.sendMessage(ChatColor.RED + "Unsupported alias, please use '/" + name + "'");
            return true;
        }
        if (!hasPermission(sender)) return true;
        if (args.length != 0) {
            try {
                parseArguments(sender, alias, args);
                return true;
            } catch (CommandSyntaxException ex) {
                sender.sendMessage(ChatColor.RED + ex.getMessage());
                return true;
            }
        }
        if (isExecutable()) {
            if (!(sender instanceof Player) && isPlayerOnly()) {
                sender.sendMessage(ChatColor.RED + playersOnlyMessage);
                return true;
            }
            final Object[] objects;
            try {
                objects = getArguments(args);
            } catch (IncorrectArgumentException ex) {
                return false;
            }
            try {
                if (executable.executeCommand(sender, alias, objects)) return true;
            } catch (CommandSyntaxException ex) {
                sender.sendMessage(ChatColor.RED + ex.getMessage());
                return true;
            }
        }
        sendRawMessage(sender, getUsage(sender));
        return true;
    }



    @NotNull
    @Deprecated
    public final String getUsage() {
        return getUsage(null);
    }

    @NotNull
    public String getUsage(@Nullable CommandSender sender) {
        if (!isRegistered()) return "";
        if (sender != null && !hasPermissionSilent(sender)) return "";
        final StringBuilder stringBuilder = new StringBuilder();
        if (isExecutable()) {
            stringBuilder.append('/').append(getName()).append("\n");
        }
        getUsage0("/" + getName(), sender, getArguments(), stringBuilder);
        if (stringBuilder.length() == 0) {
            stringBuilder.append("None... Ehm... Strange... Did something go wrong?\n");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.insert(0,(sender instanceof ConsoleCommandSender ? "\n" : "") +
                "Usage(s) of command '" + getName() + "':\n");
        return stringBuilder.toString();
    }

    private StringBuilder getUsage0(String prefix, CommandSender sender, List<Argument<?>> arguments, StringBuilder stringBuilder) {
        for (Argument<?> argument : arguments) {
            if (sender == null || argument.hasPermissionSilent(sender)) {
                if (argument instanceof SubCommand) {
                    if (argument.isExecutable()) {
                        stringBuilder.append(prefix).append(" ").append(argument.getName()).append("\n");
                    }
                    if (!argument.getChildren().isEmpty()) {
                        getUsage0(prefix + " " + argument.getName(), sender,
                                argument.getChildren(), stringBuilder);
                    }
                    continue;
                }
                if (argument.isExecutable()) {
                    stringBuilder.append(prefix).append(" (").append(argument.getName()).append(")\n");
                }
                if (!argument.getChildren().isEmpty()) {
                    getUsage0(prefix + " (" + argument.getName() + ")", sender,
                            argument.getChildren(), stringBuilder);
                }
            }
            ////noinspection EqualsBetweenInconvertibleTypes
            /*if (argument.getExecutable() == this) {
                if (isExecutable()) {
                    stringBuilder.append('[').append(argument.getName()).append(']');
                } else {
                    stringBuilder.append('<').append(argument.getName()).append('>');
                }
            }
            if (argument.getExecutable() == argument) {
                stringBuilder.append(argument.getName());
            } else if (argument.getExecutable() instanceof SubCommand) {
                stringBuilder.append('<').append(argument.getName()).append('>');
            }*/
        }
        return stringBuilder;
    }

    @Deprecated
    public final @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) throws IllegalArgumentException {
        return tabComplete(sender, getName(), args);
    }

    @Override
    @Deprecated
    public final @NotNull List<String> tabComplete
            (@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {

        return tabCompleteCommand(sender, alias, args);
    }

    @Deprecated
    public final @NotNull List<String> tabComplete
            (@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) throws IllegalArgumentException {
        return tabComplete(sender, alias, args);
    }

    @Deprecated
    protected @NotNull List<String> tabCompleteCommand
            (@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        final List<String> suggestions = new ArrayList<>();
        if (!hasPermissionSilent(sender) || alias.contains(":")) return suggestions;
        //if (CommandManager.getInstance().isPaper()) return suggestions;
        for (Suggestion suggestion : getSuggestions(sender, alias, args)) {
            suggestions.add(suggestion.getText());
        }
        return suggestions;
    }

    @NotNull
    public List<Suggestion> getSuggestions(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (!hasPermissionSilent(sender) || alias.contains(":")) return new ArrayList<>();
        final List<Suggestion> suggestions = new ArrayList<>();
        int lastExecutable = 0;
        List<Argument<?>> arguments = getArguments();
        if (arguments.isEmpty()) return suggestions;
        final Iterator<String> argsIterator = Arrays.stream(args).iterator();
        while (argsIterator.hasNext()) {
            final String arg = argsIterator.next();
            if (!argsIterator.hasNext()) {
                for (Argument<?> argument : arguments) {
                    if (argument.hasPermissionSilent(sender)) {
                        suggestions.addAll(argument.listSuggestions(sender, arg, args));
                    }
                }
                return suggestions;
            }
            boolean match = false;
            final List<Argument<?>> oldArg = new ArrayList<>(arguments);
            arguments.clear();
            for (Argument<?> argument : oldArg) {
                try {
                    if (argument.hasPermissionSilent(sender)) {
                        argument.checkValue(arg);
                        match = true;
                        arguments.addAll(argument.getChildren());
                    }
                } catch (CommandSyntaxException ignored) {
                }
            }
            if (!match) return suggestions;
        }
        return suggestions;
    }

    @Override
    @Deprecated
    public final void setPermission(@Nullable String permission) {
        if (permission == null) {
            permissions.clear();
            return;
        }
        setPermissions(permission.split(";"));
    }

    public final List<Permission> setPermissions(@NotNull List<String> permissions) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        setPermissionDefault(null);
        final List<Permission> perms = new ArrayList<>();
        for (String permissionS : permissions) {
            perms.add(new SpigotPermission(permissionS));
        }
        this.permissions.clear();
        this.permissions.addAll(perms);
        return perms;
    }

    @SuppressWarnings("UnusedReturnValue")
    public final List<Permission> setPermissions(@NotNull String... permissions) {
        return setPermissions(Arrays.asList(permissions));
    }

    public final Permission addPermission(@Nullable String permissionString) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        if (permissionString == null || permissionString.isEmpty()) return null;
        Permission permission = Bukkit.getPluginManager().getPermission(permissionString);
        if (permission == null) {
            permission = new SpigotPermission(permissionString);
        }
        permissions.add(permission);
        return permission;
    }

    public final Permission removePermission(@Nullable String permissionString) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        if (permissionString == null || permissionString.isEmpty()) return null;
        for (Permission permission : permissions) {
            if (permission.getName().equalsIgnoreCase(permissionString)) {
                permissions.remove(permission);
                return permission;
            }
        }
        return null;
    }

    public final Permission registerExtraPermission(@NotNull String permissionString) {
        Permission permission = Bukkit.getPluginManager().getPermission(permissionString);
        if (permission == null) {
            permission = new SpigotPermission(permissionString);
        }
        permission.setDefault(permissionDefault);
        extraPermissions.add(permission);
        return permission;
    }

    public final List<Permission> registerExtraPermissions(@NotNull String... permissions) {
        return registerExtraPermissions(Arrays.asList(permissions));
    }

    public final List<Permission> registerExtraPermissions(@NotNull List<String> permissions) {
        final List<Permission> spigotPermissions = new ArrayList<>();
        for (String perm : permissions) {
            spigotPermissions.add(registerExtraPermission(perm));
        }
        return spigotPermissions;
    }

    @SuppressWarnings("DeprecatedIsStillUsed")
    @Override
    @Deprecated
    public final String getPermission() {
        return String.join(";", getPermissionsNames());
    }

    public final List<Permission> getPermissions() {
        return new ArrayList<>(permissions);
    }

    public final List<String> getPermissionsNames() {
        final List<String> names = new ArrayList<>();
        for (Permission permission : permissions) {
            names.add(permission.getName());
        }
        return names;
    }

    public final void setPermissionDefault(@Nullable PermissionDefault permissionDefault) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        if (permissionDefault == null) {
            permissionDefault = PermissionDefault.OP;
        }
        this.permissionDefault = permissionDefault;
    }

    @NotNull
    public final PermissionDefault getPermissionDefault() {
        return permissionDefault;
    }

    @NotNull
    @Override
    public final String getName() {
        return name;
    }

    @Override
    @Deprecated
    public final boolean setName(@NotNull String name) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("not supported");
    }

    @Override
    @Deprecated
    public final @NotNull String getLabel() {
        return name;
    }

    @Override
    @Deprecated
    public final boolean setLabel(@NotNull String name) {
        return false;
    }

    @NotNull
    @Override
    public final List<String> getAliases() {
        return new ArrayList<>(aliases);
    }

    @Override
    public final @NotNull Command setAliases(@NotNull List<String> aliases) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        this.aliases.clear();
        for (String alias : new ArrayList<>(aliases)) {
            if (alias == null) continue;
            if (alias.equalsIgnoreCase(getName())) {
                CommandManager.getLogger().warning("Tried to set alias '" + alias
                        + "', but equals the main command! Plugin: " + getPlugin().getName());
                continue;
            }
            this.aliases.add(alias.trim().toLowerCase(Locale.ENGLISH));
        }
        return this;
    }

    public final void setAliases(@NotNull String... aliases) {
        setAliases(Arrays.asList(aliases));
    }

    @Override
    public final @NotNull String getDescription() {
        return super.getDescription();
    }

    @Override
    public final @NotNull Command setDescription(@NotNull String description) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        return super.setDescription(
                ChatColor.translateAlternateColorCodes('&',description));
    }

    @Override
    public final @Nullable String getPermissionMessage() {
        return super.getPermissionMessage();
    }

    @Override
    public final @NotNull Command setPermissionMessage(@Nullable String permissionMessage) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        if (permissionMessage == null) {
            return super.setPermissionMessage(null);
        }
        return super.setPermissionMessage(
                ChatColor.translateAlternateColorCodes('&',permissionMessage));
    }

    @Deprecated
    @Override
    public final @NotNull Command setUsage(@NotNull String usage) {
        throw new UnsupportedOperationException("method no longer in use!");
    }

    public final void addArguments(@NotNull Argument<?>... arguments) {
        if (isRegistered()) throw new IllegalStateException("command is registered");
        for (Argument<?> argument : arguments) {
            argument.register(this, null);
            this.arguments.add(argument);
        }
    }

    @NotNull
    public final List<Argument<?>> getArguments() {
        return new ArrayList<>(arguments);
    }


    @Deprecated
    @Override
    public boolean testPermissionSilent(@NotNull CommandSender sender) {
        return true;
    }

    @Deprecated
    @Override
    public boolean testPermission(@NotNull CommandSender sender) {
        return true;
    }

    public boolean hasPermissionSilent(@NotNull CommandSender sender) {
        if (permissions.isEmpty()) return true;
        if (sender instanceof ConsoleCommandSender) return true;
        for (Permission permission : permissions) {
            if (sender.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(@NotNull CommandSender sender) {
        if (hasPermissionSilent(sender)) return true;
        if (getPermissionMessage() == null) {
            sender.sendMessage(ChatColor.RED + "I'm sorry, but you do not have permission to perform this command. Please contact the server administrators if you believe that this is a mistake.");
            return false;
        }
        if (!getPermissionMessage().isEmpty()) {
            sender.sendMessage(getPermissionMessage().replace("<permission>", getPermission()));
        }
        return false;
    }

    public final boolean hasPermission(@NotNull CommandSender sender, @NotNull String permission) {
        if (sender instanceof ConsoleCommandSender) return true;
        return sender.hasPermission(permission);
    }

    public final boolean hasPermission(@NotNull CommandSender sender, @NotNull Permission permission) {
        if (sender instanceof ConsoleCommandSender) return true;
        return sender.hasPermission(permission);
    }

    public final void registerExecutable(@NotNull Executable executable) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        if (this.executable != null) throw new IllegalStateException("executable already registered");
        this.executable = executable;
    }

    @Nullable
    public final Executable getExecutable() {
        return executable;
    }

    public final boolean isExecutable() {
        return executable != null;
    }

    @Override
    public final boolean isRegistered() {
        return super.isRegistered();
    }


    @Override
    public final boolean register(@NotNull CommandMap commandMap) {
        if (this instanceof Executable && !isExecutable()) {
            registerExecutable((Executable) this);
        }
        for (Permission permission : permissions) {
            permission.setDefault(permissionDefault);
            if (permission instanceof SpigotPermission) {
                ((SpigotPermission) permission).register(this);
            }
        }
        for (Permission permission : extraPermissions) {
            if (permission instanceof SpigotPermission) {
                ((SpigotPermission) permission).register(this);
            }
        }
        if (CommandManager.getInstance().isPaper()) {
            try {
                final CommandImp commandImp = new CommandImp(this);
                final LiteralArgumentBuilder<BukkitBrigadierCommandSource> literalArgumentBuilder
                        = LiteralArgumentBuilder.literal(getName());
                literalArgumentBuilder.requires(commandImp);
                if (isExecutable()) {
                    literalArgumentBuilder.executes(commandImp);
                }
                registerChildren(literalArgumentBuilder, commandImp, getArguments());
                commandNode = literalArgumentBuilder.build();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        return super.register(commandMap);
    }

    private void registerChildren(ArgumentBuilder<BukkitBrigadierCommandSource, ?> argumentBuilder, CommandImp commandImp,
                                  List<Argument<?>> arguments) {
        for (Argument<?> argument : arguments) {
            final ArgumentTypeImp<?> argumentImp = new ArgumentTypeImp<>(argument);
            final RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> requiredArgumentBuilder =
                    RequiredArgumentBuilder.argument(argument.getName(), argumentImp);
            requiredArgumentBuilder.suggests(commandImp);
            requiredArgumentBuilder.requires((sender)-> true/*argument.hasPermissionSilent(sender.getBukkitSender())*/);
            if (argument.isExecutable()) {
                requiredArgumentBuilder.executes(commandImp);
            }
            //final CommandNode<BukkitBrigadierCommandSource> builtArgument = requiredArgumentBuilder.build();
            /*if (argument instanceof SubCommand) {
                for (String alias : ((SubCommand)argument).getAliases()) {
                    final RequiredArgumentBuilder<BukkitBrigadierCommandSource, ?> aliasBuilder =
                            RequiredArgumentBuilder.argument(alias, argumentImp);
                    aliasBuilder.redirect(requiredArgumentBuilder);
                }
            }*/
            argumentBuilder.then(requiredArgumentBuilder);
            registerChildren(requiredArgumentBuilder, commandImp, argument.getChildren());
        }
    }

    @Override
    public final boolean unregister(@NotNull CommandMap commandMap) {
        for (Argument<?> argument : getArguments()) {
            argument.unregister();
        }
        arguments.clear();
        for (Permission permission : permissions) {
            if (permission instanceof SpigotPermission) {
                ((SpigotPermission) permission).unregister();
            }
        }
        permissions.clear();
        for (Permission permission : extraPermissions) {
            if (permission instanceof SpigotPermission) {
                ((SpigotPermission) permission).unregister();
            }
        }
        extraPermissions.clear();
        return super.unregister(commandMap);
    }
    final void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    @Override
    public final Plugin getPlugin() {
        return plugin;
    }

    private boolean parseArguments(@NotNull CommandSender sender, String alias, @NotNull String[] args)
            throws CommandSyntaxException {
        List<Argument<?>> argumentList = arguments;
        List<Object> objects = new ArrayList<>();
        int lastExecutable = 0;
        final Iterator<String> argsIterator = Arrays.stream(args).iterator();
        argLoop: while (argsIterator.hasNext()) {
            final String arg = argsIterator.next();
            final Iterator<Argument<?>> argumentTypeIterator = argumentList.iterator();
            while (argumentTypeIterator.hasNext()) {
                final Argument<?> argument = argumentTypeIterator.next();
                try {
                    objects.add(argument.checkValue(arg));
                    if (argument instanceof SubCommand) {
                        lastExecutable = objects.size()-1;
                    }
                    argumentList = argument.getChildren();
                    if (argsIterator.hasNext()) {
                        if (argumentList.isEmpty()) {
                            sendRawMessage(sender, getUsage(sender));
                            return true;
                        }
                        continue argLoop;
                    }
                    if (argument.isExecutable()) {
                        if (!argument.hasPermission(sender)) return true;
                        if (lastExecutable > 1) {
                            objects.subList(0, lastExecutable-1).clear();
                        }
                        final Executable executable = argument.getExecutable();
                        final String executeAlias = (executable instanceof SpigotCommand
                                ? alias : String.valueOf(objects.remove(0)));
                        //noinspection DataFlowIssue
                        executable.execute(sender, alias, objects.toArray(new Object[0]));
                        return true;
                    }
                    sendRawMessage(sender, getUsage(sender));
                    return true;
                } catch (CommandSyntaxException ex) {
                    if (!argumentTypeIterator.hasNext()) {
                        throw new IncorrectArgumentException(ex.getMessage());
                    }
                }
            }
            sendRawMessage(sender, getUsage(sender));
        }
        return true;
    }

    public Object[] getArguments(@NotNull String[] args) throws IncorrectArgumentException {
        final List<Object> objects = new ArrayList<>();
        List<Argument<?>> argumentList = arguments;
        argLoop: for (String arg : args) {
            final Iterator<Argument<?>> argumentTypeIterator = argumentList.iterator();
            while (argumentTypeIterator.hasNext()) {
                final Argument<?> argument = argumentTypeIterator.next();
                try {
                    objects.add(argument.checkValue(arg));
                    argumentList = argument.getChildren();
                    continue argLoop;
                } catch (CommandSyntaxException ex) {
                    if (!argumentTypeIterator.hasNext()) {
                        throw new IncorrectArgumentException(arg);
                    }
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        }
        return objects.toArray(new Object[0]);
    }

    @ApiStatus.Internal
    @Nullable
    public CommandNode<BukkitBrigadierCommandSource> getCommandNode() {
        return commandNode;
    }

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
