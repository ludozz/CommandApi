package me.ludozz.commandapi.brigadier;

import org.jetbrains.annotations.ApiStatus;

@SuppressWarnings("unused")
public interface Suggestion {

    String getText();

    @ApiStatus.Internal
    Object toBrigadier(int start);

}
