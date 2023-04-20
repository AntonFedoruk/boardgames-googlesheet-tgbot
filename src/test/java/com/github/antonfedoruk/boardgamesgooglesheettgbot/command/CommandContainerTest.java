package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.command.annotation.AdminCommand;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.controller.GOauthController;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.testing.auth.oauth2.MockGoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.*;
import static java.util.Objects.nonNull;
import static org.mockito.ArgumentMatchers.anyString;

@DisplayName("Unit-level testing for CommandContainer")
class CommandContainerTest {
    private CommandContainer commandContainer;

    @BeforeEach
    public void init() throws IOException {
        SendBotMessageService sendBotMessageService = Mockito.mock(SendBotMessageService.class);
        TelegramUserService telegramUserService = Mockito.mock(TelegramUserService.class);
        GoogleApiService googleApiService = Mockito.mock(GoogleApiService.class);
        GoogleAuthorizationCodeFlow flow = Mockito.mock(GoogleAuthorizationCodeFlow.class);
        GOauthController gOauthController = Mockito.mock(GOauthController.class);
        Mockito.when(gOauthController.getFlow()).thenReturn(flow);
        Credential credential = new MockGoogleCredential((MockGoogleCredential.Builder) new MockGoogleCredential.Builder().setJsonFactory(GsonFactory.getDefaultInstance()).setTransport(new NetHttpTransport()).setClientSecrets("user", "secret"));
        credential.setRefreshToken("123123123");
        Mockito.when(flow.loadCredential(anyString())).thenReturn(credential);
        List<String> admins = List.of("admin");
        commandContainer = new CommandContainer(sendBotMessageService, telegramUserService, googleApiService, "userIdentifyKey", gOauthController, admins);
    }

    @Test
    @DisplayName("Should get all existing commands")
    public void shouldGetAllExistingCommands() {
        //when-then
        Arrays.stream(CommandName.values())
                .forEach(commandName -> {
                    Command command = commandContainer.retrieveCommand(commandName.getCommandName(), "admin");
                    Assertions.assertNotEquals(UnknownCommand.class, command.getClass());
                });
    }

    @Test
    @DisplayName("Should get all available commands fro non-admin user")
    public void shouldGetAllAvailableCommandsForNonAdminUser() {
        //when-then
        Arrays.stream(values())
                .filter(commandName -> !List.of(ADMIN_HELP, GIVE_ACCESS_TO_GSHEETS, RESTRICT_ACCESS_TO_GSHEETS).contains(commandName))
                .forEach(commandName -> {
                    Command command = commandContainer.retrieveCommand(commandName.getCommandName(), "user");
                    if (!nonNull(command.getClass().getAnnotation(AdminCommand.class))) {
                        Assertions.assertNotEquals(UnknownCommand.class, command.getClass());
                    }
                });
    }

    @Test
    @DisplayName("Should return UnknownCommand")
    public void shouldReturnUnknownCommand() {
        //given
        String unknownCommand = "/qwerty";

        //when
        Command command = commandContainer.retrieveCommand(unknownCommand, "user");

        //then
        Assertions.assertEquals(UnknownCommand.class, command.getClass());
    }

    @Test
    @DisplayName("Commands should be instance of LoggingCommandDecorator")
    public void commandShouldBeInstancceOfLoggingCommandDecorator() {
        //when-then
        Arrays.stream(CommandName.values())
                .forEach(commandName -> {
                    Command command = commandContainer.retrieveCommandWrapCommandWithLoggingDecorator(commandName.getCommandName(), "admin");
                    Assertions.assertEquals(LoggingCommandDecorator.class, command.getClass());
                });
    }
}