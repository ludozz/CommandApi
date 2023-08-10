package me.ludozz.commandapi;

import me.ludozz.commandapi.brigadier.Argument;
import org.bukkit.Bukkit;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@SuppressWarnings("unused")
public class SpigotPermission extends Permission {

    private SpigotCommand command;
    private Argument<?> argument;


    public SpigotPermission(@NotNull String name) {
        super(name);
    }

    public SpigotPermission(@NotNull String name, @Nullable String description) {
        super(name, description);
    }

    public SpigotPermission(@NotNull String name, @Nullable PermissionDefault defaultValue) {
        super(name, defaultValue);
    }

    public SpigotPermission(@NotNull String name, @Nullable String description, @Nullable PermissionDefault defaultValue) {
        super(name, description, defaultValue);
    }

    public SpigotPermission(@NotNull String name, @Nullable Map<String, Boolean> children) {
        super(name, children);
    }

    public SpigotPermission(@NotNull String name, @Nullable String description, @Nullable Map<String, Boolean> children) {
        super(name, description, children);
    }

    public SpigotPermission(@NotNull String name, @Nullable PermissionDefault defaultValue, @Nullable Map<String, Boolean> children) {
        super(name, defaultValue, children);
    }

    public SpigotPermission(@NotNull String name, @Nullable String description, @Nullable PermissionDefault defaultValue,
                            @Nullable Map<String, Boolean> children) {
        super(name, description, defaultValue, children);
    }

    private final PluginManager pluginManager = Bukkit.getPluginManager();
    void register(SpigotCommand command) {
        if (Bukkit.getPluginManager().getPermission(getName()) == null) {
            pluginManager.addPermission(this);
        }
        this.command = command;
        this.argument = null;
        if (getDescription().isEmpty()) super.setDescription("Permission for " + command.getName());
    }

    public void register(@NotNull Argument<?> argument) {
        if (Bukkit.getPluginManager().getPermission(getName()) == null) {
            pluginManager.addPermission(this);
        }
        this.command = null;
        this.argument = argument;
        if (getDescription().isEmpty()) super.setDescription("Permission for " +
                argument.getName());
    }

    @ApiStatus.Internal
    public void unregister() {
        pluginManager.removePermission(this);
        super.getChildren().clear();
    }

    public boolean isRegistered() {
        return argument != null || command != null;
    }

    @NotNull
    @Override
    public final String getName() {
        return super.getName();
    }

    @NotNull
    @Override
    public final Map<String, Boolean> getChildren() {
        return new HashMap<>(super.getChildren());
    }

    @NotNull
    @Override
    public final PermissionDefault getDefault() {
        return super.getDefault();
    }

    @Override
    public final void setDefault(@NotNull PermissionDefault value) {
        if (isRegistered()) throw new IllegalStateException("permission is registered");
        super.setDefault(value);
    }

    @NotNull
    @Override
    public final String getDescription() {
        return super.getDescription();
    }

    @Override
    public final void setDescription(@Nullable String value) {
        if (isRegistered()) throw new IllegalStateException("permission is registered");
        super.setDescription(value);
    }

    @NotNull
    @Override
    public final Set<Permissible> getPermissibles() {
        return super.getPermissibles();
    }

    @Override
    public final void recalculatePermissibles() {
        super.recalculatePermissibles();
    }

    @NotNull
    @Override
    public final Permission addParent(@NotNull String name, boolean value) {
        if (isRegistered()) throw new IllegalStateException("permission is registered");
        name = name.toLowerCase(Locale.ENGLISH);
        Permission perm = pluginManager.getPermission(name);
        if (perm == null) {
            perm = new SpigotPermission(name);
            pluginManager.addPermission(perm);
        }
        this.addParent(perm, value);
        return perm;
    }

    @Override
    public final void addParent(@NotNull Permission perm, boolean value) {
        if (isRegistered()) throw new IllegalStateException("permission is registered");
        super.addParent(perm, value);
    }
}
