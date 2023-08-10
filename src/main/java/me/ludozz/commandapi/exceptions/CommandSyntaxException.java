package me.ludozz.commandapi.exceptions;

import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class CommandSyntaxException extends Exception {

    private String message;
    //private final CommandSyntaxType type;

    public CommandSyntaxException(@NotNull String message) {
        this.message = message;
        //this.type = CommandSyntaxType.CUSTOM;
    }

    /*@NotNull
    public CommandSyntaxType getType() {
        return type;
    }*/

    public void setMessage(@NotNull String message) {
        this.message = message;
    }

    //@Nullable
    @NotNull
    public String getMessage() {
        return message;
    }



    /*public enum CommandSyntaxType {
        DOUBLE_TOO_LOW, DOUBLE_TOO_HIGH, FLOAT_TOO_LOW, FLOAT_TOO_HIGH, INTEGER_TOO_LOW, INTEGER_TOO_HIGH, LONG_TOO_LOW,
        LONG_TOO_HIGH, INVALID_BOOLEAN, INVALID_INT, EXPECTED_INT, INVALID_LONG, EXPECTED_LONG, INVALID_DOUBLE,
        EXPECTED_DOUBLE, INVALID_FLOAT, EXPECTED_FLOAT, EXPECTED_BOOLEAN, CUSTOM;
    }*/

}
