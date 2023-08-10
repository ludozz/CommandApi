package me.ludozz.commandapi.brigadier;

import me.ludozz.commandapi.exceptions.CommandSyntaxException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public final class BooleanArgument extends Argument<Boolean> {

    private final List<String> asTrue = new ArrayList<>();
    private final List<String> asFalse = new ArrayList<>();

    private String invalidBooleanMessage = "Invalid boolean, expected true or false but found '%arg%'";

    public BooleanArgument(@NotNull String name) {
        super(name);
    }

    public BooleanArgument(@NotNull String name, @NotNull String[] asTrue, @NotNull String[] asFalse) {
        this(name, Arrays.asList(asTrue), Arrays.asList(asFalse));
    }

    public BooleanArgument(@NotNull String name, @NotNull List<String> asTrue, @NotNull List<String> asFalse) {
        super(name);
        this.asTrue.addAll(asTrue);
        this.asFalse.addAll(asFalse);
        final List<String> examples = new ArrayList<>();
        examples.add("true");
        examples.add("false");
        examples.addAll(asTrue);
        examples.addAll(asFalse);
        setExamples(examples);
    }

    public List<String> getAsTrue() {
        return new ArrayList<>(asTrue);
    }

    public List<String> getAsFalse() {
        return  new ArrayList<>(asFalse);
    }


    @Override
    public @NotNull Boolean checkValue(@NotNull String arg) throws CommandSyntaxException {
        if (arg.isEmpty()) {
            throw new CommandSyntaxException(invalidBooleanMessage.replace("%arg%", arg));
        }
        if (arg.equalsIgnoreCase("true")) {
            return true;
        }
        if (arg.equalsIgnoreCase("false")) {
            return false;
        }
        for (String trueString : getAsTrue()) {
            if (arg.equalsIgnoreCase(trueString)) {
                return true;
            }
        }
        for (String falseString : getAsFalse()) {
            if (arg.equalsIgnoreCase(falseString)) {
                return false;
            }
        }
        throw new CommandSyntaxException(invalidBooleanMessage.replace("%arg%", arg));
    }


    public String getInvalidBooleanMessage() {
        return invalidBooleanMessage;
    }

    public void setInvalidBooleanMessage(@NotNull String invalidBooleanMessage) {
        if (isRegistered()) throw new IllegalStateException("argument is registered");
        this.invalidBooleanMessage = invalidBooleanMessage;
    }

}
