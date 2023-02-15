package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.*;

/**
 * Help {@link Command}.
 */
public class HelpCommand implements Command{
    private final SendBotMessageService sendBotMessageService;

    public static final String HELP_MESSAGE = String.format("<b>Дотупні команди</b>\n\n"
                    + "<i>Почати\\зупинить роботу з ботом:</i>\n"
                    + "%s - розпочати роботу зі мною;\n"
                    + "%s - призупинити роботу зі мною;\n"
                    + "%s - дізнатись що я можу.\n",
            START.getCommandName(), STOP.getCommandName(), HELP.getCommandName());

    public HelpCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
sendBotMessageService.sendMessage(CommandUtils.getChatId(update), HELP_MESSAGE);
    }
}