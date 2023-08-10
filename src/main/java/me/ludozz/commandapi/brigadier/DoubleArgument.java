package me.ludozz.commandapi.brigadier;

import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class DoubleArgument extends Argument<Double> {

    private final double min, max;

    private String invalidDoubleMessage = "Invalid double '%arg%'",
            doubleTooSmallMessage = "Double must not be less than %min%, found '%arg%'",
            doubleTooBigMessage = "Double must not be more than %max%, found '%arg%'";

    public DoubleArgument(@NotNull String name) {
        this(name, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public DoubleArgument(@NotNull String name, @NotNull Double... examples) {
        this(name, Double.MIN_VALUE, Double.MAX_VALUE, examples);
    }

    public DoubleArgument(@NotNull String name, @NotNull List<Double> examples) {
        this(name, Double.MIN_VALUE, Double.MAX_VALUE, examples);
    }

    public DoubleArgument(@NotNull String name, double min, double max) {
        super(name);
        if (!Double.isFinite(min)) throw new IllegalArgumentException("min cannot be Infinite or NaN");
        if (!Double.isFinite(max)) throw new IllegalArgumentException("max cannot be Infinite or NaN");
        this.min = min;
        this.max = max;
    }

    public DoubleArgument(@NotNull String name, double min, double max, @NotNull Double... examples) {
        this(name, min, max, Arrays.asList(examples));
    }

    public DoubleArgument(@NotNull String name, double min, double max, @NotNull List<Double> examples) {
        super(name, examples);
        for (double example : examples) {
            if (!Double.isFinite(example)) {
                throw new IllegalArgumentException("example cannot be Infinite or NaN");
            }
        }
        if (!Double.isFinite(min)) throw new IllegalArgumentException("min cannot be Infinite or NaN");
        if (!Double.isFinite(max)) throw new IllegalArgumentException("max cannot be Infinite or NaN");
        this.min = min;
        this.max = max;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    @Override
    public @NotNull Double checkValue(@NotNull String arg) throws CommandSyntaxException {
        final double result;
        try {
            result = Double.parseDouble(arg);
        } catch (NumberFormatException ex) {
            throw new CommandSyntaxException(invalidDoubleMessage.replace("%arg%", arg));
        }
        if (!Double.isFinite(result)) {
            throw new CommandSyntaxException(invalidDoubleMessage.replace("%arg%", arg));
        }
        if (result < min) {
            throw new CommandSyntaxException(doubleTooSmallMessage
                    .replace("%min%", String.valueOf(min)).replace("%arg%", arg));
        }
        if (result > max) {
            throw new CommandSyntaxException(doubleTooBigMessage
                    .replace("%max%", String.valueOf(max)).replace("%arg%", arg));
        }
        return result;
    }

    @NotNull
    public String getInvalidDoubleMessage() {
        return invalidDoubleMessage;
    }

    public void setInvalidDoubleMessage(@NotNull String invalidDoubleMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.invalidDoubleMessage = invalidDoubleMessage;
    }

    @NotNull
    public String getDoubleTooSmallMessage() {
        return doubleTooSmallMessage;
    }

    public void setDoubleTooSmallMessage(@NotNull String doubleTooSmallMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.doubleTooSmallMessage = doubleTooSmallMessage;
    }

    @NotNull
    public String getDoubleTooBigMessage() {
        return doubleTooBigMessage;
    }

    public void setDoubleTooBigMessage(@NotNull String doubleTooBigMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.doubleTooBigMessage = doubleTooBigMessage;
    }
}
