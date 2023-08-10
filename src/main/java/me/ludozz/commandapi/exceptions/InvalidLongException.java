package me.ludozz.commandapi.exceptions;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class InvalidLongException extends CommandSyntaxException {
    public InvalidLongException(@NotNull String value) {
        super("Invalid long '" + value + "'");
    }

}
