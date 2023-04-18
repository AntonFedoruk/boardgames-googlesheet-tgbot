package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getChatId;

/**
 * Unknown {@link Command}.
 */
public class UnknownCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(UnknownCommand.class);
    private final SendBotMessageService sendBotMessageService;

    public static final String UNKNOWN_MESSAGE = "Не розумію що від мене хочуть. Скористайтесь /help перш ніж писати мені такі дурниці.";

    public UnknownCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(getChatId(update), UNKNOWN_MESSAGE);
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}