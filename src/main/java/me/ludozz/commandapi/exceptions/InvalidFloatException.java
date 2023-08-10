package me.ludozz.commandapi.exceptions;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class InvalidFloatException extends CommandSyntaxException {
    public InvalidFloatException(@NotNull String value) {
        super("Invalid float '" + value + "'");
    }

}
