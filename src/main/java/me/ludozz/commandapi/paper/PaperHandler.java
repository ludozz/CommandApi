package me.ludozz.commandapi.paper;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.tree.LiteralCommandNode;
import me.ludozz.commandapi.SpigotCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class PaperHandler implements Listener {

    /*@EventHandler(ignoreCancelled = true)
    public void onTabComplete(AsyncPlayerSendSuggestionsEvent event) {
        if (event.isAsynchronous()) return;
        final String[] args = event.getBuffer().split(" ");
        if (CommandManager.getInstance().getCommand(args[0]) instanceof SpigotCommand spigotCommand) {
            final List<Suggestion> suggestions = spigotCommand.getSuggestions(event.getPlayer(), args[0],
                    Arrays.copyOfRange(args, 1, args.length));
            final List<com.mojang.brigadier.suggestion.Suggestion> brigadierSuggestion = new ArrayList<>();
            for (Suggestion suggestion : suggestions) {
                brigadierSuggestion.add((com.mojang.brigadier.suggestion.Suggestion) suggestion.toBrigadier(0));
            }
            event.setSuggestions(Suggestions.create(args[0], brigadierSuggestion));
        }
    }*/

    @SuppressWarnings("deprecation")
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onCommandRegister(CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
        if(event.isAsynchronous()) return;
        if (event.getCommand() instanceof SpigotCommand) {
            final SpigotCommand spigotCommand = (SpigotCommand) event.getCommand();
            event.setLiteral((LiteralCommandNode<BukkitBrigadierCommandSource>) spigotCommand.getCommandNode());
        }
    }

}
