package me.ludozz.commandapi.exceptions;

@SuppressWarnings("unused")
public class IntegerTooBigException extends CommandSyntaxException {

    public IntegerTooBigException(int value, int max) {
        super("Integer must not be more than " + max + ", found " + value);
    }

}
