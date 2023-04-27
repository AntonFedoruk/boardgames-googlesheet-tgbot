package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleApiIOException;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleApiOnExecuteException;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.GAMES;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.GamesCommand.GAMES_FOUND_MESSAGE;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.GamesCommand.GAMES_NOT_FOUND_MESSAGE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@DisplayName("Unit-level testing for GamesCommand")
class GamesCommandTest {
    Command gamesCommand;
    SendBotMessageService sendBotMessageService;
    GoogleApiService googleApiService;

    @BeforeEach
    void init() {
        sendBotMessageService = Mockito.mock(SendBotMessageService.class);
        googleApiService = Mockito.mock(GoogleApiService.class);
        gamesCommand = new GamesCommand(sendBotMessageService, googleApiService);
    }

    @Test
    @DisplayName("Should send proper message when game list is empty")
    void shouldSendProperMessageWhenGameListIsEmpty() throws GoogleApiIOException, GoogleApiOnExecuteException {
        // given
        Mockito.when(googleApiService.getGamesFromGoogleSheet()).thenReturn(new HashMap<>());

        Long chatId = 1L;
        Update  update = prepearedUpdate(chatId);
        // when
        gamesCommand.execute(update);
        // then
        Mockito.verify(sendBotMessageService).sendMessage(chatId, GAMES_NOT_FOUND_MESSAGE);
        Mockito.verify(sendBotMessageService, Mockito.times(1)).sendMessage(any(), anyString());
    }

    @Test
    @DisplayName("Should send image when game list is not empty")
    void shouldSendImageWhenGameListIsNotEmpty() throws GoogleApiIOException, GoogleApiOnExecuteException {
        // given
        Map<String, Game> games = new HashMap<>();
        Game game = new Game("1", "Inish", "2-5", "Nick", "At the office");
        games.put("1", game);
        Mockito.when(googleApiService.getGamesFromGoogleSheet()).thenReturn(games);

        Long chatId = 1L;
        Update  update = prepearedUpdate(chatId);
        // when
        gamesCommand.execute(update);
        // then
        Mockito.verify(sendBotMessageService).sendMessage(chatId, GAMES_FOUND_MESSAGE);
        Mockito.verify(sendBotMessageService).sendPhoto(any(), any(File.class));
    }

    private Update prepearedUpdate(Long chatId) {
        Update update = new Update();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getText()).thenReturn(GAMES.getCommandName());
        update.setMessage(message);
        return update;
    }
}