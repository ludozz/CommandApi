package me.ludozz.commandapi.brigadier;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import me.ludozz.commandapi.CommandManager;
import me.ludozz.commandapi.SpigotCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.logging.Level;

public final class CommandImp implements Command<BukkitBrigadierCommandSource>, SuggestionProvider<BukkitBrigadierCommandSource>,
        Predicate<BukkitBrigadierCommandSource> {

        private final SpigotCommand spigotCommand;

        public CommandImp(SpigotCommand spigotCommand) {
                this.spigotCommand = spigotCommand;
                
        }

        @Override
        public int run(CommandContext<BukkitBrigadierCommandSource> cmd) {
                final String[] args = cmd.getInput().split(" ");
                final CommandSender sender = cmd.getSource().getBukkitSender();
                try {
                        if (spigotCommand.execute(sender, args[0].toLowerCase(Locale.ENGLISH),
                                Arrays.copyOfRange(args, 1, args.length))) return 1;
                        return 0;
                } catch (CommandException ex) {
                        sender.sendMessage(ChatColor.RED +
                                "An internal error occurred while attempting to perform this command");
                        Bukkit.getLogger().log(Level.SEVERE, null, ex);
                        return 0;
                } catch (Throwable ex) {
                        throw new CommandException("Unhandled exception executing '" + cmd.getInput() + "' in "
                                + spigotCommand, ex);
                }
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<BukkitBrigadierCommandSource> commandContext,
                                                             SuggestionsBuilder suggestionsBuilder) {
                final String[] args = commandContext.getInput().split(" ");
                if (CommandManager.getInstance().getCommand(args[0]) instanceof SpigotCommand) {
                        final org.bukkit.command.Command command = CommandManager.getInstance().getCommand(args[0]);
                        if (command != this.spigotCommand)  return suggestionsBuilder.buildFuture();
                        final List<Suggestion> suggestions = this.spigotCommand.getSuggestions(
                                commandContext.getSource().getBukkitSender(), args[0], Arrays.copyOfRange(args, 1, args.length));
                        for (Suggestion suggestion : suggestions) {
                                if (suggestion instanceof IntegerSuggestion) {
                                        suggestionsBuilder.suggest(((IntegerSuggestion) suggestion).getNumber());
                                        continue;
                                }
                                suggestionsBuilder.suggest(suggestion.getText());
                        }
                        return suggestionsBuilder.buildFuture();
                }
                return suggestionsBuilder.buildFuture();
        }

        @Override
        public boolean test(BukkitBrigadierCommandSource sender) {
                return true;
        }
}