package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getChatId;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getUserId;
/**
 * Stop {@link Command}.
 */
public class StopCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(StopCommand.class);
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;

    public static final String STOP_MESSAGE = "ееее, то є зрада(\n Добавлю тебе в список пісюнів, але поки олівцем, надіюсь ти виправишся...";

    public StopCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(getChatId(update), STOP_MESSAGE);
        telegramUserService.findByUserId(getUserId(update)).ifPresent(telegramUser -> {
            telegramUser.setActive(false);
            telegramUser.setHasGoogleAccess(false);
            telegramUserService.save(telegramUser);
        });
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}