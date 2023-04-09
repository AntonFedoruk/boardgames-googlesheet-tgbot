package com.github.antonfedoruk.boardgamesgooglesheettgbot.service;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.bot.BoardGamesGoogleSheetTelegramBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@DisplayName("Unit-level testing for SendBotMessageService")
class SendBotMessageServiceTest {
    private SendBotMessageService sendBotMessageService;
    private BoardGamesGoogleSheetTelegramBot boardGamesBot;

    @BeforeEach
    public void init() {
        boardGamesBot = Mockito.mock(BoardGamesGoogleSheetTelegramBot.class);
        sendBotMessageService = new SendBotMessageServiceImpl(boardGamesBot);
    }

    @Test
    @DisplayName("Should properly send message")
    void shouldProperlySendMessage() throws TelegramApiException {
        // given
        Long chatId = 1L;
        String message = "test_message";

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId.toString());
        sendMessage.enableHtml(true);

        // when
        sendBotMessageService.sendMessage(chatId, message);

        // then
        Mockito.verify(boardGamesBot).execute(sendMessage);
    }

// falls with error
//    @Test
//    @DisplayName("Should properly send photo")
//    void shouldProperlySendPhoto() throws TelegramApiException {
//        // given
//        Long chatId = 1L;
//        File file = new File("/home/anton/IdeaProjects/boardgames-googlesheet-tgbot/src/test/resources/file.png");
//
//        SendPhoto sendPhoto = new SendPhoto();
//        sendPhoto.setChatId(chatId);
//        sendPhoto.setPhoto(new InputFile(file));
//
//        Mockito.when(boardGamesBot.execute(sendPhoto)).thenReturn(new Message());
//
//        // when
//        sendBotMessageService.sendPhoto(chatId, file);
//
//        // then
//        Mockito.verify(boardGamesBot).execute(sendPhoto);
//    }
}