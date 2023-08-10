package me.ludozz.commandapi.brigadier;

import me.ludozz.reflectionutils.nms.NmsUtils;


public final class CommandBrigadier {


    /*private static final String version = NmsUtils.getBukkitVersion();


    /*private static Class<?> commandListenerWrapperClass;
    static Class<?> argumentTypeImpClass, commandImpClass;
    private static final Method commandListenerWrapperToCommandSenderMethod;


    static {
        try {
            if (NmsUtils.useNewClasses()) {
                commandListenerWrapperClass = Class.forName("net.minecraft.commands.CommandListenerWrapper");
            } else {
                commandListenerWrapperClass = Class.forName("net.minecraft.server" + version + ".CommandListenerWrapper");
            }
            commandListenerWrapperToCommandSenderMethod = ReflectionUtils.getMethod(commandListenerWrapperClass,
                    "getBukkitSender");
            final String[] classPaths = new String[]{
                    "libraries\\com\\mojang\\brigadier\\1.1.8\\brigadier-1.1.8.jar",
                    "plugins/CommandAPI-2.1.0.jar"};
            System.err.println(Arrays.toString(classPaths));
            final ClassLoader classLoader = CommandManagerPlugin.class.getClassLoader();
            System.err.println(ClassUtils.createClass(classLoader.getResource("Test.java").openStream(),
                    "Test", "me.ludozz.commandapi.brigadier", classPaths).get());
            argumentTypeImpClass = ClassUtils.createClass(classLoader.getResource("ArgumentTypeImp.java").openStream(),
                    "ArgumentTypeImp", "me.ludozz.commandapi.brigadier", classPaths).get();
            System.err.println(argumentTypeImpClass);
            commandImpClass = ClassUtils.createClass(classLoader.getResource("CommandImp.java").openStream(),
                    "CommandImp", "me.ludozz.commandapi.brigadier", classPaths).get();
            System.err.println(commandImpClass);
        } catch (ReflectiveOperationException | InterruptedException | ExecutionException | IOException ex) {
            throw new RuntimeException("Could not load required classes or methods", ex);
        }
    }

    public static void init() {
        CommandArgumentBuilder.init();
    }

    CommandSender fromCommandListenerWrapper(@NotNull Object object) {
        try {
            return (CommandSender) commandListenerWrapperToCommandSenderMethod.invoke(null, object);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    Object commandSenderFromCommandListenerWrapper(@NotNull Object object) {
        try {
            return commandListenerWrapperToCommandSenderMethod.invoke(null, object);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    /*private static Class<?> minecraftServer, nmsCommandDispatcher, commandListenerWrapper, craftPlayer,
            commandNodeClass, brigadierDispatcherClass/*rootCommandNodeClass/*, entityPlayer*/;
    /*private static Method getMinecraftServer, getNmsCommandDispatcher, getBrigadierCommandDispatcher, getEntityPlayer,
    commandListenerWrapperToCommandSender, sendCommands, registerCommandMethod;
    private static Object minecraftServerObject, nmsCommandDispatcherObject;
    private static Field rootCommandNodeField;
    private final CommandManager commandManager;
    private final List<CommandArgument> commands = new ArrayList<>();



    private void init() {

        try {
            craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
            commandNodeClass = Class.forName("com.mojang.brigadier.tree.CommandNode");
            //rootCommandNodeClass = Class.forName("com.mojang.brigadier.tree.RootCommandNode");
            if (NmsUtils.useNewClasses()) {
                minecraftServer = Class.forName("net.minecraft.server.MinecraftServer");
                nmsCommandDispatcher = Class.forName("net.minecraft.commands.CommandDispatcher");
                commandListenerWrapper = Class.forName("net.minecraft.commands.CommandListenerWrapper");
                //entityPlayer = Class.forName("net.minecraft.server.level.EntityPlayer");
            } else {
                minecraftServer = Class.forName("net.minecraft.server." + version + ".MinecraftServer");
                nmsCommandDispatcher = Class.forName("net.minecraft.server." + version + ".CommandDispatcher");
                commandListenerWrapper = Class.forName("net.minecraft.server" + version + ".CommandListenerWrapper");
                //entityPlayer = Class.forName("net.minecraft.server." + version + ".EntityPlayer");
            }
            //stringRangeClass = Class.forName("com.mojang.brigadier.context.StringRange");
            brigadierDispatcherClass = Class.forName("com.mojang.brigadier.CommandDispatcher");
            rootCommandNodeField = ReflectionUtils.getField(brigadierDispatcherClass,"root");
            registerCommandMethod = ReflectionUtils.getMethod(commandNodeClass, "addChild", commandNodeClass);
            getEntityPlayer = ReflectionUtils.getMethod(craftPlayer, "getHandle");
            getMinecraftServer = ReflectionUtils.getMethod(minecraftServer, "getServer");
            commandListenerWrapperToCommandSender = ReflectionUtils.getMethod(commandListenerWrapper, "getBukkitSender");
            minecraftServerObject = getMinecraftServer.invoke(null);
            Method getNmsCmdDispatcher = null;
            for (Method method : minecraftServer.getDeclaredMethods()) {
                if (method.getReturnType() == nmsCommandDispatcher) {
                    getNmsCmdDispatcher = method;
                    break;
                }
            }
            if (getNmsCmdDispatcher == null) {
                throw new NoSuchMethodException("could not find a method to get the NmsCommandDispatcher");
            } else {
                getNmsCommandDispatcher = getNmsCmdDispatcher;
            }
            sendCommands = ReflectionUtils.getMethod(nmsCommandDispatcher, "a");
            getBrigadierCommandDispatcher = ReflectionUtils.getMethod(nmsCommandDispatcher, "a");
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 NoSuchFieldException ex) {
            throw new RuntimeException("Could not load required classes or methods", ex);
        }
        nmsCommandDispatcherObject = getNmsCommandDispatcher();
        //initMap();
    }

    /*private void initMap() {
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.EXPECTED_DOUBLE,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.INVALID_DOUBLE,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidDouble());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.DOUBLE_TOO_HIGH,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooHigh());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.DOUBLE_TOO_LOW,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.doubleTooLow());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.EXPECTED_BOOLEAN,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedBool());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.INVALID_BOOLEAN,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidBool());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.EXPECTED_INT,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedInt());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.INVALID_INT,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidInt());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.INTEGER_TOO_HIGH,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooHigh());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.INTEGER_TOO_LOW,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.integerTooLow());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.EXPECTED_LONG,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedLong());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.INVALID_LONG,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidLong());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.LONG_TOO_HIGH,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooHigh());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.LONG_TOO_LOW,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.longTooLow());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.EXPECTED_FLOAT,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedFloat());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.INVALID_FLOAT,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidFloat());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.FLOAT_TOO_HIGH,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooHigh());
        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.FLOAT_TOO_LOW,
                CommandSyntaxException.BUILT_IN_EXCEPTIONS.floatTooLow());
//        toException.put(me.ludozz.commandapi.exceptions.CommandSyntaxException.CommandSyntaxType.CUSTOM,
//                CommandSyntaxException.BUILT_IN_EXCEPTIONS.());

    }*/


    /*public CommandBrigadier(@NotNull final CommandManager commandManager) {
        try {
            Class.forName("com.mojang.brigadier.CommandDispatcher");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Brigadier not supported", ex);
        }
        this.commandManager = commandManager;
        init();
        CommandArgumentBuilder.init();
    }


    public void register(@NotNull CommandArgument commandArgument) {
        try {
            ReflectionUtils.invokeMethod(rootCommandNodeField.get(getBrigadierCommandDispatcher), registerCommandMethod,
                    commandArgument.getCommandNode());
            commands.add(commandArgument);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    public void syncCommands() {
        final Object old = nmsCommandDispatcherObject;
        nmsCommandDispatcherObject = this.getNmsCommandDispatcher();
        if (old != nmsCommandDispatcherObject) updateCommands();
    }

    private void updateCommands() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            final Object entityPlayer = getEntityPlayer(player);
            try {
                sendCommands.invoke(getNmsCommandDispatcher(), entityPlayer);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private Object getNmsCommandDispatcher() {
        return ReflectionUtils.throwableToNull(() -> getNmsCommandDispatcher.invoke(minecraftServerObject));
    }

    private Object getBrigadierDispatcher() {
        return ReflectionUtils.throwableToNull(() ->
                getBrigadierCommandDispatcher.invoke(nmsCommandDispatcherObject));
    }



    CommandSender fromCommandListenerWrapper(Object sender) {
        return (CommandSender)
                ReflectionUtils.throwableToNull(() -> commandListenerWrapperToCommandSender.invoke(sender));
    }



    private Object getEntityPlayer(Player player) {
        return ReflectionUtils.throwableToNull(() -> getEntityPlayer.invoke(player));
    }

    /*private LiteralCommandNode<?> toLiteralArgumentBuilder(@NotNull SpigotCommand spigotCommand,
                                                               @NotNull ArgumentType<?> argument) {
        final LiteralArgumentBuilder<Object> literalArgumentBuilder = LiteralArgumentBuilder.literal(spigotCommand.getName());
        literalArgumentBuilder.requires((sender) -> spigotCommand.hasPermission(fromCommandListenerWrapper(sender)));
        final ArgumentBuilder<Object, ?> argumentBuilder = literalArgumentBuilder.then(
                RequiredArgumentBuilder.argument(argument.getName(), argument.getBrigadier()));
        if (argument.isExecutable()) {
            setExecutable(spigotCommand, argumentBuilder);
        }
        registerChild(spigotCommand, argumentBuilder, argument.getChildren());
        final LiteralCommandNode<Object> builtNode = literalArgumentBuilder.build();
        for (String alias : spigotCommand.getAliases()) {
            final LiteralArgumentBuilder<Object> aliasBuilder = LiteralArgumentBuilder.literal(alias);
            aliasBuilder.redirect(builtNode);
        }
        return builtNode;
    }

    private LiteralCommandNode<?> toLiteralArgumentBuilder2(@NotNull SpigotCommand spigotCommand) {
        final Object literalArgumentBuilder =


    }

    private void addPermissionCheck(ArgumentBuilder<Object, ?> argumentBuilder, ArgumentType<?> argumentType) {
        if (!argumentType.getPermissions().isEmpty()) {
            argumentBuilder.requires((sender) -> {
                for (Permission perm : argumentType.getPermissions()) {
                    final CommandSender commandSender = fromCommandListenerWrapper(sender);
                    if (commandSender.hasPermission(perm) || commandSender instanceof ConsoleCommandSender) {
                        return true;
                    }
                }
                return false;
            });
        }
    }

    private void setExecutable(SpigotCommand spigotCommand, ArgumentBuilder<Object, ?> argumentBuilder) {
        argumentBuilder.executes((cmd) -> {
            final String[] args = cmd.getInput().split(" ");
            final CommandSender sender = fromCommandListenerWrapper(cmd.getSource());
            try {
                spigotCommand.execute(sender, args[0].toLowerCase(Locale.ENGLISH),
                        Arrays.copyOfRange(args, 1, args.length));
                return 1;
            } catch (CommandException ex) {
                sender.sendMessage(ChatColor.RED + "An internal error occurred while attempting to perform this command");
                Bukkit.getLogger().log(Level.SEVERE, null, ex);
                return 0;
            } catch (Throwable ex) {
                throw new CommandException("Unhandled exception executing '" + cmd.getInput() + "' in " + spigotCommand, ex);
            }
        });
    }

    private void registerChild(SpigotCommand spigotCommand, ArgumentBuilder<Object, ?> argumentBuilder, List<ArgumentType<?>> arguments) {
        for (ArgumentType<?> argumentType : arguments) {
            RequiredArgumentBuilder<Object,?> requiredArgumentBuilder = RequiredArgumentBuilder.argument(
                    argumentType.getName(), argumentType.getBrigadier());
            addPermissionCheck(requiredArgumentBuilder, argumentType);
            final ArgumentBuilder<Object, ?> childBuilder = argumentBuilder.then(requiredArgumentBuilder);
            if (argumentType.isExecutable()) {
                setExecutable(spigotCommand, childBuilder);
            }
            if (!argumentType.getChildren().isEmpty()) {
                registerChild(spigotCommand, childBuilder, argumentType.getChildren());
            }
        }
    }

    private Object getNmsCommandDispatcher() {
        return ReflectionUtils.throwableToNull(() -> getNmsCommandDispatcher.invoke(minecraftServerObject));
    }

    /*private com.mojang.brigadier.arguments.ArgumentType<?> convert(ArgumentType argument) {
        return argument.getBrigadier();
    }

    private com.mojang.brigadier.arguments.ArgumentType<?> convert(ArgumentType<?> argumentType) {
        return new com.mojang.brigadier.arguments.ArgumentType<Object>() {

            @Override
            public Collection<String> getExamples() {
                return argumentType.getExamples();
            }

            @Override
            public Object parse(StringReader reader) throws CommandSyntaxException {
                final String text = reader.readUnquotedString();
                try {
                    return argumentType.checkValue(text);
                } catch (me.ludozz.commandapi.exceptions.CommandSyntaxException ex) {
                    final Message message = new LiteralMessage(ex.getMessage());
                    throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message, reader.getString(), reader.getCursor());
                    //throw new SimpleCommandExceptionType(new LiteralMessage(ex.getMessage())).createWithContext(reader);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    CommandManager.getLogger().warning("An error occurred while validating a command suggestion");
                    throw new SimpleCommandExceptionType(
                            new LiteralMessage("An error occurred while validating a command suggestion")).createWithContext(reader);
                }
            }

            @Override
            public CompletableFuture<Suggestions> listSuggestions(CommandContext context, SuggestionsBuilder builder) {
                final String[] args = context.getInput().split(" ");
                List<Suggestion> suggestions = new ArrayList<>();
                try {
                     suggestions = argumentType.listSuggestions(
                            fromCommandListenerWrapper(context.getInput()), args[0], Arrays.copyOfRange(args, 1, args.length));
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    CommandManager.getLogger().warning("An error occurred while getting command suggestions");
                }
                for (Suggestion suggestion : suggestions) {
                    if (suggestion instanceof IntegerSuggestion) {
                        builder.suggest(((IntegerSuggestion) suggestion).getNumber());
                    } else if (suggestion instanceof StringSuggestion) {
                        builder.suggest(((StringSuggestion) suggestion).getText());
                    }
                }
                return builder.buildFuture();
            }
        };
    }*/

}
