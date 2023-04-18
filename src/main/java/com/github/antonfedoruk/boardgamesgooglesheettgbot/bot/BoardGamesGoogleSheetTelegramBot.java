package com.github.antonfedoruk.boardgamesgooglesheettgbot.bot;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandContainer;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.controller.GOauthController;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageServiceImpl;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.NO;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getUserName;

/**
 * Telegram bot that allow users to maintain with their Google Sheet "Board games".
 */
@Component
public class BoardGamesGoogleSheetTelegramBot extends TelegramLongPollingBot {
    public static final String COMMAND_PREFIX = "/";
    private final String username;
    private final CommandContainer commandContainer;

    public BoardGamesGoogleSheetTelegramBot(TelegramUserService telegramUserService,
                                            GoogleApiService googleApiService,
                                            @Value("${bot.token}") String token,
                                            @Value("${bot.username}") String username,
                                            @Value("${google.user.identify.key}") String userIdentifyKey,
                                            GOauthController gOauthController) {
        super(token);
        this.username = username;
        this.commandContainer = new CommandContainer(new SendBotMessageServiceImpl(this), telegramUserService, googleApiService, userIdentifyKey, gOauthController);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();

            MDC.put("user", getUserName(update));
            MDC.put("message", message);

            if (message.startsWith(COMMAND_PREFIX)) {
                String commandIdentifier = message.split(" ")[0].toLowerCase().split("@")[0];
                commandContainer.retrieveCommandWrapCommandWithLoggingDecorator(commandIdentifier).execute(update);
            } else {
                commandContainer.retrieveCommandWrapCommandWithLoggingDecorator(NO.getCommandName()).execute(update);
            }

            MDC.remove("user");
            MDC.remove("message");
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }
}
