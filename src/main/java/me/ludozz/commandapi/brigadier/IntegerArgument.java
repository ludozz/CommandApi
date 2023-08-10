package me.ludozz.commandapi.brigadier;

import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class IntegerArgument extends Argument<Integer> {

    private final int min, max;

    private String invalidIntegerMessage = "Invalid integer '%arg%'",
            integerTooSmallMessage = "Integer must not be less than %min%, found '%arg%'",
            integerTooBigMessage = "Integer must not be more than %max%, found '%arg%'";

    public IntegerArgument(@NotNull String name) {
        this(name, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntegerArgument(@NotNull String name, @NotNull Integer... examples) {
        this(name, Integer.MIN_VALUE, Integer.MAX_VALUE, examples);
    }

    public IntegerArgument(@NotNull String name, @NotNull List<Integer> examples) {
        this(name, Integer.MIN_VALUE, Integer.MAX_VALUE, examples);
    }

    public IntegerArgument(@NotNull String name, int min, int max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public IntegerArgument(@NotNull String name, int min, int max, @NotNull Integer... examples) {
        this(name, min, max, Arrays.asList(examples));
    }

    public IntegerArgument(@NotNull String name, int min, int max, @NotNull List<Integer> examples) {
        super(name, examples);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    @Override
    public @NotNull Integer checkValue(@NotNull String arg) throws CommandSyntaxException {
        final int result;
        try {
            result = Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            throw new CommandSyntaxException(invalidIntegerMessage.replace("%arg%", arg));
        }
        if (result < min) {
            throw new CommandSyntaxException(integerTooSmallMessage
                    .replace("%min%", String.valueOf(min)).replace("%arg%", arg));
        }
        if (result > max) {
            throw new CommandSyntaxException(integerTooBigMessage
                    .replace("%max%", String.valueOf(max)).replace("%arg%", arg));
        }
        return result;
    }

    @NotNull
    public String getInvalidIntegerMessage() {
        return invalidIntegerMessage;
    }

    public void setInvalidIntegerMessage(@NotNull String invalidIntegerMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.invalidIntegerMessage = invalidIntegerMessage;
    }

    @NotNull
    public String getIntegerTooSmallMessage() {
        return integerTooSmallMessage;
    }

    public void setIntegerTooSmallMessage(@NotNull String integerTooSmallMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.integerTooSmallMessage = integerTooSmallMessage;
    }

    @NotNull
    public String getIntegerTooBigMessage() {
        return integerTooBigMessage;
    }

    public void setIntegerTooBigMessage(@NotNull String integerTooBigMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.integerTooBigMessage = integerTooBigMessage;
    }
}
