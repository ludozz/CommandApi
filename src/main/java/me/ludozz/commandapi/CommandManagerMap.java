package me.ludozz.commandapi;

import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.BukkitCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings("unused")
public final class CommandManagerMap implements Map<String, Command> {

    private final Map<String, Command> knownCommands;

    CommandManagerMap(@Nullable Map<String,Command> knownCommands) {
        if (knownCommands == null) knownCommands = new HashMap<>();
        this.knownCommands = knownCommands;
    }

    CommandManagerMap() {
        this(new HashMap<>());
    }

    @Override
    public int size() {
        return knownCommands.size();
    }

    @Override
    public boolean isEmpty() {
        return knownCommands.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return knownCommands.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return knownCommands.containsValue(value);
    }

    @Override
    public Command get(Object key) {
        return knownCommands.get(key);
    }

    @Nullable
    @Override
    public Command put(String key, Command value) {
        final Command oldCommand = get(key);
        if (key.contains(":")) return oldCommand;
        if (oldCommand == null || (key.equals(value.getName()) && !key.equals(oldCommand.getName()))
                || CommandType.fromCommand(value).getPriority() > CommandType.fromCommand(oldCommand).getPriority()) {
            return knownCommands.put(key, value);
        }
        return oldCommand;
    }

    @Override
    public Command remove(Object key) {
        return knownCommands.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends String, ? extends Command> m) {
        m.forEach((String key, Command value) -> put(key, value));
    }

    @Override
    public void clear() {
        knownCommands.clear();
    }

    @NotNull
    @Override
    public Set<String> keySet() {
        return new HashSet<>(knownCommands.keySet());
    }

    @NotNull
    @Override
    public Collection<Command> values() {
        return new ArrayList<>(knownCommands.values());
    }

    @NotNull
    @Override
    public Set<Entry<String, Command>> entrySet() {
        return new HashSet<>(knownCommands.entrySet());
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Override
    public Command getOrDefault(Object key, Command defaultValue) {
        return knownCommands.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super Command> action) {
        knownCommands.forEach(action);
    }

    @Override
    @Deprecated
    public void replaceAll(BiFunction<? super String, ? super Command, ? extends Command> function) {
        knownCommands.replaceAll(function);
    }

    @Nullable
    @Override
    public Command putIfAbsent(String key, Command value) {
        return knownCommands.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return knownCommands.remove(key, value);
    }

    @Override
    public boolean replace(String key, Command oldValue, Command newValue) {
        return knownCommands.replace(key, oldValue, newValue);
    }

    @Nullable
    @Override
    public Command replace(String key, Command value) {
        return knownCommands.replace(key, value);
    }

    @Override
    public Command computeIfAbsent(String key, @NotNull Function<? super String, ? extends Command> mappingFunction) {
        return knownCommands.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public Command computeIfPresent(String key, @NotNull BiFunction<? super String, ? super Command, ? extends Command> remappingFunction) {
        return knownCommands.computeIfPresent(key, remappingFunction);
    }

    @Override
    public Command compute(String key, @NotNull BiFunction<? super String, ? super Command, ? extends Command> remappingFunction) {
        return knownCommands.compute(key, remappingFunction);
    }

    @Override
    public Command merge(String key, @NotNull Command value, @NotNull BiFunction<? super Command, ? super Command, ? extends Command> remappingFunction) {
        return knownCommands.merge(key, value, remappingFunction);
    }

    Map<String, Command> getMap() {
        return knownCommands;
    }

    @SuppressWarnings("SpellCheckingInspection")
    private enum CommandType {
        COMMAND(0), BUKKITCOMMAND(1), PLUGINCOMMAND(2), SPIGOTCOMMAND(3);

        public final int priority;
        CommandType(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }

        static CommandType fromKey(@NotNull String command) {
            if (command.isEmpty()) return null;
            try {
                return valueOf(command.toUpperCase());
            } catch (IllegalArgumentException ex) {
                return null;
            }
        }

        static CommandType fromCommand(@NotNull Command command) {
            if (command instanceof SpigotCommand) {
                return SPIGOTCOMMAND;
            }
            if (command instanceof PluginCommand) {
                return PLUGINCOMMAND;
            }
            if (command instanceof BukkitCommand) {
                return BUKKITCOMMAND;
            }
            return COMMAND;
        }
    }
}
