package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Stop {@link Command}.
 */
public class StopCommand implements Command{
    private final SendBotMessageService sendBotMessageService;

    public static final String STOP_MESSAGE = "ееее, то є зрада(";

    public StopCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
sendBotMessageService.sendMessage(CommandUtils.getChatId(update), STOP_MESSAGE);
    }
}