package me.ludozz.commandapi.brigadier;

@SuppressWarnings("unused")
public final class CommandArgument {
    private final Object commandNode;

    CommandArgument(Object commandNode) {
        this.commandNode = commandNode;
    }

    Object getCommandNode() {
        return commandNode;
    }

}
