package me.ludozz.commandapi.brigadier;

import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class SafePlayerArgument extends Argument<String> {

    private String invalidPlayerMessage = "Invalid player '%arg%', length must be between 3 and 16 characters";

    public SafePlayerArgument(@NotNull String name) {
        super(name);
    }

    @Override
    public @NotNull String checkValue(@NotNull String arg) throws CommandSyntaxException {
        if (arg.length() < 3 || arg.length() > 16)
            throw new CommandSyntaxException(invalidPlayerMessage.replace("%arg%", arg));
        return arg;
    }

    @Override
    public @NotNull List<Suggestion> listSuggestions(@NotNull CommandSender sender, @NotNull String alias,
                                                     @NotNull String[] args) {
        final List<Suggestion> suggestions = new ArrayList<>();
        if (args.length == 0) return suggestions;
        final String arg = args[args.length-1];
        final boolean isPlayer = sender instanceof Player;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if ((!isPlayer || ((Player)sender).canSee(player))
                    && StringUtil.startsWithIgnoreCase(player.getName(), arg)) {
                suggestions.add(new StringSuggestion(player.getName()));
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
