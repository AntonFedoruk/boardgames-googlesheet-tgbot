package com.github.antonfedoruk.boardgamesgooglesheettgbot.service;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.bot.BoardGamesGoogleSheetTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;

@Slf4j
@Service
public class SendBotMessageServiceImpl implements SendBotMessageService {

    private final BoardGamesGoogleSheetTelegramBot boardGamesBot;

    @Autowired
    public SendBotMessageServiceImpl(BoardGamesGoogleSheetTelegramBot boardGamesBot) {
        this.boardGamesBot = boardGamesBot;
    }

    @Override
    public void sendMessage(Long chatId, String message) {
        log.trace("SendBotMessageServiceImpl's sendMessage() invoked...");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.enableHtml(true);
        try {
            boardGamesBot.execute(sendMessage);
            log.trace("Message sent: '" + message + "'.");
        } catch (TelegramApiException e) {
            log.error("Exception occurred trying to send message: '" + message + "'.", e);
        }
        log.trace("...SendBotMessageServiceImpl's sendMessage() completed.");
    }

    @Override
    public void sendPhoto(Long chatId, File file) {
        log.trace("SendBotMessageServiceImpl's sendPhoto() invoked...");
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(file));
        try {
            boardGamesBot.execute(sendPhoto);
            log.trace("Photo sent: '" + file.getName() + "'.");
        } catch (TelegramApiException e) {
            log.error("Exception occurred trying to send photo: '" + file.getName() + "'.", e);
        }
        log.trace("...SendBotMessageServiceImpl's sendPhoto() completed.");
    }
}