package me.ludozz.commandapi.exceptions;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class InvalidBooleanException extends CommandSyntaxException {
    public InvalidBooleanException(@NotNull String value) {
        super("Invalid boolean, expected true or false but found '" + value + "'");
    }

}
