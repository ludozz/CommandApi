package me.ludozz.commandapi.exceptions;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class LongTooBigException extends CommandSyntaxException {

    public LongTooBigException(@NotNull String value, long max) {
        super("Long must not be more than " + max + ", found " + value);
    }

}
