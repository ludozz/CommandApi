package me.ludozz.commandapi.exceptions;

@SuppressWarnings("unused")
public class IntegerTooSmallException extends CommandSyntaxException {

    public IntegerTooSmallException(int value, int min) {
        super("Integer must not be less than " + min + ", found " + value);
    }

}
