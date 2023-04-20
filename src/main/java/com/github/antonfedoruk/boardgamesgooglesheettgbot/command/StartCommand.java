package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.repository.entity.TelegramUser;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.HELP;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.*;

/**
 * Start {@link Command}.
 */
public class StartCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(StartCommand.class);
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;

    public static final String START_MESSAGE = "Привіт, скажи 300)) Запишу тебе в свій список, побачимо що з цього вийде.\n" +
            "Я тут для того, щоб допомогти зібратись на настолку та вести перелік ігор що в нас є і веду статистику по перемогам.\n" +
            "Скористайся командою " + HELP.getCommandName() + " щоб дізнатись як можна зі мною взаємодіяти.";

    public StartCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public void execute(Update update) {
        Long userId = getUserId(update);
        telegramUserService.findByUserId(userId).ifPresentOrElse(
                user -> {
                    user.setActive(true);
                    telegramUserService.save(user);
                },
                () -> {
                    TelegramUser telegramUser = new TelegramUser();
                    telegramUser.setActive(true);
                    telegramUser.setUserId(userId);
                    telegramUser.setUserName(getUserName(update));
                    telegramUser.setHasGoogleAccess(false);
                    telegramUserService.save(telegramUser);
                });
        sendBotMessageService.sendMessage(getChatId(update), START_MESSAGE);
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}