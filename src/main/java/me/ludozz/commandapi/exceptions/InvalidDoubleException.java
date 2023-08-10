package me.ludozz.commandapi.exceptions;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class InvalidDoubleException extends CommandSyntaxException {
    public InvalidDoubleException(@NotNull String value) {
        super("Invalid double '" + value + "'");
    }

}
