package me.ludozz.commandapi.brigadier;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.ludozz.commandapi.CommandManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class ArgumentTypeImp<T> implements ArgumentType<T> {

    private final Argument<T> argument;

    public ArgumentTypeImp(Argument<T> argument) {
        this.argument = argument;
        ArgumentRegistry.registerArgumentType(this,
                CommandManager.getInstance().getPlugin(), argument.getName());
    }

    @Override
    public T parse(StringReader reader) throws CommandSyntaxException {
        final int start = reader.getCursor();
        final String text = reader.readUnquotedString();
        reader.setCursor(start);
        try {
            return argument.checkValue(text);
        } catch (me.ludozz.commandapi.exceptions.CommandSyntaxException ex) {
            final Message message = new LiteralMessage(ex.getMessage());
            throw new CommandSyntaxException(
                    new SimpleCommandExceptionType(message), message, reader.getString(), reader.getCursor());
        } catch (Throwable ex) {
            ex.printStackTrace();
            CommandManager.getLogger().warning("An error occurred while validating a command suggestion");
            throw new SimpleCommandExceptionType(
                    new LiteralMessage("An error occurred while validating a command suggestion"))
                    .createWithContext(reader);
        }
    }

    @Override
    public CompletableFuture<Suggestions> listSuggestions(CommandContext context, SuggestionsBuilder builder) {
        final String[] args = context.getInput().split(" ");
        List<Suggestion> suggestions = new ArrayList<>();
        if (args.length < 2) return builder.buildFuture();
        try {
            suggestions = argument.listSuggestions(((BukkitBrigadierCommandSource)context.getSource()).getBukkitSender(),
                    args[0], Arrays.copyOfRange(args, 1, args.length));
        } catch (Throwable ex) {
            ex.printStackTrace();
            CommandManager.getLogger().warning("An error occurred while getting command suggestions");
        }
        for (Suggestion suggestion : suggestions) {
            if (suggestion instanceof IntegerSuggestion) {
                builder.suggest(((IntegerSuggestion) suggestion).getNumber());
                continue;
            }
            builder.suggest(suggestion.getText());
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return argument.getExamples();
    }

    @NotNull
    public Argument<T> getArgument() {
        return argument;
    }

}
