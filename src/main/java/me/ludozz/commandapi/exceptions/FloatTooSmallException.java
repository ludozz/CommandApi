package me.ludozz.commandapi.exceptions;

@SuppressWarnings("unused")
public class FloatTooSmallException extends CommandSyntaxException {

    public FloatTooSmallException(float value, float min) {
        super("Float must not be less than " + min + ", found " + value);
    }

}
