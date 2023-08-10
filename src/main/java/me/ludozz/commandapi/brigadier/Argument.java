package me.ludozz.commandapi.brigadier;

import me.ludozz.commandapi.Executable;
import me.ludozz.commandapi.SpigotCommand;
import me.ludozz.commandapi.SpigotPermission;
import me.ludozz.commandapi.SubCommand;
import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public abstract class Argument<T> {

    private final String name;
    private SpigotCommand mainCommand = null;
    private Executable executable = null;
    private boolean playersOnly = false;
    private Argument<?> parentArgument;
    private final List<Argument<?>> children = new ArrayList<>();
    private final List<String> examples = new ArrayList<>();
    private final List<Permission> permissions = new ArrayList<>();
    private final List<Permission> extraPermissions = new ArrayList<>();
    private PermissionDefault permissionDefault = PermissionDefault.OP;
    private String permissionMessage;

    public Argument(@NotNull String name) {
        this.name = name;
    }

    public Argument(@NotNull String name, @NotNull Object... examples) {
        this(name, Arrays.asList(examples));
    }

    public Argument(@NotNull String name, @NotNull List<?> examples) {
        this.name = name;
        for (Object example : examples) {
            this.examples.add(example.toString());
        }
    }

    public final List<String> getExamples() {
        return new ArrayList<>(examples);
    }

    public void addChildren(@NotNull Argument<?>... arguments) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        if (this instanceof StringArgument) {
            if (((StringArgument) this).getStringType() == StringArgument.StringType.GREEDY_PHRASE) {
                throw new IllegalStateException("Cannot add children with StringArgumentType with " +
                        "StringType.GREEDY_PHRASE");
            }
        }
        this.children.addAll(Arrays.asList(arguments));
    }

    public List<Argument<?>> getChildren() {
        return new ArrayList<>(children);
    }

    @NotNull
    public String getName() {
        return name;
    }

    public final void setExamples(@NotNull String... examples) {
        setExamples(Arrays.asList(examples));
    }

    public final void setExamples(@NotNull List<String> examples) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.examples.clear();
        this.examples.addAll(examples);
    }

    public void setPlayersOnly(boolean playersOnly) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.playersOnly = playersOnly;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isPlayerOnly() {
        return playersOnly;
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
            sender.sendMessage(getPermissionMessage().replace("<permission>", String.join(";", getPermissionsName())));
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

    public final void addPermissions(@NotNull Permission... permissions) {
        this.permissions.addAll(Arrays.asList(permissions));
    }

    public final List<Permission> getPermissions() {
        return new ArrayList<>(permissions);
    }

    @NotNull
    public final List<String> getPermissionsName() {
        final List<String> names = new ArrayList<>();
        for (Permission permission : permissions) {
            names.add(permission.getName());
        }
        return names;
    }

    public final @Nullable String getPermissionMessage() {
        return permissionMessage;
    }

    public final void setPermissionMessage(@Nullable String permissionMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        if (permissionMessage == null) {
            this.permissionMessage = null;
            return;
        }
        this.permissionMessage = ChatColor.translateAlternateColorCodes('&', permissionMessage);
    }

    @NotNull
    public abstract T checkValue(@NotNull String arg) throws CommandSyntaxException;

    @NotNull
    public List<Suggestion> listSuggestions(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        final List<Suggestion> suggestions = new ArrayList<>();
        if (args.length == 0) return suggestions;
        if (!hasPermissionSilent(sender)) return suggestions;
        final String arg = args[args.length-1];
        for (String example : getExamples()) {
            if (StringUtil.startsWithIgnoreCase(example, arg)) {
                suggestions.add(new StringSuggestion(example));
            }
        }
        return suggestions;
    }

    @NotNull
    public final List<Suggestion> listSuggestionsWhenStartsWith(@NotNull List<Suggestion> suggestions, @NotNull String arg) {
        arg = arg.toLowerCase(Locale.ENGLISH);
        final List<Suggestion> newSuggestions = new ArrayList<>();
        for (Suggestion suggestion : suggestions) {
            final String stringArg = suggestion.getText();
            if (stringArg.toLowerCase(Locale.ENGLISH).startsWith(arg)) {
                newSuggestions.add(suggestion);
            }
        }
        return newSuggestions;
    }

    @NotNull
    @Deprecated
    public String getUsage() {
        return getUsage(Bukkit.getConsoleSender());
    }

    @NotNull
    public String getUsage(@NotNull CommandSender sender) {
        if (!isRegistered()) return "";
        final StringBuilder stringBuilder = new StringBuilder();
        Argument<?> parent = parentArgument;
        while (true) {
            if (parent == null) {
                if (!mainCommand.hasPermissionSilent(sender)) return "";
                stringBuilder.insert(0, "/" + mainCommand.getName());
                break;
            }
            if (!parent.hasPermissionSilent(sender)) return "";
            stringBuilder.insert(0, " " + parent.getName());
            parent = parent.getParentArgument();
        }
        return stringBuilder.toString();
    }



    public final void setPermissionDefault(@Nullable PermissionDefault permissionDefault) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        if (permissionDefault == null) {
            permissionDefault = PermissionDefault.OP;
        }
        this.permissionDefault = permissionDefault;
    }

    @NotNull
    public final PermissionDefault getPermissionDefault() {
        return permissionDefault;
    }

    public final Permission registerExtraPermission(@NotNull String permissionString) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        Permission permission = Bukkit.getPluginManager().getPermission(permissionString);
        if (permission == null) {
            permission = new SpigotPermission(permissionString);
        }
        permission.setDefault(permissionDefault);
        extraPermissions.add(permission);
        return permission;
    }

    @NotNull
    public final List<Permission> registerExtraPermissions(@NotNull String... permissions) {
        return registerExtraPermissions(Arrays.asList(permissions));
    }

    @NotNull
    public final List<Permission> registerExtraPermissions(@NotNull List<String> permissions) {
        final List<Permission> spigotPermissions = new ArrayList<>();
        for (String perm : permissions) {
            spigotPermissions.add(registerExtraPermission(perm));
        }
        return spigotPermissions;
    }

    @NotNull
    public List<Permission> getExtraPermissions() {
        return new ArrayList<>(extraPermissions);
    }

    public final void registerExecutable(@NotNull Executable executable) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        if (this.executable != null) throw new IllegalStateException("executable already registered");
        this.executable = executable;
    }

    public final boolean isExecutable() {
        return executable != null;
    }

    @Nullable
    public final Executable getExecutable() {
        return executable;
    }

    public final boolean isRegistered() {
        return this.mainCommand != null;
    }

    @Nullable
    public Argument<?> getParentArgument() {
        return parentArgument;
    }


    public Command getCommand() {
        return mainCommand;
    }

    @ApiStatus.Internal
    protected String getUsageName(@NotNull CommandSender sender) {
        if (this instanceof SubCommand) return getName();
        if (mainCommand.isExecutable() && (!isPlayerOnly() || sender instanceof Player)) return "["  + getName() + "]";
        Argument<?> parent = parentArgument;
        while (parent != null) {
            //if (parent instanceof SubCommand && parent.isExecutable() && (!parent.isPlayerOnly() || sender instanceof Player)) {
            if (parent.isExecutable() && (!parent.isPlayerOnly() || sender instanceof Player)) return "["  + getName() + "]";
            parent = parent.getParentArgument();
        }
        return "<"  + getName() + ">";
    }

    @ApiStatus.Internal
    public void register(@NotNull SpigotCommand command, @Nullable Argument<?> parentArgument) {
        if (isRegistered()) throw new IllegalStateException("argument is already registered");
        this.mainCommand = command;
        this.parentArgument = parentArgument;
        for (Argument<?> argument : getChildren()) {
            argument.register(mainCommand, this);
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
    }

    @ApiStatus.Internal
    public void unregister() {
        mainCommand = null;
        parentArgument = null;
        for (Argument<?> argument : getChildren()) {
            argument.unregister();
        }
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
    }

}
