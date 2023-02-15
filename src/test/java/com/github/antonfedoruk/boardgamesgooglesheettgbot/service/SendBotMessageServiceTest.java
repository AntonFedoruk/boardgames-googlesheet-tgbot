package com.github.antonfedoruk.boardgamesgooglesheettgbot.service;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.bot.BoardGamesGoogleSheetTelegramBot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
}