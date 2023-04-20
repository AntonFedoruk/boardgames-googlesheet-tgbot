package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.command.annotation.AdminCommand;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.repository.entity.TelegramUser;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.*;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getChatId;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getMessage;

/**
 * GiveAccessToGSheets {@link Command}.
 */
@AdminCommand
public class GiveAccessToGSheetsCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(GiveAccessToGSheetsCommand.class);
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;

    public static final String ACCESS_PROVIDED_MESSAGE = "Тепер <b>%s</b> отримав повний доступ до взаємодії зі мною!";
    public static final String HOW_TO_USE_MESSAGE = "Щоб надати доступ користувачеві скористайся наступним синтиксисом: \n" +
            GIVE_ACCESS_TO_GSHEETS.getCommandName() + " <b>ім'я_користувача</b>\n" +
            "Для зручності ось список користувачів без такого доступу:<i>\n%s</i>";
    public static final String ALL_USERS_HAS_ACCESS_MESSAGE = "Вже всі користувачі отримали доступ.\n" +
            "П.С.: окрім тих, хто не хоче[не ввів команду " + START.getCommandName() + ", aбо ж відмінив сповіщення командою " +
            STOP.getCommandName() + "].";
    public static final String NO_SUCH_USER_MESSAGE = "Нажаль я не знайшов куористувача з таким userName: <b>%s</b>.";


    public GiveAccessToGSheetsCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public void execute(Update update) {
        String commandFromUser = getMessage(update);
        if (commandFromUser.equalsIgnoreCase(GIVE_ACCESS_TO_GSHEETS.getCommandName())) {
            log.info(commandFromUser + " was send without parameters, so instruction message should be sent.");
            List<TelegramUser> allUsersWithoutAccessToGoogleSheets = telegramUserService.findAllUsersWithoutAccessToGoogleSheets();
            if (allUsersWithoutAccessToGoogleSheets.isEmpty()) {
                sendBotMessageService.sendMessage(getChatId(update), ALL_USERS_HAS_ACCESS_MESSAGE);
                return;
            }
            StringBuilder users = new StringBuilder();
            allUsersWithoutAccessToGoogleSheets.forEach(telegramUser -> users.append(telegramUser.getUserName()).append("\n "));
            sendBotMessageService.sendMessage(getChatId(update), String.format(HOW_TO_USE_MESSAGE, users));
            return;
        }

        String[] commandByParts = commandFromUser.split(" ");
        String userName = commandByParts[1];

        telegramUserService.findByUserName(userName).ifPresentOrElse(
                user -> {
                    user.setHasGoogleAccess(true);
                    telegramUserService.save(user);
                    sendBotMessageService.sendMessage(getChatId(update), String.format(ACCESS_PROVIDED_MESSAGE, userName));
                },
                () -> {
                    sendBotMessageService.sendMessage(getChatId(update), String.format(NO_SUCH_USER_MESSAGE, userName));
                });
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}