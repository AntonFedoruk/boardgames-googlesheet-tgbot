package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import com.github.antonfedoruk.boardgamesgooglesheettgbot.command.annotation.AdminCommand;
import com.github.antonfedoruk.boardgamesgooglesheettgbot.service.SendBotMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.*;

/**
 * Help {@link Command}.
 */
@AdminCommand
public class AdminHelpCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(AdminHelpCommand.class);
    private final SendBotMessageService sendBotMessageService;

    public static final String HELP_MESSAGE = String.format("<b>Дотупні команди</b>\n"
                    + "%s - надати доступу до таблиць Google;\n"
                    + "%s - позбавити доступу до таблиць Google;\n"
                    + "%s - відобразити к-ть активних учасників бота.\n",
            GIVE_ACCESS_TO_GSHEETS.getCommandName(), RESTRICT_ACCESS_TO_GSHEETS.getCommandName(), STAT.getCommandName());

    public AdminHelpCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(CommandUtils.getChatId(update), HELP_MESSAGE);
    }

    @Override
    public Logger getLogger() {
        return log;
    }
}