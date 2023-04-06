package com.github.antonfedoruk.boardgamesgooglesheettgbot.bot;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandContainer;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageServiceImpl;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.NO;

/**
 * Telegram bot that allow users to maintain with their Google Sheet "Board games".
 */
@Component
public class BoardGamesGoogleSheetTelegramBot extends TelegramLongPollingBot {
    @Value("${bot.username}")
    private String username;

    public static String COMMAND_PREFIX = "/";

    private final CommandContainer commandContainer;

    @Autowired
    public BoardGamesGoogleSheetTelegramBot(TelegramUserService telegramUserService, GoogleApiService googleApiService, @Value("${bot.token}") String token) {
        super(token);
        this.commandContainer = new CommandContainer(new SendBotMessageServiceImpl(this), telegramUserService, googleApiService);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            if (message.startsWith(COMMAND_PREFIX)) {
                String commandIdentifier = message.split(" ")[0].toLowerCase().split("@")[0];

                commandContainer.retrieveCommand(commandIdentifier).execute(update);
            } else {
                commandContainer.retrieveCommand(NO.getCommandName()).execute(update);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return username;
    }
}
