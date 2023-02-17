package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.bot.BoardGamesGoogleSheetTelegramBot;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageServiceImpl;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Abstract class for testing {@link Command}s.
 */
abstract class AbstractCommandTest {
    protected BoardGamesGoogleSheetTelegramBot boardGamesBot = Mockito.mock(BoardGamesGoogleSheetTelegramBot.class);
    protected TelegramUserService telegramUserService = Mockito.mock(TelegramUserService.class);
    protected SendBotMessageService sendBotMessageService = new SendBotMessageServiceImpl(boardGamesBot);

    abstract String getCommandName();

    abstract String getCommandMessage();

    abstract Command getCommand();

    @Test
    @DisplayName("should properly execute command")
    void shouldProperlyExecuteCommand() throws TelegramApiException {
        // given
        Long chatId = 123L;
        Long userId = 33L;

        Update update = prepareUpdate(chatId, userId, getCommandName());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(getCommandMessage());
        sendMessage.enableHtml(true);

        // when
        getCommand().execute(update);

        // then
        Mockito.verify(boardGamesBot).execute(sendMessage);
    }

    public static Update prepareUpdate(Long chatId, Long userId,  String commandName) {
        Update update = new Update();
        User user = new User();
        user.setId(userId);
        Message message = Mockito.mock(Message.class);
        Mockito.when(message.getChatId()).thenReturn(chatId);
        Mockito.when(message.getText()).thenReturn(commandName);
        Mockito.when(message.getFrom()).thenReturn(user);
        update.setMessage(message);
        return update;
    }
}