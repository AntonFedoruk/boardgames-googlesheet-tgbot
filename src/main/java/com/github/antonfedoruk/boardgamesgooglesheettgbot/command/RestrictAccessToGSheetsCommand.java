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
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.*;

/**
 * RestrictAccessToGSheets {@link Command}.
 */
@AdminCommand
public class RestrictAccessToGSheetsCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(RestrictAccessToGSheetsCommand.class);
    private final SendBotMessageService sendBotMessageService;
    private final TelegramUserService telegramUserService;

    public static final String ACCESS_RESTRICTED_MESSAGE = "Користувач <b>%s</b> позбавлений повного доступу до взаємодії зі мною!";
    public static final String HOW_TO_USE_MESSAGE = "Щоб позбавити доступа користувача скористайся наступним синтиксисом: \n" +
            RESTRICT_ACCESS_TO_GSHEETS.getCommandName() + " <b>ім'я_користувача</b>\n" +
            "Для зручності ось список користувачів з таким доступом:<i>\n%s</i>";
    public static final String NO_USERS_TO_RESTRICT_ACCESS_MESSAGE = "Жоден користувач не має доступу до таблиць Google.";
    public static final String NO_SUCH_USER_MESSAGE = "Нажаль я не знайшов куористувача з таким userName: <b>%s</b>.";


    public RestrictAccessToGSheetsCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.sendBotMessageService = sendBotMessageService;
        this.telegramUserService = telegramUserService;
    }

    @Override
    public void execute(Update update) {
        String commandFromUser = getMessage(update);
        if (commandFromUser.equalsIgnoreCase(RESTRICT_ACCESS_TO_GSHEETS.getCommandName())) {
            log.info(commandFromUser + " was send without parameters, so instruction message should be sent.");
            List<TelegramUser> allUsersWithAccessToGoogleSheets = telegramUserService.findAllUsersWithAccessToGoogleSheets();
            if (allUsersWithAccessToGoogleSheets.isEmpty()) {
                sendBotMessageService.sendMessage(getChatId(update), NO_USERS_TO_RESTRICT_ACCESS_MESSAGE);
                return;
            }
            StringBuilder users = new StringBuilder();
            allUsersWithAccessToGoogleSheets.forEach(telegramUser -> {
                if (telegramUser.getUserName().equals(getUserName(update))){
                    users.append(telegramUser.getUserName()).append(" (Ви)").append("\n ");
                } else {
                    users.append(telegramUser.getUserName()).append("\n ");
                }
            });
            sendBotMessageService.sendMessage(getChatId(update), String.format(HOW_TO_USE_MESSAGE, users));
            return;
        }

        String[] commandByParts = commandFromUser.split(" ");
        String userName = commandByParts[1];

        telegramUserService.findByUserName(userName).ifPresentOrElse(
                user -> {
                    user.setHasGoogleAccess(false);
                    telegramUserService.save(user);
                    sendBotMessageService.sendMessage(getChatId(update), String.format(ACCESS_RESTRICTED_MESSAGE, userName));
                },
                () -> sendBotMessageService.sendMessage(getChatId(update), String.format(NO_SUCH_USER_MESSAGE, userName)));
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}