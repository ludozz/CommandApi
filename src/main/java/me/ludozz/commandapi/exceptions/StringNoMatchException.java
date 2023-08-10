package me.ludozz.commandapi.exceptions;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StringNoMatchException extends CommandSyntaxException {

    public StringNoMatchException(@NotNull String arg, @NotNull List<String> valid) {
        super(arg + " is an incorrect arguments for this command, examples are: " + String.join(", ", valid));
    }

}
