package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import org.junit.jupiter.api.DisplayName;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.CommandName.STAT;
import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.StatCommand.STAT_MESSAGE;

@DisplayName("Unit-level testing for StatCommand")
public class StatCommandTest extends AbstractCommandTest {

    @Override
    String getCommandName() {
        return STAT.getCommandName();
    }

    @Override
    String getCommandMessage() {
        return String.format(STAT_MESSAGE, 0);
    }

    @Override
    Command getCommand() {
        return new StatCommand(sendBotMessageService, telegramUserService);
    }
}