package com.github.antonfedoruk.boardgamesgooglesheettgbot.service;

import java.io.File;

/**
 * Service for sending messages via telegram-bot.
 */
public interface SendBotMessageService {

    /**
     * Send message via telegram bot.
     *
     * @param chatId provided chatId in which messages would be sent.
     * @param message provided message to be sent.
     */
    void sendMessage(Long chatId, String message);

    /**
     * Send photo via telegram bot.
     *
     * @param chatId provided chatId in which messages would be sent.
     * @param file provided photo to be sent.
     */
    void sendPhoto(Long chatId, File file);
}
