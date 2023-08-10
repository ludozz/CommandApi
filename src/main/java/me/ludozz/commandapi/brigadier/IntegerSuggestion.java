package me.ludozz.commandapi.brigadier;

import me.ludozz.commandapi.CommandManager;
import me.ludozz.reflectionutils.ReflectionUtils;

import java.lang.reflect.Constructor;

@SuppressWarnings("unused")
public final class IntegerSuggestion implements Suggestion {

    private static final Class<?> intSuggestionClass;
    private static final Constructor<?> intSuggestionConstructor;

    private final int number;

    public IntegerSuggestion(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public String getText() {
        return Integer.toString(number);
    }

    @Override
    public Object toBrigadier(int start) {
        if (intSuggestionClass == null) throw new IllegalStateException("brigadier is not present");
        try {
            @SuppressWarnings("DataFlowIssue")
            final Object stringRange = ReflectionUtils.invokeMethod(StringSuggestion.stringRangeClass,
                    StringSuggestion.toStringRangeMethod, start);
            return ReflectionUtils.newInstance(intSuggestionClass, stringRange, number);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "IntegerSuggestion{" +
                "number=" + number +
                '}';
    }

    static {
        if (CommandManager.getInstance().getCommandBrigadier() != null) {
            try {
                intSuggestionClass = Class.forName("com.mojang.brigadier.suggestion.Suggestion");
                intSuggestionConstructor = ReflectionUtils.getConstructorByClasses(intSuggestionClass,
                        StringSuggestion.stringRangeClass, int.class);
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                throw new RuntimeException("Could not load required classes or methods", ex);
            }
        } else {
            intSuggestionClass = null;
            intSuggestionConstructor = null;
        }
    }

}
