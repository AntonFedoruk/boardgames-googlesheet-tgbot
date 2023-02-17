package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.TelegramUserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandUtils.getChatId;

public class StatCommand implements Command{
    private final TelegramUserService telegramUserService;
    private final SendBotMessageService sendBotMessageService;

    public final static String STAT_MESSAGE = "К-ть людей, які залюбки пограли б в настолки: <b>%s</b>.";

    public StatCommand(SendBotMessageService sendBotMessageService, TelegramUserService telegramUserService) {
        this.telegramUserService = telegramUserService;
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        int activeUserCount = telegramUserService.findAllActiveUsers().size();
        sendBotMessageService.sendMessage(getChatId(update), String.format(STAT_MESSAGE, activeUserCount));
    }
}
