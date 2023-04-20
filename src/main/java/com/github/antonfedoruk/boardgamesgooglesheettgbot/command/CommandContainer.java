package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.command.annotation.AdminCommand;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.command.annotation.GoogleAPICommand;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.controller.GOauthController;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
    private final GoogleAuthorizationCodeFlow flow;
    private final List<String> admins;

    public CommandContainer(SendBotMessageService sendBotMessageService,
                            TelegramUserService telegramUserService,
                            GoogleApiService googleApiService,
                            String userIdentifyKey,
                            GOauthController gOauthController,
                            List<String> admins) {
        flow = gOauthController.getFlow();
        USER_IDENTIFY_KEY = userIdentifyKey;
        this.admins = admins;
        commandMap = new HashMap<>();
        commandMap.put(START.getCommandName(), new StartCommand(sendBotMessageService, telegramUserService));
        commandMap.put(STOP.getCommandName(), new StopCommand(sendBotMessageService, telegramUserService));
        commandMap.put(HELP.getCommandName(), new HelpCommand(sendBotMessageService));
        commandMap.put(ADMIN_HELP.getCommandName(), new AdminHelpCommand(sendBotMessageService));
        commandMap.put(STAT.getCommandName(), new StatCommand(sendBotMessageService, telegramUserService));
        commandMap.put(GAMES.getCommandName(), new GamesCommand(sendBotMessageService, googleApiService));
        commandMap.put(UPDATE_LOCATION.getCommandName(), new UpdateLocationCommand(sendBotMessageService, googleApiService));
        commandMap.put(WINNERS.getCommandName(), new WinnersCommand(sendBotMessageService, googleApiService));
        commandMap.put(WIN.getCommandName(), new WinCommand(sendBotMessageService, googleApiService));
        commandMap.put(GIVE_ACCESS_TO_GSHEETS.getCommandName(), new GiveAccessToGSheetsCommand(sendBotMessageService, telegramUserService));
        commandMap.put(RESTRICT_ACCESS_TO_GSHEETS.getCommandName(), new RestrictAccessToGSheetsCommand(sendBotMessageService, telegramUserService));
        commandMap.put(NO.getCommandName(), new NoCommand(sendBotMessageService));

        unknownCommand = new UnknownCommand(sendBotMessageService);
        googleSignInCommand = new SignInCommand(sendBotMessageService);
        log.trace("Created CommandContainer for commands: " + commandMap.keySet() + ".");
    }

    public Command retrieveCommand(String commandIdentifier, String username) {
        Command appropriateOrDefaultCommand = commandMap.getOrDefault(commandIdentifier, unknownCommand);

        log.trace("Check if this command requires admin rights..");
        if (isAdminCommand(appropriateOrDefaultCommand)) {
            if (admins.contains(username)) {
                return appropriateOrDefaultCommand;
            } else {
                return unknownCommand;
            }
        }

        log.trace("Check if this command needs access to Google API.");
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

    public Command retrieveCommandWrapCommandWithLoggingDecorator(String commandIdentifier, String username) {
        return new LoggingCommandDecorator(retrieveCommand(commandIdentifier, username));
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

    private boolean isAdminCommand(Command command) {
        boolean isAdminCommand = nonNull(command.getClass().getAnnotation(AdminCommand.class));
        if (isAdminCommand) {
            log.trace("The user NEED an admin role to use this command.");
        } else {
            log.trace("The user DOES NOT NEED an admin role to use this command.");
        }
        return isAdminCommand;
    }
}