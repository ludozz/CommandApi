package me.ludozz.commandapi.brigadier;

import me.ludozz.commandapi.CommandManager;
import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

@ApiStatus.Experimental
@SuppressWarnings("unused")
public final class PlayerArgument extends Argument<Player> {

    /*public static DynamicCommandExceptionType READER_INVALID_PLAYER = new DynamicCommandExceptionType(
            player -> new LiteralMessage("Invalid player '" + player + "'"));
    public static DynamicCommandExceptionType READER_INVALID_PLAYER_LENGTH = new DynamicCommandExceptionType(
            player -> new LiteralMessage("Invalid player '" + player + "' length must be between 3 and 16 characters'"));
    */

    private String invalidPlayerMessage = "Invalid player '%arg%', length must be between 3 and 16 characters";
    private final Callable<List<Player>> callable;

    /*private final com.mojang.brigadier.arguments.ArgumentType<Player> brigadier = new com.mojang.brigadier.arguments.ArgumentType<Player>() {

        @Override
        public Player parse(final StringReader reader) throws CommandSyntaxException {
            final String text = reader.readUnquotedString();
            if (text.length() < 3 || text.length() > 16) throw READER_INVALID_PLAYER_LENGTH.createWithContext(reader, text);
            List<Player> players = new ArrayList<>();
            try {
                players = callable.call();
            } catch (Throwable ex) {
                ex.printStackTrace();
                CommandManager.getLogger().warning("Something went wrong while getting the suggestions");
            }
            for (Player example : players) {
                if (text.equalsIgnoreCase(example.getName())) {
                    return example;
                }
            }
            throw READER_INVALID_PLAYER.createWithContext(reader, text);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
            if (getExamples().isEmpty()) return Suggestions.empty();
            final String text = builder.getRemainingLowerCase();
            List<Player> players = new ArrayList<>();
            try {
                players = callable.call();
            } catch (Throwable ex) {
                ex.printStackTrace();
                CommandManager.getLogger().warning("Something went wrong while getting the suggestions");
            }
            for (Player suggestion : players) {
                if (StringUtil.startsWithIgnoreCase(suggestion.getName(), text)) {
                    builder.suggest(suggestion.getName());
                }
            }
            return builder.buildFuture();
        }
    };*/

    public PlayerArgument(@NotNull String name) {
        this(name, () -> new ArrayList<>(Bukkit.getOnlinePlayers()));
    }

    public PlayerArgument(@NotNull String name, @NotNull Callable<List<Player>> players) {
        super(name);
        this.callable = players;
    }

    @Override
    public @NotNull Player checkValue(@NotNull String arg) throws CommandSyntaxException {
        if (arg.length() < 3 || arg.length() > 16)
            throw new CommandSyntaxException("Invalid player '" + arg + "' length must be between 3 and 16 characters'");
        List<Player> players = new ArrayList<>();
        try {
            players = callable.call();
        } catch (Throwable ex) {
            ex.printStackTrace();
            CommandManager.getLogger().warning("Something went wrong while getting the suggestions");
        }
        for (Player example : players) {
            if (arg.equalsIgnoreCase(example.getName())) {
                return example;
            }
        }
        throw new CommandSyntaxException("Invalid player '" + arg + "'");
    }

    @Override
    public @NotNull List<Suggestion> listSuggestions(@NotNull CommandSender sender, @NotNull String alias,
                                                     @NotNull String[] args) {
        final List<Suggestion> suggestions = new ArrayList<>();
        if (args.length == 0) return suggestions;
        final String arg = args[args.length-1];
        List<Player> players = new ArrayList<>();
        try {
            players = callable.call();
        } catch (Throwable ex) {
            ex.printStackTrace();
            CommandManager.getLogger().warning("Something went wrong while getting the suggestions");
        }
        for (Player suggestion : players) {
            if (StringUtil.startsWithIgnoreCase(suggestion.getName(), arg)) {
                suggestions.add(new StringSuggestion(suggestion.getName()));
            }
        }
        return suggestions;
    }

    @NotNull
    public final String getInvalidPlayerMessage() {
        return invalidPlayerMessage;
    }

    public final void setInvalidPlayerMessage(@NotNull String invalidPlayerMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.invalidPlayerMessage = invalidPlayerMessage;
    }


}
