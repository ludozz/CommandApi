package me.ludozz.commandapi.exceptions;

@SuppressWarnings("unused")
public class DoubleTooBigException extends CommandSyntaxException {

    public DoubleTooBigException(double value, double max) {
        super("Double must not be more than " + max + ", found " + value);
    }

}
