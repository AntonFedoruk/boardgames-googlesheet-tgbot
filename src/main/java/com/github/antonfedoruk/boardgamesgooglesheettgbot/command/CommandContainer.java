package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;

import java.util.HashMap;
import java.util.Map;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.*;

/**
 * Container of the {@link Command}s, which are using for handling telegram commands.
 */
public class CommandContainer {
    private final Map<String, Command> commandMap;
    private final Command unknownCommand;

    public CommandContainer(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService, GoogleApiService googleApiService) {
        commandMap = new HashMap<>();
        commandMap.put(START.getCommandName(), new StartCommand(sendBotMessageService, telegramUserService));
        commandMap.put(STOP.getCommandName(), new StopCommand(sendBotMessageService, telegramUserService));
        commandMap.put(HELP.getCommandName(), new HelpCommand(sendBotMessageService));
        commandMap.put(STAT.getCommandName(), new StatCommand(sendBotMessageService, telegramUserService));
        commandMap.put(GAMES.getCommandName(), new GamesCommand(sendBotMessageService, googleApiService));
        commandMap.put(UPDATE_LOCATION.getCommandName(), new UpdateLocationCommand(sendBotMessageService, googleApiService));
        commandMap.put(WINNERS.getCommandName(), new WinnersCommand(sendBotMessageService, googleApiService));
        commandMap.put(WIN.getCommandName(), new WinCommand(sendBotMessageService, googleApiService));
        commandMap.put(NO.getCommandName(), new NoCommand(sendBotMessageService));

        unknownCommand = new UnknownCommand(sendBotMessageService);
    }

    public Command retrieveCommand(String commandIdentifier) {
        return commandMap.getOrDefault(commandIdentifier, unknownCommand);
    }
}