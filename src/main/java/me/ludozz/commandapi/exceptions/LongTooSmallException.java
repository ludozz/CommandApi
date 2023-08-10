package me.ludozz.commandapi.exceptions;

@SuppressWarnings("unused")
public class LongTooSmallException extends CommandSyntaxException {

    public LongTooSmallException(long value, long min) {
        super("Long must not be less than " + min + ", found " + value);
    }

}
