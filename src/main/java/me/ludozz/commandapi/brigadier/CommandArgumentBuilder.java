package me.ludozz.commandapi.brigadier;

public final class CommandArgumentBuilder {

}

    /*private static final Class<?> argumentBuilderClass, commandClass, commandNodeClass, argumentTypeClass;
    private static final Constructor<?> literalArgumentBuilderConstructor, requiredArgumentBuilderConstructor,
            argumentTypeConstructor, commandConstructor;
    private static final Method thenMethod, thenMethod2, requiresMethod, redirectMethod, buildMethod, executesMethod;

    static {
        try {
            argumentBuilderClass = Class.forName("com.mojang.brigadier.builder.ArgumentBuilder");
            commandClass = Class.forName("com.mojang.brigadier.Command");
            commandNodeClass = Class.forName("com.mojang.brigadier.tree.CommandNode");
            argumentTypeClass = Class.forName("com.mojang.brigadier.arguments.ArgumentType");
            argumentTypeConstructor = CommandBrigadier.argumentTypeImpClass.getConstructor(Argument.class);
            commandConstructor = CommandBrigadier.commandImpClass.getConstructor(SpigotCommand.class);
            /*final ClassUtils.ClassBuilder classBuilder = new ClassUtils.ClassBuilder("ArgumentType",
                    "private final me.ludozz.commandapi.brigadier.ArgumentType argumentType;" +
                            "public ArgumentType(me.ludozz.commandapi.brigadier.ArgumentType argumentType) {" +
                            "this.argumentType = argumentType;" +
                            "}" +
                            "public Object parse(com.mojang.brigadier.StringReader reader) throws " +
                            "com.mojang.brigadier.Message.CommandSyntaxException {" +
                            "final String text = reader.readUnquotedString();" +
                            "try {" +
                            "return argumentType.checkValue(text);" +
                            "} catch (me.ludozz.commandapi.exceptions.CommandSyntaxException ex) {" +
                            "final Message message = new LiteralMessage(ex.getMessage());" +
                            "throw new CommandSyntaxException(new SimpleCommandExceptionType(message), message, " +
                            "reader.getString(), reader.getCursor());" +
                            "} catch (Throwable ex) {" +
                            "ex.printStackTrace();" +
                            "CommandManager.getLogger().warning(\"An error occurred while validating a command suggestion\");" +
                            "throw new SimpleCommandExceptionType(" +
                            "new com.mojang.brigadier.LiteralMessage(\"An error occurred while validating a command suggestion\"))" +
                            ".createWithContext(reader);" +
                            "}" +
                            "public CompletableFuture<Suggestions> listSuggestions(CommandContext context, SuggestionsBuilder builder) {" +
                            "final String[] args = context.getInput().split(\" \");" +
                            "List<Suggestion> suggestions = new ArrayList<>();" +
                            "try {" +
                            "suggestions = argumentType.listSuggestions(" +
                            "fromCommandListenerWrapper(context.getInput()), args[0], Arrays.copyOfRange(args, 1, args.length));" +
                            "} catch (Throwable ex) {" +
                            "ex.printStackTrace();" +
                            "CommandManager.getLogger().warning(\"An error occurred while getting command suggestions\");" +
                            "}" +
                            "for (Suggestion suggestion : suggestions) {" +
                            "if (suggestion instanceof IntegerSuggestion) {" +
                            "builder.suggest(((IntegerSuggestion) suggestion).getNumber());" +
                            "} else if (suggestion instanceof StringSuggestion) {" +
                            "builder.suggest(((StringSuggestion) suggestion).getText());" +
                            "}" +
                            "}" +
                            "return builder.buildFuture();" +
                            "}" +
                            "public Collection<String> getExamples() {" +
                            "return argumentType.getExamples();" +
                            "}" +
                            "}", true, null, argumentTypeClass);
            argumentTypeConstructor = ClassUtils.createClass(classBuilder).getConstructor(Argument.class);
            final ClassUtils.ClassBuilder classBuilder2 = new ClassUtils.ClassBuilder("Command",
                    "private final SpigotCommand = spigotCommand;" +
                    "public Command(me.ludozz.commandapi.SpigotCommand spigotCommand) {" +
                    "this.spigotCommand = spigotCommand;" +
                    "}" +
                    "public int run(CommandContext<S> cmd) throws com.mojang.brigadier.exceptions.CommandSyntaxException {" +
                    "final String[] args = cmd.getInput().split(\" \");" +
                    "final CommandSender sender = CommandBrigadier.fromCommandListenerWrapper(cmd.getSource());" +
                    "try {" +
                    "spigotCommand.execute(sender, args[0].toLowerCase(Locale.ENGLISH)," +
                    "Arrays.copyOfRange(args, 1, args.length));" +
                    "return 1;" +
                    "} catch (CommandException ex) {" +
                    "sender.sendMessage(ChatColor.RED + \"An internal error occurred while attempting to perform this command\");" +
                    "Bukkit.getLogger().log(Level.SEVERE, null, ex);" +
                    "return 0;" +
                    "} catch (Throwable ex) {" +
                    "throw new CommandException(\"Unhandled exception executing '\" + cmd.getInput() + \"' in \" + spigotCommand, ex);" +
                    "}" +
                    "}",
                    true, null, commandClass);
            commandConstructor = ClassUtils.createClass(classBuilder2).getConstructor(SpigotCommand.class);
            thenMethod = ReflectionUtils.getMethodByClasses(argumentBuilderClass, "then",
                    argumentBuilderClass);
            thenMethod2 = ReflectionUtils.getMethodByClasses(argumentBuilderClass, "then", commandNodeClass);
            requiresMethod = ReflectionUtils.getMethodByClasses(argumentBuilderClass, "requires", Predicate.class);
            redirectMethod = ReflectionUtils.getMethodByClasses(argumentBuilderClass, "redirect",
                    commandNodeClass);
            buildMethod = ReflectionUtils.getMethodByClasses(argumentBuilderClass, "build");
            executesMethod = ReflectionUtils.getMethodByClasses(argumentBuilderClass, "executes", commandClass);
            literalArgumentBuilderConstructor = ReflectionUtils.getConstructor(
                    "com.mojang.brigadier.builder.LiteralArgumentBuilder", String.class);
            requiredArgumentBuilderConstructor = ReflectionUtils.getConstructor(
                    "com.mojang.brigadier.builder.RequiredArgumentBuilder", String.class,
                    Class.forName("com.mojang.brigadier.arguments.ArgumentType"));
        } catch (ClassNotFoundException | NoSuchMethodException ex) {
            throw new RuntimeException("Could not load required classes or methods", ex);
        }
    }

    public static void init() {}

    private final SpigotCommand spigotCommand;
    private final String name;
    private final Object argumentBuilder;
    private final List<CommandArgumentBuilder> aliases = new ArrayList<>();

    public CommandArgumentBuilder(@NotNull SpigotCommand spigotCommand) {
        this.spigotCommand = spigotCommand;
        this.name = spigotCommand.getName();
        try {
            this.argumentBuilder = literalArgumentBuilderConstructor.newInstance(name);
            for (Argument<?> argument : spigotCommand.getArguments()) {
                addArgument(new CommandArgumentBuilder(spigotCommand, argument));
            }
            for (String alias : spigotCommand.getAliases()) {
                aliases.add(new CommandArgumentBuilder(spigotCommand, alias));
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public CommandArgumentBuilder(@NotNull SpigotCommand spigotCommand, @NotNull Argument<?> argumentType) {
        this.spigotCommand = spigotCommand;
        this.name = argumentType.getName();
        try {
            this.argumentBuilder = requiredArgumentBuilderConstructor.newInstance(argumentType.getName(),
                    ReflectionUtils.newInstance(argumentTypeConstructor, argumentType));
            requires(argumentType.getPermissions());
            if (argumentType.isExecutable()) {
                setExecutable();
            }
            for (Argument<?> argument : argumentType.getChildren()) {
                addArgument(new CommandArgumentBuilder(spigotCommand, argument));
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private CommandArgumentBuilder(@NotNull SpigotCommand spigotCommand, @NotNull String alias) {
        this.spigotCommand = spigotCommand;
        this.name = alias;
        try {
            this.argumentBuilder = literalArgumentBuilderConstructor.newInstance(alias);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public SpigotCommand getSpigotCommand() {
        return spigotCommand;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public CommandArgumentBuilder addArgument(@NotNull CommandArgumentBuilder commandArgumentBuilder) {
        try {
            thenMethod.invoke(argumentBuilder, commandArgumentBuilder.getArgumentBuilder());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public CommandArgumentBuilder addArgument(@NotNull CommandArgument commandArgument) {
        try {
            thenMethod2.invoke(argumentBuilder, commandArgument.getCommandNode());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public CommandArgumentBuilder requires(@NotNull Predicate<CommandSender> requirement) {
        requires(argumentBuilder, requirement);
        return this;
    }

    public CommandArgumentBuilder requires(@NotNull List<Permission> permissions) {
        if (permissions.isEmpty()) return this;
        requires(argumentBuilder, sender -> {
            if (sender instanceof ConsoleCommandSender) return true;
            for (Permission permission :  permissions) {
                if (sender.hasPermission(permission)) return true;
            }
            return false;
        });
        return this;
    }

    private void requires(Object argumentBuilder, @NotNull Predicate<CommandSender> requirement) {
        try {
            requiresMethod.invoke(argumentBuilder, (Predicate<?>) commandListenerWrapper -> {
                final CommandSender commandSender = CommandManager.getInstance().getCommandBrigadier()
                        .fromCommandListenerWrapper(commandListenerWrapper);
                 return requirement.test(commandSender);
            });
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public CommandArgumentBuilder redirect(@NotNull CommandArgument commandArgument) {
        redirect(argumentBuilder, commandArgument);
        return this;
    }

    private void redirect(Object argumentBuilder, @NotNull CommandArgument commandArgument) {
        try {
            redirectMethod.invoke(argumentBuilder, commandArgument.getCommandNode());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public void setExecutable() {
        setExecutable(argumentBuilder);
    }

    private void setExecutable(Object argumentBuilder) {
        try {
            ReflectionUtils.invokeMethod(argumentBuilder, executesMethod,
                    commandConstructor.newInstance(spigotCommand));
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    Object getArgumentBuilder() {
        return argumentBuilder;
    }

    public CommandArgument build() {
        try {
            final CommandArgument commandArgument = new CommandArgument(buildMethod.invoke(argumentBuilder));
            for (CommandArgumentBuilder alias : aliases) {
                alias.redirect(commandArgument);
            }
            return commandArgument;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }



}*/
