package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getChatId;

/**
 * No {@link Command}.
 */
public class NoCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(NoCommand.class);
    private final SendBotMessageService sendBotMessageService;

    public static final String NO_MESSAGE = "Я підтримую команди які починаються зі слешу(/).\n"
            + "Для перегляду доступних команд скористайтесь /help.";

    public NoCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(getChatId(update), NO_MESSAGE);
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}