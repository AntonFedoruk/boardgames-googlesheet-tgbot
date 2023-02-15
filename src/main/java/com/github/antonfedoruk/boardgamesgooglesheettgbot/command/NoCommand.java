package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * No {@link Command}.
 */
public class NoCommand implements Command{
    private final SendBotMessageService sendBotMessageService;

    public static final String NO_MESSAGE = "Я підтримую команди які починаються зі слешу(/).\n"
            + "Для перегляду доступних команд скористайтесь /help.";

    public NoCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
sendBotMessageService.sendMessage(CommandUtils.getChatId(update), NO_MESSAGE);
    }
}