package me.ludozz.commandapi.exceptions;

@SuppressWarnings("unused")
public class FloatTooBigException extends CommandSyntaxException {

    public FloatTooBigException(float value, float max) {
        super("Float must not be more than " + max + ", found " + value);
    }

}
