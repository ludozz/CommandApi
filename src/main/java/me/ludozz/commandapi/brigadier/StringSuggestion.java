package me.ludozz.commandapi.brigadier;

import me.ludozz.commandapi.CommandManager;
import me.ludozz.reflectionutils.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public final class StringSuggestion implements Suggestion {

    static final Class<?> stringRangeClass;
    private static final Class<?> suggestionClass;
    static final Method toStringRangeMethod;
    private static final Constructor<?> suggestionConstructor;


    private final String text;

    public StringSuggestion(@NotNull String text) {
        this.text = text;
    }

    @NotNull
    public String getText() {
        return text;
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public Object toBrigadier(int start) {
        if (suggestionClass == null) throw new IllegalStateException("brigadier is not present");
        try {
            final Object stringRange = ReflectionUtils.invokeMethod(stringRangeClass, toStringRangeMethod, start);
            return ReflectionUtils.newInstance(suggestionConstructor, stringRange, text);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        //com.mojang.brigadier.suggestion.Suggestion(StringRange.at(args.lastIndexOf(32)+1), )
    }

    @Override
    public String toString() {
        return "StringSuggestion{" +
                "text='" + text + '\'' +
                '}';
    }

    static {
        if (CommandManager.getInstance().getCommandBrigadier() != null) {
            try {
                stringRangeClass = Class.forName("com.mojang.brigadier.context.StringRange");
                toStringRangeMethod = ReflectionUtils.getMethodByClasses(stringRangeClass, "at", int.class);
                suggestionClass = Class.forName("com.mojang.brigadier.suggestion.Suggestion");
                suggestionConstructor = ReflectionUtils.getConstructorByClasses(suggestionClass, stringRangeClass,
                        String.class);
            } catch (ClassNotFoundException | NoSuchMethodException ex) {
                throw new RuntimeException("Could not load required classes or methods", ex);
            }
        } else {
            stringRangeClass = null;
            toStringRangeMethod = null;
            suggestionClass = null;
            suggestionConstructor = null;
        }
    }

}
