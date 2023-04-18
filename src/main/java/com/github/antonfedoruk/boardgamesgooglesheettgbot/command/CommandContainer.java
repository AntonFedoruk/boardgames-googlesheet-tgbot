package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.command.annotation.GoogleAPICommand;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.controller.GOauthController;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleApiException;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.util.GoogleAuthorizeUtil;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.*;
import static java.util.Objects.nonNull;

/**
 * Container of the {@link Command}s, which are using for handling telegram commands.
 */
@Slf4j
public class CommandContainer {
    private final Map<String, Command> commandMap;
    private final Command unknownCommand;
    private final Command googleSignInCommand;
    private final String USER_IDENTIFY_KEY;
    private GoogleAuthorizationCodeFlow flow;

    public CommandContainer(SendBotMessageService sendBotMessageService,
                            TelegramUserService telegramUserService,
                            GoogleApiService googleApiService,
                            String userIdentifyKey,
                            GOauthController gOauthController) {
        flow = gOauthController.getFlow();
        USER_IDENTIFY_KEY = userIdentifyKey;
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
        googleSignInCommand = new SignInCommand(sendBotMessageService);
        log.trace("Created CommandContainer for commands: " + commandMap.keySet() + ".");
    }

    public Command retrieveCommand(String commandIdentifier) {
        Command appropriateOrDefaultCommand = commandMap.getOrDefault(commandIdentifier, unknownCommand);
        log.trace("Checking if this command need access to Google API.");
        if (isCommandAssociatedWithGoogleAPI(appropriateOrDefaultCommand)) {
            Credential credential;
            try {
                credential = flow.loadCredential(USER_IDENTIFY_KEY);
                if (credential != null) {
                    if (credential.refreshToken()) {
                        log.trace("User IS authenticated -> further operation with command '" + appropriateOrDefaultCommand.getClass().getSimpleName() + "' confirmed.");
                        return appropriateOrDefaultCommand;
                    }
                }
            } catch (IOException e) {
                log.error("Failed to load credential for use '" + USER_IDENTIFY_KEY + "'.", e);
            }
            return googleSignInCommand;
        }
        return appropriateOrDefaultCommand;
    }

    public Command retrieveCommandWrapCommandWithLoggingDecorator(String commandIdentifier) {
        return new LoggingCommandDecorator(retrieveCommand(commandIdentifier));
    }

    private boolean isCommandAssociatedWithGoogleAPI(Command command) {
        boolean isAccessToGoogleAPINeeded = nonNull(command.getClass().getAnnotation(GoogleAPICommand.class));
        if (isAccessToGoogleAPINeeded) {
            log.trace("Access to Google API needed: YES.");
        } else {
            log.trace("Access to Google API needed: NO.");
        }
        return isAccessToGoogleAPINeeded;
    }
}