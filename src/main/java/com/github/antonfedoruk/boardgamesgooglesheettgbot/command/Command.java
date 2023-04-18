package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import org.slf4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Command interface for handling telegram-bot commands.
 */
public interface Command {

    /**
     * Main method, which is executing command logic.
     *
     * @param update provided {@link Update} object with all the needed data for command.
     */
    void execute(Update update);

    Logger getLogger();
}