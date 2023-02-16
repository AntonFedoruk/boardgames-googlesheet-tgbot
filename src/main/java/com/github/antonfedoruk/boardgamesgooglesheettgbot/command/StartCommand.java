package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.repository.entity.TelegramUser;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getChatId;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getUserName;

/**
 * Start {@link Command}.
 */
public class StartCommand implements Command {
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;

    public static final String START_MESSAGE = "Опача, ти хто такий? Запишу тебе в свій список, побачимо що з цього вийде. \n" +
            "А взагалі, то я тут для того, щоб допомогти зібратись на настолку та вести перелік ігор що в нас є, ну і пізніше має добавитись статистика по перемогам.";
    public StartCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public void execute(Update update) {
        Long userId = CommandUtils.getUserId(update);
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
                    telegramUserService.save(telegramUser);
                });
        sendBotMessageService.sendMessage(getChatId(update), START_MESSAGE);
    }
}