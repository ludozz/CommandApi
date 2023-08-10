package me.ludozz.commandapi.brigadier;

import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class StringArgument extends Argument<String> {

    private final StringType stringType;

    public StringArgument(@NotNull String name, @NotNull StringType stringType) {
        super(name);
        this.stringType = stringType;
    }

    public StringArgument(@NotNull String name, @NotNull StringType stringType, @NotNull String... examples) {
        this(name, stringType, Arrays.asList(examples));
    }

    public StringArgument(@NotNull String name, @NotNull StringType stringType, @NotNull List<String> examples) {
        super(name, examples);
        this.stringType = stringType;
    }

    @Override
    public @NotNull String checkValue(@NotNull String arg) throws CommandSyntaxException {
        return arg;
    }

    @NotNull
    public StringType getStringType() {
        return stringType;
    }

    public enum StringType {
        SINGLE_WORD,
        @ApiStatus.Experimental
        QUOTABLE_PHRASE,
        @ApiStatus.Experimental
        GREEDY_PHRASE;
        StringType() {}
    }

}
