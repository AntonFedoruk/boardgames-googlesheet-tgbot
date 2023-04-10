package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.UPDATE_LOCATION;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.UpdateLocationCommand.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@DisplayName("Unit-level testing for GamesCommand")
class UpdateLocationCommandTest {
    Command updateLocationCommand;
    SendBotMessageService sendBotMessageService;
    GoogleApiService googleApiService;

    String gamesId;
    String newLocation;
    Long chatId;

    @BeforeEach
    void init() {
        sendBotMessageService = Mockito.mock(SendBotMessageService.class);
        googleApiService = Mockito.mock(GoogleApiService.class);
        updateLocationCommand = new UpdateLocationCommand(sendBotMessageService, googleApiService);
        gamesId = "1";
        newLocation = "new location";
        chatId = 1L;

        try {
            Map<String, Game> games = new HashMap<>();
            Game game = new Game("1", "Inish", "2-5", "Nick", "At the office");
            games.put("1", game);
            Mockito.when(googleApiService.getGamesFromGoogleSheet()).thenReturn(games);
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Should send proper message when location updated")
    void shouldSendProperMessageWhenLocationUpdated() throws GeneralSecurityException, IOException {
        // given
        Mockito.when(googleApiService.updateGameLocation(gamesId, newLocation)).thenReturn(newLocation);

        Update update = prepearedUpdate(UPDATE_LOCATION.getCommandName() + " "+ gamesId + " " + newLocation);
        // when
        updateLocationCommand.execute(update);
        // then
        Mockito.verify(sendBotMessageService).sendMessage(chatId, String.format(LOCATION_CHANGED_MESSAGE, gamesId, newLocation));
        Mockito.verify(sendBotMessageService, Mockito.times(1)).sendMessage(any(), anyString());
    }

    @Test
    @DisplayName("Should send proper message when user enter command without any parameters")
    void shouldSendProperMessageWhenUserEnterCommandWithoutAnyParameters() throws GeneralSecurityException, IOException {
        // given
        Update update = prepearedUpdate(UPDATE_LOCATION.getCommandName());
        // when
        updateLocationCommand.execute(update);
        // then
        Mockito.verify(sendBotMessageService).sendMessage(chatId, HOW_TO_USE_MESSAGE);
        Mockito.verify(sendBotMessageService, Mockito.times(1)).sendMessage(any(), anyString());
    }

    @Test
    @DisplayName("Should send proper message when user enter command with larger games Id")
    void shouldSendProperMessageWhenUserEnterCommandWithLargerGamesId() throws GeneralSecurityException, IOException {
        // given
        Update update = prepearedUpdate(UPDATE_LOCATION.getCommandName() + " 666 new location");
        // when
        updateLocationCommand.execute(update);
        // then
        Mockito.verify(sendBotMessageService).sendMessage(chatId, OUT_OF_RANGE_GAME_ID_MESSAGE);
        Mockito.verify(sendBotMessageService, Mockito.times(1)).sendMessage(any(), anyString());
    }

    @Test
    @DisplayName("Should send proper message when user enter command with to long location length")
    void shouldSendProperMessageWhenUserEnterCommandWithToLongLocationLength() throws GeneralSecurityException, IOException {
        // given
        Update update = prepearedUpdate(UPDATE_LOCATION.getCommandName() + " 1 this is definitely longer then 20 symbols");
        // when
        updateLocationCommand.execute(update);
        // then
        Mockito.verify(sendBotMessageService).sendMessage(chatId, TO_LONG_LOCATION_NAME_MESSAGE);
        Mockito.verify(sendBotMessageService, Mockito.times(1)).sendMessage(any(), anyString());
    }

    private Update prepearedUpdate(String commandFromUser) {
        Update update = new Update();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getText()).thenReturn(commandFromUser);
        update.setMessage(message);
        return update;
    }
}