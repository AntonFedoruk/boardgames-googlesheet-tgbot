package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.Game;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.dto.WinRecord;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.googlesheetclient.GoogleApiException;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.GoogleApiService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.WINNERS;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.WinnersCommand.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@DisplayName("Unit-level testing for WinnersCommand")
@ExtendWith(MockitoExtension.class)
class WinnersCommandTest {
    Command winnersCommand;
    SendBotMessageService sendBotMessageService;
    GoogleApiService googleApiService;

    @BeforeEach
    void init() {
        sendBotMessageService = Mockito.mock(SendBotMessageService.class);
        googleApiService = Mockito.mock(GoogleApiService.class);
        winnersCommand = new WinnersCommand(sendBotMessageService, googleApiService);
    }

    @Test
    @DisplayName("Should send proper message when there is no necessary parameters.")
    void shouldSendProperMessageWhenThereIsNoNecessaryParameters() throws GoogleApiException {
        // given
        Long chatId = 1L;
        Update  update = prepearedUpdate(chatId, "");
        // when
        winnersCommand.execute(update);
        // then
        Mockito.verify(sendBotMessageService).sendMessage(chatId, HOW_TO_USE_MESSAGE);
        Mockito.verify(sendBotMessageService, Mockito.times(1)).sendMessage(any(), anyString());
    }

    @Test
    @DisplayName("Should send proper message when there is no win records.")
    void shouldSendProperMessageWhenThereIsNoWinRecords() throws GoogleApiException {
        // given
        String gamesId = "1";
        String gameName = "chess";

        Map<String, Game> games = new HashMap<>();
        Game game = new Game("1", gameName, "2", "Nick", "In the park");
        games.put("1", game);
        Mockito.when(googleApiService.getGamesFromGoogleSheet()).thenReturn(games);

        Mockito.when(googleApiService.getWinRecordsForGameFromGoogleSheet(gameName)).thenReturn(new ArrayList<>());

        Long chatId = 1L;
        Update  update = prepearedUpdate(chatId, gamesId);
        // when
        winnersCommand.execute(update);
        // then
        Mockito.verify(sendBotMessageService).sendMessage(chatId,  String.format(NO_WIN_RECORDS_MESSAGE, gameName));
        Mockito.verify(sendBotMessageService, Mockito.times(1)).sendMessage(any(), anyString());
    }

    @Test
    @DisplayName("Should send proper message when win records exist")
    void shouldSendProperMessageWhenWinRecordsExist() throws GoogleApiException {
        // given
        String gamesId = "1";
        String gameName = "chess";

        Map<String, Game> games = new HashMap<>();
        Game game = new Game("1", gameName, "2", "Nick", "In the park");
        games.put("1", game);
        Mockito.when(googleApiService.getGamesFromGoogleSheet()).thenReturn(games);

        List<WinRecord> winRecordsForGameFromGoogleSheet = new ArrayList<>();
        WinRecord winRecord = new WinRecord(LocalDate.of(2023, 4, 20), "Mazar", "1:0", "Mike", "Nice gameplay");
        winRecordsForGameFromGoogleSheet.add(winRecord);
        Mockito.when(googleApiService.getWinRecordsForGameFromGoogleSheet(gameName)).thenReturn(winRecordsForGameFromGoogleSheet);

        Long chatId = 1L;
        Update  update = prepearedUpdate(chatId, gamesId);
        // when
        winnersCommand.execute(update);
        // then
        Mockito.verify(sendBotMessageService).sendMessage(chatId, WIN_RECORDS_FOUND_MESSAGE);
        Mockito.verify(sendBotMessageService).sendPhoto(any(), any(File.class));
    }

//    @Test
//    @DisplayName("Should send proper message when AWTError occurs")
//    void shouldSendProperMessageWhenAWTErrorOccurs() throws GoogleApiException {
//        // given
//        String gamesId = "1";
//        String gameName = "chess";
//
//        Map<String, Game> games = new HashMap<>();
//        Game game = new Game("1", gameName, "2", "Nick", "In the park");
//        games.put("1", game);
//        Mockito.when(googleApiService.getGamesFromGoogleSheet()).thenReturn(games);
//
//        List<WinRecord> winRecordsForGameFromGoogleSheet = new ArrayList<>();
//        WinRecord winRecord = new WinRecord(LocalDate.of(2023, 4, 20), "Mazar", "1:0", "Mike", "Nice gameplay");
//        winRecordsForGameFromGoogleSheet.add(winRecord);
//        Mockito.when(googleApiService.getWinRecordsForGameFromGoogleSheet(gameName)).thenReturn(winRecordsForGameFromGoogleSheet).thenThrow(new AWTError("X11 error"));
////        Mockito.when(googleApiService.getWinRecordsForGameFromGoogleSheet(gameName)).thenThrow(new AWTError("X11 error"));
//
//        Long chatId = 1L;
//        Update  update = prepearedUpdate(chatId, gamesId);
//
//        String message = "<b>Записи перемог</b>: \n";
//        String winRecords = winRecordsForGameFromGoogleSheet.stream().sorted()
//                .map(record -> "<b>" + winRecord.getWinner() + "</b> (<i>" + winRecord.getScore() + "</i> | <u> " + winRecord.getDate() + "</u>)")
//                .collect(Collectors.joining("\n"));
//
//        // when
//        winnersCommand.execute(update);
//        // then
//        Mockito.verify(sendBotMessageService).sendMessage(chatId, WIN_RECORDS_FOUND_MESSAGE);
//        Mockito.verify(sendBotMessageService).sendMessage(chatId, message + winRecords);
//    }

    private Update prepearedUpdate(Long chatId, String gameId) {
        Update update = new Update();
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getText()).thenReturn(WINNERS.getCommandName() + " " + gameId);
        update.setMessage(message);
        return update;
    }
}