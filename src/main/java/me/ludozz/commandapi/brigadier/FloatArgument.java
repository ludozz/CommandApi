package me.ludozz.commandapi.brigadier;

import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class FloatArgument extends Argument<Float> {

    private final float min, max;
    private String invalidFloatMessage = "Invalid float '%arg%'",
            floatTooSmallMessage = "Float must not be less than %min%, found '%arg%'",
            floatTooBigMessage = "Float must not be more than %max%, found '%arg%'";

    public FloatArgument(@NotNull String name) {
        this(name, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public FloatArgument(@NotNull String name, @NotNull Float... examples) {
        this(name, Float.MIN_VALUE, Float.MAX_VALUE, examples);
    }

    public FloatArgument(@NotNull String name, @NotNull List<Float> examples) {
        this(name, Float.MIN_VALUE, Float.MAX_VALUE, examples);
    }

    public FloatArgument(@NotNull String name, float min, float max) {
        super(name);
        if (!Float.isFinite(min)) throw new IllegalArgumentException("min cannot be Infinite or NaN");
        if (!Float.isFinite(max)) throw new IllegalArgumentException("max cannot be Infinite or NaN");
        this.min = min;
        this.max = max;
    }

    public FloatArgument(@NotNull String name, float min, float max, @NotNull Float... examples) {
        this(name, min, max, Arrays.asList(examples));
    }

    public FloatArgument(@NotNull String name, float min, float max, @NotNull List<Float> examples) {
        super(name, examples);
        for (float example : examples) {
            if (!Float.isFinite(example)) {
                throw new IllegalArgumentException("example cannot be Infinite or NaN");
            }
        }
        if (!Float.isFinite(min)) throw new IllegalArgumentException("min cannot be Infinite or NaN");
        if (!Float.isFinite(max)) throw new IllegalArgumentException("max cannot be Infinite or NaN");
        this.min = min;
        this.max = max;
    }


    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    @Override
    public @NotNull Float checkValue(@NotNull String arg) throws me.ludozz.commandapi.exceptions.CommandSyntaxException {
        final float result;
        try {
            result = Float.parseFloat(arg);
        } catch (NumberFormatException ex) {
            throw new CommandSyntaxException(invalidFloatMessage.replace("%arg%", arg));
        }
        if (!Double.isFinite(result)) {
            throw new CommandSyntaxException(invalidFloatMessage.replace("%arg%", arg));
        }
        if (result < min) {
            throw new CommandSyntaxException(floatTooSmallMessage
                    .replace("%min%", String.valueOf(min)).replace("%arg%", arg));
        }
        if (result > max) {
            throw new CommandSyntaxException(floatTooBigMessage
                    .replace("%max%", String.valueOf(max)).replace("%arg%", arg));
        }
        return result;
    }

    @NotNull
    public String getInvalidFloatMessage() {
        return invalidFloatMessage;
    }

    public void setInvalidFloatMessage(@NotNull String invalidFloatMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.invalidFloatMessage = invalidFloatMessage;
    }

    @NotNull
    public String getFloatTooSmallMessage() {
        return floatTooSmallMessage;
    }

    public void setFloatTooSmallMessage(@NotNull String floatTooSmallMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.floatTooSmallMessage = floatTooSmallMessage;
    }

    @NotNull
    public String getFloatTooBigMessage() {
        return floatTooBigMessage;
    }

    public void setFloatTooBigMessage(@NotNull String floatTooBigMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.floatTooBigMessage = floatTooBigMessage;
    }
}
