package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.*;

/**
 * Help {@link Command}.
 */
public class HelpCommand implements Command {
    private final SendBotMessageService sendBotMessageService;

    public static final String HELP_MESSAGE = String.format("<b>Дотупні команди</b>\n"
                    + "<i>Пов'язані з настолочками:</i>\n"
                    + "%s - список наших ігор;\n"
                    + "%s - відобразити записи про перемоги;\n"
                    + "%s - змінити місцезнаходження гри;\n"
                    + "%s - добавити запис про перемогу.\n"
                    + "<i>Загальні:</i>\n"
                    + "%s - розпочати роботу зі мною;\n"
                    + "%s - призупинити роботу зі мною;\n"
                    + "%s - відобразити активних учасників бота;\n"
                    + "%s - дізнатись що я можу.\n",
            GAMES.getCommandName(), WINNERS.getCommandName(), UPDATE_LOCATION.getCommandName(), WIN.getCommandName(),
            START.getCommandName(), STOP.getCommandName(), STAT.getCommandName(), HELP.getCommandName());

    public HelpCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(CommandUtils.getChatId(update), HELP_MESSAGE);
    }
}