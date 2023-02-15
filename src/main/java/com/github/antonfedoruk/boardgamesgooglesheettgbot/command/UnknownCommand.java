package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Unknown {@link Command}.
 */
public class UnknownCommand implements Command{
    private final SendBotMessageService sendBotMessageService;

    public static final String UNKNOWN_MESSAGE = "Не розумію що від мене хочуть. Скористайтесь /help перш ніж писати мені такі дурниці.";

    public UnknownCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
sendBotMessageService.sendMessage(CommandUtils.getChatId(update), UNKNOWN_MESSAGE);
    }
}