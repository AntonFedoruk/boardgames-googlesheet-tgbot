package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

/**
 * Enumeration for {@link Command}'s.
 */
public enum CommandName {
    START("/start"),
    STOP("/stop"),
    HELP("/help"),
    ADMIN_HELP("/ahelp"),
    STAT("/stat"),
    GAMES("/games"),
    UPDATE_LOCATION("/updatelocation"),
    WINNERS("/winners"),
    WIN("/win"),
    GIVE_ACCESS_TO_GSHEETS("/promote"),
    RESTRICT_ACCESS_TO_GSHEETS("/demote"),
    NO("");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}