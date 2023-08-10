package me.ludozz.commandapi.exceptions;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class InvalidPlayerLengthException extends CommandSyntaxException {

    public InvalidPlayerLengthException(@NotNull String player) {
        super("Invalid player '" + player + "' length must be between 3 and 16 characters");
    }



}
