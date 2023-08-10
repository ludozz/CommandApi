package me.ludozz.commandapi;

import com.google.common.collect.ImmutableMap;
import me.ludozz.commandapi.brigadier.CommandBrigadier;
import me.ludozz.reflectionutils.ReflectionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.*;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.*;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.CustomTimingsHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class CommandManager {

    private static Logger logger = null;
    private static CommandManager commandManager = null;
    private final CommandManagerPlugin commandManagerPlugin;
    private final boolean experimental;
    private static final String version = Bukkit.getServer().getClass().getName().split("\\.")[3];
    private final List<Command> commands = new ArrayList<>();
    private final SimpleCommandMap commandMap;
    private final Map<String,Command> knownCommands;
    private final Map<Plugin,List<Command>> pluginCommands = new HashMap<>();
    private final Map<Command, Plugin> commandByPlugin = new HashMap<>();
    private final CommandBrigadier commandBrigadier;
    private final boolean paper;
    private final ConcurrentMap<Method, Class<? extends EventExecutor>> eventExecutorMap;
    private boolean override;

    private static final Method syncCommandsMethod;

    static {
        try {
            syncCommandsMethod = ReflectionUtils.getMethod(Bukkit.getServer().getClass(), "syncCommands");
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException("Could not load required classes or methods", ex);
        }
    }


    CommandManager(CommandManagerPlugin commandManagerPlugin) {
        this.commandManagerPlugin = commandManagerPlugin;
        this.experimental = commandManagerPlugin.getConfig().getBoolean("experimental", false);
        logger = commandManagerPlugin.getLogger();
        this.commandMap = getCommandMap();
        this.knownCommands = getKnownCommands();
        this.override = knownCommands instanceof CommandManagerMap;
        this.paper = (ReflectionUtils.throwableToNull(() -> Class.forName(
                "com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent")) != null) && experimental;
        CommandBrigadier commandBrigadier = null;
        if (isPaper()) {
            try {
                Class.forName("com.mojang.brigadier.CommandDispatcher");
                commandBrigadier = new CommandBrigadier();
            } catch (ClassNotFoundException ignored) {
            }
        }
        try {
            //noinspection unchecked
            eventExecutorMap = (ConcurrentMap<Method, Class<? extends EventExecutor>>)
                    ReflectionUtils.getValueExact(EventExecutor.class, "eventExecutorMap");
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
        this.commandBrigadier = commandBrigadier;
    }

    void unregister() {
        unregisterCommands();
        //commandManagerPlugin = null;
        commandManager = null;
        logger = null;
    }

    static void setInstance(CommandManager commandManager) {
        if (CommandManager.commandManager != null) throw new IllegalStateException("Instance is already set");
        CommandManager.commandManager = commandManager;
    }

    public static CommandManager getInstance() {
        if (commandManager == null) throw new IllegalStateException("plugin not loaded yet...");
        return commandManager;
    }

    private SimpleCommandMap getCommandMap() {
        try {
            final String serverName = "org.bukkit.craftbukkit." + version + ".CraftServer";
            final Class<?> server = Class.forName(serverName);
            return (SimpleCommandMap) server.getMethod("getCommandMap").invoke(Bukkit.getServer());
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String,Command> getKnownCommands() {
        Map<String, Command> knownCommands = null;
        try {
            knownCommands = (Map<String, Command>) ReflectionUtils.getValue(commandMap, "knownCommands");
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
        return knownCommands;
    }

    private void setKnownCommands(Map<String, Command> knownCommands) {
        try {
            ReflectionUtils.setValue(commandMap, "knownCommands", knownCommands);
        } catch (NoSuchFieldException ex) {
            ex.printStackTrace();
        }
    }

    public boolean isPaper() {
        return paper;
    }

    public boolean isExperimental() {
        return experimental;
    }

    public synchronized void registerCommands(@NotNull Plugin plugin, @NotNull Command... commands) {
        for (Command command : commands) {
            registerCommand0(plugin, command);
        }
        syncCommands();
    }

    public synchronized boolean registerCommand(@NotNull Plugin plugin, @NotNull Command command) {
        boolean result = registerCommand0(plugin, command);
        syncCommands();
        return result;
    }

    private synchronized boolean registerCommand0(@NotNull Plugin plugin, @NotNull Command command) {
        try {
            boolean result;
            final String uuid = UUID.randomUUID().toString();
            result = commandMap.register(uuid, command);
            if (command instanceof SpigotCommand) {
                ((SpigotCommand) command).setPlugin(plugin);
            }
            if (mayRegisterCommand(command, command.getName())) knownCommands.put(command.getName(), command);
            knownCommands.remove(uuid + ":" + command.getName());
            for (String alias : command.getAliases()) {
                knownCommands.remove(uuid + ":" + alias);
                if (mayRegisterCommand(command, alias)) knownCommands.put(alias.toLowerCase(Locale.ENGLISH).trim(), command);
            }
            commands.add(command);
            List<Command> commands = pluginCommands.get(plugin);
            if (commands == null) {
                commands = new ArrayList<>();
            }
            commands.add(command);
            pluginCommands.put(plugin, commands);
            commandByPlugin.put(command, plugin);
            final Map<String, Map<String, Object>> commandsMap = new HashMap<>();
            //noinspection ConstantValue
            if (plugin.getDescription().getCommands() != null) {
                commandsMap.putAll(plugin.getDescription().getCommands());
            }
            final Map<String, Object> commandDetails = new HashMap<>();
            if (!command.getDescription().isEmpty()) {
                commandDetails.put("description", command.getDescription());
            }
            if (!command.getAliases().isEmpty()) {
                commandDetails.put("aliases", command.getAliases());
            }
            if (command.getPermission() != null && !command.getPermission().isEmpty()) {
                commandDetails.put("permission", command.getPermission());
            }
            if (command.getPermissionMessage() != null && !command.getPermissionMessage().isEmpty()) {
                commandDetails.put("permission-message", command.getPermissionMessage());
            }
            if (command instanceof SpigotCommand) {
                final PermissionDefault permissionDefault = ((SpigotCommand) command).getPermissionDefault();
                if (permissionDefault == PermissionDefault.NOT_OP) {
                    commandDetails.put("permission-default", "notop");
                } else {
                    commandDetails.put("permission-default", ((SpigotCommand) command).getPermissionDefault());
                }
            }
            if (!command.getUsage().isEmpty()) {
                commandDetails.put("usage", command.getUsage());
            }
            commandsMap.put(command.getName(), commandDetails);
            try {
                ReflectionUtils.setValue(plugin.getDescription(),
                        PluginDescriptionFile.class, "commands", ImmutableMap.copyOf(commandsMap));
            } catch (NoSuchFieldException ignored) {
            }
            return result;
        } catch (Throwable ex) {
            ex.printStackTrace();
            getLogger().warning("Failed to register " + command.getName());
            return false;
        }
    }

    public synchronized void unregisterCommands() {
        for (Plugin plugin : pluginCommands.keySet()) {
            unregisterCommands0(plugin);
        }
        syncCommands();
    }

    public synchronized void unregisterCommands(@NotNull Plugin plugin) {
        unregisterCommands0(plugin);
        syncCommands();
    }

    private synchronized void unregisterCommands0(@NotNull Plugin plugin) {
        for (Command cmd : pluginCommands.get(plugin)) {
            unregisterCommand0(cmd);
        }
        pluginCommands.remove(plugin);
    }

    public synchronized void unregisterCommand(@NotNull Command command) {
        unregisterCommand0(command);
        syncCommands();
    }
    private synchronized void unregisterCommand0(Command command) {
        knownCommands.remove(command.getName().toLowerCase().trim(), command);
        for (String alias : command.getAliases()) {
            knownCommands.remove(alias.toLowerCase().trim(), command);
        }
        try {
            command.unregister(commandMap);
        } catch (Throwable ex) {
            ex.printStackTrace();
            getLogger().warning("Failed to unregister " + command.getName() + " properly");
        }
        commandByPlugin.remove(command);
        commands.remove(command);
    }

    public void syncCommands() {
        try {
            syncCommandsMethod.invoke(Bukkit.getServer());
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    private boolean mayRegisterCommand(Command command, String alias) {
        final Command conflict = getCommand(command.getName());
        if (conflict == command || conflict == null) return true;
        if (!(conflict instanceof SpigotCommand) && command instanceof SpigotCommand) return true;
        return !conflict.getName().equals(alias) && command.getName().equals(alias);
    }



    @Deprecated
    public void clearManagerFallBackCommands() {
        for (String cmd : new ArrayList<>(knownCommands.keySet())) {
            if (commands.contains(knownCommands.get(cmd)) && cmd.contains(":")) {
                knownCommands.remove(cmd);
            }
        }
        syncCommands();
    }

    public void clearFallBackCommands() {
        for (String cmd : new ArrayList<>(knownCommands.keySet())) {
            if (cmd.contains(":")) {
                knownCommands.remove(cmd);
            }
        }
    }

    @Nullable
    public CommandBrigadier getCommandBrigadier() {
        return commandBrigadier;
    }

    public void setOverride(final boolean override) {
        if (this.override == override) return;
        this.override = override;
        if (override) {
            if (getKnownCommands() instanceof CommandManagerMap) return;
            setKnownCommands(new CommandManagerMap(knownCommands));
        } else {
            setKnownCommands(((CommandManagerMap)knownCommands).getMap());
        }
    }

    public boolean isOverride() {
        return override;
    }

    public synchronized void clearCommands() {
        for (Command command : new ArrayList<>(commands)) {
            unregisterCommand(command);
        }
    }

    public List<Command> getCommands() {
        return new ArrayList<>(commands);
    }

    public List<String> getCommandsString() {
        return new ArrayList<>(knownCommands.keySet());
    }

    @SuppressWarnings("SpellCheckingInspection")
    public List<String> getCommandsStringNoAlias() {
        final List<String> cmds = new ArrayList<>();
        for (Command cmd : knownCommands.values()) {
            if (knownCommands.get(cmd.getName()).equals(cmd)) {
                cmds.add(cmd.getName());
            }
        }
        return cmds;
    }

    @Nullable
    public Command getCommand(String command) {
        return knownCommands.get(command.toLowerCase(Locale.ENGLISH));
    }

    @Nullable
    public Plugin getPlugin(Command command) {
        return commandByPlugin.get(command);
    }

    @Nullable
    public Plugin getPlugin(String alias) {
        final Command command = knownCommands.get(alias);
        if (command == null) return null;
        return commandByPlugin.get(command);
    }

    public static Logger getLogger() {
        return logger;
    }


    @SuppressWarnings("deprecation")
    public void registerListener(@NotNull final Plugin plugin,
                                 @NotNull final Listener listener) {
        if (!plugin.isEnabled()) throw new IllegalPluginAccessException("Plugin attempted to register "
                + listener + " while not enabled");
        for (Method method : listener.getClass().getDeclaredMethods()) {
            if (method.getReturnType() != Void.TYPE) continue;
            if (method.isBridge() || method.isSynthetic()) continue;
            if (method.getParameterTypes().length != 1) continue;
            final SpigotEventHandler spigotEventHandler = method.getAnnotation(SpigotEventHandler.class);
            if (spigotEventHandler == null) continue;
            final Class<?> classFromString;
            try {
                classFromString = Class.forName(spigotEventHandler.eventClass());
            } catch (ClassNotFoundException ex) {
                getLogger().warning("Could not find class " + spigotEventHandler.eventClass());
                return;
            }
            if (!Event.class.isAssignableFrom(classFromString)) {
                getLogger().warning(spigotEventHandler.eventClass() + " does not extend " + Event.class.getName());
                return;
            }
            @SuppressWarnings("unchecked")
            final Class<? extends Event> eventClass = (Class<? extends Event>) classFromString;
            final EventPriority priority = spigotEventHandler.priority();
            final boolean ignoreCancelled = spigotEventHandler.ignoreCancelled();
            if (!Event.class.isAssignableFrom(method.getParameterTypes()[0])) continue;
            if (Modifier.isStatic(method.getModifiers()) || !Modifier.isPrivate(method.getModifiers())) {
                getLogger().warning("It is recommended to only use private non-static methods for " + plugin.getName());
            }
            try {
                final HandlerList handlerList = (HandlerList) ReflectionUtils.invokeMethodExact(eventClass, "getHandlerList");
                final RegisteredListener registeredListener;
                method.setAccessible(true);
                if (isPaper()) {
                    final EventExecutor executor = (EventExecutor)
                            ReflectionUtils.newInstance("co.aikar.timings.TimedEventExecutor",
                                    getPaperEventExecutor(method, eventClass), plugin, method, eventClass);
                    registeredListener = new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled);
                } else {
                    final CustomTimingsHandler timings =
                            ReflectionUtils.newInstance(CustomTimingsHandler.class,
                                    "Plugin: " + plugin.getDescription().getFullName() + " Event: "
                                            + listener.getClass().getName() + "::" + method.getName() + "("
                                            + eventClass.getSimpleName() + ")",
                                    ReflectionUtils.getValueExact(JavaPluginLoader.class,
                                            "pluginParentTimer"));
                    final EventExecutor executor = (listener1, event) -> {
                        try {
                            if (!eventClass.isAssignableFrom(event.getClass())) {
                                return;
                            }
                            boolean isAsync = event.isAsynchronous();
                            if (!isAsync) timings.startTiming();
                            method.invoke(listener1, event);
                            if (!isAsync) timings.stopTiming();
                        } catch (InvocationTargetException ex) {
                            throw new EventException(ex.getCause());
                        } catch (Throwable t) {
                            throw new EventException(t);
                        }
                    };
                    registeredListener = new RegisteredListener(listener, executor, priority, plugin, ignoreCancelled);
                }
                handlerList.register(registeredListener);
            } catch (Throwable ex) {
                ex.printStackTrace();
                getLogger().warning("Could not register " + method.getName() + " to event " + eventClass.getName());
            }
        }
    }

    private EventExecutor getPaperEventExecutor(Method method, Class<? extends Event> eventClass) throws ReflectiveOperationException {
        final Object definer = ReflectionUtils.invokeMethod(
                "com.destroystokyo.paper.event.executor.asm.ClassDefiner", "getInstance");
        if (Modifier.isStatic(method.getModifiers())) {
            return (EventExecutor) ReflectionUtils.newInstance(
                    "com.destroystokyo.paper.event.executor.StaticMethodHandleEventExecutor", eventClass, method);
        } else if ((boolean)ReflectionUtils.invokeMethod(definer, "isBypassAccessChecks") || Modifier.isPublic(method.getDeclaringClass().getModifiers()) && Modifier.isPublic(method.getModifiers())) {
            final Class<? extends EventExecutor> executorClass = eventExecutorMap.computeIfAbsent(method, (__) -> {
                try {
                String name = (String) ReflectionUtils.invokeMethod(
                        "com.destroystokyo.paper.event.executor.asm.ASMEventExecutorGenerator", "generateName");
                byte[] classData = (byte[]) ReflectionUtils.invokeMethod(
                        "com.destroystokyo.paper.event.executor.asm.ASMEventExecutorGenerator",
                        "generateEventExecutor", method, name);
                    return ((Class<?>)ReflectionUtils.invokeMethod(definer, "defineClass",
                            method.getDeclaringClass().getClassLoader(), name, classData)).asSubclass(EventExecutor.class);
                } catch (ReflectiveOperationException ex) {
                    throw new RuntimeException(ex);
                }
            });
            try {
                @SuppressWarnings("deprecation")
                final EventExecutor asmExecutor = executorClass.newInstance();
                return new EventExecutor() {
                    public void execute(@NotNull Listener listener, @NotNull Event event) throws EventException {
                        if (eventClass.isInstance(event)) {
                            asmExecutor.execute(listener, event);
                        }
                    }

                    public @NotNull String toString() {
                        return "ASMEventExecutor['" + method + "']";
                    }
                };
            } catch (IllegalAccessException | InstantiationException var5) {
                throw new AssertionError("Unable to initialize generated event executor", var5);
            }
        } else {
            return (EventExecutor) ReflectionUtils.newInstance(
                    "com.destroystokyo.paper.event.executor.MethodHandleEventExecutor", eventClass, method);
        }
    }

    @ApiStatus.Internal
    public CommandManagerPlugin getPlugin() {
        return commandManagerPlugin;
    }
}
