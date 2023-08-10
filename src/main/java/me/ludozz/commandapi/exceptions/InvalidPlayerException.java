package me.ludozz.commandapi.exceptions;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class InvalidPlayerException extends CommandSyntaxException {

    public InvalidPlayerException(@NotNull String player) {
        super("Invalid player '" + player + "'");
    }



}
