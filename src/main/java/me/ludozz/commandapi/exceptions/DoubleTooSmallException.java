package me.ludozz.commandapi.exceptions;

@SuppressWarnings("unused")
public class DoubleTooSmallException extends CommandSyntaxException {

    public DoubleTooSmallException(double value, double min) {
        super("Double must not be less than " + min + ", found " + value);
    }

}
