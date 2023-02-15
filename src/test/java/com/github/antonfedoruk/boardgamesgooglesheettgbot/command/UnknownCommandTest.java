package com.github.antonfedoruk.boardgamesgooglesheettgbot.command;

import org.junit.jupiter.api.DisplayName;

import static com.github.antonfedoruk.boardgamesgooglesheettgbot.command.UnknownCommand.UNKNOWN_MESSAGE;

@DisplayName("Unit-level testing for UnknownCommand")
public class UnknownCommandTest extends AbstractCommandTest {

    @Override
    String getCommandName() {
        return "/xyz";
    }

    @Override
    String getCommandMessage() {
        return UNKNOWN_MESSAGE;
    }

    @Override
    Command getCommand() {
        return new UnknownCommand(sendBotMessageService);
    }
}