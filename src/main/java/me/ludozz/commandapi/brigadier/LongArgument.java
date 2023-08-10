package me.ludozz.commandapi.brigadier;

import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class LongArgument extends Argument<Long> {

    private final long min, max;
    private String invalidLongMessage = "Invalid long '%arg%'",
            longTooSmallMessage = "Long must not be less than %min%, found '%arg%'",
            longTooBigMessage = "Long must not be more than %max%, found '%arg%'";

    public LongArgument(@NotNull String name) {
        this(name, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public LongArgument(@NotNull String name, @NotNull Long... examples) {
        this(name, Long.MIN_VALUE, Long.MAX_VALUE, examples);
    }

    public LongArgument(@NotNull String name, @NotNull List<Long> examples) {
        this(name, Long.MIN_VALUE, Long.MAX_VALUE, examples);
    }


    public LongArgument(@NotNull String name, long min, long max) {
        super(name);
        this.min = min;
        this.max = max;
    }

    public LongArgument(@NotNull String name, long min, long max, @NotNull Long... examples) {
        this(name, min, max, Arrays.asList(examples));
    }

    public LongArgument(@NotNull String name, long min, long max, @NotNull List<Long> examples) {
        super(name, examples);
        this.min = min;
        this.max = max;
    }

    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    @Override
    public @NotNull Long checkValue(@NotNull String arg) throws CommandSyntaxException {
        final long result;
        try {
            result = Long.parseLong(arg);
        } catch (NumberFormatException ex) {
            throw new CommandSyntaxException(invalidLongMessage.replace("%arg%", arg));
        }
        if (result < min) {
            throw new CommandSyntaxException(longTooSmallMessage
                    .replace("%min%", String.valueOf(min)).replace("%arg%", arg));
        }
        if (result > max) {
            throw new CommandSyntaxException(longTooBigMessage
                    .replace("%max%", String.valueOf(max)).replace("%arg%", arg));
        }
        return result;
    }

    @NotNull
    public String getInvalidLongMessage() {
        return invalidLongMessage;
    }

    public void setInvalidLongMessage(@NotNull String invalidLongMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.invalidLongMessage = invalidLongMessage;
    }

    @NotNull
    public String getLongTooSmallMessage() {
        return longTooSmallMessage;
    }

    public void setLongTooSmallMessage(@NotNull String longTooSmallMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.longTooSmallMessage = longTooSmallMessage;
    }

    @NotNull
    public String getLongTooBigMessage() {
        return longTooBigMessage;
    }

    public void setLongTooBigMessage(@NotNull String longTooBigMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.longTooBigMessage = longTooBigMessage;
    }
}
