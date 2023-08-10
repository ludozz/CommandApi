package me.ludozz.commandapi.exceptions;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class InvalidIntegerException extends CommandSyntaxException {
    public InvalidIntegerException(@NotNull String value) {
        super("Invalid integer '" + value + "'");
    }

}
