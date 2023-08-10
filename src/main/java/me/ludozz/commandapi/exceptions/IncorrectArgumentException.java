package me.ludozz.commandapi.exceptions;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class IncorrectArgumentException extends CommandSyntaxException{

    public IncorrectArgumentException() {
        super("Incorrect arguments for this command");
    }

    public IncorrectArgumentException(@NotNull String arg) {
        super(arg /*+ " is an incorrect arguments for this command"*/);
    }
}
